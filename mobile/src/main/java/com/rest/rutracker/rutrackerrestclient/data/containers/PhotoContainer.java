package com.rest.rutracker.rutrackerrestclient.data.containers;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ilia on 23.06.15.
 *
 * @author ilia
 */
public class PhotoContainer implements Parcelable {

    public static final Creator<PhotoContainer> CREATOR = new Creator<PhotoContainer>() {

        public PhotoContainer createFromParcel(Parcel in) {
            return new PhotoContainer(in);
        }

        public PhotoContainer[] newArray(int size) {
            return new PhotoContainer[size];
        }
    };
    private String url;

    public PhotoContainer(String url) {
        this.url = url;
    }


    public PhotoContainer(Parcel in) {
        url = in.readString();
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
    }
}