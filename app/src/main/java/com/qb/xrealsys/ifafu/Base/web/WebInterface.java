package com.qb.xrealsys.ifafu.Base.web;

import com.qb.xrealsys.ifafu.User.controller.UserAsyncController;
import com.qb.xrealsys.ifafu.Base.model.Search;
import com.qb.xrealsys.ifafu.Tool.HttpHelper;
import com.qb.xrealsys.ifafu.Tool.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sky on 10/02/2018.
 */

public class WebInterface {

    protected String host;

    protected String viewState;

    protected String viewStateGenerator;

    protected UserAsyncController userController;

    public WebInterface(String inHost, UserAsyncController userController) {
        this.userController = userController;
        this.host           = inHost;
    }

    public String getViewState() {
        return viewState;
    }

    public String getViewStateGenerator() {
        return viewStateGenerator;
    }

    public Map<String, String> GetRefererHeader(String number) {
        Map<String, String> header = new HashMap<>();
        header.put("Referer", makeAccessUrlHead() + "xs_main.aspx?xh=" + number);
        return header;
    }

    protected String makeAccessUrlHead() {
        String token = userController.getData().getToken();
        return String.format(Locale.getDefault(), "%s/(%s)/", host, token);
    }

    protected void getSearchOptions(
            String html,
            Search searchModel,
            String splitYear,
            String splitTerm) {
        int yearStrIndex = html.indexOf(splitYear);
        int termStrIndex = html.indexOf(splitTerm);
        Pattern patternA = Pattern.compile("<option( selected=\"selected\"){0,1} value=\"(.*)\">");

        /* Get options for searching */
        String yearOptionString         = html.substring(yearStrIndex, termStrIndex);
        Matcher matcherYearOption       = patternA.matcher(yearOptionString);
        List<String> searchYearOptions  = searchModel.getSearchYearOptions();
        while (matcherYearOption.find()) {
            searchYearOptions.add(matcherYearOption.group(2));
            if (matcherYearOption.group(1) != null) {
                searchModel.setSelectedYearOption(searchYearOptions.size() - 1);
            }
        }

        String termOptionString         = html.substring(termStrIndex);
        Matcher matcherTermOption       = patternA.matcher(termOptionString);
        List<String> searchTermOptions  = searchModel.getSearchTermOptions();
        while (matcherTermOption.find()) {
            searchTermOptions.add(matcherTermOption.group(2));
            if (matcherTermOption.group(1) != null) {
                searchModel.setSelectedTermOption(searchTermOptions.size() - 1);
            }
        }
    }

    protected boolean LoginedCheck(String html) {
        if (html.indexOf("请登录") > 0) {
            return userController.ReLogin();
        }
        return true;
    }

    protected boolean syncViewParams(String url) throws IOException {
        HttpHelper   request  = new HttpHelper(url);
        HttpResponse response = request.Get();
        if (response.getStatus() != 200) {
            return false;
        } else {
            String html = response.getResponse();
            return setViewParams(html);
        }
    }

    protected boolean setViewParams(String html) {
        Pattern patternA = Pattern.compile("__VIEWSTATE\" value=\"(.*)\"");
        Pattern patternB = Pattern.compile("__VIEWSTATEGENERATOR\" value=\"(.*)\"");
        Matcher matcherA = patternA.matcher(html);
        Matcher matcherB = patternB.matcher(html);
        if (matcherA.find() && matcherB.find()) {
            viewState           = matcherA.group(1);
            viewStateGenerator  = matcherB.group(1);
            viewState = viewState.replace(" ", "");
            viewState = viewState.replace("\n", "");
//            Log.d("debug", viewState);
//            Log.d("debug", viewStateGenerator);

            return true;
        } else {
            return false;
        }
    }

    protected float getRealFloatData(String srcData) {
        if (srcData.equals("&nbsp;")) {
            return (float) 0.0;
        } else {
            return Float.parseFloat(srcData);
        }
    }

    protected String getRealStringData(String srcData) {
        if (srcData.equals("&nbsp;")) {
            return "";
        } else {
            return srcData;
        }
    }
}
