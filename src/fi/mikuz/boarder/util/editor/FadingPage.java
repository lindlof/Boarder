package fi.mikuz.boarder.util.editor;

import android.graphics.Bitmap;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundboard;

public class FadingPage {
	
	public enum FadeState {FADING_IN, FADING_OUT}
	private FadeState fadeState;
	
	public enum FadeDirection {NO_DIRECTION, LEFT, RIGHT}
	private FadeDirection fadeDirection;
	
	/**
	 * 0: Faded out, invisible
	 * 100: Faded in, fully visible
	 */
	private int fadeProgress;
	private GraphicalSoundboard gsb;
	private Bitmap drawCache;
	
	private boolean fadingOutWhenFinished;
	
	public FadingPage(GraphicalSoundboard gsb, FadeState fadeState, FadeDirection fadeDirection) {
		this.setGsb(gsb);
		this.setFadeState(fadeState);
		if (fadeState == FadeState.FADING_IN) {
			this.fadeProgress = 0;
		} else if (fadeState == FadeState.FADING_OUT) {
			this.fadeProgress = 100;
		}
		this.fadeDirection = fadeDirection;
		drawCache = null;
	}

	public GraphicalSoundboard getGsb() {
		return gsb;
	}

	public void setGsb(GraphicalSoundboard gsb) {
		this.gsb = gsb;
	}

	public FadeState getFadeState() {
		return fadeState;
	}

	public void setFadeState(FadeState fadeState) {
		this.fadeState = fadeState;
	}

	public int getFadeProgress() {
		return fadeProgress;
	}

	public void updateFadeProgress() {
		if (fadeState == FadeState.FADING_IN) {
			this.fadeProgress = this.fadeProgress + 11;
			
			if (this.fadeProgress >= 100) {
				this.fadeProgress = 100;
				
				if (this.fadingOutWhenFinished) {
					fadeState = FadeState.FADING_OUT;
					updateFadeProgress();
				}
			}
		} else if (fadeState == FadeState.FADING_OUT) {
			this.fadeProgress = this.fadeProgress - 15;
			
			if (this.fadeProgress <= 0) {
				this.fadeProgress = 0;
			}
		}
	}

	public Bitmap getDrawCache() {
		return drawCache;
	}

	public void setDrawCache(Bitmap drawCache) {
		this.drawCache = drawCache;
	}
	
	public void setFadeDirection(FadeDirection fadeDirection) {
		this.fadeDirection = fadeDirection;
	}

	public FadeDirection getFadeDirection() {
		return fadeDirection;
	}

	public void fadeOutWhenFinished() {
		this.fadingOutWhenFinished = true;
	}
}
