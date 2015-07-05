package com.rest.rutracker.rutrackerrestclient.data.api;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;


import com.rest.rutracker.rutrackerrestclient.data.api.request.DataAuthRequest;
import com.rest.rutracker.rutrackerrestclient.data.api.request.ViewTopicRequest;
import com.rest.rutracker.rutrackerrestclient.data.api.response.DataLoginResponse;
import com.rest.rutracker.rutrackerrestclient.data.api.response.DataResponse;
import com.rest.rutracker.rutrackerrestclient.data.api.request.DataRequest;
import com.rest.rutracker.rutrackerrestclient.data.api.request.DeleteDataRequest;
import com.rest.rutracker.rutrackerrestclient.data.api.response.DescriptionDataResponse;
import com.rest.rutracker.rutrackerrestclient.data.api.response.TorrentFileDataResponse;
import com.rest.rutracker.rutrackerrestclient.data.containers.Article;
import com.rest.rutracker.rutrackerrestclient.data.containers.TopicData;
import com.rest.rutracker.rutrackerrestclient.data.containers.Val;

import pct.droid.activities.StreamLoadingActivity;
import pct.droid.base.PopcornApplication;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rest.rutracker.rutrackerrestclient.data.model.RutrackerFeedParcer;
import com.rest.rutracker.rutrackerrestclient.ui.framework.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rest.rutracker.rutrackerrestclient.data.model.AppContentProvider.CONTENT_URI_ARTICLES;
import static com.rest.rutracker.rutrackerrestclient.data.model.AppContentProvider.getArticlesUri;
import static com.rest.rutracker.rutrackerrestclient.data.model.OpenDBHelper.ARTICLES_PHOTO_URL;
import static com.rest.rutracker.rutrackerrestclient.data.model.OpenDBHelper.COLUMN_ID;



public class Requester {

    public static final String DEFAULT_MOVIE_URL = "2200.atom";

    private static final String RUTRACKER_AUTH_URL  = "http://login.rutracker.org/forum/login.php";
    private static final String SERVER = "http://feed.rutracker.org/atom/f/";
    private static final String BASE_VIEW_TOPIC_URL = "http://rutracker.org/forum/viewtopic.php?t=";
    private static final String BASE_TORRENT_URL = "http://dl.rutracker.org/forum/dl.php?t=";
    private static final String BASE_RUTRACKER_URL = "http://rutracker.org/";

	private static final String POST_PARAM_REDIRECT			= "redirect";
	private static final String POST_PARAM_LOGIN_USER		= "login_user";
	private static final String POST_PARAM_LOGIN			= "login";
	private static final String POST_PARAM_LOGIN_PASSWORD	= "login_password";



    private static final String TAG = Requester.class.getSimpleName();

    public Requester() {
    }

	private Map<String,String> auth(String login, String password){
		Map<String, String> cookie = null;
		String result = null;
		try {


			Connection.Response response = Jsoup.connect("http://login.rutracker.org/forum/login.php")
					.method(Connection.Method.GET)
					.execute();

			response = Jsoup.connect("http://login.rutracker.org/forum/login.php")
					.referrer("http://rutracker.org/forum/index.php")
					.data("login_username", login)
					.data("login", login)
					.data("login_password", password)
					.cookies(response.cookies())
					.method(Connection.Method.POST)
					.execute();

			cookie	= response.cookies();


/*
			if(cookie!=null && cookie.size() > 0){
				StringBuilder cookieContainer = new StringBuilder();
				for(Map.Entry<String,String> entry: cookie.entrySet()){
					if(cookieContainer.length() > 0){
						cookieContainer.append(";");
					}
					cookieContainer.append(entry.getKey());
					cookieContainer.append(":");
					cookieContainer.append(entry.getValue());
				}
				result	= cookieContainer.toString();
			}
			*/

		}catch(Exception e){e.printStackTrace();}
		return cookie;
	}

	public DataLoginResponse getAuth(DataAuthRequest data){

		Map<String, String> cookie	= auth(data.getLogin(), data.getPassword());
		PopcornApplication.setCookie( cookie );

		return new DataLoginResponse(cookie != null);

	}

