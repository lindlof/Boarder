package fi.mikuz.boarder.app;

import fi.mikuz.boarder.util.GlobalSettings;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BoarderService extends Service {
	
    @Override
    public void onCreate() {
    	GlobalSettings.init(this);
    }

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
