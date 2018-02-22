package com.qb.xrealsys.ifafu.tool;

import android.graphics.Bitmap;

import java.io.IOException;

/**
 * Created by sky on 22/02/2018.
 */

public class OSSHelper {

    private String host;

    private Bitmap background;

    private Bitmap ad;

    public OSSHelper(String host) {
        this.host = host;
    }

    public void syncData() throws IOException {
        setBackground();
        setAd();
    }

    public void setBackground() throws IOException {
        String accessUrl = host + "ifafuBac.jpg";

        HttpHelper request = new HttpHelper(accessUrl);
        background = request.GetHttpGragh();
    }

    public void setAd() throws IOException {
        String accessUrl = host + "ifafuAd.jpg";

        HttpHelper request = new HttpHelper(accessUrl);
        ad = request.GetHttpGragh();
    }

    public Bitmap getBackground() {
        return background;
    }

    public Bitmap getAd() {
        return ad;
    }
}
