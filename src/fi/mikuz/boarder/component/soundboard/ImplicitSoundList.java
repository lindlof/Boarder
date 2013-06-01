package fi.mikuz.boarder.component.soundboard;

import java.util.List;


public class ImplicitSoundList {
	private GraphicalSoundList list;

	protected List<GraphicalSound> getList() {
		if (list == null) {
			list = new GraphicalSoundList();
		}
		return list;
	}

	protected void setList(List<GraphicalSound> list) {
		this.list = (GraphicalSoundList) list;
	}
}
