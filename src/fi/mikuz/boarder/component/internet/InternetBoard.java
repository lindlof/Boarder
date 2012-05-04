package fi.mikuz.boarder.component.internet;

import org.json.JSONException;
import org.json.JSONObject;

import fi.mikuz.boarder.gui.internet.InternetMenu;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
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
