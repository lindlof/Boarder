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
