package com.rest.rutracker.rutrackerrestclient.data.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MultipartUtility {

    private static final String LINE_FEED = "\r\n";
    private final String boundary;
    private HttpURLConnection httpConn;
    private OutputStream outputStream;
    private PrintWriter writer;


    public MultipartUtility(String requestURL, String charset, Map<String, String> header)
            throws IOException {

        boundary = "===" + System.currentTimeMillis() + "===";

        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.setRequestMethod("POST");
        for (Map.Entry<String, String> entry : header.entrySet()) {
            httpConn.setRequestProperty(entry.getKey(), entry.getValue());
        }
        httpConn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary + "; charset=" + charset);

        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                true);
    }

    public void addFilePart(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--")
                .append(boundary)
                .append(LINE_FEED)
                .append("Content-Disposition: form-data; name=\"")
                .append(fieldName)
                .append("\"; filename=\"")
                .append(fileName)
                .append("\"")
                .append(LINE_FEED)
                .append("Content-Type: ")
                .append(URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED)
                .append("Content-Transfer-Encoding: binary")
                .append(LINE_FEED)
                .append(LINE_FEED)
                .flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();

        writer.append(LINE_FEED)
                .flush();
    }

    public List<String> finish() throws IOException {
        List<String> response = new ArrayList<>();

        writer.append(LINE_FEED).flush();
        writer.append("--")
                .append(boundary)
                .append("--")
                .append(LINE_FEED);
        writer.close();

        int mStatus = httpConn.getResponseCode();
        if (mStatus == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response.add(line);
            }
            reader.close();
            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + mStatus);
        }

        return response;
    }
}