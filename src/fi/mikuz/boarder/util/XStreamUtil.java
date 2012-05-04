package fi.mikuz.boarder.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import fi.mikuz.boarder.component.soundboard.GraphicalSound;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundboard;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundboardHolder;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
public class XStreamUtil {

	public static XStream graphicalBoardXStream() {
		XStream xstream = new XStream(new DomDriver());
		xstream.processAnnotations(GraphicalSoundboardHolder.class);
		xstream.processAnnotations(GraphicalSoundboard.class);
		xstream.processAnnotations(GraphicalSound.class);
		return xstream;
	}
}
