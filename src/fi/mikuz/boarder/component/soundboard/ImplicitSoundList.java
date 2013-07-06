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

import java.util.List;
import fi.mikuz.boarder.util.XStreamUtil;

/**
 * XStream collection hack.
 * <p>
 * This collection is used in {@link GraphicalSoundboard} and declared as
 * implicit collection in {@link XStreamUtil}.
 * <p>
 * Implicit collection class itself is ignored in serialized XML and all collection 
 * items it contains are directly inserted without wrapping object.
 * I want that wrapping object so an extra intermediary object was introduced.
 * This is that extra object.
 * <p>
 * The need to use implicit collection comes from XStreams inability to use 
 * custom collections without introducing a bogus class attribute.
 * That class attribute could create serious problems with loading legacy
 * boards in the future.
 */
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
