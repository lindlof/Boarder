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

package fi.mikuz.boarder.component.soundboard;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fi.mikuz.boarder.util.Logger;

/**
 * Holds list of saved boards. Allocates resources for new boards to be saved.
 */
@XStreamAlias("graphical-soundboard-holder")
public class GraphicalSoundboardHolder {
	private static final String TAG = GraphicalSoundboardHolder.class.getSimpleName();
	
	private int version;
	
	public enum OrientationMode {ORIENTATION_MODE_PORTRAIT, ORIENTATION_MODE_LANDSCAPE, ORIENTATION_MODE_HYBRID};
	private OrientationMode orientationMode;
	
	/**
	 * Page events have effect in both orientations.
	 */
	private boolean paginationSynchronizedBetweenOrientations;
	
	private List<GraphicalSoundboard> boardList;
	
	public GraphicalSoundboardHolder() {
		this.orientationMode = OrientationMode.ORIENTATION_MODE_PORTRAIT;
		this.setPaginationSynchronizedBetweenOrientations(true);
		this.boardList = new ArrayList<GraphicalSoundboard>();
		this.version = 1;
	}
	
	public static GraphicalSoundboardHolder copy(Context context, GraphicalSoundboardHolder tempHolder) {
		
		GraphicalSoundboardHolder holder = new GraphicalSoundboardHolder();
		holder.setOrientationMode(tempHolder.getOrientationMode());
		holder.setPaginationSynchronizedBetweenOrientations(
				tempHolder.isPaginationSynchronizedBetweenOrientations());
		holder.version = tempHolder.getVersion();
		
		for (GraphicalSoundboard gsb : tempHolder.boardList) {
			GraphicalSoundboard gsbCopy = GraphicalSoundboard.copy(context, gsb);
			holder.boardList.add(gsbCopy);
		}
		
		return holder;
	}
	
	/**
	 * 
	 * @param gsbTemplate
	 * @return Board with new id
	 */
	public synchronized GraphicalSoundboard allocateBoardResources(GraphicalSoundboard gsbTemplate) {
		int boardId = allocateBoardId();
		int boardPageNumber = allocateBoardPage(gsbTemplate.getScreenOrientation());
		gsbTemplate.setId(boardId);
		gsbTemplate.setPageNumber(boardPageNumber);
		
		this.boardList.add(gsbTemplate);
		return gsbTemplate;
	}
	
	private synchronized int allocateBoardId() {
		return highestPageId() + 1;
	}
	
	/**
	 * Find highest page ID.
	 * @return Highest page ID or -1 if no pages found
	 */
	private int highestPageId() {
		int highestId = -1;
		for (GraphicalSoundboard board : this.getBoardList()) {
			highestId = (board.getId() > highestId) ? board.getId() : highestId;
		}
		return highestId;
	}
	
	private synchronized int allocateBoardPage(int screenOrientation) {
		int highestPage = highestPageNumber(screenOrientation);
		short[] pagesCounters = new short[highestPage+1];
		
		for (GraphicalSoundboard board : getBoardList()) {
			if (board.getScreenOrientation() == screenOrientation) {
				pagesCounters[board.getPageNumber()]++;
			}
		}
		
		for (int i = 0; i < pagesCounters.length; i++) {
			if (pagesCounters[i] == 0) {
				Log.d(TAG, "Allocating missing page " + i);
				return i;
			}
		}
		Log.d(TAG, "Allocating new page after " + highestPage);
		return highestPage + 1;
	}
	
	/**
	 * Find highest page number.
	 * @return Highest page number or -1 if no pages found
	 */
	private int highestPageNumber(int screenOrientation) {
		int highestPage = -1;
		for (GraphicalSoundboard board : this.getBoardList()) {
			if (board.getScreenOrientation() == screenOrientation) {
				highestPage = (board.getPageNumber() > highestPage) ? board.getPageNumber() : highestPage;
			}
		}
		return highestPage;
	}
	
