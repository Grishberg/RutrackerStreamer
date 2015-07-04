package com.rest.rutracker.rutrackerrestclient.data.api;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.Window;


import com.rest.rutracker.rutrackerrestclient.data.api.request.ViewTopicRequest;
import com.rest.rutracker.rutrackerrestclient.data.api.response.DataResponse;
import com.rest.rutracker.rutrackerrestclient.data.api.request.DataRequest;
import com.rest.rutracker.rutrackerrestclient.data.api.request.DeleteDataRequest;
import com.rest.rutracker.rutrackerrestclient.data.api.response.DescriptionDataResponse;
import com.rest.rutracker.rutrackerrestclient.data.containers.Article;
import com.rest.rutracker.rutrackerrestclient.data.containers.TopicData;
import com.rest.rutracker.rutrackerrestclient.data.containers.Val;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rest.rutracker.rutrackerrestclient.data.model.RutrackerFeedParcer;
import com.rest.rutracker.rutrackerrestclient.ui.framework.Utils;

import org.json.JSONException;
import org.json.JSONObject;
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
import java.util.List;

import pct.droid.base.PopcornApplication;

import static com.rest.rutracker.rutrackerrestclient.data.model.AppContentProvider.CONTENT_URI_ARTICLES;
import static com.rest.rutracker.rutrackerrestclient.data.model.AppContentProvider.getArticlesUri;
import static com.rest.rutracker.rutrackerrestclient.data.model.OpenDBHelper.ARTICLES_PHOTO_URL;
import static com.rest.rutracker.rutrackerrestclient.data.model.OpenDBHelper.COLUMN_ID;


public class Requester {

    public static final String DEFAULT_MOVIE_URL = "2200.atom";

    private static final String SERVER = "http://feed.rutracker.org/atom/f/";
    private static final String BASE_VIEW_TOPIC_URL = "http://rutracker.org/forum/viewtopic.php?t=";
    private static final String BASE_RUTRACKER_URL = "http://rutracker.org/";


    private static final String TAG = Requester.class.getSimpleName();

    public Requester() {
    }

