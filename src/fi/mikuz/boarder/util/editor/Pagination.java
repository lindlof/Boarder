package fi.mikuz.boarder.util.editor;

public class Pagination {
	private boolean movePageMode;
	private int moveFromPageNumber;
	private int movePageOrientation;
	
	private int pageNumberPortrait;
	private int pageNumberLandscape;
	
	public Pagination() {
		resetMove();
		pageNumberPortrait = 0;
		pageNumberLandscape = 0;
	}
	
	public void initMove(int movePageOrientation, int moveFromPageNumber) {
		this.movePageMode = true;
		this.movePageOrientation = movePageOrientation;
		this.moveFromPageNumber = moveFromPageNumber;
	}
	
	public void resetMove() {
		this.movePageMode = false;
		this.moveFromPageNumber = -1;
		this.movePageOrientation = -1;
	}
	
	public boolean isMovePageMode() {
		return movePageMode;
	}
	public int getMoveFromPageNumber() {
		return moveFromPageNumber;
	}
	public int getMovePageOrientation() {
		return movePageOrientation;
	}
	public int getPageNumberPortrait() {
		return pageNumberPortrait;
	}
	public void setPageNumberPortrait(int pageNumberPortrait) {
		this.pageNumberPortrait = pageNumberPortrait;
	}
	public int getPageNumberLandscape() {
		return pageNumberLandscape;
	}
	public void setPageNumberLandscape(int pageNumberLandscape) {
		this.pageNumberLandscape = pageNumberLandscape;
	}
}
