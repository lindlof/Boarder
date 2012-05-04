package fi.mikuz.boarder.component;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
@XStreamAlias("dropbox-cache")
public class DropboxCache {
	
	private List<DropboxCacheFile> files;
	
	public DropboxCache() {}
	
	public List<DropboxCacheFile> getFiles() {
		return files;
	}
	
	public void setFiles(List<DropboxCacheFile> boards) {
		this.files = boards;
	}
}
