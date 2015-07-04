package com.rest.rutracker.rutrackerrestclient.data.api.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.rest.rutracker.rutrackerrestclient.data.model.RutrackerFeedParcer;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ilia on 23.06.15.
 *
 * @author ilia
 */
public class DataResponse implements Serializable {

 /*   public static final Creator<DataResponse> CREATOR
            = new Creator<DataResponse>() {
        public DataResponse createFromParcel(Parcel in) {
            return new DataResponse(in);
        }

        public DataResponse[] newArray(int size) {
            return new DataResponse[size];
        }
    };
 */
    private long id;
    private String mXMLString;


    List<RutrackerFeedParcer.Entry> mXMLEntry;

    public DataResponse(String xml){
        mXMLString=xml;
    }

    public DataResponse() {
        id = 0;
    }

    public DataResponse(long id) {
        this.id = id;
    }

    public DataResponse(List<RutrackerFeedParcer.Entry> parse) {
        mXMLEntry=parse;
    }


    public long getId() {
        return id;
    }

    public String getXMLString() {
        return mXMLString;
    }

    public List<RutrackerFeedParcer.Entry> getXMLEntry() {
        return mXMLEntry;
    }

}