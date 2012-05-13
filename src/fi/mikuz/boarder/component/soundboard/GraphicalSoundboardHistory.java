package fi.mikuz.boarder.component.soundboard;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Stores changes in soundboard
 * 
 * @author Jan Mikael Lindlöf
 */
@XStreamAlias("graphical-soundboard-history")
public class GraphicalSoundboardHistory {
	
	List<GraphicalSoundboard> history;
	int index;

	public GraphicalSoundboardHistory() {
		this.history = new ArrayList<GraphicalSoundboard>();
		this.index = -1;
	}
	
	public List<GraphicalSoundboard> getHistory() {
		return history;
	}

	public void setHistory(List<GraphicalSoundboard> history) {
		this.history = history;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
