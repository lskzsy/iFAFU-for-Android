package com.qb.xrealsys.ifafu.Tool;

import android.graphics.Bitmap;

import java.io.IOException;

/**
 * Created by sky on 22/02/2018.
 */

public class OSSHelper {

    private String host;

    private String key;

    private Bitmap background;

    private Bitmap ad;

    private String responsibility;

    public OSSHelper(String host, String key) {
        this.host = host;
        this.key  = key;
    }

    public void syncData() throws IOException {
        setBackground();
        setAd();
        setResponsibility();
    }

    public void setBackground() throws IOException {
        String accessUrl = host + "ifafuBac.jpg";

        HttpHelper request = new HttpHelper(accessUrl);
        background = request.GetHttpGragh();
    }

    public void setResponsibility() throws IOException {
//        String accessUrl = host + "ifafuPrivacy.txt";

//        HttpHelper request  = new HttpHelper(accessUrl);
//        responsibility      = request.GetAES(this.key);
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
