package fi.mikuz.boarder.util;

import org.acra.ACRA;

import android.util.Log;

public class Logger {

	public static void silentError(String TAG, String msg) {
		ACRA.getErrorReporter().handleSilentException(new Exception(msg));
		Log.e(TAG, msg);
	}
	
	public static void silentWarning(String TAG, String msg) {
		ACRA.getErrorReporter().handleSilentException(new Exception(msg));
		Log.w(TAG, msg);
	}

}
