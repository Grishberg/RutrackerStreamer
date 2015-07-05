package com.rest.rutracker.rutrackerrestclient.data.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import static com.rest.rutracker.rutrackerrestclient.data.model.OpenDBHelper.ARTICLES_CATEGORY_ID;
import static com.rest.rutracker.rutrackerrestclient.data.model.OpenDBHelper.CATEGORIES_TITLE;
import static com.rest.rutracker.rutrackerrestclient.data.model.OpenDBHelper.COLUMN_ID;
import static com.rest.rutracker.rutrackerrestclient.data.model.OpenDBHelper.TABLE_ARTICLES;
import static com.rest.rutracker.rutrackerrestclient.data.model.OpenDBHelper.TABLE_CATEGORIES;


public class AppContentProvider extends ContentProvider {

    private static final String AUTHORITY = "com.example.ilia.final_exercise.content_provider";

    private static final String PATH_CATEGORIES = TABLE_CATEGORIES;
    public static final Uri CONTENT_URI_CATEGORIES = Uri.parse("content://" + AUTHORITY + "/" + PATH_CATEGORIES);
    private static final String PATH_ARTICLES = TABLE_ARTICLES;
    public static final Uri CONTENT_URI_ARTICLES = Uri.parse("content://" + AUTHORITY + "/" + PATH_ARTICLES);
    private static final String PATH_CATEGORIES_NOT_EMPTY = "notEmptyCategories";
    public static final Uri CONTENT_URI_CATEGORIES_NOT_EMPTY = Uri.parse("content://" + AUTHORITY + "/" + PATH_CATEGORIES_NOT_EMPTY);

    private static final int CODE_CATEGORIES = 0;
    private static final int CODE_ARTICLES = 1;
    private static final int CODE_ARTICLES_ID = 2;
    private static final int CODE_CATEGORIES_NOT_EMPTY = 3;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static OpenDBHelper openDBHelper;

    static {
        URI_MATCHER.addURI(AUTHORITY, PATH_CATEGORIES, CODE_CATEGORIES);
        URI_MATCHER.addURI(AUTHORITY, PATH_ARTICLES, CODE_ARTICLES);
        URI_MATCHER.addURI(AUTHORITY, PATH_ARTICLES + "/#", CODE_ARTICLES_ID);
        URI_MATCHER.addURI(AUTHORITY, PATH_CATEGORIES_NOT_EMPTY, CODE_CATEGORIES_NOT_EMPTY);
    }

    public static Uri getArticlesUri() {
        return CONTENT_URI_ARTICLES;
    }

    public static Uri getArticlesUri(Long id) {
        return Uri.withAppendedPath(AppContentProvider.CONTENT_URI_ARTICLES, id.toString());
    }

    public synchronized static OpenDBHelper getDbHelper(Context context) {
        if (null == openDBHelper) {
            openDBHelper = new OpenDBHelper(context);
        }
        return openDBHelper;
    }

    @Override
    public boolean onCreate() {
        getDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int uriId = URI_MATCHER.match(uri);
        Cursor cursor;
        switch (uriId) {
            case CODE_CATEGORIES:
                cursor = openDBHelper.getReadableDatabase()
                        .query(TABLE_CATEGORIES, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_ARTICLES:
                cursor = openDBHelper.getReadableDatabase()
                        .query(TABLE_ARTICLES, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_ARTICLES_ID:
                cursor = openDBHelper.getReadableDatabase()
                        .query(TABLE_ARTICLES, projection
                                , COLUMN_ID + " = ?"
                                , new String[]{uri.getLastPathSegment()}, null, null, sortOrder);
                break;

            case CODE_CATEGORIES_NOT_EMPTY:
                cursor = getCategories();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String table = parseUri(uri);
        SQLiteDatabase db = openDBHelper.getWritableDatabase();
        long id = insertOrUpdateById(db, uri, table, values, COLUMN_ID);
        Uri resultUri = ContentUris.withAppendedId(uri, id);
        getContext().getContentResolver().notifyChange(resultUri, null);

        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int result = 0;
        int uriId = URI_MATCHER.match(uri);
        SQLiteDatabase db = openDBHelper.getWritableDatabase();
        switch (uriId) {
            case CODE_ARTICLES:
                result = db.delete(TABLE_ARTICLES, selection, selectionArgs);
                break;

            case CODE_ARTICLES_ID:
                String id = uri.getLastPathSegment();
                result = db.delete(TABLE_ARTICLES
                        , COLUMN_ID + " = ?", new String[]{id});
                break;
            case CODE_CATEGORIES:
                result = db.delete(TABLE_CATEGORIES, selection, selectionArgs);
                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int result = 0;
        int uriId = URI_MATCHER.match(uri);
        SQLiteDatabase db = openDBHelper.getWritableDatabase();
        switch (uriId) {

            case CODE_ARTICLES:
                result = db.update(TABLE_ARTICLES, values, selection, selectionArgs);
                break;

            case CODE_ARTICLES_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    result = db.update(TABLE_ARTICLES
                            , values, COLUMN_ID + " = ?", new String[]{id});
                } else {
                    result = db.update(TABLE_ARTICLES
                            , values, COLUMN_ID + " = " + id + " AND "
                            + selection, selectionArgs);
                }
                break;

            case CODE_CATEGORIES:
                result = db.update(TABLE_CATEGORIES, values, selection, selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    private String parseUri(Uri uri) {
        return parseUri(URI_MATCHER.match(uri));
    }

    private String parseUri(int match) {
        String table;
        switch (match) {
            case CODE_CATEGORIES:
                table = TABLE_CATEGORIES;
                break;
            case CODE_ARTICLES:
            case CODE_ARTICLES_ID:
                table = TABLE_ARTICLES;
                break;
            default:
                throw new IllegalArgumentException("Invalid DB code: " + match);
        }
        return table;
    }

    private long insertOrUpdateById(SQLiteDatabase db, Uri uri, String table,
                                    ContentValues values, String column) throws SQLiteConstraintException {
        long result = -1;

        try {
            result = db.insertOrThrow(table, null, values);
        } catch (SQLiteConstraintException e) {
            int nrRows = update(uri, values, column + "=?",
                    new String[]{values.getAsString(column)});
            if (nrRows == 0) {
                throw e;
            }
        }
        return result;
    }

    private Cursor getCategories() {
        SQLiteDatabase db = openDBHelper.getReadableDatabase();
        StringBuilder sqlBuilder;
        sqlBuilder = new StringBuilder("SELECT art.").append(ARTICLES_CATEGORY_ID)
                .append(" AS ").append(COLUMN_ID)
                .append(", cat.").append(CATEGORIES_TITLE)
                .append(" FROM ").append(TABLE_ARTICLES)
                .append(" AS art INNER JOIN ").append(TABLE_CATEGORIES)
                .append(" AS cat ON art.").append(ARTICLES_CATEGORY_ID)
                .append(" = cat.").append(COLUMN_ID)
                .append(" GROUP BY art.").append(ARTICLES_CATEGORY_ID);

        String sql = sqlBuilder.toString();
        return db.rawQuery(sql, null);
    }
}