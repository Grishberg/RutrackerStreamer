package com.rest.rutracker.rutrackerrestclient.data.api.response;

/**
 * Created by G on 05.07.15.
 */
public class TorrentFileDataResponse extends DataResponse {
	private byte[] torrentFile;
	public TorrentFileDataResponse(byte[] torrentFile){
		this.torrentFile	= torrentFile;
	}

	public byte[] getTorrentFile() {
		return torrentFile;
	}
}
