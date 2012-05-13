package fi.mikuz.boarder.component.soundboard;

import java.io.File;
import java.util.ArrayList;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
@XStreamAlias("graphical-soundboard")
public class GraphicalSoundboard {
	
	private int id;
	private int version;
	
	private ArrayList<GraphicalSound> soundList;
	
	public static final int SCREEN_ORIENTATION_PORTAIT = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	public static final int SCREEN_ORIENTATION_LANDSCAPE = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
	private int screenOrientation;
	
	private boolean playSimultaneously;
	private Float boardVolume;
	
	private boolean useBackgroundImage;
	private int backgroundColor;
	private File backgroundImagePath;
	private Bitmap backgroundImage;
	private float backgroundX;
	private float backgroundY;
	private float backgroundWidth;
	private float backgroundHeight;
	
	private boolean autoArrange;
	private int autoArrangeColumns;
	private int autoArrangeRows;
	
	private int screenHeight;
	private int screenWidth;

	public GraphicalSoundboard() {
		id = 0;
		version = 1;
		soundList = new ArrayList<GraphicalSound>();
		playSimultaneously = true;
		boardVolume = (float) 1;
		useBackgroundImage = false;
		backgroundColor = Color.BLACK;
		backgroundImagePath = null;
		backgroundImage = null;
		backgroundX = 0;
		backgroundY = 0;
		backgroundWidth = 0;
		backgroundHeight = 0;
		autoArrange = false;
		autoArrangeColumns = 3;
		autoArrangeRows = 5;
		screenOrientation = SCREEN_ORIENTATION_PORTAIT;
		screenHeight = 0;
		screenWidth = 0;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public static GraphicalSoundboard copy(GraphicalSoundboard tempGsb) {
		GraphicalSoundboard gsb = new GraphicalSoundboard();
		
		ArrayList<GraphicalSound> gsbSoundList = new ArrayList<GraphicalSound>();
		
		for (GraphicalSound sound : tempGsb.getSoundList()) {
			gsbSoundList.add((GraphicalSound)sound.clone());
		}
		
		gsb.setSoundList(gsbSoundList);
		gsb.setPlaySimultaneously(tempGsb.getPlaySimultaneously());
		gsb.setBoardVolume(tempGsb.getBoardVolume());
		gsb.setUseBackgroundImage(tempGsb.getUseBackgroundImage());
		gsb.setBackgroundColor(tempGsb.getBackgroundColor());
		gsb.setBackgroundImagePath(tempGsb.getBackgroundImagePath());
		gsb.setBackgroundImage(tempGsb.getBackgroundImage());
		gsb.setBackgroundX(tempGsb.getBackgroundX());
		gsb.setBackgroundY(tempGsb.getBackgroundY());
		gsb.setBackgroundWidth(tempGsb.getBackgroundWidth());
		gsb.setBackgroundHeight(tempGsb.getBackgroundHeight());
		gsb.setAutoArrange(tempGsb.getAutoArrange());
		gsb.setAutoArrangeColumns(tempGsb.getAutoArrangeColumns());
		gsb.setAutoArrangeRows(tempGsb.getAutoArrangeRows());
		gsb.setScreenOrientation(tempGsb.getScreenOrientation());
		gsb.setScreenHeight(tempGsb.getScreenHeight());
		gsb.setScreenWidth(tempGsb.getScreenWidth());
		
		return gsb;
	}
	
	static public void unloadImages(GraphicalSoundboard gsb) {
		gsb.setBackgroundImage(null);
		for (GraphicalSound sound : gsb.getSoundList()) {
			GraphicalSound.unloadImages(sound);
		}
	}
	
	public boolean getAutoArrange() {
		return autoArrange;
	}
	public void setAutoArrange(boolean autoArrange) {
		this.autoArrange = autoArrange;
	}
	public int getAutoArrangeColumns() {
		return autoArrangeColumns;
	}
	public void setAutoArrangeColumns(int autoArrangeColumns) {
		this.autoArrangeColumns = autoArrangeColumns;
	}
	public int getAutoArrangeRows() {
		return autoArrangeRows;
	}
	public void setAutoArrangeRows(int autoArrangeRows) {
		this.autoArrangeRows = autoArrangeRows;
	}
	
	
	
	public int getScreenOrientation() {
		return screenOrientation;
	}
	public void setScreenOrientation(int screenOrientation) {
		this.screenOrientation = screenOrientation;
		this.setScreenHeight(0);
		this.setScreenWidth(0);
	}
	public float getBackgroundX() {
		return backgroundX;
	}
	public void setBackgroundX(float backgroundX) {
		this.backgroundX = backgroundX;
	}
	public float getBackgroundY() {
		return backgroundY;
	}
	public void setBackgroundY(float backgroundY) {
		this.backgroundY = backgroundY;
	}
	public float getBackgroundWidth() {
		return backgroundWidth;
	}
	public void setBackgroundWidth(float backgroundWidth) {
		this.backgroundWidth = backgroundWidth;
	}
	public float getBackgroundHeight() {
		return backgroundHeight;
	}
	public void setBackgroundHeight(float backgroundHeight) {
		this.backgroundHeight = backgroundHeight;
	}
	public boolean getUseBackgroundImage() {
		return useBackgroundImage;
	}
	public void setUseBackgroundImage(boolean useBackgroundImage) {
		this.useBackgroundImage = useBackgroundImage;
	}
	public int getBackgroundColor() {
		return backgroundColor;
	}
	public void setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	public void setBackgroundColor(int alpha, int red, int green, int blue) {
		this.backgroundColor = Color.argb(alpha, red, green, blue);
	}
	public File getBackgroundImagePath() {
		return backgroundImagePath;
	}
	public void setBackgroundImagePath(File backgroundImagePath) {
		this.backgroundImagePath = backgroundImagePath;
	}
	public Bitmap getBackgroundImage() {
		return backgroundImage;
	}
	public void setBackgroundImage(Bitmap backgroundImage) {
		this.backgroundImage = backgroundImage;
	}
	public Float getBoardVolume() {
		return boardVolume;
	}
	
	
	
	public void setBoardVolume(Float boardVolume) {
		this.boardVolume = boardVolume;
	}
	public ArrayList<GraphicalSound> getSoundList() {
		return soundList;
	}
	public void setSoundList(ArrayList<GraphicalSound> soundList) {
		this.soundList = soundList;
	}
	public void addSound(GraphicalSound sound) {
		this.soundList.add(sound);
	}
	public boolean getPlaySimultaneously() {
		return playSimultaneously;
	}
	public void setPlaySimultaneously(boolean playSimultaneously) {
		this.playSimultaneously = playSimultaneously;
	}
	
	
	
	public int getScreenHeight() {
		return screenHeight;
	}
	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}
	public int getScreenWidth() {
		return screenWidth;
	}
	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}
}
