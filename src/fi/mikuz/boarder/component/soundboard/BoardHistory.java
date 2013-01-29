package fi.mikuz.boarder.component.soundboard;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import fi.mikuz.boarder.gui.BoardEditor;

/**
 * Stores changes in soundboard
 * 
 * @author Jan Mikael Lindlöf
 */
public class BoardHistory {
	private static final String TAG = BoardHistory.class.getSimpleName();
	
	private final int boardId;
	private final Object lock = new Object();
	
	List<GraphicalSoundboard> history;
	int index;

	public BoardHistory(int boardId) {
		this.history = new ArrayList<GraphicalSoundboard>();
		this.index = -1;
		this.boardId = boardId;
	}
	
	public int getBoardId() {
		return boardId;
	}

	public void createHistoryCheckpoint(Context context, GraphicalSoundboard board) {
		synchronized (lock) {
			for (int i = history.size()-index; i > 1; i--) {
				// User has undone and then creates a checkpoint
				history.remove(history.size()-1);
				Log.v(TAG, "removing history from end, size is " + history.size());
			}
			
			index++;
			setHistoryCheckpoint(context, board);
			
			while (history.size() >= 30) {
				Log.v(TAG, "removing history from start, size is " + history.size());
				index--;
				// Remove the second last save, keep the originally loaded board
				history.remove(1);
			}
			//StackTraceElement[] stack = Thread.currentThread().getStackTrace();
			//Log.d(TAG, "create: index is " + mCurrentHistoryIndex + " size is " + mHistory.size() + " caller: " + stack[3].getMethodName() + " - " + stack[3].getLineNumber());
		}
	}
	
	public void setHistoryCheckpoint(Context context, GraphicalSoundboard board) {
		synchronized (lock) {
			GraphicalSoundboard gsbh = GraphicalSoundboard.copy(context, board);
			GraphicalSoundboard.unloadImages(gsbh);
			
			// If new checkpoint is being created then index is ahead of list by 1 now
			if (history.size() < 0) {
				throw new IllegalArgumentException("History index is negative");
			} else if (history.size() == index) {
				history.add(gsbh);
			} else if (history.size() > index) {
				history.set(index, gsbh);
			} else {
				throw new IllegalArgumentException("History index is ahead of history list");
			}
		}
	}

	public void undo(Context context, BoardEditor editor) {
		synchronized (lock) {
			if (index <= 0) {
				Toast.makeText(editor.getApplicationContext(), "Unable to undo", Toast.LENGTH_SHORT).show();
			} else {
				index--;
				GraphicalSoundboard undoGsb = history.get(index);
				editor.loadBoard(GraphicalSoundboard.copy(context, undoGsb));
				editor.issueResolutionConversion(undoGsb.getScreenOrientation());
			}
			Log.v(TAG, "undo: index is " + index + " size is " + history.size());
		}
	}

	public void redo(Context context, BoardEditor editor) {
		synchronized (lock) {
			if (index+1 >= history.size()) {
				Toast.makeText(editor.getApplicationContext(), "Unable to redo", Toast.LENGTH_SHORT).show();
			} else {
				index++;
				editor.loadBoard(GraphicalSoundboard.copy(context, history.get(index)));
			}
			Log.v(TAG, "redo: index is " + index + " size is " + history.size());
		}
	}

}
