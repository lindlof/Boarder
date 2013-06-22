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

package fi.mikuz.boarder.service;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import fi.mikuz.boarder.app.BoarderService;
import fi.mikuz.boarder.util.SoundPlayerControl;

public class TogglePlayPauseService extends BoarderService {
	public static final String TAG = "TogglePlayPauseService";
	
    @Override
    public void onCreate() {
    	super.onCreate();
    	
    	try {
    		SoundPlayerControl.togglePlayPause(this.getApplicationContext());
    	} catch (NullPointerException e) {
    		Log.w(TAG, "Boarder is not running, nothing to do", e);
    	}
		this.stopSelf();
    }

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
