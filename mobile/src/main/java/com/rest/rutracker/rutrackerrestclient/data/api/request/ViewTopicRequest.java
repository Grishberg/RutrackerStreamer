package com.rest.rutracker.rutrackerrestclient.data.api.request;

import com.rest.rutracker.rutrackerrestclient.data.containers.Article;

import java.io.Serializable;

/**
 * Created by ilia on 03.07.15.
 */
public class ViewTopicRequest extends DataRequest implements Serializable {

    private String keyViewTopic;

    public ViewTopicRequest(String keyViewTopic) {
        super();
        this.keyViewTopic = keyViewTopic;
    }


    public String getKeyViewTopic() {
        return keyViewTopic;
    }
}
