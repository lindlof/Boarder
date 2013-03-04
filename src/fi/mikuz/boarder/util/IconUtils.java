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

import java.io.File;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
public class IconUtils {
	public static Bitmap resizeIcon(Context context, Bitmap bitmap, float pixelsPerInch) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		
		float density = metrics.densityDpi;
		float imageSize = density/pixelsPerInch;
		
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
        float scaleWidth = ((float) imageSize) / width;
        float scaleHeight = ((float) imageSize) / height;
       
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	}
	
	public static Bitmap decodeIcon(Context context, File iconPath) {
		int viewSize = getMenuIconSize(context);
		return ImageDrawing.decodeFile(context, iconPath, viewSize, viewSize);
	}
	
	public static int getMenuIconSize(Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, context.getResources().getDisplayMetrics());
	}
	
}
