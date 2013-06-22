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

package fi.mikuz.boarder.component;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("dropbox-cache-file")
public class DropboxCacheFile {
	
	public DropboxCacheFile() {}
	
	public DropboxCacheFile(String path, String rev, String md5) {
		this.path = path;
		this.rev = rev;
		this.md5 = md5;
	}
	
	String path;
	String rev;
	String md5;
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getRev() {
		return rev;
	}
	public void setRev(String dropboxHash) {
		this.rev = dropboxHash;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String localMd5) {
		this.md5 = localMd5;
	}
}