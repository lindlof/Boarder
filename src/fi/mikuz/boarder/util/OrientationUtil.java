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