	public OrientationMode getOrientationMode() {
		return orientationMode;
	}

	public void setOrientationMode(OrientationMode orientationMode) {
		this.orientationMode = orientationMode;
	}

	public List<GraphicalSoundboard> getBoardList() {
		return boardList;
	}

	public boolean isPaginationSynchronizedBetweenOrientations() {
		return paginationSynchronizedBetweenOrientations;
	}

	public void setPaginationSynchronizedBetweenOrientations(
			boolean paginationSynchronizedBetweenOrientations) {
		this.paginationSynchronizedBetweenOrientations = paginationSynchronizedBetweenOrientations;
	}
	
	private int getVersion() {
		return version;
	}
	
	public void migrate(boolean log) {
		final int currentVersion = this.version;
		if (currentVersion < 2) {
			migrateVersion2();
			this.version = 2;
			if (log) Log.d(TAG, "Version migrated from " + currentVersion + " to " + this.version);
		}
	}
	
	private void migrateVersion2() {
		for (GraphicalSoundboard board : getBoardList()) {
			GraphicalSoundList soundList = (GraphicalSoundList) board.getSoundList();
			for (GraphicalSound sound : soundList) {
				soundList.soundIdCheck(sound);
			}
		}
	}
	
	/**
	 * Verify and fix board data.
	 * <p>
	 * Load time verification that board data isn't in corrupted state.
	 * @return true if data is valid
	 */
	public synchronized boolean verifyIntegrity() {
		short[] idCounters = new short[highestPageId()+1];
		short[] portraitPageCounters = 
				new short[highestPageNumber(GraphicalSoundboard.SCREEN_ORIENTATION_PORTRAIT)+1];
		short[] landscapePageCounters = 
				new short[highestPageNumber(GraphicalSoundboard.SCREEN_ORIENTATION_LANDSCAPE)+1];
		

		for (GraphicalSoundboard board : getBoardList()) {
			int id = board.getId();
			int pageNum = board.getPageNumber();
			
			idCounters[id]++;
			if (idCounters[id] > 1) {
				Logger.silentError(TAG, "Detected pages with duplicate ID " + id + ", allocating a new ID");
				board.setId(allocateBoardId());
				return false;
			}
			
			switch (board.getScreenOrientation()) {
			case GraphicalSoundboard.SCREEN_ORIENTATION_PORTRAIT:
				portraitPageCounters[pageNum]++;
				if (portraitPageCounters[pageNum] > 1) {
					Logger.silentError(TAG, "Detected portrait pages with duplicate page number " + pageNum+ ", allocating a new number");
					board.setPageNumber(allocateBoardPage(GraphicalSoundboard.SCREEN_ORIENTATION_PORTRAIT));
					return false;
				}
				break;
			case GraphicalSoundboard.SCREEN_ORIENTATION_LANDSCAPE:
				landscapePageCounters[pageNum]++;
				if (landscapePageCounters[pageNum] > 1) {
					Logger.silentError(TAG, "Detected landscape pages with duplicate page number " + pageNum+ ", allocating a new number");
					board.setPageNumber(allocateBoardPage(GraphicalSoundboard.SCREEN_ORIENTATION_LANDSCAPE));
					return false;
				}
				break;
			default:
				Log.e(TAG, "Illegal screen orientation " + board.getScreenOrientation());
			}
		}
		
		for (int i = 0; i < portraitPageCounters.length; i++) {
			if (portraitPageCounters[i] == 0) {
				Logger.silentWarning(TAG, "Detected missing portrait page number " + i);
				// User should be able to fix this by adding a new page
			}
		}
		
		for (int i = 0; i < landscapePageCounters.length; i++) {
			if (portraitPageCounters[i] == 0) {
				Logger.silentWarning(TAG, "Detected missing landscape page number " + i);
				// User should be able to fix this by adding a new page
			}
		}
		
		return true;
	}

}
