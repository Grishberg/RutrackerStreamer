package com.rest.rutracker.rutrackerrestclient.data.api.response;

/**
 * Created by ilia on 03.07.15.
 *
 * @author ilia
 */
public class DescriptionDataResponse extends DataResponse {

    private String urlImage;
    private String mHtml;

    public DescriptionDataResponse(String urlImage) {
        super();
        this.urlImage = urlImage;
    }


    public String getUrlImage() {
        return urlImage;
    }

    public void setHtml(String html) {
        mHtml = html;
    }

    public String getHtml() {
        return mHtml;
    }
}
