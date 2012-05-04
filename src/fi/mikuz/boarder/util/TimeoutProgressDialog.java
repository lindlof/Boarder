package fi.mikuz.boarder.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
public class TimeoutProgressDialog {
	
	private int timeout = 30000;
	private ProgressDialog dialog;
	private boolean dieOnDismiss;

	public TimeoutProgressDialog(final Activity activity, String message, final String TAG, final boolean dieOnDismiss) {
		this.dieOnDismiss = dieOnDismiss;
		dialog = new ProgressDialog(activity);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage(message);
		
//		dialog.setButton("Dismiss", new DialogInterface.OnClickListener() {
//	        public void onClick(DialogInterface dialog, int which) {
//	        	activity.finish();
//				dialog.dismiss();
//	        }
//	    });
		
		dialog.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				if (TimeoutProgressDialog.this.dieOnDismiss) {
					Toast.makeText(activity, "User cancel", Toast.LENGTH_SHORT).show();
					activity.finish();
				}
			}
		});
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				if (dialog.isShowing()) {
					try {
						Toast.makeText(activity, "Error: dialog timeout", Toast.LENGTH_LONG).show();
						TimeoutProgressDialog.this.dieOnDismiss = false;
						activity.finish();
						dialog.dismiss();
					} catch (IllegalArgumentException e) {
						Log.e(TAG, "dialog.isShowing() returned false postitive in timeout", e);
					}
					Log.e(TAG, "Error: dialog timeout, server failed to respond in " + timeout + " milliseconds");
				}
			}
		}, timeout);
		dialog.show();
	}
	
	public void dismiss() {
		TimeoutProgressDialog.this.dieOnDismiss = false;
		dialog.dismiss();
	}
	
}
