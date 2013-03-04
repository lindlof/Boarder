/* ========================================================================= *
 * Boarder                                                                   *
 * http://boarder.mikuz.org/                                                 *
 * ========================================================================= *
 * Copyright (C) 2013 Boarder                                                *
 *                                                                           *
 * Licensed under the Apache License, Version 2.0 (the "License");           *
 * you may not use this file except in compliance with the License.          *
 * You may obtain a copy of the License at                                   *
 *                                                                           *
 *     http://www.apache.org/licenses/LICENSE-2.0                            *
 *                                                                           *
 * Unless required by applicable law or agreed to in writing, software       *
 * distributed under the License is distributed on an "AS IS" BASIS,         *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 * See the License for the specific language governing permissions and       *
 * limitations under the License.                                            *
 * ========================================================================= */

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
