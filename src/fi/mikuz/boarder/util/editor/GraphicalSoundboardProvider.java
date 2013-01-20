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
		
		GraphicalSoundboard startBoardPage = getPage(preferredOrientation, 0);
		if (startBoardPage != null) return startBoardPage;
		
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
		
		GraphicalSoundboard gsb = addBoard(preferredOrientation);
		return gsb;
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
				return gsb;
			}
		}
		
		return null;
	}
	
	public void movePage(int orientation, int fromPageNumber, int toPageNumber) {
		for (GraphicalSoundboard gsb : boardHolder.getBoardList()) {
			int pageNumber = gsb.getPageNumber();
			
			if (gsb.getScreenOrientation() == orientation) {
				if (pageNumber == fromPageNumber) {
					gsb.setPageNumber(toPageNumber);
					boardHolder.overrideBoard(gsb);
				} else if (fromPageNumber > toPageNumber) {
					if (pageNumber >= toPageNumber &&
							pageNumber < fromPageNumber ) {
						gsb.setPageNumber(pageNumber + 1);
						boardHolder.overrideBoard(gsb);
					}
				} else if (fromPageNumber < toPageNumber) {
					if (pageNumber <= toPageNumber &&
							pageNumber > fromPageNumber) {
						gsb.setPageNumber(pageNumber - 1);
						boardHolder.overrideBoard(gsb);
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param current gsb
	 * @return next board page or null
	 */
	public GraphicalSoundboard getNextBoardPage(GraphicalSoundboard lastGsb) {
		int orientation = lastGsb.getScreenOrientation();
		GraphicalSoundboard selectedBoard = null;
		
		selectedBoard = getPage(orientation, lastGsb.getPageNumber() + 1);
		
		if (selectedBoard == null) selectedBoard = getPage(orientation, 0); // Last page, go to first page.
		
		return selectedBoard;
	}
	
	/**
	 * 
	 * @param current gsb
	 * @return next board page or null
	 */
	public GraphicalSoundboard getPreviousPage(GraphicalSoundboard lastGsb) {
		int orientation = lastGsb.getScreenOrientation();
		GraphicalSoundboard selectedBoard = null;
		
		selectedBoard = getPage(orientation, lastGsb.getPageNumber() - 1);
		
		if (selectedBoard == null) selectedBoard = getLastPage(orientation); // First page, go to last page.
		
		return selectedBoard;
	}
	
	private GraphicalSoundboard getLastPage(int preferredOrientation) {
		GraphicalSoundboard selectedBoard = null;
		
		for (GraphicalSoundboard gsb : boardHolder.getBoardList()) {
			if (gsb.getScreenOrientation() == preferredOrientation) {
				if (selectedBoard == null) {
					selectedBoard = gsb;
				} else if (gsb.getPageNumber() > selectedBoard.getPageNumber()) {
					selectedBoard = gsb;
				}
			}
		}
		
		return selectedBoard;
	}
	
	public void deletePage(GraphicalSoundboard deleteGsb) {
		Log.v(TAG, "Going to delete page " + deleteGsb.getPageNumber());
		deleteBoardId(deleteGsb.getId());
		
		Log.v(TAG, "Reducing following page numbers.");
		for (GraphicalSoundboard gsb : boardHolder.getBoardList()) {
			if (gsb.getScreenOrientation() == deleteGsb.getScreenOrientation() && 
					gsb.getPageNumber() > deleteGsb.getPageNumber()) {
				gsb.setPageNumber(gsb.getPageNumber() - 1);
				boardHolder.overrideBoard(gsb);
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
	
}
