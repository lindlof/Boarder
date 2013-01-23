package fi.mikuz.boarder.util.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import fi.mikuz.boarder.util.Handlers.ToastHandler;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
public class ImageDrawing {
	public static final String TAG = "ImageDrawing";
	
	static final int IMAGE_MAX_SIZE = 4000;

	/**
	 * Custom bitmap decoder to avoid and improve memory errors with enormous images and large amount of huge images.
	 * 
	 * @param image file
	 * @return image bitmap
	 */
	public static Bitmap decodeFile(ToastHandler toasthandler, File f) { // TODO Could a same bitmap in memory be reused elegantly here?
	    Bitmap b = null;
	    
	    // Bitmaps can take large amounts of memory.
	    // To avoid unexpected stuff bitmaps won't be decoded if memory is running very low.
	    if (underFivePercentOfMemoryLeft()) {
	    	String errorMessage = "Not enough memory, won't decode image " + f.getAbsolutePath();
	    	Log.e(TAG, errorMessage);
	    	toasthandler.toast("Not enough memory");
	    	return null;
	    }
	    
	    try {
	    	try {
		    	b = BitmapFactory.decodeFile(f.getAbsolutePath());
		    } catch (OutOfMemoryError ome) {
		    	Log.w(TAG, "Image " + f.getAbsolutePath() + " is enormous! It has to be decoded to smaller resolution.");
		    	try {
			        //Decode image size
			        BitmapFactory.Options o = new BitmapFactory.Options();
			        o.inJustDecodeBounds = true;

			        FileInputStream fis = new FileInputStream(f);
			        BitmapFactory.decodeStream(fis, null, o);
			        fis.close();

			        int scale = 1;
			        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
			            scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
			        }

			        //Decode with inSampleSize
			        BitmapFactory.Options o2 = new BitmapFactory.Options();
			        o2.inSampleSize = scale;
			        fis = new FileInputStream(f);
			        b = BitmapFactory.decodeStream(fis, null, o2);
			        fis.close();
			    } catch (IOException e) {
			    	Log.e(TAG, "Unable to read image file");
			    }
		    }
	    } catch (OutOfMemoryError ome2) {
	    	String errorMessage = "Unable to decode image, out of memory";
	    	Log.e(TAG, errorMessage, ome2);
	    	toasthandler.toast("Out of memory");
	    }
	    
	    return b;
	    
	}
	
	private static boolean underFivePercentOfMemoryLeft() {
	    Runtime runtime = Runtime.getRuntime();
	    long usedMemory = (runtime.totalMemory() - runtime.freeMemory());
	    long maxMemory = runtime.maxMemory();
	    
	    return (usedMemory > (maxMemory - maxMemory*0.05));
	}

}
