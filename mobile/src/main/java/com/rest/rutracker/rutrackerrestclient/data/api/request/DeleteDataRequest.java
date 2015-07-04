package com.rest.rutracker.rutrackerrestclient.data.api.request;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by ilia on 23.06.15.
 *
 * @author ilia
 */
public class DeleteDataRequest implements Parcelable {
    public static final Creator<DeleteDataRequest> CREATOR = new Creator<DeleteDataRequest>() {

        public DeleteDataRequest createFromParcel(Parcel in) {
            return new DeleteDataRequest(in);
        }

        public DeleteDataRequest[] newArray(int size) {
            return new DeleteDataRequest[size];
        }
    };
    private long id;

    public DeleteDataRequest(long id) {
        this.id = id;
    }


    public DeleteDataRequest(Parcel in) {
        id = in.readLong();
    }

    public long getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
    }
}