package fi.mikuz.boarder.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.wuman.twolevellrucache.TwoLevelLruCache;
import com.wuman.twolevellrucache.TwoLevelLruCache.Converter;

import fi.mikuz.boarder.gui.SoundboardMenu;

public class ImageCache implements TwoLevelLruCache.Converter<Bitmap> {
	private static final String TAG = ImageCache.class.getSimpleName();
	
    private TwoLevelLruCache<Bitmap> cache;
    private CompressFormat compressFormat = CompressFormat.JPEG;
    private int compressQuality = 80;
    private static final int APP_VERSION = 1;
    
    public static final int IO_BUFFER_SIZE = 8 * 1024;

    public ImageCache() throws IOException {
    	final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
	    final int maxMemCacheSize = maxMemory / 3;
	    int maxDiskCacheSize = 4000000;  // 4MB
	    Converter<Bitmap> converter = (Converter<Bitmap>) this;
	    
	    // TwoLevelLruCache throws an exception if disk cache isn't greater than memory cache
	    if (maxMemCacheSize >= maxDiskCacheSize) {
	    	maxDiskCacheSize = maxMemCacheSize + 10000;
	    	Log.v(TAG, "Increased disk cache to be greater than memory cache.");
	    }
	    
    	cache = new TwoLevelLruCache<Bitmap>(SoundboardMenu.mImageCacheDir, APP_VERSION, maxMemCacheSize, maxDiskCacheSize, converter);
    }
    
    public void add(String key, Bitmap bitmap) {
    	cache.put(key, bitmap);
    }
    
    public Bitmap get(String key) {
    	return cache.get(key);
    }
    
    /**
     * Forces DiskLruCache journal, which holds information about caches, to be saved on disk
     * @throws IOException
     */
    public void saveCacheMetadata() throws IOException {
    	cache.flush();
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
		bitmap.compress(compressFormat, compressQuality, bytes);
	}
}
