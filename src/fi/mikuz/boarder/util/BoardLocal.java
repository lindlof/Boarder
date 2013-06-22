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

import android.util.Log;
import fi.mikuz.boarder.component.soundboard.GraphicalSound;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundboard;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundboardHolder;
import fi.mikuz.boarder.gui.SoundboardMenu;
import fi.mikuz.boarder.util.dbadapter.MenuDbAdapter;

public class BoardLocal {
	public static final String TAG = "BoardLocal";

	public static int testIfBoardIsLocal(String boardName) throws IOException {
		
		File boardDir = new File(SoundboardMenu.mSbDir, boardName);
		int soundlistSizes = 0;
		
		try {
			// No failsafes, return red if error occurs
			GraphicalSoundboardHolder boardHolder = FileProcessor.loadGraphicalSoundboardHolder(boardName, false);
			
			for (GraphicalSoundboard gsb : boardHolder.getBoardList()) {
				
				if (gsb.getBackgroundImagePath() != null) {
					if (!gsb.getBackgroundImagePath().exists()) return MenuDbAdapter.LOCAL_ORANGE;
					else if (!gsb.getBackgroundImagePath().getAbsolutePath().contains(boardDir.getAbsolutePath())) return MenuDbAdapter.LOCAL_YELLOW;
				}
				
				soundlistSizes = soundlistSizes + gsb.getSoundList().size();
				
				for (GraphicalSound sound : gsb.getSoundList()) {
					if (sound.getPath() != null) {
						if (isFunctionSound(sound));
						else if (!sound.getPath().exists()) return MenuDbAdapter.LOCAL_ORANGE;
						else if (!sound.getPath().getAbsolutePath().contains(boardDir.getAbsolutePath())) return MenuDbAdapter.LOCAL_YELLOW;
					} else {
						return MenuDbAdapter.LOCAL_RED;
					}
					if (sound.getImagePath() != null) {
						if (!sound.getImagePath().exists()) return MenuDbAdapter.LOCAL_ORANGE;
						else if (!sound.getImagePath().getAbsolutePath().contains(boardDir.getAbsolutePath())) return MenuDbAdapter.LOCAL_YELLOW;
					}
					if (sound.getActiveImagePath() != null) {
						if (!sound.getActiveImagePath().exists()) return MenuDbAdapter.LOCAL_ORANGE;
						else if (!sound.getActiveImagePath().getAbsolutePath().contains(boardDir.getAbsolutePath())) return MenuDbAdapter.LOCAL_YELLOW;
					}
				}
				
			}
			
			if (soundlistSizes < 1) return MenuDbAdapter.LOCAL_WHITE;
			return MenuDbAdapter.LOCAL_GREEN;
		} catch (Exception e) {
			// Do not crash here, just return red
			Log.w(TAG, "Board \"" + boardName + "\" is broken. Try restore guide in the wiki.");
			return MenuDbAdapter.LOCAL_RED;
		}
    }
	
	static boolean isFunctionSound(GraphicalSound sound) {
		for (String functionSound : SoundboardMenu.mFunctionSounds) {
			if (functionSound.equals(sound.getPath().getAbsolutePath())) return true;
		}
		return false;
	}
	
}
