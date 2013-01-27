package fi.mikuz.boarder.util.editor;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import fi.mikuz.boarder.component.soundboard.BoardHistory;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundboard;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
public class BoardHistoryProvider {
	private static final String TAG = BoardHistoryProvider.class.getSimpleName();
	
	List<BoardHistory> historyList;
	
	public BoardHistoryProvider() {
		this.historyList = new ArrayList<BoardHistory>();
	}
	
	public BoardHistory createBoardHistory(Context context, int boardId, GraphicalSoundboard initialHistoryCheckpoint) {
		Log.v(TAG, "Creating a new history for board id " + boardId);
		
		BoardHistory history = new BoardHistory(boardId);
		history.createHistoryCheckpoint(context, initialHistoryCheckpoint);
		
		historyList.add(history);
		
		return history;
	}
	
	public BoardHistory getBoardHistory(int boardId) {
		for (BoardHistory history : historyList) {
			if (history.getBoardId() == boardId) return history;
		}
		Log.v(TAG, "No history for board id " + boardId);
		return null;
	}
	
}
