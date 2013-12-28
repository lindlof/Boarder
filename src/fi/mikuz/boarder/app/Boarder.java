package fi.mikuz.boarder.app;

import org.acra.ACRA;
import org.acra.ACRAConfiguration;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender.Method;
import org.acra.sender.HttpSender.Type;

import android.app.Application;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import fi.mikuz.boarder.R;
import fi.mikuz.boarder.util.ApiKeyLoader;

@ReportsCrashes(formKey = "", formUri = "",
	mode = ReportingInteractionMode.TOAST,
	resToastText = R.string.crash_toast_text)
public class Boarder extends Application {

	public static final String TAG = Boarder.class.getSimpleName();
	public static final boolean mDevelopmentMode = true; //FIXME for release
	
	@Override
	public final void onCreate() {
		super.onCreate();
		
		String versionName = null;
    	try {
    		versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Unable to get info from manifest", e);
		}
    	Log.i(TAG, "Starting Boarder v" + versionName + " dev: " + mDevelopmentMode);
		
		if (!mDevelopmentMode) {
			String url = ApiKeyLoader.loadAcraApiUrl(getApplicationContext(), TAG);
			String user = ApiKeyLoader.loadAcraApiUser(getApplicationContext(), TAG);
			String pass = ApiKeyLoader.loadAcraApiPassword(getApplicationContext(), TAG);
			
			ACRAConfiguration acraConf = ACRA.getNewDefaultConfig(this);
	    	acraConf.setFormUri(url);
	    	acraConf.setFormUriBasicAuthLogin(user);
	    	acraConf.setFormUriBasicAuthPassword(pass);
	    	acraConf.setReportType(Type.JSON);
	    	acraConf.setHttpMethod(Method.PUT);
	    	ACRA.setConfig(acraConf);
		}
    	
    	ACRA.init(this);
	}
}
