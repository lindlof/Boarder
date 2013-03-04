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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundboard;
import fi.mikuz.boarder.util.ContextUtils;

/**
 * Knows current status of pages. Provides pagination functionality.
 * 
 * @author Jan Mikael Lindlöf
 */
public class Pagination {
	public static final String TAG = Pagination.class.getSimpleName();
	
	private static final String PAGE_NUMBER_PORTRAIT_KEY = "pageNumberPortraitKey";
	private static final String PAGE_NUMBER_LANDSCAPE_KEY = "pageNumberLandscapeKey";
	
	GraphicalSoundboardProvider gsbp;
	
	private boolean movePageMode;
	private int moveFromPageNumber;
	private int movePageOrientation;
	
	private int pageNumberPortrait;
	private int pageNumberLandscape;
	
	public Pagination(GraphicalSoundboardProvider gsbp) {
		this.gsbp = gsbp;
		resetMove();
		pageNumberPortrait = 0;
		pageNumberLandscape = 0;
	}
	
	public void initMove(GraphicalSoundboard gsb) {
		this.movePageMode = true;
		this.movePageOrientation = gsb.getScreenOrientation();
		this.moveFromPageNumber = gsb.getPageNumber();
	}
	
	private void resetMove() {
		this.movePageMode = false;
		this.moveFromPageNumber = -1;
		this.movePageOrientation = -1;
	}
	
	public void savePaginationInstance(Bundle outState) {
		outState.putInt(PAGE_NUMBER_PORTRAIT_KEY, pageNumberPortrait);
		outState.putInt(PAGE_NUMBER_LANDSCAPE_KEY, pageNumberLandscape);
	}
	
	public void restorePaginationInstance(Bundle state) {
		try {
			pageNumberPortrait = state.getInt(PAGE_NUMBER_PORTRAIT_KEY);
			pageNumberLandscape = state.getInt(PAGE_NUMBER_LANDSCAPE_KEY);
			Log.v(TAG, "Restored pagination instance");
		} catch (NullPointerException e) {}
	}
	
	public GraphicalSoundboard getBoard(Context context, int orientation) {
		
		int pageNumber = getPageIndexForOrientation(orientation);
		GraphicalSoundboard gsb = gsbp.getPage(context, orientation, pageNumber);
		if (gsb != null) return gsb;
		Log.w(TAG, "Can not find expected page. Giving last page available.");
		pageNumber = getLastPageNumber(context, orientation);
		gsb = gsbp.getPage(context, orientation, pageNumber);
		if (gsb != null) return gsb;
		
		Log.v(TAG, "No pages in this orientation. Adding page.");
		gsb = gsbp.addBoardPage(orientation);
		return gsb;
	}
	
	public void movePage(Context context, GraphicalSoundboard toGsb) {
		int orientation = this.movePageOrientation;
		int fromPageNumber = this.moveFromPageNumber;
		int toPageNumber = toGsb.getPageNumber();
		
		if (toGsb.getScreenOrientation() != orientation) {
			ContextUtils.toast(context, "Wrong orientation!");
    		return;
    	}
		resetMove();
		
		List<GraphicalSoundboard> pages = new ArrayList<GraphicalSoundboard>();
		
		int beginPageNumber = (fromPageNumber < toPageNumber) ? fromPageNumber : toPageNumber;
		int endPageNumber = beginPageNumber + Math.abs(fromPageNumber-toPageNumber);
		
		for (int i = beginPageNumber; i <= endPageNumber; i++) {
			GraphicalSoundboard gsb = gsbp.getPage(context, orientation, i);
			pages.add(gsb);
		}
		
		for (GraphicalSoundboard gsb : pages) {
			int pageNumber = gsb.getPageNumber();

			if (pageNumber == fromPageNumber) {
				gsb.setPageNumber(toPageNumber);
				gsbp.overrideBoard(context, gsb);
			} else if (fromPageNumber > toPageNumber) {
				gsb.setPageNumber(pageNumber + 1);
				gsbp.overrideBoard(context, gsb);
			} else if (fromPageNumber < toPageNumber) {
				gsb.setPageNumber(pageNumber - 1);
				gsbp.overrideBoard(context, gsb);
			}
		}
	}
	
	/**
	 * 
	 * @param current gsb
	 * @return next board page or null
	 */
	public GraphicalSoundboard getNextBoardPage(Context context, GraphicalSoundboard lastGsb) {
		int orientation = lastGsb.getScreenOrientation();
		GraphicalSoundboard selectedBoard = null;
		
		selectedBoard = gsbp.getPage(context, orientation, lastGsb.getPageNumber() + 1);
		
		if (selectedBoard == null) selectedBoard = gsbp.getPage(context, orientation, 0); // Last page, go to first page.
		
		updatePageNumber(selectedBoard);
		return selectedBoard;
	}
	
	/**
	 * 
	 * @param current gsb
	 * @return next board page or null
	 */
	public GraphicalSoundboard getPreviousPage(Context context, GraphicalSoundboard lastGsb) {
		int orientation = lastGsb.getScreenOrientation();
		GraphicalSoundboard selectedBoard = null;
		
		selectedBoard = gsbp.getPage(context, orientation, lastGsb.getPageNumber() - 1);
		
		if (selectedBoard == null) {
			int lastPage = getLastPageNumber(context, orientation); // First page, go to last page.
			selectedBoard = gsbp.getPage(context, orientation, lastPage);
		}
		
		updatePageNumber(selectedBoard);
		return selectedBoard;
	}
	
	private int getLastPageNumber(Context context, int orientation) {
		int lastPage = -1;
		
		while (gsbp.getPage(context, orientation, lastPage + 1) != null) {
			lastPage++;
		}
		
		return lastPage;
	}
	
	private void updatePageNumber(GraphicalSoundboard gsb) {
		if (gsb != null) {
			int pageNumber = gsb.getPageNumber();
			int orientation = gsb.getScreenOrientation();
			if (orientation == GraphicalSoundboard.SCREEN_ORIENTATION_PORTRAIT) {
				setPageNumberPortrait(pageNumber);
			} else if (orientation == GraphicalSoundboard.SCREEN_ORIENTATION_LANDSCAPE) {
				setPageNumberLandscape(pageNumber);
			}
		}
	}
	
	public boolean isMovePageMode() {
		return movePageMode;
	}
	public int getPageIndexForOrientation(int orientation) {
		if (orientation == GraphicalSoundboard.SCREEN_ORIENTATION_PORTRAIT) {
			return pageNumberPortrait;
		} else if (orientation == GraphicalSoundboard.SCREEN_ORIENTATION_LANDSCAPE) {
			return pageNumberLandscape;
		} else {
			return -1;
		}
	}
	public void setPageNumberPortrait(int pageNumberPortrait) {
		this.pageNumberPortrait = pageNumberPortrait;
		if (gsbp.isPaginationSynchronizedBetweenOrientations()) {
			this.pageNumberLandscape = pageNumberPortrait;
		}
	}
	public void setPageNumberLandscape(int pageNumberLandscape) {
		this.pageNumberLandscape = pageNumberLandscape;
		if (gsbp.isPaginationSynchronizedBetweenOrientations()) {
			this.pageNumberPortrait = pageNumberLandscape;
		}
	}
}
