package com.rest.rutracker.rutrackerrestclient.data.containers;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import static com.rest.rutracker.rutrackerrestclient.data.model.OpenDBHelper.*;


public class Article implements Parcelable {

    public static final Creator<Article> CREATOR = new Creator<Article>() {

        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
    private long id;
    @Expose
    private String title;
    @Expose
    private String description;
    private PhotoContainer photo;
    @Expose
    private boolean published;
    @Expose
    @SerializedName("category_id")
    private long categoryId;
    @SerializedName("created_at")
    private Date createdAt;
    @SerializedName("updated_at")
    private Date updatedAt;
    private boolean own;

    public Article(long id
            , String title, String description, String photoUrl, boolean isPublished
            , long categoryId, long created, long updated, boolean own) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.photo = new PhotoContainer(photoUrl);
        this.published = isPublished;
        this.categoryId = categoryId;
        this.createdAt = new Date(created);
        this.updatedAt = new Date(updated);
        this.own = own;

    }

    public Article(Parcel in) {

        id = in.readLong();
        title = in.readString();
        description = in.readString();
        photo = in.readParcelable(PhotoContainer.class.getClassLoader());
        published = in.readInt() == 1;
        categoryId = in.readLong();
        createdAt = new Date(in.readLong());
        updatedAt = new Date(in.readLong());
        own = in.readInt() == 1;
    }

    public static Article fromCursor(Cursor c) {
        int idColId = c.getColumnIndex(COLUMN_ID);
        int titleColId = c.getColumnIndex(ARTICLES_TITLE);
        int descriptionColId = c.getColumnIndex(ARTICLES_DESCRIPTION);
        int photoUrlColId = c.getColumnIndex(ARTICLES_PHOTO_URL);
        int isPublishColId = c.getColumnIndex(ARTICLES_PUBLISHED);
        int categoryColId = c.getColumnIndex(ARTICLES_CATEGORY_ID);
        int createdColId = c.getColumnIndex(ARTICLES_CREATED);
        int updatedColId = c.getColumnIndex(ARTICLES_PUBLISHED);
        int isMineColId = c.getColumnIndex(ARTICLES_OWN);

        return new Article(
                c.getLong(idColId),
                c.getString(titleColId),
                c.getString(descriptionColId),
                c.getString(photoUrlColId),
                c.getInt(isPublishColId) == 1,
                c.getLong(categoryColId),
                c.getLong(createdColId),
                c.getLong(updatedColId),
                c.getInt(isMineColId) == 1
        );
    }

    public ContentValues buildContentValues() {
        ContentValues cv = new ContentValues();
        if (id >= 0) {
            cv.put(COLUMN_ID, id);
        }
        cv.put(ARTICLES_TITLE, title);
        cv.put(ARTICLES_DESCRIPTION, description);
        if (photo != null) {
            cv.put(ARTICLES_PHOTO_URL, photo.getUrl());
        }
        cv.put(ARTICLES_PUBLISHED, published);
        cv.put(ARTICLES_CATEGORY_ID, categoryId);
        cv.put(ARTICLES_CREATED, createdAt.getTime());
        cv.put(ARTICLES_UPDATED, updatedAt.getTime());
        cv.put(ARTICLES_OWN, own);

        return cv;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPhotoUrl() {
        return photo.getUrl();
    }

    public boolean isPublished() {
        return published;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public Date getCreated() {
        return createdAt;
    }

    public boolean getIsMine() {
        return own;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeParcelable(photo, flags);
        dest.writeInt(published ? 1 : 0);
        dest.writeLong(categoryId);
        dest.writeLong(createdAt.getTime());
        dest.writeLong(updatedAt.getTime());
        dest.writeInt(own ? 1 : 0);
    }
}
