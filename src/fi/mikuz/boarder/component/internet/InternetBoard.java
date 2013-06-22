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

package fi.mikuz.boarder.component.internet;

import org.json.JSONException;
import org.json.JSONObject;

import fi.mikuz.boarder.gui.internet.InternetMenu;

public class InternetBoard {
	
	private int boardId;
	private String boardName;
	private String uploaderUsername;
	private String rating;
	
	long id;
	
	public InternetBoard() {}
	
	public InternetBoard(JSONObject jData) throws JSONException {
		this.setBoardId(jData.getInt(InternetMenu.BOARD_ID_KEY));
    	this.setBoardName(jData.getString(InternetMenu.BOARD_NAME_KEY));
		this.setUploaderUsername(jData.getString(InternetMenu.USERNAME_KEY));
		this.setRating(jData.getString(InternetMenu.RATING_KEY));
    }
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public int getBoardId() {
		return boardId;
	}
	public void setBoardId(int boardId) {
		this.boardId = boardId;
	}
	public String getBoardName() {
		return boardName;
	}
	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}
	public String getUploaderUsername() {
		return uploaderUsername;
	}
	public void setUploaderUsername(String uploaderUsername) {
		this.uploaderUsername = uploaderUsername;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
}
