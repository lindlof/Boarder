package fi.mikuz.boarder.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import com.wuman.twolevellrucache.TwoLevelLruCache;
import com.wuman.twolevellrucache.TwoLevelLruCache.Converter;

import fi.mikuz.boarder.gui.SoundboardMenu;

public class ImageCache implements TwoLevelLruCache.Converter<Bitmap> {
	private static final String TAG = ImageCache.class.getSimpleName();
	
    private TwoLevelLruCache<Bitmap> cache;
    private CompressFormat mCompressFormat = CompressFormat.PNG;
    private int mCompressQuality = 100;
    private static final int APP_VERSION = 1;
    
    public static final int IO_BUFFER_SIZE = 8 * 1024;

    public ImageCache() throws IOException {
    	final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
	    final int memCacheSize = maxMemory / 4;
	    final int diskCacheSize = 100000;
	    Converter<Bitmap> converter = (Converter<Bitmap>) this;
	    
    	cache = new TwoLevelLruCache<Bitmap>(SoundboardMenu.mImageCacheDir, APP_VERSION, memCacheSize, diskCacheSize, converter);
    }
    
    public void add(String key, Bitmap bitmap) {
    	cache.put(key, bitmap);
    }
    
    public Bitmap get(String key) {
    	return cache.get(key);
    }
    
    public long hashBitmap(Bitmap bmp){
    	  long hash = 31; //or a higher prime at your choice
    	  for(int x = 0; x < bmp.getWidth(); x++){
    	    for (int y = 0; y < bmp.getHeight(); y++){
    	      hash *= (bmp.getPixel(x,y) + 31);
    	    }
    	  }
    	  return hash;
    	}

    /** Converts bytes to Bitmap. */
	@Override
	public Bitmap from(byte[] bytes) throws IOException {
		InputStream is = new ByteArrayInputStream(bytes);
		return BitmapFactory.decodeStream(is);
	}

	/** Converts Bitmap to bytes written to the specified stream. */
	@Override
	public void toStream(Bitmap bitmap, OutputStream bytes) throws IOException {
		bitmap.compress(mCompressFormat, mCompressQuality, bytes);
	}
}
