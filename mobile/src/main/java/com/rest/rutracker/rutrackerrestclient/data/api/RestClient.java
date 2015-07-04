package com.rest.rutracker.rutrackerrestclient.data.api;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestClient {

    public static final String API_KEY = "bdf6064c9b5a4011ee2f36b082bb4e5d";
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    private static final String REST_CLIENT = "RestClient";
    private static final String UTF_8 = "UTF-8";

    public RestClient() {
    }

    public ApiResponse doGet(String url) {
        return doGet(url, null);
    }

    public ApiResponse doGet(String url, Map<String, String> headers) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod(GET);
          //  connection.setDoInput(true);

        //    setDefaultHeaders(connection);
            if (headers != null) {
                for (Map.Entry<String, String> currentHeader : headers.entrySet()) {
                    connection.setRequestProperty(currentHeader.getKey(), currentHeader.getValue());
                }
            }
            int responseCode = connection.getResponseCode();
            apiResponse = new ApiResponse(responseCode, connection.getInputStream());
            Log.d(REST_CLIENT, "doGet: " + url);
        } catch (Exception e) {
            Log.e(REST_CLIENT, Log.getStackTraceString(e));
        }

        return apiResponse;
    }

    public ApiResponse doPost(String url, Map<String, Object> postParams, String string) {
        ApiResponse apiResponse = new ApiResponse();

        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod(POST);
            setDefaultHeaders(connection);

            byte[] postDataBytes;
            StringBuilder postData = new StringBuilder();
            if (postParams != null) {
                for (Map.Entry<String, Object> param : postParams.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), UTF_8));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), UTF_8));
                }
                postDataBytes = postData.toString().getBytes(UTF_8);
                connection.getOutputStream().write(postDataBytes);
            }

            postDataBytes = string.getBytes(UTF_8);
            connection.getOutputStream().write(postDataBytes);

            int responseCode = connection.getResponseCode();
            apiResponse = new ApiResponse(responseCode, connection.getInputStream());

            Log.d(REST_CLIENT, "doPost: " + url);
        } catch (final IOException e) {
            Log.e(REST_CLIENT, Log.getStackTraceString(e));
        }

        return apiResponse;
    }

    public ApiResponse doPut(String url, String string) {
        ApiResponse apiResponse = new ApiResponse();

        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod(PUT);
            setDefaultHeaders(connection);

            byte[] postDataBytes = string.getBytes(UTF_8);
            connection.getOutputStream().write(postDataBytes);

            int responseCode = connection.getResponseCode();
            apiResponse = new ApiResponse(responseCode, connection.getInputStream());

            Log.d(REST_CLIENT, "doPut: " + url);
        } catch (final IOException e) {
            Log.e(REST_CLIENT, Log.getStackTraceString(e));
        }

        return apiResponse;
    }

    public ApiResponse doDelete(String url) {
        ApiResponse apiResponse = new ApiResponse();

        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod(DELETE);
            setDefaultHeaders(connection);

            int responseCode = connection.getResponseCode();
            apiResponse = new ApiResponse(responseCode, connection.getInputStream());

            Log.d(REST_CLIENT, "doDelete: " + url);
        } catch (final IOException e) {
            Log.e(REST_CLIENT, Log.getStackTraceString(e));
        }

        return apiResponse;
    }


    public String doUploadFile(String requestURL, File uploadFile1, String fileParameterName) {
        String result = null;
        try {

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Token token=" + API_KEY);
            headers.put("Content-Type", "application/json");
            MultipartUtility multipart = new MultipartUtility(requestURL, UTF_8, headers);


            multipart.addFilePart(fileParameterName, uploadFile1);
            List<String> response = multipart.finish();

            StringBuilder responseBuffer = new StringBuilder();

            for (String line : response) {
                responseBuffer.append(line);
            }
            result = responseBuffer.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private void setDefaultHeaders(HttpURLConnection httpRequest) {
        //httpRequest.setRequestProperty("Authorization", "Token token=" + API_KEY);
        //httpRequest.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        //httpRequest.setRequestProperty("Accept-Encoding", UTF_8);
    }
}
