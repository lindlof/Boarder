package fi.mikuz.boarder.util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public abstract class Handlers {
	private static final String TAG = Handlers.class.getSimpleName();
	
	public static class ContextHandler extends Handler {
		private Context context;
		
		private ContextHandler(Context context) {
			this.context = context;
		}
		
		private Context getContext() {
			return context;
		}
	}
	
	public static class ToastHandler extends ContextHandler {
		public ToastHandler(Context context) {
			super(context);
		}
		
		public void toast(String toast) {
			Context context = super.getContext();
			if (context != null) {
				Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
			} else {
				Throwable t = new Throwable();
				Log.e(TAG, "Unable to toast message: \"" + toast + "\"", t);
			}
		}
	}
}
