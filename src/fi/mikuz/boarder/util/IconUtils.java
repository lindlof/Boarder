package fi.mikuz.boarder.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
public class IconUtils {
	public static Bitmap resizeIcon(Activity activity, Bitmap bitmap, float pixelsPerInch) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
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
