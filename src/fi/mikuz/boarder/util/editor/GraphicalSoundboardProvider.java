package fi.mikuz.boarder.util.editor;

import java.io.IOException;

import android.util.Log;

import fi.mikuz.boarder.component.soundboard.GraphicalSoundboard;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundboardHolder;
import fi.mikuz.boarder.util.FileProcessor;

/**
 * Provides the soundboard editor a relevant soundboard from soundboard holder.
 * 
 * @author Jan Mikael Lindlöf
 */
public class GraphicalSoundboardProvider {
	public static final String TAG = "GraphicalSoundboardLoader";
	
	public static GraphicalSoundboard getBoard(String boardName, int preferredOrientation) {
		
		try {
			GraphicalSoundboardHolder boardHolder = FileProcessor.loadGraphicalSoundboardHolder(boardName);
			for (GraphicalSoundboard board : boardHolder.getBoardList()) {
				if (board.getScreenOrientation() == preferredOrientation) return board;
			}
			Log.v(TAG, "No board in preferred orientation, give something.");
			for (GraphicalSoundboard board : boardHolder.getBoardList()) {
				return board;
			}
		} catch (IOException e) {
			Log.e(TAG, "Error importing board", e);
			return newGraphicalSoundboard(boardName);
		}
		return null;
	}
	
	public static void saveBoard(String boardName, GraphicalSoundboard tempGsb) throws IOException {
		
		// Future functionality
		/*
		try {
			boardHolder = FileProcessor.loadGraphicalSoundboardHolder(boardName);
		} catch (IOException e) {
			Log.e(TAG, "Error loading board holder, emergency overriding it!", e);
			boardHolder = new GraphicalSoundboardHolder();
		}
		*/
		
		
		GraphicalSoundboard gsb = GraphicalSoundboard.copy(tempGsb);
		GraphicalSoundboard.unloadImages(gsb);
		
		GraphicalSoundboardHolder boardHolder = new GraphicalSoundboardHolder();
		boardHolder.addBoard(gsb);
		
		FileProcessor.saveGraphicalSoundboardHolder(boardName, boardHolder);
	}
	
	public static GraphicalSoundboard newGraphicalSoundboard(String boardName) {
		int highestId = -1;
		try {
			GraphicalSoundboardHolder boardHolder = FileProcessor.loadGraphicalSoundboardHolder(boardName);
			for (GraphicalSoundboard board : boardHolder.getBoardList()) {
				highestId = (board.getId() > highestId) ? board.getId() : highestId;
			}
		} catch (IOException e) {
			Log.e(TAG, "No boards found", e);
		}
		
		GraphicalSoundboard gsb = new GraphicalSoundboard();
		gsb.setId(highestId+1);
		return gsb;
	}
}
