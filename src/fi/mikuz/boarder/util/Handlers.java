package fi.mikuz.boarder.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
		private static final String TOAST_KEY = "message";
		
		public ToastHandler(Context context) {
			super(context);
		}
		
		public Message wrapMessage(String toast) {
			Bundle data = new Bundle();
			data.putString(ToastHandler.TOAST_KEY, toast);
			Message m = new Message();
			m.setData(data);
			
			return m;
		}
		
		public void handleMessage(Message m) {
			Context context = super.getContext();
			if (context != null) {
				String messageStr = m.getData().getString(TOAST_KEY);
				Toast.makeText(context, messageStr, Toast.LENGTH_SHORT).show();
			} else {
				Throwable t = new Throwable();
				Log.e(TAG, "Unable to toast message: " + m.toString(), t);
			}
        }
	}
}
