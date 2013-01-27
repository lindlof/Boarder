package fi.mikuz.boarder.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public abstract class ContextUtils {
	private static final String TAG = ContextUtils.class.getSimpleName();
	
	public static void toast(Context context, String toast) {
		try {
			Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
		} catch (NullPointerException e) {
			Log.e(TAG, "Unable to toast message: \"" + toast + "\"", e);
		}
	}
}
