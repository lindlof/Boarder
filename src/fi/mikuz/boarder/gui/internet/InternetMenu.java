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

import java.util.HashMap;

import org.json.JSONException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import fi.mikuz.boarder.R;
import fi.mikuz.boarder.app.BoarderActivity;
import fi.mikuz.boarder.connection.ConnectionErrorResponse;
import fi.mikuz.boarder.connection.ConnectionListener;
import fi.mikuz.boarder.connection.ConnectionManager;
import fi.mikuz.boarder.connection.ConnectionSuccessfulResponse;
import fi.mikuz.boarder.connection.ConnectionUtils;
import fi.mikuz.boarder.gui.SoundboardMenu;
import fi.mikuz.boarder.util.ExternalIntent;
import fi.mikuz.boarder.util.TimeoutProgressDialog;
import fi.mikuz.boarder.util.dbadapter.GlobalVariablesDbAdapter;
import fi.mikuz.boarder.util.dbadapter.LoginDbAdapter;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
public class InternetMenu extends BoarderActivity implements ConnectionListener {
    private static final String TAG = "InternetMenu";
    
    private static final String phpRepURL = (SoundboardMenu.mDevelopmentMode) ? "http://test.boarder.mikuz.org/" : "https://secure.bluehost.com/~mikuzorg/boarder/";
    static final String mGetSessionValidURL = InternetMenu.phpRepURL + "getSessionInfo.php";
    static final String mGetBoardsURL = InternetMenu.phpRepURL + "getBoards.php";
    static final String mGetBoardURL = InternetMenu.phpRepURL + "getBoard.php";
    static final String mRegistrationURL = InternetMenu.phpRepURL + "register.php";
    static final String mLoginURL = InternetMenu.phpRepURL + "login.php";
    static final String mLogoutURL = InternetMenu.phpRepURL + "logout.php";
    static final String mRecoverPasswordURL = InternetMenu.phpRepURL + "recoverPassword.php";
    static final String mChangePasswordURL = InternetMenu.phpRepURL + "changePassword.php";
    static final String mChangeEmailURL = InternetMenu.phpRepURL + "changeEmail.php";
    static final String mUserUploadListURL = InternetMenu.phpRepURL + "getUserUploads.php";
    static final String mUploadBoardURL = InternetMenu.phpRepURL + "uploadBoard.php";
    static final String mFavoriteListURL = InternetMenu.phpRepURL + "getFavorites.php";
    static final String mFavoriteURL = InternetMenu.phpRepURL + "favorite.php";
    static final String mUpdateFavoriteBoardURL = InternetMenu.phpRepURL + "updateFavoriteBoard.php";
    static final String mGetBoardThumbStatusURL = InternetMenu.phpRepURL + "getBoardThumbStatus.php";
    static final String mRateBoardURL = InternetMenu.phpRepURL + "rateBoard.php";
    static final String mCommentURL = InternetMenu.phpRepURL + "comment.php";
    static final String mGetCommentsURL = InternetMenu.phpRepURL + "getComments.php";
    static final String mDeleteUploadedBoardURL = InternetMenu.phpRepURL + "deleteUploadedBoard.php";
    static final String mDonationNotificationURL = InternetMenu.phpRepURL + "donationNotification.php";
    static final String mGetServiceVersionURL = InternetMenu.phpRepURL + "getServiceVersion.php";
    
    public static final String BOARD_ID_KEY = "board_id";
    public static final String BOARD_VERSION_KEY = "board_version";
    public static final String FAVORITE_BOARD_VERSION_KEY = "favorite_board_version";
    public static final String BOARD_NAME_KEY = "board_name";
    public static final String BOARD_DESCRIPTION_KEY = "board_description";
    public static final String BOARD_URL_0_KEY = "board_0_url";
    public static final String BOARD_URL_1_KEY = "board_1_url";
    public static final String BOARD_URL_2_KEY = "board_2_url";
    public static final String BOARD_URL_3_KEY = "board_3_url";
    public static final String BOARD_URL_4_KEY = "board_4_url";
    public static final String BOARD_SCREENSHOT_0_URL_KEY = "board_screenshot_0_url";
    public static final String UPLOADER_ID_KEY = "uploader_id";
    public static final String USERNAME_KEY = "username";
    public static final String USER_ID_KEY = "user_id";
    public static final String PASSWORD_KEY = "password";
    public static final String ENTRANCE_PASSWORD_KEY = "entrance_password";
    public static final String OLD_PASSWORD_KEY = "old_password";
    public static final String EMAIL_KEY = "email";
    public static final String SESSION_VALID_KEY = "session_valid";
    public static final String ACCOUNT_MESSAGE_KEY = "account_message";
    public static final String OPERATION_KEY = "operation";
    public static final String RATING_KEY = "board_rating";
    public static final String SESSION_TOKEN_KEY = "session_token";
    public static final String RATE_GOOD_KEY = "rate_good";
    public static final String COMMENT_KEY = "comment";
    public static final String ORDER_RULE_KEY = "order_rule";
    public static final String ORDER_DIRECTION_KEY = "order_direction";
    public static final String REQUEST_COUNT_KEY = "request_count";
    public static final String PAGE_NUMBER_KEY = "page_number";
    public static final String MAX_RESULTS_KEY = "max_results";
    public static final String SEARCH_WORD_KEY = "search_word";
    public static final String HTML_FILTER = "htmlFilter";
    
