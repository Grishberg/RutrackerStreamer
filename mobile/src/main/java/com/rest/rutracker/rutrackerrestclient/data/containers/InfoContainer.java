package com.rest.rutracker.rutrackerrestclient.data.containers;

import java.io.Serializable;

/**
 * Created by ilia on 03.07.15.
 *
 * @author ilia
 */
public class InfoContainer implements Serializable{
    private String torrentName;
    private String torrentKey;

    public InfoContainer(String torrentName, String torrentKey) {
        this.torrentName = torrentName;
        this.torrentKey = torrentKey;
    }

    public String getTorrentName() {
        return torrentName;
    }

    public String getTorrentKey() {
        return torrentKey;
    }
}
