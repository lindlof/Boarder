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

/**
 * 
 * @author Jan Mikael Lindlöf
 */
public class InternetVersionBoard extends InternetBoard {
	
	private int boardVersion;
	private int favoriteBoardVersion;
	
	long id;
	
	public InternetVersionBoard() {}
	
	public InternetVersionBoard(JSONObject jData, boolean useFavoriteVersion) throws JSONException {
		super(jData);
		
		this.setBoardVersion(jData.getInt(InternetMenu.BOARD_VERSION_KEY));
		if (useFavoriteVersion) this.setFavoriteBoardVersion(jData.getInt(InternetMenu.FAVORITE_BOARD_VERSION_KEY));
    }
	
	public int getBoardVersion() {
		return boardVersion;
	}
	public void setBoardVersion(int boardVersion) {
		this.boardVersion = boardVersion;
	}
	public int getFavoriteBoardVersion() {
		return favoriteBoardVersion;
	}
	public void setFavoriteBoardVersion(int favoriteVersion) {
		this.favoriteBoardVersion = favoriteVersion;
	}
}
