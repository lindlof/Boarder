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

package fi.mikuz.boarder.gui;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;

import fi.mikuz.boarder.R;
import fi.mikuz.boarder.app.BoarderListActivity;
import fi.mikuz.boarder.component.DropboxCache;
import fi.mikuz.boarder.component.DropboxCacheFile;
import fi.mikuz.boarder.gui.checkboxList.ExtendedCheckBox;
import fi.mikuz.boarder.gui.checkboxList.ExtendedCheckBoxListAdapter;
import fi.mikuz.boarder.util.ApiKeyLoader;
import fi.mikuz.boarder.util.DropboxCacheUtils;
import fi.mikuz.boarder.util.ExternalIntent;

public class DropboxMenu extends BoarderListActivity {
	private static final String TAG = "Dropbox Menu";

	private static String APP_KEY;
	private static String APP_SECRET;
	final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	final static private String ACCOUNT_PREFS_NAME = "prefs";
	final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
	final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";

	protected static DropboxAPI<AndroidAuthSession> mApi;
	private boolean mLoggedIn;

	private String mInfo = "";
	private int mProgressValue = 0;
	final Handler mHandler = new Handler();
	private ProgressDialog mWaitDialog;
	private Thread t;

	private LinearLayout mDropboxContent;
	private Button mAuthDropbox;
    private Button mDropboxDownload;
    private Button mDropboxUpload;
	private Button mSelectAll;
	private Button mStart;
	private ExtendedCheckBoxListAdapter mCbla;
	
	private static final int DOWNLOAD_OPERATION = 0;
	private static final int UPLOAD_OPERATION = 1;
	private int mOperation = UPLOAD_OPERATION;
	
