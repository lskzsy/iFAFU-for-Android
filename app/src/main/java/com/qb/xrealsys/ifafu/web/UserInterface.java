package com.qb.xrealsys.ifafu.web;

import android.util.Log;

import com.qb.xrealsys.ifafu.R;
import com.qb.xrealsys.ifafu.UserController;
import com.qb.xrealsys.ifafu.model.Response;
import com.qb.xrealsys.ifafu.tool.HttpHelper;
import com.qb.xrealsys.ifafu.tool.HttpResponse;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sky on 10/02/2018.
 */

public class UserInterface extends WebInterface {

    private static final String LoginPage = "default6.aspx";

    public UserInterface(String inHost, UserController userController) throws IOException {
        super(inHost, userController);
    }

    public String getIndexUrl(String number) {
        return makeAccessUrlHead() + "xs_main.aspx?xh=" + number;
    }

    public Response Login(String account, String password) throws IOException {
        String accessUrl = makeAccessUrlHead() + LoginPage;
        if (!syncViewParams(accessUrl)) {
            return new Response(false, 0, R.string.error_view_params_not_found);
        }

        HttpHelper request           = new HttpHelper(accessUrl, "gbk");
        Map<String, String> postData = new HashMap<>();
        postData.put("__VIEWSTATE", URLEncoder.encode(viewState, "gbk"));
        postData.put("__VIEWSTATEGENERATOR", viewStateGenerator);
        postData.put("tname", "");
        postData.put("tbtns", "");
        postData.put("tnameXw", "yhdl");
        postData.put("tbtnsXw", URLEncoder.encode("yhdl|xwxsdl", "gbk"));
        postData.put("txtYhm", account);
        postData.put("txtXm", "");
        postData.put("txtMm", password);
        postData.put("rblJs", "%B5%C7+%C2%BC");
        postData.put("btnDl", "%D1%A7%C9%FA");

        HttpResponse response = request.Post(postData, false);
        if (response.getStatus() != 200) {
            return new Response(false, 0, R.string.error_system);
        }

        String       html     = response.getResponse();
        Pattern      patternA = Pattern.compile("alert\\('(.*)'\\)");
        Matcher      matcherA = patternA.matcher(html);
        if (matcherA.find()) {
            return new Response(false, 0, matcherA.group(1));
        }

        Pattern      patternB = Pattern.compile("xhxm\">(.*)同学");
        Matcher      matcherB = patternB.matcher(html);
        String       name     = "";
        if (matcherB.find()) {
            name = matcherB.group(1);
        }

        return new Response(true, 0, name);
    }
}
