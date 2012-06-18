package fi.mikuz.boarder.app;

import fi.mikuz.boarder.util.GlobalSettings;
import android.app.Activity;
import android.os.Bundle;

public class BoarderActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GlobalSettings.init(this);
	}
}
