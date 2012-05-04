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