	private String mToastMessage;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dropbox_menu);
		setTitle("Dropbox");
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

		APP_KEY = ApiKeyLoader.loadDropboxApiKey(this.getApplicationContext(), TAG);
		APP_SECRET = ApiKeyLoader.loadDropboxApiSecret(this.getApplicationContext(), TAG);

		AndroidAuthSession session = buildSession();
		mApi = new DropboxAPI<AndroidAuthSession>(session);

		try {
			loadCbla();
		} catch (NullPointerException npe) {}

		mDropboxContent = (LinearLayout)findViewById(R.id.dropbox_content);
		mAuthDropbox = (Button)findViewById(R.id.auth_button);
		mDropboxDownload = (Button)findViewById(R.id.dropbox_download);
        mDropboxUpload = (Button)findViewById(R.id.dropbox_upload);
		mSelectAll = (Button)findViewById(R.id.select_all);
		mStart = (Button)findViewById(R.id.start);

		mAuthDropbox.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// This logs you out if you're logged in, or vice versa
				if (mLoggedIn) {
					logOut();
				} else {
					// Start the remote authentication
					mApi.getSession().startAuthentication(DropboxMenu.this);
				}
			}
		});

		mDropboxDownload.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mOperation = DOWNLOAD_OPERATION;
				try {
					loadCbla();
				} catch (NullPointerException npe) {}
			}
		});

		mDropboxUpload.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mOperation = UPLOAD_OPERATION;
				try {
					loadCbla();
				} catch (NullPointerException npe) {}
			}
		});

		mSelectAll.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mCbla.selectAll();
			}
		});

		mStart.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder cleanerBuilder = new AlertDialog.Builder(DropboxMenu.this);
          	  	cleanerBuilder.setTitle("Cleaner");
          	  	if (mOperation == UPLOAD_OPERATION) {
          	  		cleanerBuilder.setMessage("Do you want to clean files that are not found locally but only in Dropbox?");
				} else if (mOperation == DOWNLOAD_OPERATION) {
					cleanerBuilder.setMessage("Do you want to clean files that are not found in Dropbox but only locally?");
				}
      	  	
	          	cleanerBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						boolean cleanUnusedFiles = true;
						initializeTransfer(mCbla.getAllSelectedTitles(), cleanUnusedFiles);
					}
	          		
	          	});

	          	cleanerBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
		          	public void onClick(DialogInterface dialog, int whichButton) {
		          		boolean cleanUnusedFiles = false;
		          		initializeTransfer(mCbla.getAllSelectedTitles(), cleanUnusedFiles);
	          	    }
	          	});
	          	cleanerBuilder.show();
			}
		});
		
        // Display the proper UI state if logged in or not
        setLoggedIn(mApi.getSession().isLinked());

	}

	private void loadCbla() throws NullPointerException {
		mCbla = new ExtendedCheckBoxListAdapter(this);
		if (mOperation == UPLOAD_OPERATION) {
			setTitle("Dropbox - Upload");
			for (File file : SoundboardMenu.mSbDir.listFiles()) {
				if (file.isDirectory()) {
					mCbla.addItem(new ExtendedCheckBox(file.getName(), false));
				}
			}
			mHandler.post(mUpdateList);
		} else if (mOperation == DOWNLOAD_OPERATION) {
			setTitle("Dropbox - Download");
	    	mWaitDialog = ProgressDialog.show(this, "", "Please wait", true);
			mWaitDialog.setCancelable(true);
			
			t = new Thread() {
				public void run() {
					Looper.prepare();
					try {
						DropboxAPI.Account account = null;
						try {
							account = mApi.accountInfo();
						} catch (DropboxException e) {
							Log.e(TAG, "Account info missing", e);
						}

						if (account != null) {
							for (Entry board : mApi.metadata("/", 0, null, true, null).contents) {
								if (board.isDir) {
									mCbla.addItem(new ExtendedCheckBox(board.fileName(), false));
								}
							}
						} else {
							mWaitDialog.dismiss();
						}
					} catch (DropboxException e) {
						Log.e(TAG, "Error getting Dropbox content", e);
					}
					mHandler.post(mUpdateList);
					mWaitDialog.dismiss();
				}
			};
			t.start();
		}
	}
	
	final Runnable mUpdateList = new Runnable() {
		public void run() {
			setListAdapter(mCbla);
		}
	};
	
    @Override
    protected void onResume() {
        super.onResume();
        AndroidAuthSession session = mApi.getSession();

        if (session.authenticationSuccessful()) {
            try {
                session.finishAuthentication();

                TokenPair tokens = session.getAccessTokenPair();
                storeKeys(tokens.key, tokens.secret);
                setLoggedIn(true);
            } catch (IllegalStateException e) {
            	Toast.makeText(this, "Couldn't authenticate with Dropbox:", Toast.LENGTH_LONG).show();
                Log.i(TAG, "Error authenticating", e);
            }
        }
    }
    
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//    	MenuInflater inflater = getMenuInflater();
//	    inflater.inflate(R.menu.dropbox_bottom, menu);
//	    return true;
//    }

    public void initializeTransfer(final ArrayList<String> boards, final boolean cleanUnusedFiles) {
    	
    	mWaitDialog = new ProgressDialog(this);
    	mWaitDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    	mWaitDialog.setMessage("Please wait");
    	mWaitDialog.show();

    	t = new Thread() {
    		public void run() {
    			Looper.prepare();

    			DropboxAPI.Account account = null;
				try {
					account = mApi.accountInfo();
				} catch (DropboxException e) {
					Log.e(TAG, "Account info missing", e);
				}

    			if (account != null) {

    				if (mOperation == UPLOAD_OPERATION) {
    					upload(boards, cleanUnusedFiles);
    				} else if (mOperation == DOWNLOAD_OPERATION) {
    					download(boards, cleanUnusedFiles);
    				}
    			} else {
    				mWaitDialog.dismiss();
    			}
    		}
    	};
    	t.start();
    }

    
    
    public void upload(final ArrayList<String> boards, boolean cleanUnusedFiles) {

    	int currentBoardIndex = 1;
    	int numberOfBoards = boards.size();
    	
    	DropboxCache dropboxCache = new DropboxCache();
		DropboxCacheUtils.load(dropboxCache);

    	if (numberOfBoards > 0) {
    		try {
    			for (String boardName : boards) {
    				
    				try {
    					Entry dbBoardDir = mApi.metadata("/" + boardName, 0, null, false, null);
        				if (!dbBoardDir.isDeleted && !dbBoardDir.isDir) {
        					mApi.delete("/" + boardName);
        					mApi.createFolder("/" + boardName);
        				}
    				} catch (DropboxException e2) {
    					Log.v(TAG, "Creating new board directory /" + boardName + " in Dropbox");
    					mApi.createFolder("/" + boardName);
    				}

    				if (cleanUnusedFiles) {
    					// Check for unnecessary files in Dropbox
    					checkUnusedDropbox(mApi.metadata("/" + boardName, 0, null, true, null), dropboxCache);
    				}    				

    				// Upload necessary files
    				uploadBoard(new File(SoundboardMenu.mSbDir, boardName), currentBoardIndex, numberOfBoards, dropboxCache);
    				currentBoardIndex++;
    			}
    		} catch (DropboxException e) {
    			Log.e(TAG, "Failed to upload", e);
    		}
    	} else {
    		Toast error = Toast.makeText(DropboxMenu.this, "Please select boards", Toast.LENGTH_LONG);
    		error.show();
    	}
    	
    	DropboxCacheUtils.save(dropboxCache);
    	mWaitDialog.dismiss();
    }
    
    private void checkUnusedDropbox(Entry dropboxDir, DropboxCache dropboxCache) throws DropboxException {
    	String localDir = DropboxCacheUtils.getLocalPath(dropboxDir.path);
    	
    	for (Entry dropboxFile : dropboxDir.contents) {
			String dropboxPath = dropboxFile.path;
			String localPath = DropboxCacheUtils.getLocalPath(dropboxPath);
			boolean fileExists = false;
			for (File file : new File(localDir).listFiles()) {
				if (localPath.equals(file.getAbsolutePath())) {
					fileExists = true;
					break;
				}
			}
			if (!fileExists) {
				DropboxCacheUtils.removeFile(dropboxCache, dropboxPath);
				mApi.delete(dropboxPath);
				Log.v(SoundboardMenu.TAG, "Deleted unused file: " + dropboxPath);
			} else if (dropboxFile.isDir) {
				checkUnusedDropbox(mApi.metadata(dropboxPath, 0, null, true, null), dropboxCache);
			}
		}
    }
    
    private void uploadBoard(File uploadDir, int currentBoardIndex, int numberOfBoards, DropboxCache dropboxCache) throws DropboxException {
    	for (File localPath : uploadDir.listFiles()) {

    		if (localPath.isDirectory()) {
    			uploadBoard(localPath, currentBoardIndex, numberOfBoards, dropboxCache);
    		} else {
    			String dropboxPath = DropboxCacheUtils.getDropboxPath(localPath.getAbsolutePath());

    			final String baseInfo = "Use back to put upload on background.\n\n" +
    					"Board " + currentBoardIndex + "/" + numberOfBoards + "\n" +
    					dropboxPath;
    			mInfo = baseInfo;
    			mProgressValue = 0;
    			mHandler.post(mUpdateResults);

    			if (DropboxCacheUtils.fileChanged(dropboxCache, mApi, dropboxPath)) {

    				if (!restoreFile(dropboxPath, dropboxCache)) {
    					uploadFile(localPath, dropboxPath, baseInfo);
    				}
    				DropboxCacheUtils.updateFile(dropboxCache, mApi, dropboxPath);

    			}
    		}
    	}
    }
    
    public boolean restoreFile(String dropboxPath, DropboxCache dropboxCache) {
    	try {
			for (DropboxCacheFile file : dropboxCache.getFiles()) {
				if (file.getPath().equals(dropboxPath) && 
						file.getMd5().equals(DropboxCacheUtils.getMd5(new File(DropboxCacheUtils.getLocalPath(file.getPath()))))) {
					Log.v(SoundboardMenu.TAG, "Restoring " + dropboxPath);
					mApi.restore(dropboxPath, file.getRev());
					return true;
				}
			}
		} catch (DropboxServerException e) {
		} catch (DropboxException e) {
		}
    	Log.v(SoundboardMenu.TAG, "Failed to restore " + dropboxPath);
    	return false;
    }
    
    public void uploadFile(File localPath, String dropboxPath, final String baseInfo) {
    	Log.v(SoundboardMenu.TAG, "Uploading " + dropboxPath);

    	try {
    		ByteArrayInputStream inputStream = new ByteArrayInputStream(IOUtils.toByteArray(new FileInputStream(localPath)));
    		mApi.putFileOverwrite(dropboxPath, inputStream, inputStream.available(), new ProgressListener() {

    			@Override
    			public void onProgress(long bytes, long total) {
    				mInfo = baseInfo + "\n";
    				Log.v(TAG, "bytes "+bytes+" total "+total);
    				mProgressValue = (int) (bytes*100/total);
    				mHandler.post(mUpdateResults);
    			}

    		});
    	} catch (FileNotFoundException e) {
    		Log.e(TAG, "Unable to upload " + localPath, e);
    	} catch (IOException e) {
    		Log.e(TAG, "Unable to upload " + localPath, e);
    	} catch (DropboxException e) {
    		Log.e(TAG, "Unable to upload " + localPath, e);
    	}
    }

    
    
    
    
    public void download(final ArrayList<String> boards, boolean cleanUnusedFiles) {

    	int currentBoardIndex = 1;
    	int numberOfBoards = boards.size();
    	
    	DropboxCache dropboxCache = new DropboxCache();
		DropboxCacheUtils.load(dropboxCache);

    	if (numberOfBoards > 0) {
    		try {
    			for (String boardName : boards) {

    				if (cleanUnusedFiles) {
    					// Clean unnecessary local files
    					try {
        					checkUnusedLocal(new File(SoundboardMenu.mSbDir, boardName), dropboxCache);
        				} catch (NullPointerException npe) {
        					Log.w(SoundboardMenu.TAG, "Failed to delete unused local files", npe);
        				} catch (IOException e) {
        					Log.w(SoundboardMenu.TAG, "Failed to delete unused local files", e);
    					}
    				}

    				// Download necessary files
    				List<Entry> fileEntrys = mApi.metadata("/" + boardName, 0, null, true, null).contents;
    				downloadBoard(fileEntrys, currentBoardIndex, numberOfBoards, dropboxCache);
    				currentBoardIndex++;
    			}
    		} catch (DropboxException e) {
    			Log.e(TAG, "Failed to download", e);
    		} catch (FileNotFoundException e) {
    			Log.e(TAG, "Failed to download", e);
    		}
    	} else {
    		Toast error = Toast.makeText(DropboxMenu.this, "Please select boards", Toast.LENGTH_LONG);
    		error.show();
    	}

    	DropboxCacheUtils.save(dropboxCache);
    	mWaitDialog.dismiss();
    }
    
    private void checkUnusedLocal(File contentDir, DropboxCache dropboxCache) throws DropboxException, IOException {
    	List<Entry> fileEntrys = mApi.metadata(DropboxCacheUtils.getDropboxPath(contentDir.getAbsolutePath()), 0, null, true, null).contents;
    	for (File file : contentDir.listFiles()) {

    		String dropboxPath = DropboxCacheUtils.getDropboxPath(file.getAbsolutePath());
    		boolean fileExists = false;
    		for (Entry fileEntry : fileEntrys) {
    			if (fileEntry.path.equals(dropboxPath)) {
    				fileExists = true;
    				break;
    			}
    		}
    		
    		if (!fileExists) {
    			DropboxCacheUtils.removeFile(dropboxCache, dropboxPath);
    			if (file.isDirectory()) {
    				FileUtils.deleteDirectory(file);
    			} else {
    				file.delete();
    			}
    			Log.v(SoundboardMenu.TAG, "Deleted unused " + dropboxPath);
    		} else if (file.isDirectory()) {
    			checkUnusedLocal(file, dropboxCache);
    		}
    	}
    }
    
    private void downloadBoard(List<Entry> fileEntrys, int currentBoardIndex, int numberOfBoards, DropboxCache dropboxCache) throws FileNotFoundException, DropboxException {
		for (Entry fileEntry : fileEntrys) {
			
			String dropboxPath = fileEntry.path;
			String localPath = DropboxCacheUtils.getLocalPath(dropboxPath);
			File localFile = new File(localPath);
			
			if (fileEntry.isDir) {
				Log.v(TAG, "Creating dir " + localPath);
				localFile.mkdirs();
				List<Entry> subFileEntrys = mApi.metadata(fileEntry.path, 0, null, true, null).contents;
				downloadBoard(subFileEntrys, currentBoardIndex, numberOfBoards, dropboxCache);
			} else {
				
				final String baseInfo = "Use back to put download on background.\n\n" +
						"Board " + currentBoardIndex + "/" + numberOfBoards + "\n" +
						dropboxPath;
				mInfo = baseInfo;
				mHandler.post(mUpdateResults);
				
				if (localFile.isDirectory()) localFile.delete();
				boolean fileChanged = DropboxCacheUtils.fileChanged(dropboxCache, mApi, dropboxPath);

				if (fileChanged) {
					Log.v(SoundboardMenu.TAG, "Downloading " + dropboxPath);
					if (!localFile.getParentFile().exists()) localFile.getParentFile().mkdirs();
					OutputStream out = new BufferedOutputStream(new FileOutputStream(localPath));
					mApi.getFile(dropboxPath, null, out, new ProgressListener() {

						@Override
						public void onProgress(long bytes, long total) {
							mInfo = baseInfo + "\n";
							mProgressValue = (int) (bytes*100/total);
			    			mHandler.post(mUpdateResults);
						}
						
					});

					DropboxCacheUtils.updateFile(dropboxCache, mApi, dropboxPath);
				}
			}
		}
    }

	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			mWaitDialog.setMessage(mInfo);
			mWaitDialog.setProgress(mProgressValue);
		}
	};
	
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_share:
            	Thread thread = new Thread() {
            		public void run() {
            			Looper.prepare();
            			try {
            				final ArrayList<String> boards = mCbla.getAllSelectedTitles();
            				if (!(mOperation == DOWNLOAD_OPERATION)) {
            					mToastMessage = "Select 'Download/Share' mode";
            					mHandler.post(mShowToast);
            				} else if (boards.size() < 1)  {
            					mToastMessage = "Select boards to share";
            					mHandler.post(mShowToast);
            				} else {
            					String shareString = 
            							"I want to share some cool soundboards to you!\n\n" +
            							"To use the boards you need to have Boarder for Android:\n" +
            							ExternalIntent.mExtLinkMarket + "\n\n" +
            							"Here are the boards:\n";

            					for (String board : boards) {
            						shareString += board + " - " + mApi.createCopyRef("/"+board).copyRef + "\n";
            					}
            					
            					shareString += "\n\n" +
            							"Importing a board:\n" +
            							"1. Open Boarder'\n" +
            							"2. Open Dropbox from menu in 'Soundboard Menu'\n" +
            							"3. Open 'Import share' from menu\n" +
            							"4. Copy a reference from above to textfield\n";

            					Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            					sharingIntent.setType("text/plain");
            					sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Sharing boards");
            					sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareString);
            					startActivity(Intent.createChooser(sharingIntent, "Share via"));
            				}
            			} catch (DropboxException e) {
            				Log.e(TAG, "Unable to share", e);
            			}
            		}
            	};
            	thread.start();
                return true;

            case R.id.menu_import_share:
            	LayoutInflater removeInflater = (LayoutInflater) 
            	DropboxMenu.this.getSystemService(LAYOUT_INFLATER_SERVICE);
            	View importLayout = removeInflater.inflate(R.layout.dropbox_menu_alert_import_share, (ViewGroup) findViewById(R.id.alert_remove_sound_root));

            	AlertDialog.Builder importBuilder = new AlertDialog.Builder(DropboxMenu.this);
            	importBuilder.setView(importLayout);
            	importBuilder.setTitle("Import share");
            	
            	final EditText importCodeInput = (EditText) importLayout.findViewById(R.id.importCodeInput);
            	final EditText importNameInput = (EditText) importLayout.findViewById(R.id.importNameInput);

            	importBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            		public void onClick(DialogInterface dialog, int whichButton) {
            			t = new Thread() {
            				public void run() {
            					Looper.prepare();
            					try {
            						mApi.addFromCopyRef(importCodeInput.getText().toString(), "/" + importNameInput.getText().toString());
            						mToastMessage = "Download the board from 'Download/Share'";
            						mHandler.post(mShowToast);
            					} catch (DropboxException e) {
            						Log.e(TAG, "Unable to get shared board", e);
            					}
            				}
            			};
            			t.start();
            		}
            	});

            	importBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            		public void onClick(DialogInterface dialog, int whichButton) {
            		}
            	});
            	importBuilder.show();
            	return true;
        }
        
        return super.onMenuItemSelected(featureId, item);
    }
    
    final Runnable mShowToast = new Runnable() {
		public void run() {
			Toast.makeText(DropboxMenu.this, mToastMessage, Toast.LENGTH_LONG).show();
		}
	};
	
    private void logOut() {
        // Remove credentials from the session
        mApi.getSession().unlink();

        // Clear our stored keys
        clearKeys();
        // Change UI state to display logged out version
        setLoggedIn(false);
    }

    /**
     * Convenience function to change UI state based on being logged in
     */
    private void setLoggedIn(boolean loggedIn) {
    	mLoggedIn = loggedIn;
    	if (loggedIn) {
    		mDropboxContent.setVisibility(View.VISIBLE);
    		mAuthDropbox.setText("Unlink from Dropbox");
    	} else {
    		mDropboxContent.setVisibility(View.INVISIBLE);
    		mAuthDropbox.setText("Link with Dropbox");
    	}
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     *
     * @return Array of [access_key, access_secret], or null if none stored
     */
    private String[] getKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key != null && secret != null) {
        	String[] ret = new String[2];
        	ret[0] = key;
        	ret[1] = secret;
        	return ret;
        } else {
        	return null;
        }
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void storeKeys(String key, String secret) {
        // Save the access key for later
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.putString(ACCESS_KEY_NAME, key);
        edit.putString(ACCESS_SECRET_NAME, secret);
        edit.commit();
    }

    private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session;

        String[] stored = getKeys();
        if (stored != null) {
            AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
        } else {
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
        }

        return session;
    }

}
