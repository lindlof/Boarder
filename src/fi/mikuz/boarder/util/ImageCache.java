package fi.mikuz.boarder.util;

import java.io.IOException;

import android.graphics.Bitmap;
import android.util.LruCache;

public class ImageCache {
	private static final String TAG = ImageCache.class.getSimpleName();
	
	private final LruCache<String, Bitmap> cache;

    public ImageCache() throws IOException {
    	final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
	    final int maxMemCacheSize = maxMemory / 3;
	    
	    cache = new LruCache<String, Bitmap>(maxMemCacheSize);
    }
    
    public void add(String key, Bitmap bitmap) {
    	cache.put(key, bitmap);
    }
    
    public Bitmap get(String key) {
    	return cache.get(key);
    }
}
