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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.text.Html;
import android.util.Log;
import fi.mikuz.boarder.gui.internet.InternetMenu;
import fi.mikuz.boarder.util.GlobalSettings;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
public class ConnectionManager {
	private static final String TAG = "ConnectionManager";
	
	ConnectionListener connectionListener;
	
	final Handler mHandler = new Handler();
	private ConnectionSuccessfulResponse connectionSuccessfulResponse = null;
	private ConnectionErrorResponse connectionErrorResponse = null;
	
	
	public ConnectionManager(ConnectionListener connectionListener, final String url, final HashMap<String, String> sendList) {
		this.connectionListener = connectionListener;
		
		Thread t = new Thread() {
			public void run() {
				JSONObject json = new JSONObject();

				try {
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(url);

					if (sendList != null) {
						for (String key : sendList.keySet()) {
							json.put(key, sendList.get(key));
						}
					}
					json.put(InternetMenu.HTML_FILTER, false);

					StringEntity se = new StringEntity(json.toString());
					se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
					httppost.setEntity(se);
					
					if (GlobalSettings.getSensitiveLogging()) Log.v(TAG, "Sending to "+url+": "+json.toString());
					
					HttpResponse response = httpclient.execute(httppost);
					InputStream in = response.getEntity().getContent(); //Get the data in the entity
					String result = convertStreamToString(in);
					
					try {
						if (GlobalSettings.getSensitiveLogging()) Log.v(TAG, "Got from "+url+": "+result);
						else Log.v(TAG, "Got answer from "+url);
						connectionSuccessfulResponse = new ConnectionSuccessfulResponse(new JSONObject(result), 
								ConnectionUtils.getUrlConnectionId(url));
						mHandler.post(connectionSuccessful);
					} catch(JSONException e) {
						Log.e(TAG, "Couldn't convert to JSON object", e);
						connectionErrorResponse = new ConnectionErrorResponse(Html.fromHtml(result).toString(), url);
						mHandler.post(connectionError);
					}

				} catch(Exception e) {
					String error = "Cannot establish connection";
					Log.e(TAG, error);
					connectionErrorResponse = new ConnectionErrorResponse(error, 
							ConnectionUtils.getUrlConnectionId(url));
					mHandler.post(connectionError);
				}
			}
		};
		t.start();
	}
	
	public String convertStreamToString(InputStream is) throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {        
			return "";
		}
	}
	
	final Runnable connectionSuccessful = new Runnable() {
    	public void run() {
    		try {
				connectionListener.onConnectionSuccessful(connectionSuccessfulResponse);
			} catch (JSONException e) {
				Log.e(TAG, "Error parsing JSON", e);
			}
    	}
	};
	
	final Runnable connectionError = new Runnable() {
    	public void run() {
			connectionListener.onConnectionError(connectionErrorResponse);
    	}
	};
}
