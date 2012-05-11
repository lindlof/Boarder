package fi.mikuz.boarder.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;

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
}