    static final String PHP_OPERATION_KEY = "php_operation";
    static final int PHP_OPERATION_ADD = 0;
    static final int PHP_OPERATION_EDIT = 1;
    
    private LoginDbAdapter mDbHelper;
    private GlobalVariablesDbAdapter mGlobalVariableDbHelper;
    
    private HashMap<String, String> mLoginInfo = null;
    static final String LOGIN_KEY = "loginKey";
    private static final int LOGIN_RETURN = 0;
    
    private boolean mInternetAlive = true;
    private TimeoutProgressDialog mWaitDialog;
    private boolean mSessionValidityChecked = false;
    private boolean mDatabaseVersionChecked = false;
    
    private static final int mServiceVersion = 4; // TODO version control variables probably don't belong here
    private static final int mTosVersion = 4;
    
    private final Handler mHandler = new Handler();

    private Button mInternetDownload;
    private Button mInternetRegisterSettings;
    private Button mInternetLoginLogout;
    private Button mInternetUploads;
    private Button mInternetFavorites;
    private static TextView mAccountMessage;
    
    private final static String LOGIN_TEXT = "Login";
    private final static String LOGOUT_TEXT = "Logout";
    
    private final static String REGISTER_TEXT = "Register";
    private final static String SETTINGS_TEXT = "Settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.internet_menu);
        setTitle("Internet Menu");
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
        mWaitDialog = new TimeoutProgressDialog(this, "Waiting for response", TAG, true);
        
        mInternetDownload = (Button)findViewById(R.id.internet_download);
        mInternetRegisterSettings = (Button)findViewById(R.id.internet_register_settings);
        mInternetLoginLogout = (Button)findViewById(R.id.internet_login_logout);
        mInternetUploads = (Button)findViewById(R.id.internet_uploads);
        mInternetFavorites = (Button)findViewById(R.id.internet_favorites);
        mAccountMessage = (TextView)findViewById(R.id.account_message_text);
        
        mDbHelper = new LoginDbAdapter(this);
		mDbHelper.open();
        
        mGlobalVariableDbHelper = new GlobalVariablesDbAdapter(this);
        mGlobalVariableDbHelper.open();
        int dbTosVersion = 0;
        try {
        	Cursor variableCursor = mGlobalVariableDbHelper.fetchVariable(GlobalVariablesDbAdapter.TOS_VERSION_KEY);
			startManagingCursor(variableCursor);
			dbTosVersion = variableCursor.getInt(variableCursor.getColumnIndexOrThrow(LoginDbAdapter.KEY_DATA));
        } catch (SQLException e) {
        	mGlobalVariableDbHelper.createIntVariable(GlobalVariablesDbAdapter.TOS_VERSION_KEY, 0);
        	Log.d(TAG, "Couldn't get tosVersion", e);
        	
		} catch (CursorIndexOutOfBoundsException e) {
			mGlobalVariableDbHelper.createIntVariable(GlobalVariablesDbAdapter.TOS_VERSION_KEY, 0);
			Log.d(TAG, "Couldn't get tosVersion", e);
		}
        
