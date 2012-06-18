package fi.mikuz.boarder.gui.internet;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.json.JSONException;

import android.app.AlertDialog;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import fi.mikuz.boarder.R;
import fi.mikuz.boarder.app.BoarderActivity;
import fi.mikuz.boarder.connection.ConnectionErrorResponse;
import fi.mikuz.boarder.connection.ConnectionListener;
import fi.mikuz.boarder.connection.ConnectionManager;
import fi.mikuz.boarder.connection.ConnectionSuccessfulResponse;
import fi.mikuz.boarder.connection.ConnectionUtils;
import fi.mikuz.boarder.util.Security;
import fi.mikuz.boarder.util.TimeoutProgressDialog;

/**
 * Internet account settings. Accessible when logged in.
 * 
 * @author Jan Mikael Lindlöf
 */
public class Settings extends BoarderActivity implements ConnectionListener {
	private static final String TAG = "InternetLogin";
	
	final Handler mHandler = new Handler();
	TimeoutProgressDialog mWaitDialog;
	
	private String mUserId;
	private String mSessionToken;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.internet_settings);
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		Bundle extras = getIntent().getExtras();
		@SuppressWarnings("unchecked")
		HashMap<String,String> lastSession = (HashMap<String,String>) extras.getSerializable(InternetMenu.LOGIN_KEY);
		
		try {
			mUserId = lastSession.get(InternetMenu.USER_ID_KEY);
			mSessionToken = lastSession.get(InternetMenu.SESSION_TOKEN_KEY);
		} catch (NullPointerException e) {
			Toast.makeText(Settings.this, "Please login", Toast.LENGTH_LONG).show();
			Settings.this.finish();
		}
		
		Button changePassword = (Button) findViewById(R.id.change_password);
		
		changePassword.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	LayoutInflater inflater = (LayoutInflater) Settings.this.getSystemService(LAYOUT_INFLATER_SERVICE);
            	View layout = inflater.inflate(R.layout.internet_settings_alert_change_password, 
            			(ViewGroup) findViewById(R.id.alert_settings_root));
            	
            	final EditText oldPasswordInput = (EditText) layout.findViewById(R.id.oldPasswordInput);
            	final EditText newPassword1Input = (EditText) layout.findViewById(R.id.newPassword1Input);
            	final EditText newPassword2Input = (EditText) layout.findViewById(R.id.newPassword2Input);
            	Button submitButton = (Button) layout.findViewById(R.id.submitButton);
            	
            	AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
            	builder.setView(layout);
            	builder.setTitle("Change password");
            	
            	submitButton.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                    	String oldPasswordText = oldPasswordInput.getText().toString();
                    	String newPassword1Text = newPassword1Input.getText().toString();
                    	String newPassword2Text = newPassword2Input.getText().toString();
                    	
                    	if (!newPassword1Text.equals(newPassword2Text)) {
                    		Toast.makeText(Settings.this, "New passwords don't match", Toast.LENGTH_LONG).show();
                    	} else if (newPassword1Text.length() < 6) {
                    		Toast.makeText(Settings.this, "Password length must be at least 6 characters", Toast.LENGTH_LONG).show();
                    	} else {
                    		try {
	                    		mWaitDialog = new TimeoutProgressDialog(Settings.this, "Waiting for response", TAG, false);
	                    		HashMap<String, String> sendList = new HashMap<String, String>();
	                        	sendList.put(InternetMenu.PASSWORD_KEY, Security.passwordHash(newPassword1Text));
	                        	sendList.put(InternetMenu.OLD_PASSWORD_KEY, Security.passwordHash(oldPasswordText));
	                        	sendList.put(InternetMenu.USER_ID_KEY, mUserId);
	                        	sendList.put(InternetMenu.SESSION_TOKEN_KEY, mSessionToken);
	                        	new ConnectionManager(Settings.this, InternetMenu.mChangePasswordURL, sendList);
                    		} catch (NoSuchAlgorithmException e) {
            					mWaitDialog.dismiss();
            					String msg = "Couldn't make md5 hash";
            					Toast.makeText(Settings.this, msg, Toast.LENGTH_LONG).show();
            					Log.e(TAG, msg, e);
            				}
                    	}
                    }
            	});

            	builder.show();
            }
		});
		
		Button changeEmail = (Button) findViewById(R.id.change_email);
		
		changeEmail.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	LayoutInflater inflater = (LayoutInflater) Settings.this.getSystemService(LAYOUT_INFLATER_SERVICE);
            	View layout = inflater.inflate(R.layout.internet_settings_alert_change_email, 
            			(ViewGroup) findViewById(R.id.alert_settings_root));
            	
            	final EditText passwordInput = (EditText) layout.findViewById(R.id.passwordInput);
            	final EditText newEmailInput = (EditText) layout.findViewById(R.id.newEmailInput);
            	Button submitButton = (Button) layout.findViewById(R.id.submitButton);
            	
            	AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
            	builder.setView(layout);
            	builder.setTitle("Change email");
            	
            	submitButton.setOnClickListener(new OnClickListener() {
            		public void onClick(View v) {
            			String passwordText = passwordInput.getText().toString();
            			String newEmailText = newEmailInput.getText().toString();

            			try {
            				mWaitDialog = new TimeoutProgressDialog(Settings.this, "Waiting for response", TAG, false);
            				HashMap<String, String> sendList = new HashMap<String, String>();
            				sendList.put(InternetMenu.PASSWORD_KEY, Security.passwordHash(passwordText));
            				sendList.put(InternetMenu.EMAIL_KEY, newEmailText);
            				sendList.put(InternetMenu.USER_ID_KEY, mUserId);
            				sendList.put(InternetMenu.SESSION_TOKEN_KEY, mSessionToken);
            				new ConnectionManager(Settings.this, InternetMenu.mChangeEmailURL, sendList);
            			} catch (NoSuchAlgorithmException e) {
            				mWaitDialog.dismiss();
            				String msg = "Couldn't make md5 hash";
            				Toast.makeText(Settings.this, msg, Toast.LENGTH_LONG).show();
            				Log.e(TAG, msg, e);
            			}
            		}
            	});

            	builder.show();
            }
		});
		
	}

	@Override
	public void onConnectionSuccessful(ConnectionSuccessfulResponse connectionSuccessfulResponse) throws JSONException {
		ConnectionUtils.connectionSuccessful(Settings.this, connectionSuccessfulResponse);
		mWaitDialog.dismiss();
		if (ConnectionUtils.checkConnectionId(connectionSuccessfulResponse, InternetMenu.mChangePasswordURL)) {
		}
	}

	@Override
	public void onConnectionError(ConnectionErrorResponse connectionErrorResponse) {
		ConnectionUtils.connectionError(this, connectionErrorResponse, TAG);
		mWaitDialog.dismiss();
	}

}
