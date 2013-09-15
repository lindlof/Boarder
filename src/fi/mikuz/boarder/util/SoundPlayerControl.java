/* ========================================================================= *
 * Boarder                                                                   *
 * http://boarder.mikuz.org/                                                 *
 * ========================================================================= *
 * Copyright (C) 2013 Boarder                                                *
 *                                                                           *
 * Licensed under the Apache License, Version 2.0 (the "License");           *
 * you may not use this file except in compliance with the License.          *
 * You may obtain a copy of the License at                                   *
 *                                                                           *
 *     http://www.apache.org/licenses/LICENSE-2.0                            *
 *                                                                           *
 * Unless required by applicable law or agreed to in writing, software       *
 * distributed under the License is distributed on an "AS IS" BASIS,         *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 * See the License for the specific language governing permissions and       *
 * limitations under the License.                                            *
 * ========================================================================= */

package fi.mikuz.boarder.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import fi.mikuz.boarder.component.SoundPlayer;
import fi.mikuz.boarder.gui.SoundboardMenu;

public abstract class SoundPlayerControl {
	private static final String TAG = SoundPlayerControl.class.getSimpleName();
	
	private static FadeThread fadeThread; // Null if not running. Only run one at once, change action using fadingIn.
	
	private enum FadeState {INACTIVE, FADING_IN, FADING_OUT}
	private static FadeState fadeState = FadeState.INACTIVE;
	
	/**
	 * Fade in or out in ten steps.
	 */
	private static class FadeThread extends Thread {
		
		public void run() {
			
			float volumeMultiplier;
			if (fadeState == FadeState.FADING_IN) volumeMultiplier = 0.1f;
			else if (fadeState == FadeState.FADING_OUT) volumeMultiplier = 1f;
			else throw new IllegalArgumentException("Illegal fade state " + fadeState.name());
			
			long operationStartTime;
			
			if (fadeState == FadeState.FADING_IN) {
				operationStartTime = System.currentTimeMillis();
				for (SoundPlayer soundPlayer : SoundboardMenu.mSoundPlayerList) {
					soundPlayer.setFadeVolume(soundPlayer.getLeftVolume()*volumeMultiplier, 
							soundPlayer.getRightVolume()*volumeMultiplier);
					soundPlayer.start();
				}
				sleepTenthOfFadedDuration(operationStartTime);
			}

			while((volumeMultiplier > 0.1f && fadeState == FadeState.FADING_OUT) || (volumeMultiplier < 1f && fadeState == FadeState.FADING_IN)) {
				operationStartTime = System.currentTimeMillis();
				
				if (fadeState == FadeState.FADING_IN) volumeMultiplier += 0.1f;
				else if (fadeState == FadeState.FADING_OUT) volumeMultiplier -= 0.1f;
				else throw new IllegalArgumentException("Illegal fade state " + fadeState.name());
				
				// If fade duration is zero then just set volumes and break.
				boolean die = false;
				if (fadeState == FadeState.FADING_IN && GlobalSettings.getFadeInDuration() == 0f) {
					volumeMultiplier = 1f;
					die = true;
				} else if (fadeState == FadeState.FADING_OUT && GlobalSettings.getFadeOutDuration() == 0f) {
					volumeMultiplier = 1f;
					die = true;
				}
				
				for (SoundPlayer soundPlayer : SoundboardMenu.mSoundPlayerList) {
					soundPlayer.setFadeVolume(soundPlayer.getLeftVolume()*volumeMultiplier, 
							soundPlayer.getRightVolume()*volumeMultiplier);
				}
				
				if (die) break;
				
				sleepTenthOfFadedDuration(operationStartTime);
			}
			
			if (fadeState == FadeState.FADING_OUT) {
				for (SoundPlayer soundPlayer : SoundboardMenu.mSoundPlayerList) {
					soundPlayer.pause();
				}
			}
			
			fadeThread = null;
			fadeState = FadeState.INACTIVE;
		}
		
		/**
		 * Accurate enough.
		 * 
		 * @param operationStartTime
		 */
		private void sleepTenthOfFadedDuration(long operationStartTime) {
			long realSleepTime = 0;
			if (fadeState == FadeState.FADING_IN) {
				realSleepTime = GlobalSettings.getFadeInDuration()/10 - (System.currentTimeMillis() - operationStartTime);
			} else if (fadeState == FadeState.FADING_OUT) {
				realSleepTime = GlobalSettings.getFadeOutDuration()/10 - (System.currentTimeMillis() - operationStartTime);
			}
			
			try {
				long sleepTime = realSleepTime > 0 ? realSleepTime : 0;
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				Log.e(TAG, "Thread failed to sleep for fading effect");
			}
		}
	}
	
