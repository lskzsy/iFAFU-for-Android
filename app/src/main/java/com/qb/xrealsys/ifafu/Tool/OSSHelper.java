package com.qb.xrealsys.ifafu.Tool;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by sky on 22/02/2018.
 */

public class OSSHelper {

    private String      host;

    private String      key;

    private Bitmap      background;

    private Bitmap      ad;

    private String      responsibility;

    private JSONObject  updateInf;

    private String      studyTime;

    public OSSHelper(String host, String key) {
        this.host = host;
        this.key  = key;
    }

    public String getStudyTime() {
        return studyTime;
    }

    public void syncData() throws IOException {
        setBackground();
        setAd();
//        setResponsibility();
        setUpdateInf();
        setStudyTime();
    }

    public void setStudyTime() throws IOException {
        String accessUrl = host + "iFAFU/studyTime.txt";

        HttpHelper      request = new HttpHelper(accessUrl);
        HttpResponse    response = request.Get();

        if (response.getStatus() == 200) {
            studyTime = response.getResponse();
        }
    }

    public JSONObject getUpdateInf() {
        return updateInf;
    }

    public void setUpdateInf() throws IOException {
        String accessUrl = host + "iFAFU/latestVersion.txt";

        HttpHelper      request = new HttpHelper(accessUrl);
        HttpResponse    response = request.Get();

        if (response.getStatus() == 200) {
            try {
                updateInf = new JSONObject(response.getResponse());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
