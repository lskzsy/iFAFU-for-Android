package com.qb.xrealsys.ifafu.web;

import android.app.Application;
import android.util.Log;

import com.qb.xrealsys.ifafu.MainApplication;
import com.qb.xrealsys.ifafu.tool.HttpHelper;
import com.qb.xrealsys.ifafu.tool.HttpResponse;

import java.io.IOException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sky on 10/02/2018.
 */

public class WebInterface {

    protected String host;

    protected String token;

    protected String accessUrlHead;

    protected String viewState;

    protected String viewStateGenerator;

    public WebInterface(String inHost, String inToken) {
        host          = inHost;
        token         = inToken;
        accessUrlHead = makeAccessUrlHead(host, token);
    }

    private String makeAccessUrlHead(String h, String t) {
        return String.format(Locale.getDefault(), "%s/(%s)/", h, t);
    }

    protected boolean syncViewParams(String url) throws IOException {
        HttpHelper   request  = new HttpHelper(url);
        HttpResponse response = request.Get();
        if (response.getStatus() != 200) {
            return false;
        } else {
            String  html     = response.getResponse();
            Pattern patternA = Pattern.compile("__VIEWSTATE\" value=\"(.*)\"");
            Pattern patternB = Pattern.compile("__VIEWSTATEGENERATOR\" value=\"(.*)\"");
            Matcher matcherA = patternA.matcher(html);
            Matcher matcherB = patternB.matcher(html);
            if (matcherA.find() && matcherB.find()) {
                viewState           = matcherA.group(1);
                viewStateGenerator  = matcherB.group(1);
                viewState = viewState.replace(" ", "");
                viewState = viewState.replace("\n", "");
//                Log.d("debug", viewState);
//                Log.d("debug", viewStateGenerator);

                return true;
            } else {
                return false;
            }
        }
    }
}
