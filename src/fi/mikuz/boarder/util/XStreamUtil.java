package fi.mikuz.boarder.util;

import java.util.ArrayList;

import android.os.Bundle;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import fi.mikuz.boarder.component.soundboard.GraphicalSound;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundboard;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundboardHistory;
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
	
	public static XStream graphicalBoardHistoryXStream() {
		XStream xstream = graphicalBoardXStream();
		xstream.processAnnotations(GraphicalSoundboardHistory.class);
		return xstream;
	}
	
	public static final String SOUND_KEY = "soundKey";
	public static final String SOUNDBOARD_KEY = "soundboardKey";
	/**
	 * 
	 * @param tempSound
	 * @param tempGsb
	 * @return Bundle including a GraphicalSound and an empty GraphicalSoundboard associated to the GraphicalSound
	 */
	public static Bundle getSoundBundle(GraphicalSound tempSound, GraphicalSoundboard tempGsb) {
		
		GraphicalSound sound = (GraphicalSound) tempSound.clone();
		GraphicalSound.unloadImages(sound);
		GraphicalSoundboard gsb = GraphicalSoundboard.copy(tempGsb);
		gsb.setSoundList(new ArrayList<GraphicalSound>());
		GraphicalSoundboard.unloadImages(gsb);
		
		Bundle soundBundle = new Bundle();
    	XStream xstream = XStreamUtil.graphicalBoardXStream();
    	soundBundle.putString(SOUND_KEY, xstream.toXML(sound));
    	soundBundle.putString(SOUNDBOARD_KEY, xstream.toXML(gsb));
		return soundBundle;
	}
	/**
	 * 
	 * @param tempGsb
	 * @return Bundle including an empty GraphicalSoundboard
	 */
	public static Bundle getSoundboardBundle(GraphicalSoundboard tempGsb) {
		
		GraphicalSoundboard gsb = GraphicalSoundboard.copy(tempGsb);
		gsb.setSoundList(new ArrayList<GraphicalSound>());
		GraphicalSoundboard.unloadImages(gsb);
		
		Bundle soundBundle = new Bundle();
    	XStream xstream = XStreamUtil.graphicalBoardXStream();
    	soundBundle.putString(SOUNDBOARD_KEY, xstream.toXML(gsb));
		return soundBundle;
	}
}