	public static void playSound(boolean playSimultaneously, boolean loopIndefinitely, File soundPath, 
			Float volumeLeft, Float volumeRight, Float boardVolume) {
		
		SoundPlayer soundPlayer = new SoundPlayer();
		try {
			soundPlayer.setDataSource(soundPath.getAbsolutePath());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		SoundboardMenu.mSoundPlayerList.add(soundPlayer);
		
		if (playSimultaneously == false) {
    		ListIterator<SoundPlayer> iterator = SoundboardMenu.mSoundPlayerList.listIterator();
			while (iterator.hasNext()) {
				SoundPlayer iteratedPlayer = iterator.next();
				if (iteratedPlayer != soundPlayer) {
					iterator.remove();
					iteratedPlayer.release();
				}
			}
        }
		
		boolean playerPrepared = false;
        try {
        	soundPlayer.prepare();
        	playerPrepared = true;
		} catch (IOException e) {
			Log.e(TAG, "Unable to play sound", e);
		} catch (IllegalStateException e) {
			Log.e(TAG, "Unable to play sound", e);
		}
        
        if (playerPrepared) {
        	soundPlayer.setVolume(volumeLeft*boardVolume, volumeRight*boardVolume);
        	soundPlayer.setLooping(loopIndefinitely);
        	soundPlayer.start();
    		
    		ListIterator<SoundPlayer> iterator = SoundboardMenu.mSoundPlayerList.listIterator();
            
    		while (iterator.hasNext()) {
    			SoundPlayer iteratedPlayer = iterator.next();
    			if (iteratedPlayer.isPlaying() == false) {
    				iterator.remove();
    				iteratedPlayer.release();
    	    	}
    		}
        }
        
        refreshDrawing();
	}

	public static void togglePlayPause(Context context) {
		
		final List<SoundPlayer> soundPlayerList = SoundboardMenu.mSoundPlayerList;
    	boolean playing = false;
    	
    	if (soundPlayerList.size() == 0) {
    		Log.w(TAG, "Nothing to play");
    		Toast.makeText(context, "Nothing to play", Toast.LENGTH_SHORT).show();
    	}
		
		for (SoundPlayer soundPlayer : soundPlayerList) {
			if (soundPlayer.isPlaying()) {
				playing = true;
				break;
	    	}
		}
		
		// Remove all idling sounds if we are toggling to pause
		if (playing == true) {
    		ListIterator<SoundPlayer> iterator = SoundboardMenu.mSoundPlayerList.listIterator();
			while (iterator.hasNext()) {
				SoundPlayer iteratedPlayer = iterator.next();
				if (!iteratedPlayer.isPlaying()) {
					iterator.remove();
					iteratedPlayer.release();
				}
			}
        }
		
		if (playing == true && (fadeState == FadeState.FADING_IN || fadeState == FadeState.INACTIVE)) {
			fadeState = FadeState.FADING_OUT;
		} else {
			fadeState = FadeState.FADING_IN;
		}
		
		if (fadeThread == null) {
			fadeThread = new FadeThread();
			fadeThread.start();
		}
		
		refreshDrawing();
    }
	
	public static boolean isPlaying(File soundPath) {
		ListIterator<SoundPlayer> iterator = SoundboardMenu.mSoundPlayerList.listIterator();
    	boolean playing = false;
        
		while (iterator.hasNext()) {
			SoundPlayer soundPlayer = iterator.next();
			if (soundPlayer.isPlaying() && soundPlayer.getPlayingFile().equals(soundPath.getAbsolutePath())) {
				playing = true;
				break;
	    	}
		}
		return playing;
	}

	public static void pauseSound(boolean playSimultaneously, boolean loopIndefinitely, File soundPath, Float volumeLeft, Float volumeRight, 
			Float boardVolume) {

		ListIterator<SoundPlayer> iterator = SoundboardMenu.mSoundPlayerList.listIterator();
		boolean soundFound = false;
		while (iterator.hasNext()) {
			SoundPlayer soundPlayer = iterator.next();
			if (soundPlayer.getPlayingFile().equals(soundPath.getAbsolutePath())) {
				if (soundPlayer.isPlaying() == true) {
					soundPlayer.pause();
				} else {
					soundPlayer.setFadeVolume(soundPlayer.getLeftVolume(), soundPlayer.getRightVolume());
					soundPlayer.setLooping(loopIndefinitely);
					soundPlayer.start();
				}
				soundFound = true;
				break;
			}
		}
		if (!soundFound) {
			playSound(playSimultaneously, loopIndefinitely, soundPath, volumeLeft, volumeRight, boardVolume);
		}
		
		refreshDrawing();
	}
	
	public static void stopSound(boolean playSimultaneously, boolean loopIndefinitely, File soundPath, Float volumeLeft, Float volumeRight, 
			Float boardVolume) {
		
		ListIterator<SoundPlayer> iterator = SoundboardMenu.mSoundPlayerList.listIterator();
		boolean soundFound = false;
		while (iterator.hasNext()) {
			SoundPlayer soundPlayer = iterator.next();
			if (soundPlayer.getPlayingFile() == soundPath.getAbsolutePath()) {
				if (soundPlayer.isPlaying() == true) {
					soundPlayer.release();
					iterator.remove();
				} else {
					soundPlayer.setFadeVolume(soundPlayer.getLeftVolume(), soundPlayer.getRightVolume());
					soundPlayer.setLooping(loopIndefinitely);
					soundPlayer.start();
				}
				soundFound = true;
				break;
			}
		}
		if (!soundFound) {
			playSound(playSimultaneously, loopIndefinitely, soundPath, volumeLeft, volumeRight, boardVolume);
		}
		
		refreshDrawing();
	}
	
	private static void refreshDrawing() {
		Thread drawing = SoundboardMenu.mDrawingThread;
        if (drawing != null) drawing.interrupt();
	}
	
	public static SoundPlayer getSoundPlayer(File soundPath) {
		SoundPlayer soundPlayer = null;
		ListIterator<SoundPlayer> iterator = SoundboardMenu.mSoundPlayerList.listIterator();
		while (iterator.hasNext()) {
			SoundPlayer iteratorSoundPlayer = iterator.next();
			if (iteratorSoundPlayer.getPlayingFile() == soundPath.getAbsolutePath()) {
				soundPlayer = iteratorSoundPlayer;
	    	}
		}
		return soundPlayer;
	}
}
