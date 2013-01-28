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
