package fi.mikuz.boarder.component.soundboard;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Holds list of saved boards. Allocates resources for new boards to be saved.
 * 
 * @author Jan Mikael Lindlöf
 */
@XStreamAlias("graphical-soundboard-holder")
public class GraphicalSoundboardHolder {
	
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
	}
	
	public static GraphicalSoundboardHolder copy(Context context, GraphicalSoundboardHolder tempHolder) {
		
		GraphicalSoundboardHolder holder = new GraphicalSoundboardHolder();
		holder.setOrientationMode(tempHolder.getOrientationMode());
		holder.setPaginationSynchronizedBetweenOrientations(
				tempHolder.isPaginationSynchronizedBetweenOrientations());
		
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
	public GraphicalSoundboard allocateBoardResources(GraphicalSoundboard gsbTemplate) {
		int boardId = allocateBoardId();
		int boardPageNumber = allocateBoardPage(gsbTemplate.getScreenOrientation());
		gsbTemplate.setId(boardId);
		gsbTemplate.setPageNumber(boardPageNumber);
		
		this.boardList.add(gsbTemplate);
		return gsbTemplate;
	}
	
	private int allocateBoardId() {
		int highestId = -1;
		for (GraphicalSoundboard board : this.getBoardList()) {
			highestId = (board.getId() > highestId) ? board.getId() : highestId;
		}
		return highestId + 1;
	}
	
	private int allocateBoardPage(int screenOrientation) {
		int highestPage = -1;
		for (GraphicalSoundboard board : this.getBoardList()) {
			if (board.getScreenOrientation() == screenOrientation) {
				highestPage = (board.getPageNumber() > highestPage) ? board.getPageNumber() : highestPage;
			}
		}
		return highestPage + 1;
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

}
