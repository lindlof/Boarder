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

package fi.mikuz.boarder.component.soundboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Custom list to allocate ID's to sounds that are added.
 * TODO: Allocate used ID's that are not reserved anymore.
 * 
 * @author Jan Mikael Lindlöf
 */
public class GraphicalSoundList extends ArrayList<GraphicalSound> {
	
	private static final long serialVersionUID = 7248111265110278835L;
	
	@Override
	public boolean add(GraphicalSound sound) {
		soundIdCheck(sound);
		boolean result = super.add(sound);
		return result;
	}
	
	@Override
	public void add(int index, GraphicalSound sound) {
		soundIdCheck(sound);
		super.add(index, sound);
	}

	@Override
	public boolean addAll(Collection<? extends GraphicalSound> sounds) {
		soundsIdCheck(sounds);
		boolean result = super.addAll(sounds);
		return result;
	}

	@Override
	public boolean addAll(int index, Collection<? extends GraphicalSound> sounds) {
		soundsIdCheck(sounds);
		boolean result = super.addAll(index, sounds);
		return result;
	}
	
	private void soundsIdCheck(Collection<? extends GraphicalSound> sounds) {
		for (GraphicalSound sound : sounds) {
			soundIdCheck(sound);
		}
	}
	
	/**
	 * Makes sure that sound's ID is unique.
	 * <p>
	 * Call before adding the sound.
	 * Sound's ID would seem to be already taken if it was already added.
	 * @param sound
	 */
	protected void soundIdCheck(GraphicalSound sound) {
		// Allocate if the sound is new
		if (sound.getId() == -1) {
			sound.setId(this.allocateSoundId());
		}
		
		// Allocate if the id is already taken
		Iterator<GraphicalSound> listSoundIter = super.iterator();
		synchronized (this) {
			while (listSoundIter.hasNext()) {
				GraphicalSound listSound = listSoundIter.next();
				if (sound.getId() == listSound.getId()) {
					sound.setId(this.allocateSoundId());
					return;
				}
			}
		}
	}
	
	protected long allocateSoundId() {
		long id = 0;
		
		synchronized (this) {
			for (GraphicalSound sound : this) {
				long soundId = sound.getId();
				if (soundId >= id) {
					id = soundId+1;
				}
			}
		}
		
		return id;
	}
	
}
