package com.rest.rutracker.rutrackerrestclient.data.containers;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/*import static com.example.ilia.final_exercise.data.model.OpenDBHelper.CATEGORIES_TITLE;
import static com.example.ilia.final_exercise.data.model.OpenDBHelper.COLUMN_ID;*/

/**
 * Created by ilia on 16.06.15.
 *
 * @author ilia
 */
public class TopicData implements Parcelable {

    public static final Creator<TopicData> CREATOR = new Creator<TopicData>() {
        public TopicData createFromParcel(Parcel in) {
            return new TopicData(in);
        }

        public TopicData[] newArray(int size) {
            return new TopicData[size];
        }
    };
    private Val result;

    public TopicData(Val result) {
        this.result = result;
    }

    public TopicData(Parcel in) {
        result = in.readParcelable(Val.class.getClassLoader());
    }

/*    public static TopicData fromCursor(Cursor c) {
        int idColId = c.getColumnIndex(COLUMN_ID);
        int titleColId = c.getColumnIndex(CATEGORIES_TITLE);

        return null;
//                new Val(
//                c.getLong(idColId),
//                c.getString(titleColId));

    }*/

    public Val getVal() {
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(result, flags);

    }
}