package com.qb.xrealsys.ifafu.User.web;

import android.util.Log;

import com.qb.xrealsys.ifafu.R;
import com.qb.xrealsys.ifafu.Tool.ZFVerify;
import com.qb.xrealsys.ifafu.User.controller.UserAsyncController;
import com.qb.xrealsys.ifafu.Base.model.Response;
import com.qb.xrealsys.ifafu.Tool.HttpHelper;
import com.qb.xrealsys.ifafu.Tool.HttpResponse;
import com.qb.xrealsys.ifafu.Base.web.WebInterface;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sky on 10/02/2018.
 */

public class UserInterface extends WebInterface {

    private static final String LoginPage = "default2.aspx";

    private static final String ModifyPage = "mmxg.aspx";

    public UserInterface(String inHost, UserAsyncController userController) throws IOException {
        super(inHost, userController);
    }

    public String getIndexUrl(String number) {
        return makeAccessUrlHead() + "xs_main.aspx?xh=" + number;
    }

    public String getVerifyCodeUrl() {
        return makeAccessUrlHead() + "CheckCode.aspx";
    }

    public Response Login(String account, String password) throws IOException {
        String accessUrl = makeAccessUrlHead() + LoginPage;
        if (!syncViewParams(accessUrl)) {
            return new Response(false, 0, R.string.error_view_params_not_found);
        }

        /* Get verify code */
        ZFVerify zfVerify = userController.getZfVerify();
        String verifyCode = zfVerify.todo(zfVerify.getVerifyImg(getVerifyCodeUrl()));

        HttpHelper request           = new HttpHelper(accessUrl, "gbk");
        Map<String, String> postData = new HashMap<>();
        postData.put("__VIEWSTATE", URLEncoder.encode(viewState, "gbk"));
        postData.put("__VIEWSTATEGENERATOR", viewStateGenerator);
        postData.put("Textbox1", "");
        postData.put("lbLanguage", "");
        postData.put("tbtnsXw", URLEncoder.encode("yhdl|xwxsdl", "gbk"));
        postData.put("txtUserName", account);
        postData.put("Button1", "");
        postData.put("txtSecretCode", verifyCode);
        postData.put("TextBox2", password);
        postData.put("hidPdrs", "");
        postData.put("hidsc", "");
        postData.put("RadioButtonList1", "%D1%A7%C9%FA");

        HttpResponse response = request.Post(postData, false);
        if (response.getStatus() != 200) {
            return new Response(false, 0, R.string.error_system);
        }
        if (response.getStatus() == -1) {
            return new Response(false, 0, R.string.error_network);
        }

        String       html     = response.getResponse();
        Pattern      patternA = Pattern.compile("alert\\('(.*?)'\\)");
        Matcher      matcherA = patternA.matcher(html);
        if (matcherA.find()) {
            if (matcherA.group(1).contains("验证码")) {
                return Login(account, password);
            } else {
                return new Response(false, -1, matcherA.group(1));
            }
        }

        String name = "佚名";
        if (html.contains("输入新密码")) {
            name = "新生";
            return new Response(true, 1, name);
        }

        Pattern      patternB = Pattern.compile("xhxm\">(.*)同学");
        Matcher      matcherB = patternB.matcher(html);
        if (matcherB.find()) {
            name = matcherB.group(1);
        }

        return new Response(true, 0, name);
    }

    public Response modifyPassword(String account, String oldPassword, String newPassword) throws IOException {
        String accessUrl = makeAccessUrlHead() + ModifyPage;
        accessUrl += "?xh=" + account;
        accessUrl += "&rmm=true";
        Log.d("debug", accessUrl);

        Map<String, String> header = new HashMap<>();
        header.put("Host", "jwgl.fafu.edu.cn");
        header.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");

        HttpHelper      request     = new HttpHelper(accessUrl, "gbk");
        HttpResponse    response    = request.Get(header);
        if (response.getStatus() != 200) {
            return new Response(false, 0, "网络异常，稍后再试");
        }
        if (!LoginedCheck(response.getResponse())) {
            return modifyPassword(account, oldPassword, newPassword);
        }
        setViewParams(response.getResponse());

        Map<String, String> postData = new HashMap<>();
        postData.put("__VIEWSTATE", URLEncoder.encode(viewState, "gbk"));
        postData.put("__VIEWSTATEGENERATOR", viewStateGenerator);
        postData.put("TextBox2", oldPassword);
        postData.put("TextBox3", newPassword);
        postData.put("Textbox4", newPassword);
        postData.put("Button1", "%D0%DE++%B8%C4");
        response = request.Post(header, postData, false);
        if (response.getStatus() != 200) {
            return new Response(false, 0, "网络异常，稍后再试");
        }

        Pattern      patternA = Pattern.compile("alert\\('(.*?)'\\)");
        Matcher      matcherA = patternA.matcher(response.getResponse());
        if (matcherA.find() && !matcherA.group(1).contains("修改成功")) {
            return new Response(false, 0, matcherA.group(1));
        }

        return new Response(true, 0, "修改成功");
    }
}
