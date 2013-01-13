package fi.mikuz.boarder.component.soundboard;

import java.util.ArrayList;
import java.util.List;

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
	List<GraphicalSoundboard> boardList;
	
	public GraphicalSoundboardHolder() {
		this.orientationMode = OrientationMode.ORIENTATION_MODE_PORTRAIT;
		this.boardList = new ArrayList<GraphicalSoundboard>();
		GraphicalSoundboard gsbTemplate = new GraphicalSoundboard();
		allocateBoardResources(gsbTemplate);
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

	public void setBoardList(List<GraphicalSoundboard> boardList) {
		this.boardList = boardList;
	}
	
	public void overrideBoard(GraphicalSoundboard gsb) {
		for (int i = 0; i < boardList.size(); i++) {
			GraphicalSoundboard existingGsb = boardList.get(i);
			if (gsb.getId() == existingGsb.getId()) {
				boardList.set(i, gsb);
				break;
			}
		}
	}
	
	public void deleteBoard(int id) {
		boardList.remove(id);
	}

}
