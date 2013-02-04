package fi.mikuz.boarder.app;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import fi.mikuz.boarder.util.GlobalSettings;
import fi.mikuz.boarder.util.ImageDrawing;

public class BoarderListActivity extends ListActivity {
	
	protected Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GlobalSettings.init(this);
	}
	
	@Override
	protected void onStop() {
    	ImageDrawing.unregisterCache(mContext);
    	super.onStop();
    }
}
