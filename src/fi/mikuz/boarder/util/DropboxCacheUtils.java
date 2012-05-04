package fi.mikuz.boarder.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;

import fi.mikuz.boarder.component.DropboxCache;
import fi.mikuz.boarder.component.DropboxCacheFile;
import fi.mikuz.boarder.gui.SoundboardMenu;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
public class DropboxCacheUtils {
	private static final String TAG = "DropboxCacheUtils";
	
	public static String getDropboxPath(String localPath) {
		return localPath.substring(SoundboardMenu.mSbDir.getAbsolutePath().length());
	}
	
	public static String getLocalPath(String dropboxPath) {
		return SoundboardMenu.mSbDir.getAbsolutePath() + dropboxPath;
	}
	
	public static void updateFile(DropboxCache dropboxCache, DropboxAPI<AndroidAuthSession> mApi, String dropboxPath) {
		try {
			String md5 = getMd5(new File(SoundboardMenu.mSbDir, dropboxPath));
			String rev = mApi.metadata(dropboxPath, 1, null, false, null).rev;
			for(DropboxCacheFile file : dropboxCache.getFiles()) {
				if (file.getPath().equals(dropboxPath)) {
					file.setMd5(md5);
					file.setRev(rev);
					return;
				}
			}
			DropboxCacheFile file = new DropboxCacheFile(dropboxPath, rev, md5);
			dropboxCache.getFiles().add(file);
		} catch (DropboxException e) {
			Log.e(TAG, "Unable to get file rev", e);
		}
	}
	
	public static void removeFile(DropboxCache dropboxCache, String dropboxPath) {
		for(DropboxCacheFile file : dropboxCache.getFiles()) {
			if (file.getPath().equals(dropboxPath)) {
				dropboxCache.getFiles().remove(file);
				break;
			}
		}
	}
	
	public static boolean fileChanged(DropboxCache dropboxCache, DropboxAPI<AndroidAuthSession> mApi, String dropboxPath) {
		try {
			String md5 = getMd5(new File(SoundboardMenu.mSbDir, dropboxPath));
			
			if (md5 == null) {
				Log.v(TAG, dropboxPath + " - local version missing");
				return true;
			}
			
			String rev = mApi.metadata(dropboxPath, 1, null, false, null).rev;
			
			for(DropboxCacheFile file : dropboxCache.getFiles()) {
				if (file.getPath().equals(dropboxPath)) {
					if (file.getMd5().equals(md5) && file.getRev().equals(rev)) {
						Log.v(TAG, dropboxPath + " - not changed");
						return false;
					} else if (file.getMd5().equals(md5)) {
						Log.v(TAG, dropboxPath + " - remote version changed");
						return true;
					} else if (file.getRev().equals(rev)) {
						Log.v(TAG, dropboxPath + " - local version changed");
						return true;
					} else {
						Log.v(TAG, dropboxPath + " - remote and local version changed");
						return true;
					}
				}
			}
			Log.v(TAG, dropboxPath + " - not in cache");
			return true;
		} catch (DropboxException e) {
			Log.v(TAG, dropboxPath + " - remote version missing");
			return true;
		}
	}
	
	public static void load(DropboxCache dropboxCache) {
		if (SoundboardMenu.mDropboxCache.exists()) {
			try {
				XStream xstream = new XStream();
				DropboxCache cache = (DropboxCache) xstream.fromXML(SoundboardMenu.mDropboxCache);
				dropboxCache.setFiles(cache.getFiles());
			} catch(StreamException e) {
				Log.e(TAG, "Corrupted dropbox cache", e);
				dropboxCache.setFiles(new ArrayList<DropboxCacheFile>());
			}
		} else {
			dropboxCache.setFiles(new ArrayList<DropboxCacheFile>());
		}
	}
	
	public static void save(DropboxCache dropboxCache) {
		try {
			XStream xstream = new XStream();
			BufferedWriter out = new BufferedWriter(new FileWriter(SoundboardMenu.mDropboxCache));
			xstream.toXML(dropboxCache, out);
			out.close();
		} catch (IOException e) {
			Log.e(TAG, "Unable to save dropbox cache", e);
		}
	}
	
	
	
	
	public static String getMd5(File file) {

		if (!file.exists()) {
			return null;
		}
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}				
		byte[] buffer = new byte[8192];
		int read = 0;
		try {
			while( (read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}		
			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			String output = bigInt.toString(16);
			return output;
		}
		catch(IOException e) {
			throw new RuntimeException("Unable to process file for MD5", e);
		}
		finally {
			try {
				is.close();
			}
			catch(IOException e) {
				throw new RuntimeException("Unable to close input stream for MD5 calculation", e);
			}
		}
	}
}