    public DataResponse getMovies() {

        RestClient restClient   = new RestClient();
        String url              = getCategoriesUrl();
		//-------------------Authorization -------------

        ApiResponse response	= null;
		response    			= restClient.doGet(url);

        RutrackerFeedParcer rutrackerFeedParcer = new RutrackerFeedParcer();
        DataResponse dataResponse = null;
        try {
            List<RutrackerFeedParcer.Entry> parse = rutrackerFeedParcer.parse(response.getInputSream());
            dataResponse=new DataResponse(parse);
        } catch (IOException | XmlPullParserException e) {
            Log.e(TAG, "EXCEPTION", e);
        }
        return dataResponse;
    }


	public DescriptionDataResponse getDescription(ViewTopicRequest keyViewTopic) {

		RestClient restClient = new RestClient();
		String url = getDescriptionUrl(keyViewTopic.getKeyViewTopic());
		ApiResponse response = restClient.doGet(url);
		DescriptionDataResponse dataResponse = null;
		try {
			Document doc = Jsoup.parse(response.getInputSream(), "windows-1251", BASE_RUTRACKER_URL);
			Elements elementsPostImage = doc.getElementsByClass("postImg");
			for (Element thisArt : elementsPostImage) {
				String title = thisArt.attr("title");
				dataResponse = new DescriptionDataResponse(title);
				Log.d(TAG, "hello");
				break;
			}
			Element content = doc.getElementsByClass("post_wrap").first();

			String html = content.html();
			dataResponse.setHtml(html);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dataResponse;
	}

	public TorrentFileDataResponse getTorrent(ViewTopicRequest keyViewTopic) {

        RestClient restClient 	= new RestClient();
        String url 				= getTorrentUrl(keyViewTopic.getKeyViewTopic());

        ApiResponse response 	= restClient.doPostTorrent(url, null, PopcornApplication.getCookie());
		byte[] torrent			= response.getAsByteArray();

		TorrentFileDataResponse torrentResponse = new TorrentFileDataResponse(torrent);

        return torrentResponse;
    }


    private static JSONObject convertInputStreamToJSONObject(InputStream inputStream)
            throws JSONException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        try {
            while((line = bufferedReader.readLine()) != null)
                result += line;
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject(result); }


    private String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }


    @NonNull
    private Gson getGSON() {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss Z").create();
    }

    private String getCategoriesUrl() {
        return SERVER + DEFAULT_MOVIE_URL;
    }

    private String getDescriptionUrl(String keyViewTopic) {
        return BASE_VIEW_TOPIC_URL + keyViewTopic;
    }

    private String getTorrentUrl(String keyViewTopic) {
        return BASE_TORRENT_URL + keyViewTopic;
    }

    private String getArticlesUrl() {
        return SERVER + "articles.json";
    }

    private String putArticleUrl() {
        return SERVER + "articles.json";
    }

    private String editArticleUrl(long id) {
        return SERVER + String.format("articles/%d.json", id);
    }

    private String deleteArticleUrl(long id) {
        return SERVER + String.format("articles/%d.json", id);
    }

    private String addImageUrl(long id) {
        return SERVER + String.format("articles/%d/photos.json", id);
    }

    private String formatArrayCondition(String field, ArrayList<Long> ids) {
        StringBuilder result = new StringBuilder();

        result.append(field);
        result.append(" in (");

        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(ids.get(i).toString());
        }

        result.append(" ) ");

        return result.toString();
    }

    private <T> T deserialize(Gson gson, ApiResponse response, Class<T> classOfT) {

        if (response != null) {
            try {
                return gson.fromJson(response.getInputStreamReader(), classOfT);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    private <T> T deserialize(Gson gson, String response, Class<T> classOfT) {
        if (response != null) {
            try {
                return gson.fromJson(response, classOfT);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }


    private static class CategoriesContainer {
        public Val[] categories;
    }

    private static class ResultContainer {
        public TopicData topicData;
    }

    private static class ArticlesContainer {
        public Article[] articles;
    }

    private static class ArticleContainer {
        public Article article;
    }

    private static class PhotoContainer {
        public Photo photo;

        class Photo {
            public long id;
            public String url;
        }
    }
}

