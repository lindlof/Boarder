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

package fi.mikuz.boarder.component.soundboard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fi.mikuz.boarder.R;
import fi.mikuz.boarder.util.ImageDrawing;

@XStreamAlias("graphical-soundboard")
public class GraphicalSoundboard {
	
	private int id;
	private int pageNumber;
	
	private ImplicitSoundList soundList;
	
	public static final int SCREEN_ORIENTATION_PORTRAIT = 1;
	public static final int SCREEN_ORIENTATION_LANDSCAPE = 0;
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
	
	public GraphicalSoundboard(int orientation) {
		init();
		screenOrientation = orientation;
	}

	public GraphicalSoundboard() {
		init();
	}
	
	private void init() {
		id = 0;
		this.soundList = new ImplicitSoundList();
		setPageNumber(0);
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
		screenOrientation = SCREEN_ORIENTATION_PORTRAIT;
		screenHeight = 0;
		screenWidth = 0;
		
		GraphicalSoundList soundList = new GraphicalSoundList();
		this.soundList.setList(soundList);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public static GraphicalSoundboard copy(Context context, GraphicalSoundboard tempGsb) {
		GraphicalSoundboard gsb = new GraphicalSoundboard();
		
		ArrayList<GraphicalSound> gsbSoundList = new GraphicalSoundList();
		
		for (GraphicalSound sound : tempGsb.getSoundList()) {
			gsbSoundList.add((GraphicalSound)sound.clone());
		}
		
		gsb.setId(tempGsb.getId());
		gsb.setPageNumber(tempGsb.getPageNumber());
		gsb.setSoundList(gsbSoundList);
		gsb.setPlaySimultaneously(tempGsb.getPlaySimultaneously());
		gsb.setBoardVolume(tempGsb.getBoardVolume());
		gsb.setUseBackgroundImage(tempGsb.getUseBackgroundImage());
		gsb.setBackgroundColor(tempGsb.getBackgroundColor());
		gsb.setBackgroundImagePath(tempGsb.getBackgroundImagePath());
		gsb.setBackgroundX(tempGsb.getBackgroundX());
		gsb.setBackgroundY(tempGsb.getBackgroundY());
		gsb.setBackgroundWidth(tempGsb.getBackgroundWidth());
		gsb.setBackgroundHeight(tempGsb.getBackgroundHeight());
		if (tempGsb.getBackgroundImage() != null) gsb.loadBackgroundImage(context);
		gsb.setAutoArrange(tempGsb.getAutoArrange());
		gsb.setAutoArrangeColumns(tempGsb.getAutoArrangeColumns());
		gsb.setAutoArrangeRows(tempGsb.getAutoArrangeRows());
		gsb.setScreenOrientation(tempGsb.getScreenOrientation());
		gsb.setScreenHeight(tempGsb.getScreenHeight());
		gsb.setScreenWidth(tempGsb.getScreenWidth());
		
//		GraphicalSoundboard.unloadImages(tempGsb);
//		XStream xstream = XStreamUtil.graphicalBoardXStream();
//		String serializedGsb = xstream.toXML(tempGsb);
//		GraphicalSoundboard gsb = (GraphicalSoundboard) xstream.fromXML(serializedGsb);
		
		return gsb;
	}
	
	static public void loadImages(Context context, GraphicalSoundboard gsb) {
		if (gsb.getBackgroundImage() == null && gsb.getBackgroundImagePath() != null) {
			gsb.loadBackgroundImage(context);
		}
		for (GraphicalSound sound : gsb.getSoundList()) {
			sound.loadImages(context);
		}
	}
	
	static public void unloadImages(GraphicalSoundboard gsb) {
		gsb.unloadBackgroundImage();
		for (GraphicalSound sound : gsb.getSoundList()) {
			sound.unloadImages();
		}
	}
	
	public void loadBackgroundImage(Context context) {
		if (this.backgroundImage == null) {
			if (this.backgroundImagePath != null) {
				this.backgroundImage = ImageDrawing.decodeFile(context, getBackgroundImagePath(), getBackgroundWidth(), getBackgroundHeight());
			} else {
				this.backgroundImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.sound);
			}
		}
	}
	
	public void reloadBackgroundImage(Context context) {
		if (this.backgroundImage != null) {
			if (this.backgroundImagePath != null) {
				this.backgroundImage = ImageDrawing.decodeFile(context, getBackgroundImagePath(), getBackgroundWidth(), getBackgroundHeight());
			} else {
				this.backgroundImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.sound);
			}
		}
	}
	
	public void loadPlaceholderBackgroundImage(Context context) {
		this.backgroundImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.sound);
	}
	
	public void unloadBackgroundImage() {
		this.backgroundImage = null;
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
	
	
	public static String getOrientationName(int screenOrientation) {
		String orientationName = null;
		if (screenOrientation == GraphicalSoundboard.SCREEN_ORIENTATION_PORTRAIT) {
			orientationName = "portrait";
		} else if (screenOrientation == GraphicalSoundboard.SCREEN_ORIENTATION_LANDSCAPE) {
			orientationName = "landscape";
		}
		return orientationName;
	}
	public static int getOppositeOrientation(int screenOrientation) {
		int oppositeOrientation = -1;
		if (screenOrientation == GraphicalSoundboard.SCREEN_ORIENTATION_PORTRAIT) {
			oppositeOrientation = GraphicalSoundboard.SCREEN_ORIENTATION_LANDSCAPE;
		} else if (screenOrientation == GraphicalSoundboard.SCREEN_ORIENTATION_LANDSCAPE) {
			oppositeOrientation = GraphicalSoundboard.SCREEN_ORIENTATION_PORTRAIT;
		}
		return oppositeOrientation;
	}
	public static String getOppositeOrientationName(int screenOrientation) {
		int oppositeOrientation = getOppositeOrientation(screenOrientation);
		String oppositeOrientationName = getOrientationName(oppositeOrientation);
		return oppositeOrientationName;
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
	public void setBackgroundWidthHeight(Context context, float backgroundWidth, float backgroundHeight) {
		this.backgroundWidth = backgroundWidth;
		this.backgroundHeight = backgroundHeight;
		reloadBackgroundImage(context);
	}
	public float getBackgroundWidth() {
		return backgroundWidth;
	}
	private void setBackgroundWidth(float backgroundWidth) {
		this.backgroundWidth = backgroundWidth;
	}
	public float getBackgroundHeight() {
		return backgroundHeight;
	}
	private void setBackgroundHeight(float backgroundHeight) {
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
	public Float getBoardVolume() {
		return boardVolume;
	}
	
	
	
	public void setBoardVolume(Float boardVolume) {
		this.boardVolume = boardVolume;
	}
	public List<GraphicalSound> getSoundList() {
		return soundList.getList();
	}
	public void setSoundList(ArrayList<GraphicalSound> soundList) {
		synchronized (soundList) {
			this.soundList.setList(soundList);
		}
	}
	public void addSound(GraphicalSound sound) {
		synchronized (soundList) {
			this.soundList.getList().add(sound);
		}
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
