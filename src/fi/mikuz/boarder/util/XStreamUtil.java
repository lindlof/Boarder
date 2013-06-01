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

package fi.mikuz.boarder.util;

import android.content.Context;
import android.os.Bundle;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import fi.mikuz.boarder.component.soundboard.GraphicalSound;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundList;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundboard;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundboardHolder;
import fi.mikuz.boarder.component.soundboard.ImplicitSoundList;

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
		xstream.addImplicitCollection(ImplicitSoundList.class, "list");
		
		// Deprecated fields
		xstream.omitField(GraphicalSoundboard.class, "version");
		
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
	public static Bundle getSoundBundle(Context context, GraphicalSound tempSound, GraphicalSoundboard tempGsb) {
		
		GraphicalSound sound = null;
		try {
			sound = (GraphicalSound) tempSound.clone();
			sound.unloadImages();
		} catch (NullPointerException e) {}
		
		GraphicalSoundboard gsb = GraphicalSoundboard.copy(context, tempGsb);
		gsb.setSoundList(new GraphicalSoundList());
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
	public static Bundle getSoundboardBundle(Context context, GraphicalSoundboard tempGsb) {
		
		GraphicalSoundboard gsb = GraphicalSoundboard.copy(context, tempGsb);
		gsb.setSoundList(new GraphicalSoundList());
		GraphicalSoundboard.unloadImages(gsb);
		
		Bundle soundBundle = new Bundle();
    	XStream xstream = XStreamUtil.graphicalBoardXStream();
    	soundBundle.putString(SOUNDBOARD_KEY, xstream.toXML(gsb));
		return soundBundle;
	}
}
