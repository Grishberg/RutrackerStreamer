package pct.droid.containers;

import java.io.Serializable;

/**
 * Created by ilia on 03.07.15.
 *
 * @author ilia
 */
public class MediaContainer implements Serializable{
	private String torrentUrl;
	private String torrentName;
	private String imageUrl;

	public MediaContainer(String torrentUrl, String torrentName, String imageUrl) {
		this.torrentUrl = torrentUrl;
		this.torrentName = torrentName;
		this.imageUrl = imageUrl;
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

}