        if (dbTosVersion < mTosVersion) {
        	AlertDialog.Builder builder = new AlertDialog.Builder(InternetMenu.this);
        	builder.setTitle("Terms of service");
        	builder.setMessage("Excited to get your hands on those sweet boards? - Good.\n\n" +
        			"There are some terms you must agree to and follow to get things rolling smoothly;\n\n" +
        			"You may only communicate in English in the Boarder Internet service.\n\n" +
        			"An uploaded board may contain any languages. However, if the board is not 'in English' that must be visibly stated in the description.\n\n" +
        			"You agree to always follow applicable laws when using Boarder.\n\n" +
        			"Pornographic and other adult only material is not allowed.\n\n" +
        			"You must be at least 13 years old to register to the Boarder Internet service.\n\n" +
        			"You may never transmit anything or communicate a way that can be deemed offensive.\n\n" +
        			"Don't make cheap copies of another users boards.\n\n" +
        			"We can use material(s) publicly shared by you as promotional material.\n\n" +
        			"We will suspend your Boarder releated accounts and/or remove your material from the Boarder service if you behave badly.");
        	
        	builder.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					mGlobalVariableDbHelper.updateIntVariable(GlobalVariablesDbAdapter.TOS_VERSION_KEY, mTosVersion);
				}
        	});

        	builder.setNegativeButton("Disagree", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int whichButton) {
        			InternetMenu.this.finish();
        		}
        	});

        	builder.setCancelable(false);
        	builder.show();
        }
        
        if (mLoginInfo == null) {
    		try {
    			String userId;
    			String sessionToken;
    			
    			Cursor loginCursor = mDbHelper.fetchLogin(USER_ID_KEY);
    			startManagingCursor(loginCursor);
    			userId = loginCursor.getString(
    					loginCursor.getColumnIndexOrThrow(LoginDbAdapter.KEY_DATA));
    			
    			loginCursor = mDbHelper.fetchLogin(SESSION_TOKEN_KEY);
    			startManagingCursor(loginCursor);
    			sessionToken = loginCursor.getString(
    					loginCursor.getColumnIndexOrThrow(LoginDbAdapter.KEY_DATA));
    			
    			mLoginInfo = new HashMap<String,String>();
    			mLoginInfo.put(USER_ID_KEY, userId);
    			mLoginInfo.put(SESSION_TOKEN_KEY, sessionToken);
    			sendDonationInfo();
    			
    			mSessionValidityChecked = false;
    			checkSessionValidity();
    			
    		} catch (CursorIndexOutOfBoundsException e) {
    			Log.d(TAG, "Couldn't get database session info", e);
    			mSessionValidityChecked = true;
    		}
        }
        
        getVersionInfo(); // Keep under login stuff

        mInternetDownload.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent(InternetMenu.this, DownloadBoardList.class);
            	i.putExtra(LOGIN_KEY, mLoginInfo);
            	startActivity(i);
            }
        });
        
        mInternetRegisterSettings.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	if (mInternetRegisterSettings.getText().toString().equals(SETTINGS_TEXT)) {
            		Intent i = new Intent(InternetMenu.this, Settings.class);
                	i.putExtra(LOGIN_KEY, mLoginInfo);
                	startActivityForResult(i, LOGIN_RETURN);
            	} else {
            		Intent i = new Intent(InternetMenu.this, Register.class);
                	startActivity(i);
            	}
            }
        });
        
        mInternetLoginLogout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	startLogin();
            }
        });
        
        mInternetUploads.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent(InternetMenu.this, Uploads.class);
            	i.putExtra(LOGIN_KEY, mLoginInfo);
            	startActivityForResult(i, LOGIN_RETURN);
            }
        });
        
        mInternetFavorites.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent(InternetMenu.this, Favorites.class);
            	i.putExtra(LOGIN_KEY, mLoginInfo);
            	startActivityForResult(i, LOGIN_RETURN);
            }
        });

    }
    
    private void startLogin() {
    	Intent i = new Intent(InternetMenu.this, Login.class);
    	i.putExtra(LOGIN_KEY, mLoginInfo);
    	startActivityForResult(i, LOGIN_RETURN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLoginInfo == null) {
        	setLoggedOutView();
        } else {
        	setLoggedInView();
        }
    }
    
	@Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        
        switch(requestCode) {
	        case LOGIN_RETURN:
	        	if (resultCode == RESULT_OK) {
		        	Bundle extras = intent.getExtras();
		        	@SuppressWarnings("unchecked")
					HashMap<String, String> returnInfo = (HashMap<String, String>) extras.getSerializable(LOGIN_KEY);
		        	mLoginInfo = returnInfo;
		        	sendDonationInfo();
		        	
		        	if (mLoginInfo == null) {
		        		setLoggedOutView();
		        	} else {
		        		setLoggedInView();
		        	}
		        	
		        	String accountMessage = extras.getString(ACCOUNT_MESSAGE_KEY);
		        	updateAccountMessage(accountMessage);
	        	}
	        	break;
        }
	}
	
	private void setLoggedInView() {
		mInternetLoginLogout.setText(LOGOUT_TEXT);
		mInternetRegisterSettings.setText(SETTINGS_TEXT);
		mInternetFavorites.setVisibility(View.VISIBLE);
	}
	
	private void setLoggedOutView() {
		mInternetLoginLogout.setText(LOGIN_TEXT);
		mInternetRegisterSettings.setText(REGISTER_TEXT);
		mInternetFavorites.setVisibility(View.INVISIBLE);
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDbHelper != null) {
        	mDbHelper.close();
        }
        
        if (mGlobalVariableDbHelper != null) {
        	mGlobalVariableDbHelper.close();
        }
        
        mInternetAlive = false;
    }
    
    private void sendDonationInfo() {
    	if (mLoginInfo != null) {
    		new ConnectionManager(this, mDonationNotificationURL, mLoginInfo);
    	}
    }
    
    private void getVersionInfo() {
    	new ConnectionManager(this, mGetServiceVersionURL, null);
    }
    
    private void checkSessionValidity() {
    	new ConnectionManager(this, mGetSessionValidURL, mLoginInfo);
    }

	@Override
	public void onConnectionSuccessful(ConnectionSuccessfulResponse connectionSuccessfulResponse) throws JSONException {
		ConnectionUtils.connectionSuccessful(InternetMenu.this, connectionSuccessfulResponse);
		
		if (ConnectionUtils.checkConnectionId(connectionSuccessfulResponse, InternetMenu.mGetServiceVersionURL)) {
			mDatabaseVersionChecked = true;
			
			int serviceVersion = connectionSuccessfulResponse.getJSONObject().getInt(ConnectionUtils.returnData);
			if (serviceVersion == -1) {
				AlertDialog.Builder builder = new AlertDialog.Builder(InternetMenu.this);
				builder.setTitle("Maintenance");
				builder.setMessage("Boarder web service is under maintenance. Please try again later.");
				builder.setCancelable(false);
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int whichButton) {
	        			InternetMenu.this.finish();
	        		}
	        	});
	        	
	        	builder.show();
			} else if (mServiceVersion < serviceVersion) {
				AlertDialog.Builder builder = new AlertDialog.Builder(InternetMenu.this);
				builder.setTitle("Old version");
				builder.setMessage("You have an old version of Boarder. Your version is not compatible with Boarder web service.\n\n" +
						"Please update.");
				builder.setCancelable(false);
				
				builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						ExternalIntent.openGooglePlay(InternetMenu.this);
	    				InternetMenu.this.finish();
					}
	        	});

	        	builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int whichButton) {
	        			InternetMenu.this.finish();
	        		}
	        	});
	        	
	        	builder.show();
			}
		} else if (ConnectionUtils.checkConnectionId(connectionSuccessfulResponse, InternetMenu.mGetSessionValidURL)) {
			mSessionValidityChecked = true;
			if (connectionSuccessfulResponse.getJSONObject().getInt(InternetMenu.SESSION_VALID_KEY) == 0) {
				startLogin();
			} else {
				String accountMessage = connectionSuccessfulResponse.getJSONObject().getString(InternetMenu.ACCOUNT_MESSAGE_KEY);
				updateAccountMessage(accountMessage);
			}
		}
		
		afterConnection();
	}

	@Override
	public void onConnectionError(ConnectionErrorResponse connectionErrorResponse) {
		ConnectionUtils.connectionError(this, connectionErrorResponse, TAG);
		
		if (ConnectionUtils.checkConnectionId(connectionErrorResponse, InternetMenu.mGetServiceVersionURL)) {
			mHandler.postDelayed(new Runnable() {           
				public void run() {
					if (mInternetAlive) {
						getVersionInfo();
					}
				}
			}, 5000);
		} else if (ConnectionUtils.checkConnectionId(connectionErrorResponse, InternetMenu.mGetSessionValidURL)) {
			mSessionValidityChecked = true;
			startLogin();
		}
		
		afterConnection();
	}
	
	private void afterConnection() {
		if (mDatabaseVersionChecked && mSessionValidityChecked) {
			mWaitDialog.dismiss();
		}
	}
	
	protected String getUsername() {
		String username = null;
		try {
			Cursor loginCursor = mDbHelper.fetchLogin(InternetMenu.USERNAME_KEY);
			startManagingCursor(loginCursor);
			username = loginCursor.getString(loginCursor.getColumnIndexOrThrow(LoginDbAdapter.KEY_DATA));
		} catch (SQLException e) {Log.d(TAG, "Couldn't get database login info", e);}
		return username;
	}
	
	private void updateAccountMessage(String accountMessage) {
		mAccountMessage.setText(accountMessage);
	}
}