    public DataResponse getMovies() {

        RestClient restClient = new RestClient();
        String url = getCategoriesUrl();
        ApiResponse response = restClient.doGet(url);

        RutrackerFeedParcer rutrackerFeedParcer=new RutrackerFeedParcer();
        DataResponse dataResponse = null;
        try {
            List<RutrackerFeedParcer.Entry> parse = rutrackerFeedParcer.parse(response.getInputSream());
            dataResponse=new DataResponse(parse);
        } catch (IOException | XmlPullParserException e) {
            Log.e(TAG, "EXCEPTION", e);
        }
        return dataResponse;
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

    public DescriptionDataResponse getDescription(ViewTopicRequest keyViewTopic) {

        RestClient restClient = new RestClient();
        String url = getDedcriptionUrl(keyViewTopic.getKeyViewTopic());
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



    private String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }


    public DataResponse getCategories() {

        RestClient restClient = new RestClient();
        String url = getCategoriesUrl();
        ApiResponse response = restClient.doGet(url);
        Gson gson = new Gson();
        JSONObject newJsonObject=null;
        try {
            newJsonObject=convertInputStreamToJSONObject(response.getInputSream());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ResultContainer responseContainer = deserialize(gson
                , response
                , ResultContainer.class);

        return new DataResponse();
    }


    public DataResponse getArticles() {

        RestClient restClient = new RestClient();
        String url = getArticlesUrl();
        ApiResponse response = restClient.doGet(url);
        Gson gson = getGSON();
        ArticlesContainer responseContainer = deserialize(gson, response, ArticlesContainer.class);

        if (responseContainer != null && responseContainer.articles != null) {
            ArrayList<Long> ids = new ArrayList<>();
            Cursor cursor = PopcornApplication.getAppContext().getContentResolver()
                    .query(CONTENT_URI_ARTICLES
                            , new String[]{COLUMN_ID}, null, null, null);

            while (cursor.moveToNext()) {
                ids.add(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
            }

            cursor.close();

            for (Article article : responseContainer.articles) {
                ids.remove(article.getId());

                PopcornApplication.getAppContext().getContentResolver()
                        .insert(CONTENT_URI_ARTICLES,
                                article.buildContentValues());
            }

            if (ids.size() > 0) {
                PopcornApplication.getAppContext().getContentResolver()
                        .delete(getArticlesUri()
                                , formatArrayCondition(COLUMN_ID, ids), null);
            }
        }
        return new DataResponse();
    }

    public DataResponse addArticle(DataRequest request) {
        long id = -1;
        RestClient restClient = new RestClient();
        String url = putArticleUrl();
        Gson gsonSerializer = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting().create();
        String jsonContent = gsonSerializer.toJson(request.getArticle());
        Gson gsonDeserializer = getGSON();
        ApiResponse response = restClient.doPost(url, null, jsonContent);
        ArticleContainer responseContainer =
                deserialize(gsonDeserializer, response, ArticleContainer.class);

        if (responseContainer != null && responseContainer.article != null) {
            id = responseContainer.article.getId();
            PopcornApplication.getAppContext().getContentResolver()
                    .insert(CONTENT_URI_ARTICLES
                            , responseContainer.article.buildContentValues());

            if (!TextUtils.isEmpty(request.getAdditionalData())) {
                addPhotoToArticle(id, request.getAdditionalData());
            }
        }
        return new DataResponse(id);
    }

    public DataResponse deleteArticle(DeleteDataRequest articleId) {

        long id = articleId.getId();
        String url = deleteArticleUrl(id);
        RestClient restClient = new RestClient();
        ApiResponse response = restClient.doDelete(url);

        if (response.getStatus() == 200) {
            PopcornApplication.getAppContext().getContentResolver()
                    .delete(getArticlesUri(id), null, null);
        } else {
            return new DataResponse(-1);
        }
        return new DataResponse(id);
    }

    public DataResponse editArticle(DataRequest request) {

        if (request.getArticle() == null) {
            return new DataResponse(-1);
        }

        long id = request.getArticle().getId();
        String url = editArticleUrl(id);
        Gson gsonSerializer = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String jsonContent = gsonSerializer.toJson(request.getArticle());
        RestClient restClient = new RestClient();
        Gson gson = getGSON();

        ApiResponse response = restClient.doPut(url, jsonContent);
        ArticleContainer responseContainer = deserialize(gson, response, ArticleContainer.class);

        if (responseContainer != null && responseContainer.article != null) {
            id = responseContainer.article.getId();
            PopcornApplication.getAppContext().getContentResolver()
                    .update(getArticlesUri(id)
                            , responseContainer.article.buildContentValues(), null, null);
            if (!TextUtils.isEmpty(request.getAdditionalData())) {
                addPhotoToArticle(id, request.getAdditionalData());
            }
        } else {
            new DataResponse(-1);
        }
        return new DataResponse(id);
    }

    private void addPhotoToArticle(long id, String imagePath) {

        RestClient restClient = new RestClient();
        String url = addImageUrl(id);
        Gson gson = new Gson();
        Uri uri = Uri.parse(imagePath);
        String utils = Utils.getPath(PopcornApplication.getAppContext(), uri);
        if (utils == null) return;
        File file = new File(utils);
        String response = restClient.doUploadFile(url, file, "photo[image]");
        PhotoContainer responseContainer = deserialize(gson, response, PhotoContainer.class);

        if (responseContainer != null && responseContainer.photo != null) {
            ContentValues values = new ContentValues();
            values.put(ARTICLES_PHOTO_URL, responseContainer.photo.url);
            PopcornApplication.getAppContext().getContentResolver()
                    .update(getArticlesUri(id)
                            , values, null, null);
        }
    }

    @NonNull
    private Gson getGSON() {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss Z").create();
    }

    private String getCategoriesUrl() {
        return SERVER + DEFAULT_MOVIE_URL;
    }

    private String getDedcriptionUrl(String keyViewTopic) {
        return BASE_VIEW_TOPIC_URL + keyViewTopic;
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

