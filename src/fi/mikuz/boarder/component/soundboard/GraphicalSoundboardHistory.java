package fi.mikuz.boarder.component.soundboard;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.widget.Toast;
import fi.mikuz.boarder.gui.GraphicalSoundboardEditor;

/**
 * Stores changes in soundboard
 * 
 * @author Jan Mikael Lindl�f
 */
public class GraphicalSoundboardHistory {
	private static final String TAG = "GraphicalSoundboardHistory";
	
	GraphicalSoundboardEditor editor;
	private final Object lock = new Object();
	
	List<GraphicalSoundboard> history;
	int index;

	public GraphicalSoundboardHistory(GraphicalSoundboardEditor editor) {
		this.editor = editor;
		this.history = new ArrayList<GraphicalSoundboard>();
		this.index = -1;
	}
	public List<GraphicalSoundboard> getHistory() {
		return history;
	}
	public void setHistory(List<GraphicalSoundboard> history) {
		this.history = history;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	public void createHistoryCheckpoint() {
		synchronized (lock) {
			for (int i = history.size()-index; i > 1; i--) {
				// User has undone and then creates a checkpoint
				history.remove(history.size()-1);
				Log.v(TAG, "removing history from end, size is " + history.size());
			}
			
			index++;
			setHistoryCheckpoint();
			
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
	
	public void setHistoryCheckpoint() {
		synchronized (lock) {
			GraphicalSoundboard gsbh = GraphicalSoundboard.copy(editor.mGsb);
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

	public void undo() {
		synchronized (lock) {
			if (index <= 0) {
				Toast.makeText(editor.getApplicationContext(), "Unable to undo", Toast.LENGTH_SHORT).show();
			} else {
				index--;
				editor.loadBoard(GraphicalSoundboard.copy(history.get(index)));
				editor.issueResolutionConversion();
			}
			Log.v(TAG, "undo: index is " + index + " size is " + history.size());
		}
	}

	public void redo() {
		synchronized (lock) {
			if (index+1 >= history.size()) {
				Toast.makeText(editor.getApplicationContext(), "Unable to redo", Toast.LENGTH_SHORT).show();
			} else {
				index++;
				editor.loadBoard(GraphicalSoundboard.copy(history.get(index)));
			}
			Log.v(TAG, "redo: index is " + index + " size is " + history.size());
		}
	}

}