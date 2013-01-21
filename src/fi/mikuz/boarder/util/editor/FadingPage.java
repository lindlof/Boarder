package fi.mikuz.boarder.util.editor;

import android.graphics.Bitmap;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundboard;

public class FadingPage {
	
	public enum FadeState {FADING_IN, FADING_OUT}
	private FadeState fadeState;
	
	/**
	 * 0: Faded out, invisible
	 * 100: Faded in, fully visible
	 */
	private int fadeProgress;
	private GraphicalSoundboard gsb;
	private Bitmap drawCache;
	
	public FadingPage(GraphicalSoundboard gsb, FadeState fadeState) {
		this.setGsb(gsb);
		this.setFadeState(fadeState);
		if (fadeState == FadeState.FADING_IN) {
			this.fadeProgress = 0;
		} else if (fadeState == FadeState.FADING_OUT) {
			this.fadeProgress = 100;
		}
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
			this.fadeProgress = this.fadeProgress + 8;
		} else if (fadeState == FadeState.FADING_OUT) {
			this.fadeProgress = this.fadeProgress - 13;
		}
	}

	public Bitmap getDrawCache() {
		return drawCache;
	}

	public void setDrawCache(Bitmap drawCache) {
		this.drawCache = drawCache;
	}
}
