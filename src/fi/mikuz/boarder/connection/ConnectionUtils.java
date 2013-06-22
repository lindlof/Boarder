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

package fi.mikuz.boarder.connection;

import org.json.JSONException;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

public class ConnectionUtils {
	
	public static final String returnData = "data";
	public static final String returnMessage = "message";

	public static boolean checkConnectionId(ConnectionResponse connectionResponse, String url) {
		return (connectionResponse.getConnectionId().equals(getUrlConnectionId(url)));
	}
	
	public static String getUrlConnectionId(String url) {
		return url.substring(url.lastIndexOf("/")+1);
	}
	
	public static void connectionSuccessful(Activity activity, ConnectionSuccessfulResponse connectionSuccessfulResponse) throws JSONException {
		if (!connectionSuccessfulResponse.getJSONObject().isNull(returnMessage)) {
			Toast.makeText(activity, connectionSuccessfulResponse.getJSONObject().getString(returnMessage), Toast.LENGTH_LONG).show();
		}
	}
	
	public static void connectionError(Activity activity, ConnectionErrorResponse connectionErrorResponse, String TAG) {
		Toast.makeText(activity, "Error: " + connectionErrorResponse.getErrorMessage(), Toast.LENGTH_LONG).show();
		Log.e(TAG, connectionErrorResponse.getErrorMessage());
	}
}
