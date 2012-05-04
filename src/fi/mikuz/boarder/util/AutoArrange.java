package fi.mikuz.boarder.util;

import java.util.List;

import fi.mikuz.boarder.component.Slot;
import fi.mikuz.boarder.component.soundboard.GraphicalSound;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
public class AutoArrange {
	public static Slot getFreeSlot(List<GraphicalSound> soundList, int autoArrangeColumns, int autoArrangeRows) {
		Slot freeSlot = null;
		int column = 0, row = 0;
		
		while (row < autoArrangeRows) {
			boolean slotIsFree = true;
			for (GraphicalSound sound : soundList) {
				if (sound.getAutoArrangeColumn() == column && sound.getAutoArrangeRow() == row) {
					slotIsFree = false;
					break;
				}
			}
			if (slotIsFree) {
				freeSlot = new Slot();
				freeSlot.setColumn(column);
				freeSlot.setRow(row);
				break;
			}
			column++;
			if (column >= autoArrangeColumns) {
				column = 0;
				row++;
			}
		}
		return freeSlot;
	}
}
