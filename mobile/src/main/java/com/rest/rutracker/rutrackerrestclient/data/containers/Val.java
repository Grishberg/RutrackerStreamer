package com.rest.rutracker.rutrackerrestclient.data.containers;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/*
import static com.example.ilia.final_exercise.data.model.OpenDBHelper.CATEGORIES_TITLE;
import static com.example.ilia.final_exercise.data.model.OpenDBHelper.COLUMN_ID;
*/

/**
 * Created by ilia on 16.06.15.
 *
 * @author ilia
 */
public class Val implements Parcelable {

    public static final Creator<Val> CREATOR = new Creator<Val>() {
        public Val createFromParcel(Parcel in) {
            return new Val(in);
        }

        public Val[] newArray(int size) {
            return new Val[size];
        }
    };
    @SerializedName("info_hash")
    private String infoHash;
    @SerializedName("forum_id")
    private int forumId;
    @SerializedName("poster_id")
    private long posterId;
    private long size;
    @SerializedName("reg_time")
    private long regTime;
    @SerializedName("tor_status")
    private int torStatus;
    private int seeders;
    @SerializedName("topic_title")
    private String topicTitle;

    public Val(String infoHash, int forumId, long posterId, long size, long regTime, int torStatus,
               int seeders, String topicTitle) {
        this.infoHash = infoHash;
        this.forumId = forumId;
        this.posterId = posterId;
        this.size = size;
        this.regTime = regTime;
        this.torStatus = torStatus;
        this.seeders = seeders;
        this.topicTitle = topicTitle;
    }

    public Val(Parcel in) {
        infoHash = in.readString();
        forumId = in.readInt();
        posterId = in.readLong();
        size = in.readLong();
        regTime = in.readLong();
        torStatus = in.readInt();
        seeders = in.readInt();
        topicTitle = in.readString();
    }
/*

    public static Val fromCursor(Cursor c) {
        int idColId = c.getColumnIndex(COLUMN_ID);
        int titleColId = c.getColumnIndex(CATEGORIES_TITLE);

        return null;
//                new Val(
//                c.getLong(idColId),
//                c.getString(titleColId));

    }

    public long getId() {
        return forumId;
    }

    public String getTitle() {
        return topicTitle;
    }

    @Override
    public String toString() {
        return topicTitle;
    }

    public ContentValues buildContentValues() {
        ContentValues cv = new ContentValues();
        if (forumId >= 0) {
            cv.put(COLUMN_ID, forumId);
        }
        cv.put(CATEGORIES_TITLE, topicTitle);
        return cv;
    }
*/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(topicTitle);
        dest.writeInt(forumId);
        dest.writeLong(posterId);
        dest.writeLong(size);
        dest.writeLong(regTime);
        dest.writeInt(torStatus);
        dest.writeInt(seeders);
        dest.writeString(topicTitle);

    }
}