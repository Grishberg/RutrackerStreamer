package com.rest.rutracker.rutrackerrestclient.data.api;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;

import com.rest.rutracker.rutrackerrestclient.data.api.request.DataAuthRequest;
import com.rest.rutracker.rutrackerrestclient.data.api.request.DataRequest;
import com.rest.rutracker.rutrackerrestclient.data.api.request.DeleteDataRequest;
import com.rest.rutracker.rutrackerrestclient.data.api.request.ViewTopicRequest;
import com.rest.rutracker.rutrackerrestclient.data.api.response.DataLoginResponse;
import com.rest.rutracker.rutrackerrestclient.data.api.response.DataResponse;

import java.io.Serializable;

public class ApiService extends IntentService {

    public static final String CALLBACK_KEY = "CALLBACK_KEY";
    public static final String ACTION_KEY = "ACTION_KEY";
    public static final String ERROR_KEY = "ERROR_KEY";
    public static final String REQUEST_OBJECT_KEY = "REQUEST_OBJECT_KEY";
    public static final String RESPONSE_OBJECT_KEY = "RESPONSE_OBJECT_KEY";

    public static final int ACTION_GET_OUR_MOVIE_RSS_FEED   = 101;
    public static final int ACTION_GET_IMAGE_URL            = 123;
    public static final int ACTION_GET_TORRENT              = 124;
    public static final int ACTION_GET_AUTH              	= 125;

    public static final int ACTION_GET_CATEGORIES = 1;
    public static final int ACTION_GET_ARTICLES = 2;
    public static final int ACTION_ADD_ARTICLE = 3;
    public static final int ACTION_EDIT_ARTICLE = 4;
    public static final int ACTION_DELETE_ARTICLE = 5;
    private static final String API_SERVICE = "ApiService";
    private boolean destroyed;
    private ResultReceiver receiver;

    public ApiService() {
        super(API_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        receiver = intent.getParcelableExtra(CALLBACK_KEY);
        int action = intent.getIntExtra(ACTION_KEY, -1);
        Bundle data = processIntent(intent, action);
        sentMessage(action, data);
    }

    private Bundle processIntent(Intent intent, int action) {

        DataResponse response;
        Requester requester = new Requester();
        Bundle bundle = new Bundle();

        switch (action) {
			case ACTION_GET_AUTH:
				DataLoginResponse loginResponse = requester.getAuth((DataAuthRequest) getSerializableRequestObject(intent));
				if (loginResponse == null) {
					bundle.putBoolean(ERROR_KEY, true);
				} else {
					bundle.putSerializable(RESPONSE_OBJECT_KEY, loginResponse);
				}
				return bundle;

			case ACTION_GET_OUR_MOVIE_RSS_FEED:
                response = requester.getMovies();
                break;

            case ACTION_GET_IMAGE_URL:
                response = requester.getDescription((ViewTopicRequest) getSerializableRequestObject(intent));
                break;

            case ACTION_GET_TORRENT:
                response = requester.getTorrent((ViewTopicRequest) getSerializableRequestObject(intent));
                break;

            default:
                return null;

        }

        if (response == null) {
            bundle.putBoolean(ERROR_KEY, true);
        } else {
            bundle.putSerializable(RESPONSE_OBJECT_KEY, response);
        }
        return bundle;
    }

    private Parcelable getRequestObject(Intent intent) {
        return intent.getParcelableExtra(REQUEST_OBJECT_KEY);
    }
    private Serializable getSerializableRequestObject(Intent intent) {
        return intent.getSerializableExtra(REQUEST_OBJECT_KEY);
    }

    private void sentMessage(int code, Bundle data) {
        if (!destroyed && receiver != null) {
            receiver.send(code, data);
        }
    }

    @Override
    public void onCreate() {
        destroyed = false;
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        destroyed = true;
        receiver = null;
        super.onDestroy();
    }
}
