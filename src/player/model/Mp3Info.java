package player.model;

import java.io.Serializable;

public class Mp3Info implements Serializable
{	
	private static final long serialVersionUID = -4383468343233451401L;
	
	private String id = null;
	private String mp3Name = null;
	private String mp3Size = null;
	private String lrcName = null;
	private String lrcSize = null;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMp3Name() {
		return mp3Name;
	}

	public void setMp3Name(String mp3Name) {
		this.mp3Name = mp3Name;
	}

	public String getMp3Size() {
		return mp3Size;
	}

	public void setMp3Size(String mp3Size) {
		this.mp3Size = mp3Size;
	}

	public String getLrcName() {
		return lrcName;
	}

	public void setLrcName(String lrcName) {
		this.lrcName = lrcName;
	}

	public String getLrcSize() {
		return lrcSize;
	}

	public void setLrcSize(String lrcSize) {
		this.lrcSize = lrcSize;
	}

	public Mp3Info(String id, String mp3Name, String mp3Size, String lrcName,
			String lrcSize) {
		super();
		this.id = id;
		this.mp3Name = mp3Name;
		this.mp3Size = mp3Size;
		this.lrcName = lrcName;
		this.lrcSize = lrcSize;
	}

	public Mp3Info() {
		super();
	}


	public String toString() {
		return "Mp3Info [id=" + id + ", mp3Name=" + mp3Name + ", mp3Size="
				+ mp3Size + ", lrcName=" + lrcName + ", lrcSize=" + lrcSize
				+ "]";
	}



	
	
}
