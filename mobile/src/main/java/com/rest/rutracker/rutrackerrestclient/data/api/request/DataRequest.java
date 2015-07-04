package com.rest.rutracker.rutrackerrestclient.data.api.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.rest.rutracker.rutrackerrestclient.data.containers.Article;

import java.io.Serializable;

/**
 * Created by ilia on 23.06.15.
 *
 * @author ilia
 */
public class DataRequest  implements Serializable {

    private Article article;
    private String uri;

    public DataRequest(Article article, String uri) {
        this.article = article;
        this.uri = uri;
    }

    public DataRequest(Parcel in) {
        article = in.readParcelable(Article.class.getClassLoader());
        uri = in.readString();
    }

    public DataRequest(){
        this(null,null);
    }

    public Article getArticle() {
        return article;
    }

    public String getAdditionalData() {
        return uri;
    }

}
