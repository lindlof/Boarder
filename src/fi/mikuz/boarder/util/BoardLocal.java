package fi.mikuz.boarder.util;

import java.io.File;
import java.io.IOException;

import android.util.Log;
import fi.mikuz.boarder.component.soundboard.GraphicalSound;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundboard;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundboardHolder;
import fi.mikuz.boarder.gui.SoundboardMenu;
import fi.mikuz.boarder.util.dbadapter.BoardsDbAdapter;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
public class BoardLocal {
	public static final String TAG = "BoardLocal";

	public static int testIfBoardIsLocal(String boardName) throws IOException {
		
		File boardDir = new File(SoundboardMenu.mSbDir, boardName);
		int soundlistSizes = 0;
		
		try {
			// No failsafes, return red if error occurs
			GraphicalSoundboardHolder boardHolder = FileProcessor.loadGraphicalSoundboardHolder(boardName);
			
			for (GraphicalSoundboard gsb : boardHolder.getBoardList()) {
				
				if (gsb.getBackgroundImagePath() != null) {
					if (!gsb.getBackgroundImagePath().exists()) return BoardsDbAdapter.LOCAL_ORANGE;
					else if (!gsb.getBackgroundImagePath().getAbsolutePath().contains(boardDir.getAbsolutePath())) return BoardsDbAdapter.LOCAL_YELLOW;
				}
				
				soundlistSizes = soundlistSizes + gsb.getSoundList().size();
				
				for (GraphicalSound sound : gsb.getSoundList()) {
					if (sound.getPath() != null) {
						if (isFunctionSound(sound));
						else if (!sound.getPath().exists()) return BoardsDbAdapter.LOCAL_ORANGE;
						else if (!sound.getPath().getAbsolutePath().contains(boardDir.getAbsolutePath())) return BoardsDbAdapter.LOCAL_YELLOW;
					} else {
						return BoardsDbAdapter.LOCAL_RED;
					}
					if (sound.getImagePath() != null) {
						if (!sound.getImagePath().exists()) return BoardsDbAdapter.LOCAL_ORANGE;
						else if (!sound.getImagePath().getAbsolutePath().contains(boardDir.getAbsolutePath())) return BoardsDbAdapter.LOCAL_YELLOW;
					}
					if (sound.getActiveImagePath() != null) {
						if (!sound.getActiveImagePath().exists()) return BoardsDbAdapter.LOCAL_ORANGE;
						else if (!sound.getActiveImagePath().getAbsolutePath().contains(boardDir.getAbsolutePath())) return BoardsDbAdapter.LOCAL_YELLOW;
					}
				}
				
			}
			
			if (soundlistSizes < 1) return BoardsDbAdapter.LOCAL_WHITE;
			return BoardsDbAdapter.LOCAL_GREEN;
		} catch (Exception e) {
			// Do not crash here, just return red
			Log.w(TAG, "Can't get board color for " + boardName + "\nError: " + e.getMessage());
			return BoardsDbAdapter.LOCAL_RED;
		}
    }
	
	static boolean isFunctionSound(GraphicalSound sound) {
		for (String functionSound : SoundboardMenu.mFunctionSounds) {
			if (functionSound.equals(sound.getPath().getAbsolutePath())) return true;
		}
		return false;
	}
	
}
