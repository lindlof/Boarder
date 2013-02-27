package fi.mikuz.boarder.util;

import java.io.File;

@SuppressWarnings("serial")
public class WillNotOverrideSoundElementException extends Exception {
	File soundElement;
	
	public WillNotOverrideSoundElementException(File soundElement) {
		this.soundElement = soundElement;
	}
	
	public File getSoundElement() {
		return soundElement;
	}
}
