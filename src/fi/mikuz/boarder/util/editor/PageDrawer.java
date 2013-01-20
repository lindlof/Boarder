package fi.mikuz.boarder.util.editor;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

import com.bugsense.trace.BugSenseHandler;

import fi.mikuz.boarder.R;
import fi.mikuz.boarder.component.soundboard.GraphicalSound;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundboard;
import fi.mikuz.boarder.gui.SoundboardMenu;
import fi.mikuz.boarder.util.SoundPlayerControl;

public class PageDrawer {
	
	public static final String TAG = PageDrawer.class.getSimpleName();
	
	private Context context;
	private Joystick joystick;
	
	private Paint mSoundImagePaint;
	
	public PageDrawer(Context context, Joystick joystick) {
		this.context = context;
		this.joystick = joystick;
		
		mSoundImagePaint = new Paint();
        mSoundImagePaint.setColor(Color.WHITE);
        mSoundImagePaint.setAntiAlias(true);
        mSoundImagePaint.setTextAlign(Align.LEFT);
	}
	
	public Canvas drawPage(Canvas canvas, View panel, GraphicalSoundboard drawGsb, GraphicalSound pressedSound, GraphicalSound fineTuningSound) {
		
		if (drawGsb.getUseBackgroundImage() == true && drawGsb.getBackgroundImagePath().exists()) {
			RectF bitmapRect = new RectF();
			bitmapRect.set(drawGsb.getBackgroundX(), drawGsb.getBackgroundY(), 
					drawGsb.getBackgroundWidth() + drawGsb.getBackgroundX(), drawGsb.getBackgroundHeight() + drawGsb.getBackgroundY());
			
			Paint bgImage = new Paint();
			bgImage.setColor(drawGsb.getBackgroundColor());
			
			try {
				canvas.drawBitmap(drawGsb.getBackgroundImage(), null, bitmapRect, bgImage);
			} catch(NullPointerException npe) {
				Log.e(TAG, "Unable to draw image " + drawGsb.getBackgroundImagePath().getAbsolutePath());
				drawGsb.setBackgroundImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.sound));
			}
		}
		
		try {
			ArrayList<GraphicalSound> drawList = new ArrayList<GraphicalSound>();
			drawList.addAll(drawGsb.getSoundList());
			if (pressedSound != null) drawList.add(pressedSound);
			
			for (GraphicalSound sound : drawList) {
				Paint barPaint = new Paint();
				barPaint.setColor(sound.getNameFrameInnerColor());
				String soundPath = sound.getPath().getAbsolutePath();
				if (soundPath.equals(SoundboardMenu.mTopBlackBarSoundFilePath)) {
					canvas.drawRect(0, 0, canvas.getWidth(), sound.getNameFrameY(), barPaint);
				} else if (soundPath.equals(SoundboardMenu.mBottomBlackBarSoundFilePath)) {
					canvas.drawRect(0, sound.getNameFrameY(), canvas.getWidth(), canvas.getHeight(), barPaint);
				} else if (soundPath.equals(SoundboardMenu.mLeftBlackBarSoundFilePath)) {
					canvas.drawRect(0, 0, sound.getNameFrameX(), canvas.getHeight(), barPaint);
				} else if (soundPath.equals(SoundboardMenu.mRightBlackBarSoundFilePath)) {
					canvas.drawRect(sound.getNameFrameX(), 0, canvas.getWidth(), canvas.getHeight(), barPaint);
				} else {
					if (sound.getHideImageOrText() != GraphicalSound.HIDE_TEXT) {
						float NAME_DRAWING_SCALE = SoundNameDrawing.NAME_DRAWING_SCALE;
						
						
						canvas.scale(1/NAME_DRAWING_SCALE, 1/NAME_DRAWING_SCALE);
						SoundNameDrawing soundNameDrawing = new SoundNameDrawing(sound);
						
						Paint nameTextPaint = soundNameDrawing.getBigCanvasNameTextPaint();
						Paint borderPaint = soundNameDrawing.getBorderPaint();
						Paint innerPaint = soundNameDrawing.getInnerPaint();
						
						RectF bigCanvasNameFrameRect = soundNameDrawing.getBigCanvasNameFrameRect();
						
						if (sound.getShowNameFrameInnerPaint() == true) {
					    	canvas.drawRoundRect(bigCanvasNameFrameRect, 2*NAME_DRAWING_SCALE, 2*NAME_DRAWING_SCALE, innerPaint);
					    }
						
						if (sound.getShowNameFrameBorderPaint()) {
							canvas.drawRoundRect(bigCanvasNameFrameRect, 2*NAME_DRAWING_SCALE, 2*NAME_DRAWING_SCALE, borderPaint);
						}
					    
						int i = 0;
					    for (String row : sound.getName().split("\n")) {
				    		canvas.drawText(row, (sound.getNameFrameX()+2)*NAME_DRAWING_SCALE, 
				    				sound.getNameFrameY()*NAME_DRAWING_SCALE+(i+1)*sound.getNameSize()*NAME_DRAWING_SCALE, nameTextPaint);
				    		i++;
					    }
					    canvas.scale(NAME_DRAWING_SCALE, NAME_DRAWING_SCALE);
					}
				    
				    if (sound.getHideImageOrText() != GraphicalSound.HIDE_IMAGE) {
					    RectF imageRect = new RectF();
					    imageRect.set(sound.getImageX(), 
								sound.getImageY(), 
								sound.getImageWidth() + sound.getImageX(), 
								sound.getImageHeight() + sound.getImageY());
						
					    try {
					    	if (SoundPlayerControl.isPlaying(sound.getPath()) && sound.getActiveImage() != null) {
					    		try {
					    			canvas.drawBitmap(sound.getActiveImage(), null, imageRect, mSoundImagePaint);
					    		} catch(NullPointerException npe) {
					    			Log.e(TAG, "Unable to draw active image for sound " + sound.getName());
									sound.setActiveImage(null);
					    			canvas.drawBitmap(sound.getImage(), null, imageRect, mSoundImagePaint);
					    		}
					    		
					    	} else {
					    		canvas.drawBitmap(sound.getImage(), null, imageRect, mSoundImagePaint);
					    	}
						} catch(NullPointerException npe) {
							Log.e(TAG, "Unable to draw image for sound " + sound.getName());
							BugSenseHandler.log(TAG, npe);
							sound.setImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.sound));
						}
				    }
				    
				    if (drawGsb.getAutoArrange() && sound == pressedSound) {
				    	int width = panel.getWidth();
						int height = panel.getHeight();
						
						Paint linePaint = new Paint();
						Paint outerLinePaint = new Paint(); {
						linePaint.setColor(Color.WHITE);
						outerLinePaint.setColor(Color.YELLOW);
						outerLinePaint.setStrokeWidth(3);
						}
						
				    	for (int i = 1; i < drawGsb.getAutoArrangeColumns(); i++) {
				    		float X = i*(width/drawGsb.getAutoArrangeColumns());
				    		canvas.drawLine(X, 0, X, height, outerLinePaint);
				    		canvas.drawLine(X, 0, X, height, linePaint);
				    	}
				    	for (int i = 1; i < drawGsb.getAutoArrangeRows(); i++) {
				    		float Y = i*(height/drawGsb.getAutoArrangeRows());
				    		canvas.drawLine(0, Y, width, Y, outerLinePaint);
				    		canvas.drawLine(0, Y, width, Y, linePaint);
				    	}
				    }
				}
			}
		} catch(ConcurrentModificationException cme) {
			Log.w(TAG, "Sound list modification while iteration");
		}
		
		if (fineTuningSound != null) {
			canvas.drawBitmap(joystick.getJoystickImage(), null, joystick.getJoystickImageRect(), mSoundImagePaint);
		}
		
		return canvas;
	}
}
