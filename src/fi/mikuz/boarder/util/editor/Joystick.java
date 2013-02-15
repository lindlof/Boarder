package fi.mikuz.boarder.util.editor;

import fi.mikuz.boarder.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.MotionEvent;

public class Joystick {
	
	private float joystickX = 0;
	private float joystickY = 0;
	private float joystickDistanceX = 0;
	private float joystickDistanceY = 0;
	
	private float joystickSide;
	private float joystickReferenceDistance;
	
	private RectF joystickImageRect;
	private Bitmap joystickImage;

	public Joystick(Context context, MotionEvent event) {
		joystickSide = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics());
        joystickReferenceDistance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
        
        joystickImageRect = new RectF();
        joystickImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.joystick);
        
        joystickX = event.getX();
		joystickY = event.getY();
		joystickDistanceX = 0;
		joystickDistanceY = 0;
		
		joystickImageRect.set(joystickX - joystickSide/2, 
				joystickY - joystickSide/2, 
				joystickX + joystickSide/2, 
				joystickY + joystickSide/2);
	}
	
	/**
	 * Calculates new distance from initial joystick postition.
	 * 
	 * @param eventX
	 * @return distanceX
	 */
	public float dragDistanceX(float eventX) {
		float moveX;
		float halfJoystickSide = joystickSide/2;

		if (Math.abs(eventX - joystickX) < halfJoystickSide) {
			moveX = 0;
		} else {
			float joystickDistanceCancel = (eventX - joystickX < 0) ? halfJoystickSide*(-1) : halfJoystickSide;
			moveX = (eventX - joystickX - joystickDistanceCancel)/joystickReferenceDistance;
		}

		joystickDistanceX = joystickDistanceX + moveX;
		float dragDistance = joystickX + joystickDistanceX;

		return dragDistance;
	}
	
	/**
	 * Calculates new distance from initial joystick postition.
	 * 
	 * @param eventY
	 * @return distanceY
	 */
	public float dragDistanceY(float eventY) {
		float moveY;
		float halfJoystickSide = joystickSide/2;

		if (Math.abs(eventY - joystickY) < halfJoystickSide) {
			moveY = 0;
		} else {
			float joystickDistanceCancel = (eventY - joystickY < 0) ? halfJoystickSide*(-1) : halfJoystickSide;
			moveY = (eventY - joystickY - joystickDistanceCancel)/joystickReferenceDistance;
		}
		
		joystickDistanceY = joystickDistanceY + moveY;
		float dragDistance = joystickY + joystickDistanceY;

		return dragDistance;
	}

	public Bitmap getJoystickImage() {
		return joystickImage;
	}

	public RectF getJoystickImageRect() {
		return joystickImageRect;
	}
}
