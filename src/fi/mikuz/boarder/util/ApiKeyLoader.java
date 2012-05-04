package fi.mikuz.boarder.util;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.util.Log;

/**
 * To support an API you need to provide a plain API key in assets folder in defined file.
 * 
 * @author Jan Mikael Lindlöf
 */
public class ApiKeyLoader {

	public static String loadBugSenseApiKey(Context context, String TAG) {
		return loadApiKey(context, TAG, "ApiKeys/BugSenseApiKey.txt");
	}
	
	public static String loadDropboxApiKey(Context context, String TAG) {
		return loadApiKey(context, TAG, "ApiKeys/DropboxApiKey.txt");
	}
	
	public static String loadDropboxApiSecret(Context context, String TAG) {
		return loadApiKey(context, TAG, "ApiKeys/DropboxApiSecret.txt");
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
