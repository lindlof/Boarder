package fi.mikuz.boarder.component.soundboard;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
@XStreamAlias("graphical-soundboard-holder")
public class GraphicalSoundboardHolder {
	List<GraphicalSoundboard> boardList;
	
	public GraphicalSoundboardHolder() {
		this.boardList = new ArrayList<GraphicalSoundboard>();
	}

	public List<GraphicalSoundboard> getBoardList() {
		return boardList;
	}

	public void setBoardList(List<GraphicalSoundboard> boardList) {
		this.boardList = boardList;
	}
	
	public void addBoard(GraphicalSoundboard board) {
		this.boardList.add(board);
	}
}
