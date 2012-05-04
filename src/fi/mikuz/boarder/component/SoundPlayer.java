package fi.mikuz.boarder.component;

import java.io.IOException;

import android.media.MediaPlayer;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
public class SoundPlayer extends MediaPlayer {
	
	private String playingFile;
	private float leftVolume;
	private float rightVolume;
	
	public float getLeftVolume() {
		return leftVolume;
	}

	public float getRightVolume() {
		return rightVolume;
	}

	public String getPlayingFile() {
		return playingFile;
	}
	
	@Override
	public void setDataSource(String path) throws IOException {
		this.playingFile = path;
		super.setDataSource(path);
	}
	
	@Override
	public void setVolume(float leftVolume, float rightVolume) {
		this.leftVolume = leftVolume;
		this.rightVolume = rightVolume;
		super.setVolume(leftVolume, rightVolume);
	}
	
	public void setFadeVolume(float currentLeftVolume, float currentRightVolume) {
		super.setVolume(currentLeftVolume, currentRightVolume);
	}
	
}
