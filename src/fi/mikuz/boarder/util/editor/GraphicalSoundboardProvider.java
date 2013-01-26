package fi.mikuz.boarder.util.editor;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

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
	public static final String TAG = GraphicalSoundboardProvider.class.getSimpleName();
	
	GraphicalSoundboardHolder boardHolder;
	
	public GraphicalSoundboardProvider(String boardName) {
		try {
			boardHolder = FileProcessor.loadGraphicalSoundboardHolder(boardName);
		} catch (IOException e) {
			Log.w(TAG, "Unable to load board holder", e);
			boardHolder = new GraphicalSoundboardHolder();
			GraphicalSoundboard initialGsb = new GraphicalSoundboard();
			boardHolder.allocateBoardResources(initialGsb);
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
	
	public GraphicalSoundboard addBoardPage(int preferredOrientation) {
		return addBoard(preferredOrientation);
	}
	
	private GraphicalSoundboard addBoard(int preferredOrientation) {
		GraphicalSoundboard gsbTemplate = new GraphicalSoundboard(preferredOrientation);
		GraphicalSoundboard gsb = boardHolder.allocateBoardResources(gsbTemplate);
		return gsb;
	}
	
	public GraphicalSoundboard getPage(int orientation, int pageNumber) {
		
		for (GraphicalSoundboard gsb : boardHolder.getBoardList()) {
			if (gsb.getScreenOrientation() == orientation &&
					gsb.getPageNumber() == pageNumber) {
				return GraphicalSoundboard.copy(gsb);
			}
		}
		
		return null;
	}
	
	public void deletePage(GraphicalSoundboard deleteGsb) {
		Log.v(TAG, "Going to delete page " + deleteGsb.getPageNumber());
		deleteBoardId(deleteGsb.getId());
		
		Log.v(TAG, "Reducing following page numbers.");
		for (GraphicalSoundboard gsb : boardHolder.getBoardList()) {
			if (gsb.getScreenOrientation() == deleteGsb.getScreenOrientation() && 
					gsb.getPageNumber() > deleteGsb.getPageNumber()) {
				gsb.setPageNumber(gsb.getPageNumber() - 1);
				overrideBoard(gsb);
			}
		}
	}
	
	private GraphicalSoundboardHolder.OrientationMode screenOrientationToOrientationMode(int screenOrientation) {
		if (screenOrientation == GraphicalSoundboard.SCREEN_ORIENTATION_PORTRAIT) {
			return GraphicalSoundboardHolder.OrientationMode.ORIENTATION_MODE_PORTRAIT;
		} else if (screenOrientation == GraphicalSoundboard.SCREEN_ORIENTATION_LANDSCAPE) {
			return GraphicalSoundboardHolder.OrientationMode.ORIENTATION_MODE_LANDSCAPE;
		}
		return null;
	}
	
	public void saveBoard(String boardName) throws IOException {
		// Since file paths may be altered while saving we need a separate cop for saving
		GraphicalSoundboardHolder savedHolder = GraphicalSoundboardHolder.copy(boardHolder);
		FileProcessor.saveGraphicalSoundboardHolder(boardName, savedHolder);
	}
	
	public void overrideBoard(GraphicalSoundboard tempGsb) {
 		GraphicalSoundboard gsb = GraphicalSoundboard.copy(tempGsb);
		GraphicalSoundboard.unloadImages(gsb);
		
		List<GraphicalSoundboard> boardList = boardHolder.getBoardList();
		for (int i = 0; i < boardList.size(); i++) {
			GraphicalSoundboard existingGsb = boardList.get(i);
			if (gsb.getId() == existingGsb.getId()) {
				boardList.set(i, gsb);
				break;
			}
		}
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
				Log.v(TAG, "Deleting board id " + gsb.getId() + " since its orientation is " + orientation);
				iterator.remove();
			}
		}
	}
	
	public void deleteBoardId(int boardId) {
		List<GraphicalSoundboard> gsbList = boardHolder.getBoardList();
		ListIterator<GraphicalSoundboard> iterator = gsbList.listIterator();
		
		while (iterator.hasNext()) {
			GraphicalSoundboard gsb = iterator.next();
			if (gsb.getId() == boardId) {
				Log.v(TAG, "Deleting board id " + gsb.getId());
				iterator.remove();
				break;
			}
		}
	}
	
	public boolean isPaginationSynchronizedBetweenOrientations() {
		return boardHolder.isPaginationSynchronizedBetweenOrientations();
	}

	public void setPaginationSynchronizedBetweenOrientations(boolean paginationSynchronizedBetweenOrientations) {
		boardHolder.setPaginationSynchronizedBetweenOrientations(paginationSynchronizedBetweenOrientations);
	}
	
}
