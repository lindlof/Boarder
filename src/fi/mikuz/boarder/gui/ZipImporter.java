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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import fi.mikuz.boarder.R;
import fi.mikuz.boarder.app.BoarderActivity;

public class ZipImporter extends BoarderActivity {
	private static final String TAG = "ZipImporter";
	
	private TextView mInfoText;
	private String mMessage = "";
	final Handler mHandler = new Handler();
	private String log = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zip_importer);
		mInfoText = (TextView)findViewById(R.id.infoText);
		mInfoText.setText("Please wait");
		
		if (getIntent().getData() != null && getIntent().getData().getPath() != null) {
			unzipArchive(new File(getIntent().getData().getPath()), SoundboardMenu.mSbDir);
		} else {
			String msg = "Invalid file path";
			Log.e(TAG, msg);
			Toast.makeText(super.mContext, msg, Toast.LENGTH_LONG).show();
		}
	}

	@SuppressWarnings("rawtypes")
	public void unzipArchive(final File archive, final File outputDir) {
		
		Thread t = new Thread(){
			public void run() {
				Looper.prepare();
				String archiveName = archive.getName();
				String boardName = archiveName.substring(0, archiveName.indexOf(".zip"));
				String boardDirectory = SoundboardMenu.mSbDir.getAbsolutePath() + "/" + boardName;
		
				try {
					File boardDirectoryFile = new File(boardDirectory);
					if (boardDirectoryFile.exists()) {
						postMessage(boardDirectoryFile.getName() + " already exists.");
					} else {
						ZipFile zipfile = new ZipFile(archive);
						boolean normalStructure = true;
						
						log = "Checking if zip structure is legal\n"+log;
						for (Enumeration e = zipfile.entries(); e.hasMoreElements(); ) {
							ZipEntry entry = (ZipEntry) e.nextElement();
							File outputFile = new File(outputDir, entry.getName());
		
							if (!Pattern.matches(boardDirectory+".*", outputFile.getAbsolutePath())) {
								normalStructure = false;
								log = entry.getName() + " failed\n" + outputFile.getAbsolutePath() + "\ndoens't match\n" + boardDirectory + "\n\n"+log;
								Log.e(TAG, entry.getName() + " failed\n" + outputFile.getAbsolutePath() + "\ndoens't match\n" + boardDirectory);
							}
						}
						
						if (normalStructure) {
							log = "\nGoing to extract\n"+log;
							outputDir.mkdirs();
							for (Enumeration e = zipfile.entries(); e.hasMoreElements(); ) {
								ZipEntry entry = (ZipEntry) e.nextElement();
								log = "Extracting " + entry.getName() + "\n"+log;
								postMessage("Please wait\n\n" + log);
								unzipEntry(zipfile, entry, outputDir);
							}
							log = "Success\n\n"+log;
							postMessage(log);
						} else {
							postMessage("Zip was not extracted because it doesn't follow the normal structure.\n\n" +
									"Please use another application to check the content of this zip file and extract it if you want to.\n\n"+log);
						}
						
					}
				} catch (Exception e) {
					log = "Couldn't extract " + archive + "\n\nError: \n" + e.getMessage() + "\n\n"+log;
					postMessage(log);
					Log.e(TAG, "Error while extracting file " + archive, e);
				}
				mHandler.post(showContinueButton);
			}
		};
		t.start();
	}
	
	private void postMessage(String message) {
		mMessage = message;
		mHandler.post(updateUI);
	}
	
	final Runnable updateUI = new Runnable() {
		public void run() {
			mInfoText.setText(mMessage);
		}
	};

	final Runnable showContinueButton = new Runnable() {
		public void run() {
			Button continueButton = (Button)findViewById(R.id.continueButton);
			continueButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(ZipImporter.this, SoundboardMenu.class);
	            	startActivity(intent);
				}
			});
			continueButton.setVisibility(View.VISIBLE);
		}
	};
	

	private void unzipEntry(ZipFile zipfile, ZipEntry entry, File outputDir) throws IOException {
		
		File outputFile = new File(outputDir, entry.getName());
		
		if (entry.isDirectory()) {
			createDir(outputFile);
			return;
		}

		if (!outputFile.getParentFile().exists()){
			createDir(outputFile.getParentFile());
		}
		
		log = "Extracting: " + entry.getName()+"\n"+log;
		postMessage("Please wait\n\n"+log);
		Log.d(TAG, "Extracting: " + entry);
		BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

		try {
			IOUtils.copy(inputStream, outputStream);
		} finally {
			outputStream.close();
			inputStream.close();
		}
		
	}

	private void createDir(File dir) {
		log = "Creating dir "+dir.getName()+"\n"+log;
		postMessage("Please wait\n\n"+log);
		Log.d(TAG, "Creating dir "+dir.getName());
		dir.mkdirs();
		if(!dir.exists()) throw new RuntimeException("Can not create dir "+dir);
	}

}