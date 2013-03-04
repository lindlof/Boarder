/* ========================================================================= *
 * Boarder                                                                   *
 * http://boarder.mikuz.org/                                                 *
 * ========================================================================= *
 * Copyright (C) 2013 Boarder                                                *
 *                                                                           *
 * Licensed under the Apache License, Version 2.0 (the "License");           *
 * you may not use this file except in compliance with the License.          *
 * You may obtain a copy of the License at                                   *
 *                                                                           *
 *     http://www.apache.org/licenses/LICENSE-2.0                            *
 *                                                                           *
 * Unless required by applicable law or agreed to in writing, software       *
 * distributed under the License is distributed on an "AS IS" BASIS,         *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 * See the License for the specific language governing permissions and       *
 * limitations under the License.                                            *
 * ========================================================================= */

package fi.mikuz.boarder.gui.internet;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.json.JSONException;

import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
 * 
 * @author Jan Mikael Lindlöf
 */
public class Register extends BoarderActivity implements ConnectionListener {
	private static final String TAG = "InternetDownload";
	
	private Button mSubmit;
	private EditText mUserName;
	private EditText mUserPassword;
	private EditText mUserPassword2;
	private EditText mUserEmail;
	
	final Handler mHandler = new Handler();
	TimeoutProgressDialog mWaitDialog;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.internet_register);
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		mSubmit = (Button)findViewById(R.id.submit);
		mUserName = (EditText)findViewById(R.id.userName);
		mUserPassword = (EditText)findViewById(R.id.userPassword);
		mUserPassword2 = (EditText)findViewById(R.id.userPassword2);
		mUserEmail = (EditText)findViewById(R.id.userEmail);
		
		mSubmit.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
            	if (!mUserPassword.getText().toString().equals(mUserPassword2.getText().toString())) {
            		Toast.makeText(Register.this, "The passwords don't match", Toast.LENGTH_LONG).show();
            	} else if (mUserPassword.length() < 6) {
            		Toast.makeText(Register.this, "Password length must be at least 6 characters", Toast.LENGTH_LONG).show();
            	} else {
            		try {
                		mWaitDialog = new TimeoutProgressDialog(Register.this, "Waiting for response", TAG, false);
                    	HashMap<String, String> sendList = new HashMap<String, String>();
                    	sendList.put(InternetMenu.USERNAME_KEY, mUserName.getText().toString());
    					sendList.put(InternetMenu.PASSWORD_KEY, Security.passwordHash(mUserPassword.getText().toString()));
    					sendList.put(InternetMenu.EMAIL_KEY, mUserEmail.getText().toString());
    	            	new ConnectionManager(Register.this, InternetMenu.mRegistrationURL, sendList);
    				} catch (NoSuchAlgorithmException e) {
    					mWaitDialog.dismiss();
    					String msg = "Couldn't hash the password";
    					Toast.makeText(Register.this, msg, Toast.LENGTH_LONG).show();
    					Log.e(TAG, msg, e);
    				}
            	}
            }
        });
		
	}

	@Override
	public void onConnectionSuccessful(ConnectionSuccessfulResponse connectionSuccessfulResponse) throws JSONException {
		ConnectionUtils.connectionSuccessful(Register.this, connectionSuccessfulResponse);
		mWaitDialog.dismiss();
		
		if (ConnectionUtils.checkConnectionId(connectionSuccessfulResponse, InternetMenu.mRegistrationURL)) {
		}
	}

	@Override
	public void onConnectionError(ConnectionErrorResponse connectionErrorResponse) {
		mWaitDialog.dismiss();
		ConnectionUtils.connectionError(this, connectionErrorResponse, TAG);
	}

}
