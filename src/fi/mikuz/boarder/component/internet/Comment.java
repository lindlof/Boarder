package fi.mikuz.boarder.component.internet;

import org.json.JSONException;
import org.json.JSONObject;

import fi.mikuz.boarder.gui.internet.InternetMenu;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
public class Comment {
	
	String username;
	String comment;
	
	public Comment() {}
	
	public Comment(JSONObject jObject) throws JSONException {
		setUsername(jObject.getString(InternetMenu.USERNAME_KEY));
		setComment(jObject.getString(InternetMenu.COMMENT_KEY));
	}
	
	long id;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}
