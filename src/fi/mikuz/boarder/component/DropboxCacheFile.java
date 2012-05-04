package fi.mikuz.boarder.component;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
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