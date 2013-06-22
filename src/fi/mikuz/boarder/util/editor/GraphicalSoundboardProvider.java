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

package fi.mikuz.boarder.util.editor;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

import android.content.Context;
import android.util.Log;
import fi.mikuz.boarder.component.soundboard.GraphicalSound;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundboard;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundboardHolder;
import fi.mikuz.boarder.util.FileProcessor;

/**
 * Provides the soundboard editor a relevant soundboard from soundboard holder.
 */
public class GraphicalSoundboardProvider {
	public static final String TAG = GraphicalSoundboardProvider.class.getSimpleName();
	
	public enum OverridePage {OVERRIDE_CURRENT, OVERRIDE_NEW, NO_OVERRIDE}
	
	GraphicalSoundboardHolder boardHolder;
	
	/**
	 * Creates new provider with specified orientation
	 * 
	 * @param orientation
	 */
	public GraphicalSoundboardProvider(int orientation) {
		boardHolder = new GraphicalSoundboardHolder();
		setOrientationMode(orientation);
		GraphicalSoundboard initialGsb = new GraphicalSoundboard();
		boardHolder.allocateBoardResources(initialGsb);
	}
	
	/**
	 * Loads provider for boardName
	 * 
	 * @param boardName
	 */
	public GraphicalSoundboardProvider(String boardName) {
		try {
			boardHolder = FileProcessor.loadGraphicalSoundboardHolder(boardName, true);
		} catch (IOException e) {
			Log.w(TAG, "Unable to load board holder", e);
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
	
	public GraphicalSoundboard getPage(Context context, int orientation, int pageNumber) {
		
		for (GraphicalSoundboard gsb : boardHolder.getBoardList()) {
			if (gsb.getScreenOrientation() == orientation &&
					gsb.getPageNumber() == pageNumber) {
				return GraphicalSoundboard.copy(context, gsb);
			}
		}
		
		return null;
	}
	
	public void deletePage(Context context, GraphicalSoundboard deleteGsb) {
		Log.v(TAG, "Going to delete page " + deleteGsb.getPageNumber());
		deleteBoardId(deleteGsb.getId());
		
		Log.v(TAG, "Reducing following page numbers.");
		for (GraphicalSoundboard gsb : boardHolder.getBoardList()) {
			if (gsb.getScreenOrientation() == deleteGsb.getScreenOrientation() && 
					gsb.getPageNumber() > deleteGsb.getPageNumber()) {
				gsb.setPageNumber(gsb.getPageNumber() - 1);
				overrideBoard(context, gsb);
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
	
	public void saveBoard(Context context, String boardName) throws IOException {
		// Since file paths may be altered while saving we need a separate cop for saving
		GraphicalSoundboardHolder savedHolder = GraphicalSoundboardHolder.copy(context, boardHolder);
		FileProcessor.saveGraphicalSoundboardHolder(boardName, savedHolder);
	}
	
	public void overrideBoard(Context context, GraphicalSoundboard tempGsb) {
 		GraphicalSoundboard gsb = GraphicalSoundboard.copy(context, tempGsb);
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
	
	public void deletePagesWithOrientation(int orientation) {
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
	
	public boolean boardUsesFile(File file) {
		for (GraphicalSoundboard gsb : boardHolder.getBoardList()) {
			try {
				if (file.getName().equals(gsb.getBackgroundImagePath().getName())) return true;
			} catch (NullPointerException e) {}

			for (GraphicalSound sound : gsb.getSoundList()) {
				try {
					if (sound.getPath().getAbsolutePath().equals(file.getAbsolutePath())) return true;
				} catch (NullPointerException e) {}

				try {
					if (sound.getImagePath().getAbsolutePath().equals(file.getAbsolutePath())) return true;
				} catch (NullPointerException e) {}

				try {
					if (sound.getActiveImagePath().getAbsolutePath().equals(file.getAbsolutePath())) return true;
				} catch (NullPointerException e) {}
			}
		}
		return false;
	}
	
	public List<GraphicalSoundboard> getBoardList() {
		return boardHolder.getBoardList();
	}
	
	public boolean isPaginationSynchronizedBetweenOrientations() {
		return boardHolder.isPaginationSynchronizedBetweenOrientations();
	}

	public void setPaginationSynchronizedBetweenOrientations(boolean paginationSynchronizedBetweenOrientations) {
		boardHolder.setPaginationSynchronizedBetweenOrientations(paginationSynchronizedBetweenOrientations);
	}
	
}
