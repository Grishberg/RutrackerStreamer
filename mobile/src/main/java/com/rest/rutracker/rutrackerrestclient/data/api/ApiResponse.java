package com.rest.rutracker.rutrackerrestclient.data.api;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by ilia on 23.06.15.
 *
 * @author ilia
 */
public class ApiResponse {
    private InputStream mInputSream;
    private int status;

    public ApiResponse() {
        this(0, null);
    }

    public ApiResponse(int status, InputStream inputStream) {
        this.status = status;
        this.mInputSream = inputStream;
    }


    public InputStreamReader getInputStreamReader() {
        if (mInputSream == null) {
            return null;
        }
        return new InputStreamReader(mInputSream);
    }

    public InputStream getInputSream() {
        return mInputSream;
    }

    public int getStatus() {
        return status;
    }
}
