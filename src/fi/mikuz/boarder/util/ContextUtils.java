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

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;

public abstract class ContextUtils {
	private static final String TAG = ContextUtils.class.getSimpleName();
	
	public static void toast(Context context, String toast) {
		toast(context, toast, Toast.LENGTH_SHORT);
	}
	
	public static void toast(Context context, String toast, int duration) {
		String errLogMsg = "Unable to toast message: \"" + toast + "\"";
		if (Looper.myLooper() == null) {
			Exception e = new IllegalStateException("Not running in a looper");
			Log.e(TAG, errLogMsg, e);
			BugSenseHandler.sendException(e);
		} else if (Looper.myLooper() != Looper.getMainLooper()) {
			Exception e = new IllegalStateException("Not running in the main looper");
			Log.e(TAG, errLogMsg, e);
			BugSenseHandler.sendException(e);
		} else {
			try {
				Toast.makeText(context, toast, duration).show();
			} catch (NullPointerException e) {
				Log.e(TAG, errLogMsg, e);
			}
		}
	}
}
