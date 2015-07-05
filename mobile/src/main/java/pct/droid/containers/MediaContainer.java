package pct.droid.containers;

import java.io.Serializable;

/**
 * Created by ilia on 03.07.15.
 *
 * @author ilia
 */
public class MediaContainer implements Serializable{
	private String torrentUrl;
	private byte[] torrenBody;
	private String torrentName;
	private String imageUrl;

	public MediaContainer(String torrentUrl, String torrentName, String imageUrl, byte[] torrentBody) {
		this.torrentUrl = torrentUrl;
		this.torrentName = torrentName;
		this.imageUrl = imageUrl;
		this.torrenBody	= torrentBody;
	}


	public String getTorrentUrl() {
		return torrentUrl;
	}

	public String getTorrentName() {
		return torrentName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public byte[] getTorrenBody() {
		return torrenBody;
	}

}