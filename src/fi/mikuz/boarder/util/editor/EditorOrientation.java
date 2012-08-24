package fi.mikuz.boarder.util.editor;

import android.view.Surface;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundboard;

public class EditorOrientation {
	
	private int currentOrientation;
	
	public static int convertRotation(int rotation) {
		int screenOrientation = -1;
		
		if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
			screenOrientation = GraphicalSoundboard.SCREEN_ORIENTATION_LANDSCAPE;
		} else {
			screenOrientation = GraphicalSoundboard.SCREEN_ORIENTATION_PORTRAIT;
		}
		
		return screenOrientation;
	}
	
	public void setCurrentOrientation(int rotation) {
		currentOrientation = EditorOrientation.convertRotation(rotation);
	}
	
	public int getCurrentOrientation() {
		return currentOrientation;
	}
}
