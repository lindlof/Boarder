package fi.mikuz.boarder.util.editor;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.bugsense.trace.BugSenseHandler;

import fi.mikuz.boarder.component.soundboard.GraphicalSound;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundboard;
import fi.mikuz.boarder.gui.SoundboardMenu;
import fi.mikuz.boarder.util.SoundPlayerControl;
import fi.mikuz.boarder.util.editor.FadingPage.FadeState;

/**
 * Draws pages and page related animations.
 * 
 * @author Jan Mikael Lindlöf
 */
public class PageDrawer {
	
	public static final String TAG = PageDrawer.class.getSimpleName();
	
	private Context context;
	private Joystick joystick;
	
	private GraphicalSoundboard topGsb;
	private List<FadingPage> fadingPages;
	private boolean initialPage;
	
	private final static Bitmap.Config BITMAP_CONF = Bitmap.Config.ARGB_8888;
	
	private Paint soundImagePaint;
	
	public PageDrawer(Context context, Joystick joystick) {
		this.context = context;
		this.joystick = joystick;
		
		this.topGsb = null;
		fadingPages = new ArrayList<FadingPage>();
		initialPage = true;
		
		soundImagePaint = new Paint();
        soundImagePaint.setColor(Color.WHITE);
        soundImagePaint.setAntiAlias(true);
        soundImagePaint.setTextAlign(Align.LEFT);
	}
	
	public boolean needAnimationRefreshSpeed() {
		return (fadingPages.size() > 0);
	}
	
	public void switchPage(GraphicalSoundboard newGsb) {
		boolean initialPage = this.initialPage;
		this.initialPage = false;
		
		GraphicalSoundboard lastGsb = topGsb;
		topGsb = newGsb;

		Bitmap newPageDrawCache = null;
		Bitmap lastPageDrawCache = null;

		for (FadingPage listedPage : fadingPages) {
			if (listedPage.getGsb().getId() == newGsb.getId()) {
				newPageDrawCache = listedPage.getDrawCache();
			} else if (listedPage.getGsb().getId() == lastGsb.getId()) {
				lastPageDrawCache = listedPage.getDrawCache();
			}
		}
		
		if (fadingPages.size() > 3) {
			try {
				fadingPages.remove(fadingPages.size()-1);
				Log.w(TAG, "Removed last fading page because of flood");
			} catch (IndexOutOfBoundsException e) {}
		}

		FadingPage newFadingPage = new FadingPage(newGsb, FadeState.FADING_IN);
		if (newPageDrawCache != null) newFadingPage.setDrawCache(newPageDrawCache);
		fadingPages.add(newFadingPage);

		if (!initialPage) {
			FadingPage lastFadingPage = new FadingPage(lastGsb, FadeState.FADING_OUT);
			if (lastPageDrawCache == null) lastPageDrawCache = genPageCache(lastGsb, null, null);
			lastFadingPage.setDrawCache(lastPageDrawCache);
			fadingPages.add(lastFadingPage);
		}
	}
	
	public Canvas drawSurface(Canvas canvas, GraphicalSound pressedSound, GraphicalSound fineTuningSound) {
		Paint paint = new Paint();
		canvas.drawColor(Color.BLACK);

		boolean topGsgFading = false;

		Iterator<FadingPage> iter = fadingPages.iterator();
		Rect screenRect = null;
		while (iter.hasNext()) {
			try {
				FadingPage listedPage = (FadingPage) iter.next();
	
				if (screenRect == null) {
					screenRect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
				}
	
				listedPage.updateFadeProgress();
				if (listedPage.getFadeProgress() >= 100 || 
						listedPage.getFadeProgress() <= 0) {
					iter.remove();
				} else {
					Bitmap pageBitmap = listedPage.getDrawCache();
					if (pageBitmap == null) {
						pageBitmap = genPageCache(listedPage.getGsb(), null, null);
						listedPage.setDrawCache(pageBitmap);
					}
	
					if (listedPage.getGsb() == topGsb) {
						topGsgFading = true;
					}
	
					float fadeProgress = (float) listedPage.getFadeProgress();
					float fadePercentage = fadeProgress/100;
	
					float xDistance = canvas.getWidth()/2 * (1-fadePercentage);
					float yDistance = canvas.getHeight()/2 * (1-fadePercentage);
					RectF fadeRect = new RectF(xDistance, yDistance, 
							canvas.getWidth() - xDistance, canvas.getHeight() - yDistance);
	
					int fadeAlpha = (int) (fadePercentage*255f);
					paint.setAlpha(fadeAlpha);
					canvas.drawBitmap(pageBitmap, screenRect, fadeRect, paint);
				}
			} catch(ConcurrentModificationException cme) {
				Log.w(TAG, "Fading page modification while iterating");
				break;
			}
		}

		if (!topGsgFading) {
			// Drawing directly to SurfaceView canvas is a lot faster.
			drawPage(canvas, topGsb, pressedSound, fineTuningSound);
		}

		return canvas;
	}
	
	private Bitmap genPageCache(GraphicalSoundboard gsb, GraphicalSound pressedSound, GraphicalSound fineTuningSound) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		
		Bitmap pageBitmap = Bitmap.createBitmap(size.x, size.y, BITMAP_CONF);
		Canvas pageCanvas = new Canvas(pageBitmap);
		drawPage(pageCanvas, gsb, pressedSound, fineTuningSound);
		
		return pageBitmap;
	}
	
	private Canvas drawPage(Canvas canvas, GraphicalSoundboard drawGsb, GraphicalSound pressedSound, GraphicalSound fineTuningSound) {
		
		canvas.drawColor(drawGsb.getBackgroundColor());
		
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
				drawGsb.loadPlaceholderBackgroundImage(context);
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
					    			canvas.drawBitmap(sound.getActiveImage(), null, imageRect, soundImagePaint);
					    		} catch(NullPointerException npe) {
					    			Log.e(TAG, "Unable to draw active image for sound " + sound.getName());
									sound.setDefaultActiveImage();
					    			canvas.drawBitmap(sound.getImage(), null, imageRect, soundImagePaint);
					    		}
					    		
					    	} else {
					    		canvas.drawBitmap(sound.getImage(), null, imageRect, soundImagePaint);
					    	}
						} catch(NullPointerException npe) {
							Log.e(TAG, "Unable to draw image for sound " + sound.getName());
							npe.printStackTrace();
							BugSenseHandler.log(TAG, npe);
							sound.setDefaultImage();
						}
				    }
				    
				    if (drawGsb.getAutoArrange() && sound == pressedSound) {
				    	int width = canvas.getWidth();
						int height = canvas.getHeight();
						
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
			Log.w(TAG, "Sound list modification while iterating");
		}
		
		if (fineTuningSound != null) {
			canvas.drawBitmap(joystick.getJoystickImage(), null, joystick.getJoystickImageRect(), soundImagePaint);
		}
		
		return canvas;
	}
}
