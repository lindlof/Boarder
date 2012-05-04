package fi.mikuz.boarder.connection;

import org.json.JSONException;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
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
