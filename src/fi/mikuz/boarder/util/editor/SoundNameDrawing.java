package fi.mikuz.boarder.util.editor;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.Log;
import fi.mikuz.boarder.component.soundboard.GraphicalSound;

/**
 * 
 * @author Jan Mikael Lindl�f
 */
public class SoundNameDrawing {
	public static final String TAG = "SoundNameDrawing";
	
	public static final float NAME_LOCATION_SCALE = 20;
	
	GraphicalSound sound;
	String name;
	float nameSize;

	public SoundNameDrawing(GraphicalSound sound) {
		this.sound = sound;
		this.name = sound.getName();
		this.nameSize = sound.getNameSize();
	}
	
	public Paint getNameTextPaint() {
		Paint nameTextPaint = new Paint();
	    int nameTextColor = sound.getNameTextColor();
	    nameTextPaint.setARGB(Color.alpha(nameTextColor), Color.red(nameTextColor), 
				Color.green(nameTextColor), Color.blue(nameTextColor));
	    nameTextPaint.setAntiAlias(true);
	    nameTextPaint.setTextAlign(Align.LEFT);
	    nameTextPaint.setTextSize(nameSize);
	    return nameTextPaint;
	}
	
	public Paint getBorderPaint() {
		Paint borderPaint = new Paint();
		int borderPaintColor = sound.getNameFrameBorderColor();
		borderPaint.setARGB(Color.alpha(borderPaintColor), Color.red(borderPaintColor), 
	    		Color.green(borderPaintColor), Color.blue(borderPaintColor));
		borderPaint.setAntiAlias(true);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setStrokeWidth(2*NAME_LOCATION_SCALE);
		return borderPaint;
	}
	
	public Paint getInnerPaint() {
		Paint innerPaint = new Paint();
    	int innerPaintColor = sound.getNameFrameInnerColor();
    	innerPaint.setARGB(Color.alpha(innerPaintColor), Color.red(innerPaintColor), 
    	Color.green(innerPaintColor), Color.blue(innerPaintColor));
    	return innerPaint;
	}
	
	public RectF getNameFrameRect() {
		
		RectF nameFrameRect = getBigCanvasNameFrameRect();
		nameFrameRect.left = nameFrameRect.left/NAME_LOCATION_SCALE;
		nameFrameRect.top = nameFrameRect.top/NAME_LOCATION_SCALE;
		nameFrameRect.right = nameFrameRect.right/NAME_LOCATION_SCALE;
		nameFrameRect.bottom = nameFrameRect.bottom/NAME_LOCATION_SCALE;
		
		return nameFrameRect;
	}
	
	public Paint getBigCanvasNameTextPaint() {
		Paint nameTextPaint = getNameTextPaint();
		nameTextPaint.setTextSize(nameTextPaint.getTextSize()*NAME_LOCATION_SCALE);
	    return nameTextPaint;
	}
	
	public RectF getBigCanvasNameFrameRect() {
		float nameFrameWidth = 10;
		for (String row : name.split("\n")) {
			float rowLength = getBigCanvasNameTextPaint().measureText(row);
			nameFrameWidth = (rowLength > nameFrameWidth) ? rowLength : nameFrameWidth;
		}
		
	    int lineCount = name.split("\n",-1).length;
	    FontMetrics metrics = getBigCanvasNameTextPaint().getFontMetrics();
	    float height = 0;
	    float textPaddingY = metrics.bottom/2;
	    float textPaddingWidth = 4*NAME_LOCATION_SCALE; //Editor adds 2 for text left padding
	    
    	if (nameSize < 14) {
    		height =+ 1;
    	}
    	
		RectF nameFrameRect = new RectF();
		nameFrameRect.set(
				sound.getNameFrameX()*NAME_LOCATION_SCALE, 
				(sound.getNameFrameY() - height)*NAME_LOCATION_SCALE + textPaddingY, 
				sound.getNameFrameX()*NAME_LOCATION_SCALE + textPaddingWidth + nameFrameWidth, 
				(sound.getNameFrameY() + height)*NAME_LOCATION_SCALE + textPaddingY + lineCount*nameSize*NAME_LOCATION_SCALE);
		
		return nameFrameRect;
	}
	
	/**
	 * Tests which text size is closest to original.
	 * 
	 * @param sound
	 * @param resolutionScale
	 * @return
	 */
	public static GraphicalSound getScaledTextSize(GraphicalSound sound, float resolutionScale) {
		float nameTargetPixelSize = sound.getNamePixelSize()*resolutionScale;
		float namePixelSize = 0;
		GraphicalSound testSound = (GraphicalSound) sound.clone();
		
		float i, j;
		for (i = 1000, j = 0; i >= 0.001; i = i/10) {
			do {
				j=j+i;
				testSound.setNameSize(j);
				namePixelSize = testSound.getNamePixelSize();
			} while(namePixelSize < nameTargetPixelSize);
			j=j-i;
			Log.v(TAG, j + " is closest in " + i + "'s");
		}
		testSound.setNameSize(j-1);
		if (Math.abs(nameTargetPixelSize-namePixelSize) < Math.abs(nameTargetPixelSize-testSound.getNamePixelSize())) {
			sound.setNameSize(j);
		} else {
			sound.setNameSize(j-1);
		}
		return sound;
	}
}
