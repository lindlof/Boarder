package fi.mikuz.boarder.app;

import android.app.ListActivity;
import android.os.Bundle;
import fi.mikuz.boarder.util.GlobalSettings;

public class BoarderListActivity extends ListActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GlobalSettings.init(this);
	}
}
