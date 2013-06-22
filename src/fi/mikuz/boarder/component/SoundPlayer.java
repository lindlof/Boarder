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

package fi.mikuz.boarder.component;

import java.io.IOException;

import android.media.MediaPlayer;

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
