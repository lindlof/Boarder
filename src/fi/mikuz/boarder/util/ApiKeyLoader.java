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

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.util.Log;

/**
 * To support an API you need to provide a plain API key in assets folder in defined file.
 */
public class ApiKeyLoader {
	
	public static String loadDropboxApiKey(Context context, String TAG) {
		return loadApiKey(context, TAG, "ApiKeys/DropboxApiKey.txt");
	}
	
	public static String loadDropboxApiSecret(Context context, String TAG) {
		return loadApiKey(context, TAG, "ApiKeys/DropboxApiSecret.txt");
	}
	
	public static String loadAcraApiUser(Context context, String TAG) {
		return loadApiKey(context, TAG, "ApiKeys/AcraApiUser.txt");
	}
	
	public static String loadAcraApiPassword(Context context, String TAG) {
		return loadApiKey(context, TAG, "ApiKeys/AcraApiPassword.txt");
	}
	
	public static String loadAcraApiUrl(Context context, String TAG) {
		return loadApiKey(context, TAG, "ApiKeys/AcraApiUrl.txt");
	}
	
	public static String loadApiKey(Context context, String TAG, String asset) {
		String key = "ApiKeyMissing";
		try {
			InputStream inputStream = context.getAssets().open(asset);
			key = readInputStream(inputStream);
			key = key.trim();
			Log.d(TAG, "Loaded an API key");
		} catch (IOException e) {
			Log.e(TAG, "Couldn't load an API key", e);
		}
		return key;
	}


	public static String readInputStream(InputStream in) throws IOException {
		StringBuffer stream = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			stream.append(new String(b, 0, n));
		}

		return stream.toString();
	}


}
