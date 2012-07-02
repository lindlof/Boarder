package fi.mikuz.boarder.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public abstract class ExternalIntent {
	private static final String TAG = ExternalIntent.class.getSimpleName();
	
	private static final String mExtLinkDonate = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=8S2QXHLP2G6YS";
	private static final String mExtLinkXDA = "http://forum.xda-developers.com/showthread.php?p=23224859#post23224859";
	public static final String mExtLinkMarket = "market://details?id=fi.mikuz.boarder";
	
	public static void openDonate(Context context) {
		try {
			Intent browserDonateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mExtLinkDonate));
			context.startActivity(browserDonateIntent);
		} catch (ActivityNotFoundException e) {
			String error = "Unable to open web browser";
			Log.e(TAG, error, e);
			Toast.makeText(context, error, Toast.LENGTH_LONG).show();
		}
	}
	
	public static void openXdaForums(Context context) {
		try {
			Intent browserXdaIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mExtLinkXDA));
			context.startActivity(browserXdaIntent);
		} catch (ActivityNotFoundException e) {
			String error = "Unable to open web browser";
    		Log.e(TAG, error, e);
    		Toast.makeText(context, error, Toast.LENGTH_LONG).show();
    	}
	}
	
	public static void openGooglePlay(Context context) {
		Intent browserRateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mExtLinkMarket));
    	try {
    		context.startActivity(browserRateIntent);
    	} catch (ActivityNotFoundException e) {
    		Log.e(TAG, "Unable to open external activity", e);
    		Toast.makeText(context, "Could not open Google Play. Opening XDA forums instead.", Toast.LENGTH_LONG).show();
    		openXdaForums(context);
    	}
	}
}
