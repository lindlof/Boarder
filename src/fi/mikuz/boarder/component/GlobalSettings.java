package fi.mikuz.boarder.component;


/**
 * 
 * @author Jan Mikael Lindlöf
 */
public class GlobalSettings {
	
	private int fadeInDuration = 0;
	private int fadeOutDuration = 0;
	private boolean sensitiveLogging = false;
	
	public int getFadeInDuration() {
		return fadeInDuration;
	}
	public void setFadeInDuration(int fadeInDuration) {
		this.fadeInDuration = fadeInDuration;
	}
	public int getFadeOutDuration() {
		return fadeOutDuration;
	}
	public void setFadeOutDuration(int fadeOutDuration) {
		this.fadeOutDuration = fadeOutDuration;
	}
	public boolean getSensitiveLogging() {
		return sensitiveLogging;
	}
	public void setSensitiveLogging(boolean sensitiveLogging) {
		this.sensitiveLogging = sensitiveLogging;
	}
	
}
