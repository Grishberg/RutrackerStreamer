package com.rest.rutracker.rutrackerrestclient.ui.framework;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/**
 * Created by ilia on 23.06.15.
 *
 * @author ilia
 */
public class Utils {

    private static final String URI_STRING = "content://downloads/public_downloads";
    private static final String IMAGE = "image";
    private static final String VIDEO = "video";
    private static final String AUDIO = "audio";
    private static final String PRIMARY = "primary";
    private static final String SELECTION = "_id=?";
    private static final String CONTENT = "content";
    private static final String FILE = "file";
    private static final String COLUMN = "_data";
    private static final String EXTERNAL_STORAGE = "com.android.externalstorage.documents";
    private static final String PROVIDERS_DOWNLOADS = "com.android.providers.downloads.documents";
    private static final String PROVIDERS_MEDIA = "com.android.providers.media.documents";
    private static final String GOOGLE_APPS_PHOTOS = "com.google.android.apps.photos.content";

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if (PRIMARY.equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse(URI_STRING), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if (IMAGE.equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if (VIDEO.equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if (AUDIO.equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = SELECTION;
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if (CONTENT.equalsIgnoreCase(uri.getScheme())) {

            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        } else if (FILE.equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = COLUMN;
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return EXTERNAL_STORAGE.equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return PROVIDERS_DOWNLOADS.equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return PROVIDERS_MEDIA.equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return GOOGLE_APPS_PHOTOS.equals(uri.getAuthority());
    }
}
