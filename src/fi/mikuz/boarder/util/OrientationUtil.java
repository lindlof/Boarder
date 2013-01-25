package fi.mikuz.boarder.util;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.Log;
import android.view.Surface;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundboard;

/**
 * Knows and converts different orientation types.
 * 
 * It is safe to assume that Boarder and Activity Info types are the same.
 * 
 * @author Jan Mikael Lindlöf
 */
public class OrientationUtil {
	public static final String TAG = OrientationUtil.class.getSimpleName();
	
	private static final int BOARDER_PORTRAIT = GraphicalSoundboard.SCREEN_ORIENTATION_PORTRAIT;
	private static final int BOARDER_LANDSCAPE = GraphicalSoundboard.SCREEN_ORIENTATION_LANDSCAPE;
	
	private static final int ACTIVITY_INFO_PORTRAIT = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	private static final int ACTIVITY_INFO_LANDSCAPE = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
	
	private static final int CONFIGURATION_PORTRAIT = Configuration.ORIENTATION_PORTRAIT;
	private static final int CONFIGURATION_LANDSCAPE = Configuration.ORIENTATION_LANDSCAPE;
	
	private static final int SURFACE_PORTRAIT_NORMAL = Surface.ROTATION_0;
	private static final int SURFACE_PORTRAIT_ALTERNATIVE = Surface.ROTATION_180;
	private static final int SURFACE_LANDSCAPE_NORMAL = Surface.ROTATION_90;
	private static final int SURFACE_LANDSCAPE_ALTERNATIVE = Surface.ROTATION_270;
	
	public static int getBoarderOrientation(Configuration configuration) {
		int orientation = configuration.orientation;
		
		if (orientation == CONFIGURATION_PORTRAIT) {
			return BOARDER_PORTRAIT;
		} else if (orientation == CONFIGURATION_LANDSCAPE) {
			return BOARDER_LANDSCAPE;
		} else {
			Log.wtf(TAG, "Invalid configuration orientation");
			new Throwable().printStackTrace();
			return -1;
		}
	}
}
