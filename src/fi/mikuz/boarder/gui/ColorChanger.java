package fi.mikuz.boarder.gui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import fi.mikuz.boarder.R;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
public class ColorChanger extends Activity implements OnSeekBarChangeListener, ColorPickerDialog.OnColorChangedListener {
	
	private String mParent;
	private int mBackgroundColor;
	private String mName;
	private float mNameFrameWidth;
	private float mNameFrameHeight;
	private int mNameTextColor;
	private int mNameFrameInnerColor;
	private int mNameFrameBorderColor;
	
	private TextView mAlphaValueText, mRedValueText, mGreenValueText, mBlueValueText;
	private SeekBar alphaBar, redBar, greenBar, blueBar;
	private View mPreview;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		Bundle extras = getIntent().getExtras();
		mParent = extras.getString("parentKey");
		if (mParent.equals("changeBackgroundColor")) {
			mBackgroundColor = extras.getInt("backgroundColorKey");
		} else {
			mName = extras.getString("nameKey");
			mNameFrameWidth = extras.getFloat("nameFrameWidthKey");
			mNameFrameHeight = extras.getFloat("nameFrameHeightKey");
			mNameTextColor = extras.getInt("nameTextColorKey");
			mNameFrameInnerColor = extras.getInt("nameFrameInnerColorKey");
			mNameFrameBorderColor = extras.getInt("nameFrameBorderColorKey");
		}
		
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.color_changer, (ViewGroup) findViewById(R.id.root));
		mPreview = layout.findViewById(R.id.preview);
		mAlphaValueText = (TextView) layout.findViewById(R.id.alphaValueText);
		mRedValueText = (TextView) layout.findViewById(R.id.redValueText);
		mGreenValueText = (TextView) layout.findViewById(R.id.greenValueText);
		mBlueValueText = (TextView) layout.findViewById(R.id.blueValueText);
		alphaBar = (SeekBar) layout.findViewById(R.id.alphaBar);
		redBar = (SeekBar) layout.findViewById(R.id.redBar);
		greenBar = (SeekBar) layout.findViewById(R.id.greenBar);
		blueBar = (SeekBar) layout.findViewById(R.id.blueBar);
		
		alphaBar.setOnSeekBarChangeListener(this);
		redBar.setOnSeekBarChangeListener(this);
		greenBar.setOnSeekBarChangeListener(this);
		blueBar.setOnSeekBarChangeListener(this);
		
		int initialColor = 0;
		if (mParent.equals("changeNameColor")) {
			initialColor = Integer.valueOf(mNameTextColor);
		} else if (mParent.equals("changeinnerPaintColor")) {
			initialColor = Integer.valueOf(mNameFrameInnerColor);
		} else if (mParent.equals("changeBorderPaintColor")) {
			initialColor = Integer.valueOf(mNameFrameBorderColor);
		} else if (mParent.equals("changeBackgroundColor")) {
			initialColor = Integer.valueOf(mBackgroundColor);
		}
		alphaBar.setProgress(Color.alpha(initialColor));
		redBar.setProgress(Color.red(initialColor));
		greenBar.setProgress(Color.green(initialColor));
		blueBar.setProgress(Color.blue(initialColor));
		
		if (mParent.equals("changeBackgroundColor") == false) {
			mPreview.setBackgroundDrawable(new NameFramePreview());	
		}
		
		setContentView(layout);
		
	}
	
	@Override
    public void colorChanged(int color) {
        redBar.setProgress(Color.red(color));
        greenBar.setProgress(Color.green(color));
        blueBar.setProgress(Color.blue(color));
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.color_changer_bottom, menu);
	    
	    if (mParent.equals("changeBackgroundColor") == true) {
	    	menu.setGroupVisible(R.id.copy, false);
	    }
	    return true;
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		
        switch(item.getItemId()) {
        
        	case R.id.menu_pick_color:
        		if (mParent.equals("changeNameColor")) {
        			Dialog colorDialog = new ColorPickerDialog(this, 
        					this, Integer.valueOf(mNameTextColor));
        			colorDialog.show();	
        		} else if (mParent.equals("changeinnerPaintColor")) {
        			Dialog colorDialog = new ColorPickerDialog(this, 
        					this, Integer.valueOf(mNameFrameInnerColor));
        			colorDialog.show();	
        		} else if (mParent.equals("changeBorderPaintColor")) {
        			Dialog colorDialog = new ColorPickerDialog(this, 
        					this, Integer.valueOf(mNameFrameBorderColor));
        			colorDialog.show();	
        		} else if (mParent.equals("changeBackgroundColor")) {
        			Dialog colorDialog = new ColorPickerDialog(this, 
        					this, Integer.valueOf(mBackgroundColor));
        			colorDialog.show();	
        		}
        		return true;
        		
        	case R.id.menu_save_color:
        		Bundle bundle = new Bundle();
        		bundle.putBoolean("copyKey", false);
    			bundle.putInt("colorKey", Color.argb(alphaBar.getProgress(), redBar.getProgress(), 
    					greenBar.getProgress(), blueBar.getProgress()));
    			
    			Intent intent = new Intent();
				intent.putExtras(bundle);
				
				setResult(RESULT_OK, intent);
				finish();
        		return true;
        		
        	case R.id.menu_cancel_color:
        		finish();
        		return true;
        		
        	case R.id.menu_copy_color:
        		Bundle copyBundle = new Bundle();
    			copyBundle.putBoolean("copyKey", true);
    			
    			Intent copyIntent = new Intent();
				copyIntent.putExtras(copyBundle);
				
				setResult(RESULT_OK, copyIntent);
        		finish();
        		return true;
        
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
	
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		update();
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {}
	
	private void update() {
		String alphaValue = Float.toString(alphaBar.getProgress());
		mAlphaValueText.setText(alphaValue.substring(0, alphaValue.indexOf('.')));
		String redValue = Float.toString(redBar.getProgress());
		mRedValueText.setText(redValue.substring(0, redValue.indexOf('.')));
		String greenValue = Float.toString(greenBar.getProgress());
		mGreenValueText.setText(greenValue.substring(0, greenValue.indexOf('.')));
		String blueValue = Float.toString(blueBar.getProgress());
		mBlueValueText.setText(blueValue.substring(0, blueValue.indexOf('.')));
		
		if (mParent.equals("changeNameColor")) {
			mNameTextColor = Color.argb(alphaBar.getProgress(), redBar.getProgress(), 
					greenBar.getProgress(), blueBar.getProgress());
		} else if (mParent.equals("changeinnerPaintColor")) {
			mNameFrameInnerColor = Color.argb(alphaBar.getProgress(), redBar.getProgress(), 
					greenBar.getProgress(), blueBar.getProgress());
		} else if (mParent.equals("changeBorderPaintColor")) {
			mNameFrameBorderColor = Color.argb(alphaBar.getProgress(), redBar.getProgress(), 
					greenBar.getProgress(), blueBar.getProgress());
		} else if (mParent.equals("changeBackgroundColor")) {
			mBackgroundColor = Color.argb(alphaBar.getProgress(), redBar.getProgress(), 
					greenBar.getProgress(), blueBar.getProgress());
		}
		
		if (mParent.equals("changeBackgroundColor")) {
			mPreview.setBackgroundColor(mBackgroundColor);
		} else {
			mPreview.invalidate();
		}
	}
	
	public class NameFramePreview extends Drawable {
		
		@Override
		public void draw(Canvas canvas) { //TODO realistic preview
			
			float initialX = canvas.getWidth()/2 - mNameFrameWidth/2;
			float initialY = 5;
			
			RectF nameFrameRect = new RectF();
			nameFrameRect.set(initialX, 
					initialY, 
					mNameFrameWidth + initialX, 
					mNameFrameHeight + initialY);
			
			//if (sound.getShowNameFrameInnerPaint() == true) {
		    	Paint innerPaint = new Paint();
		    	int innerPaintColor = mNameFrameInnerColor;
		    	innerPaint.setARGB(Color.alpha(innerPaintColor), Color.red(innerPaintColor), 
		    			Color.green(innerPaintColor), Color.blue(innerPaintColor));
		    	canvas.drawRoundRect(nameFrameRect, 2, 2, innerPaint);
		    //}
			
			//if (sound.getShowNameFrameBorderPaint()) {
				Paint borderPaint = new Paint();
				int borderPaintColor = mNameFrameBorderColor;
				borderPaint.setARGB(Color.alpha(borderPaintColor), Color.red(borderPaintColor), 
			    		Color.green(borderPaintColor), Color.blue(borderPaintColor));
				borderPaint.setAntiAlias(true);
				borderPaint.setStyle(Style.STROKE);
				borderPaint.setStrokeWidth(2);
				canvas.drawRoundRect(nameFrameRect, 2, 2, borderPaint);
			//}
			
			Paint nameTextPaint = new Paint();
		    int nameTextColor = mNameTextColor;
		    nameTextPaint.setARGB(Color.alpha(nameTextColor), Color.red(nameTextColor), 
					Color.green(nameTextColor), Color.blue(nameTextColor));
		    nameTextPaint.setAntiAlias(true);
		    nameTextPaint.setTextAlign(Align.LEFT);
		    
			if (mName.contains("\n")) {
				int indexCursor = -1;
		    	int lastIndex = -1;
		    	int i = 0;
		    	do {
		    		if (indexCursor == mName.lastIndexOf("\n")) {
		    			indexCursor = mName.length();
		    		} else {
		    			indexCursor = mName.indexOf("\n", indexCursor+1);
		    		}
		    		canvas.drawText(mName.substring(lastIndex+1, indexCursor), 
		    				0+2+initialX, 0+(i+1)*12+initialY, nameTextPaint);
		    		lastIndex = indexCursor;
		    		i++;
		    	} while(indexCursor < mName.length());
		    } else {
		    	canvas.drawText(mName, 0+2+initialX, 0+12+initialY, nameTextPaint);
		    }
		}

		@Override
		public int getOpacity() {
			return 0;
		}

		@Override
		public void setAlpha(int alpha) {
			
		}

		@Override
		public void setColorFilter(ColorFilter cf) {
			
		}
	}

}
