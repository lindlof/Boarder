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
import android.content.SharedPreferences;


/**
 * Stores global settings to SharedPreferences for static access across application sessions.
 * Settings are loaded from database to here.
 */
public abstract class GlobalSettings {
	
	private static SharedPreferences settings;
	
	private static final String GLOBAL_SETTINGS_NAME = "gloabalSettings";
	private static final String GLOBAL_SETTING_FADE_IN_DURATION = "gloabalSettingFadeInDuration";
	private static final String GLOBAL_SETTING_FADE_OUT_DURATION = "gloabalSettingFadeOutDuration";
	private static final String GLOBAL_SETTING_SENSITIVE_LOGGING = "gloabalSettingSensitiveLogging";
	
	public static void init(Context context) {
		settings = context.getSharedPreferences(GLOBAL_SETTINGS_NAME, 0);
	}
	
	public static int getFadeInDuration() {
		return settings.getInt(GLOBAL_SETTING_FADE_IN_DURATION, 0);
	}
	public static void setFadeInDuration(int fadeInDuration) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(GLOBAL_SETTING_FADE_IN_DURATION, fadeInDuration);
		editor.commit();
	}
	public static int getFadeOutDuration() {
		return settings.getInt(GLOBAL_SETTING_FADE_OUT_DURATION, 0);
	}
	public static void setFadeOutDuration(int fadeOutDuration) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(GLOBAL_SETTING_FADE_OUT_DURATION, fadeOutDuration);
		editor.commit();
	}
	public static boolean getSensitiveLogging() {
		return settings.getBoolean(GLOBAL_SETTING_SENSITIVE_LOGGING, false);
	}
	public static void setSensitiveLogging(boolean sensitiveLogging) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(GLOBAL_SETTING_SENSITIVE_LOGGING, sensitiveLogging);
		editor.commit();
	}
	
}
