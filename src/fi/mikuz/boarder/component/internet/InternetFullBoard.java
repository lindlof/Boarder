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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import fi.mikuz.boarder.gui.internet.InternetMenu;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
public class InternetFullBoard extends InternetVersionBoard {
	
	private String description;
	private String screenshot0Url;
	private List<String> urlList;
    
	public InternetFullBoard() {
		init();
	}

	public InternetFullBoard(JSONObject jData) throws JSONException {
		super(jData, false);
		init();
		
		try {
			this.setFavoriteBoardVersion(jData.getInt(InternetMenu.FAVORITE_BOARD_VERSION_KEY));
		} catch (JSONException e) {
			this.setFavoriteBoardVersion(-1);
		}
		
		this.setDescription(jData.getString(InternetMenu.BOARD_DESCRIPTION_KEY));
		this.setScreenshot0Url(jData.getString(InternetMenu.BOARD_SCREENSHOT_0_URL_KEY));
		
		this.addUrl(jData.getString(InternetMenu.BOARD_URL_0_KEY));
		this.addUrl(jData.getString(InternetMenu.BOARD_URL_1_KEY));
		this.addUrl(jData.getString(InternetMenu.BOARD_URL_2_KEY));
		this.addUrl(jData.getString(InternetMenu.BOARD_URL_3_KEY));
		this.addUrl(jData.getString(InternetMenu.BOARD_URL_4_KEY));
    }
	

	private void init() {
    	urlList = new ArrayList<String>();
    }
    
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getScreenshot0Url() {
		return screenshot0Url;
	}
	public void setScreenshot0Url(String screenshot0Url) {
		this.screenshot0Url = screenshot0Url;
	}
	public List<String> getUrlList() {
		return urlList;
	}
	public void setUrlList(List<String> urlList) {
		this.urlList = urlList;
	}
	public void addUrl(String url) {
		this.urlList.add(url);
	}
}
