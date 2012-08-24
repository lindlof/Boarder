package fi.mikuz.boarder.util.editor;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

import android.util.Log;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundboard;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundboardHolder;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundboardHolder.OrientationMode;
import fi.mikuz.boarder.util.FileProcessor;

/**
 * Provides the soundboard editor a relevant soundboard from soundboard holder.
 * 
 * @author Jan Mikael Lindlöf
 */
public class GraphicalSoundboardProvider {
	public static final String TAG = GraphicalSoundboardProvider.class.getSimpleName();
	
	GraphicalSoundboardHolder boardHolder;
	
	public GraphicalSoundboardProvider(String boardName) {
		try {
			boardHolder = FileProcessor.loadGraphicalSoundboardHolder(boardName);
		} catch (IOException e) {
			Log.w(TAG, "Error importing board", e);
			boardHolder = new GraphicalSoundboardHolder();
		}
	}
	
	public boolean orientationChangeAllowed() {
		if (boardHolder.getOrientationMode() == GraphicalSoundboardHolder.OrientationMode.ORIENTATION_MODE_HYBRID) {
			return true;
		} else {
			return false;
		}
	}
	
	public GraphicalSoundboardHolder.OrientationMode getOrientationMode() {
		return boardHolder.getOrientationMode();
	}
	
	public void setOrientationMode(GraphicalSoundboardHolder.OrientationMode orientationMode) {
		boardHolder.setOrientationMode(orientationMode);
	}
	
	public void setOrientationMode(int screenOrientation) {
		GraphicalSoundboardHolder.OrientationMode orientationMode = screenOrientationToOrientationMode(screenOrientation);
		setOrientationMode(orientationMode);
	}
	
	public GraphicalSoundboard getBoardForRotation(int preferredRotation) {
		
		int preferredOrientation = EditorOrientation.convertRotation(preferredRotation);
		
		return getBoard(preferredOrientation);
	}
	
	public GraphicalSoundboard getBoard(int preferredOrientation) {
		
		if (boardHolder.getOrientationMode() == GraphicalSoundboardHolder.OrientationMode.ORIENTATION_MODE_PORTRAIT) {
			preferredOrientation = GraphicalSoundboard.SCREEN_ORIENTATION_PORTRAIT;
		} else if (boardHolder.getOrientationMode() == GraphicalSoundboardHolder.OrientationMode.ORIENTATION_MODE_LANDSCAPE) {
			preferredOrientation = GraphicalSoundboard.SCREEN_ORIENTATION_LANDSCAPE;
		}
		
		for (GraphicalSoundboard board : boardHolder.getBoardList()) {
			if (board.getScreenOrientation() == preferredOrientation) return board;
		}
		
		if (boardHolder.getOrientationMode() == GraphicalSoundboardHolder.OrientationMode.ORIENTATION_MODE_PORTRAIT) {
			Log.v(TAG, "No board in preferred orientation. Single orientation mode. Giving any board.");
			for (GraphicalSoundboard board : boardHolder.getBoardList()) {
				OrientationMode orientationMode = screenOrientationToOrientationMode(board.getScreenOrientation());
				boardHolder.setOrientationMode(orientationMode);
				return board;
			}
			Log.v(TAG, "No boards found. Giving a new board.");
		} else if (boardHolder.getOrientationMode() == GraphicalSoundboardHolder.OrientationMode.ORIENTATION_MODE_HYBRID) {
			Log.v(TAG, "No board in preferred orientation. Hybrid orientation mode. Giving a new board.");
		}
		
		GraphicalSoundboard gsbTemplate = new GraphicalSoundboard(preferredOrientation);
		GraphicalSoundboard gsb = boardHolder.allocateBoardId(gsbTemplate);
		return gsb;
	}
	
	private GraphicalSoundboardHolder.OrientationMode screenOrientationToOrientationMode(int screenOrientation) {
		if (screenOrientation == GraphicalSoundboard.SCREEN_ORIENTATION_PORTRAIT) {
			return GraphicalSoundboardHolder.OrientationMode.ORIENTATION_MODE_PORTRAIT;
		} else if (screenOrientation == GraphicalSoundboard.SCREEN_ORIENTATION_LANDSCAPE) {
			return GraphicalSoundboardHolder.OrientationMode.ORIENTATION_MODE_LANDSCAPE;
		}
		return null;
	}
	
	public void saveBoard(String boardName, GraphicalSoundboard tempGsb) throws IOException {
		overrideBoard(tempGsb);
		FileProcessor.saveGraphicalSoundboardHolder(boardName, boardHolder);
	}
	
	public void overrideBoard(GraphicalSoundboard tempGsb) {
 		GraphicalSoundboard gsb = GraphicalSoundboard.copy(tempGsb);
		GraphicalSoundboard.unloadImages(gsb);
		
		boardHolder.overrideBoard(gsb);
	}
	
	public boolean boardWithOrientationExists(final int screenOrientation) {
		for (GraphicalSoundboard gsb : boardHolder.getBoardList()) {
			if (gsb.getScreenOrientation() == screenOrientation) return true;
		}
		return false;
	}
	
	public void deleteBoardWithOrientation(int orientation) {
		List<GraphicalSoundboard> gsbList = boardHolder.getBoardList();
		ListIterator<GraphicalSoundboard> iterator = gsbList.listIterator();
		
		while (iterator.hasNext()) {
			GraphicalSoundboard gsb = iterator.next();
			if (gsb.getScreenOrientation() == orientation) {
				Log.v(TAG, "Deleting board id " + gsb.getId() + " since it's orientation is " + orientation);
				iterator.remove();
			}
		}
	}
	
}
