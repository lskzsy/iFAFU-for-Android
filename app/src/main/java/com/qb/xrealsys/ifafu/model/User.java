package com.qb.xrealsys.ifafu.model;

import android.content.Context;

import com.qb.xrealsys.ifafu.R;
import com.qb.xrealsys.ifafu.tool.ConfigHelper;
import com.qb.xrealsys.ifafu.web.UserInterface;

import java.io.IOException;

import java.util.Random;

/**
 * Created by sky on 10/02/2018.
 */

public class User {

    private UserData      data;

    private UserInterface userInterface;

    private Context       context;

    private ConfigHelper  configHelper;

    public User(Context inContext) throws IOException {
        context         = inContext;

        data = new UserData();

        data.setLogin(false);
        data.setToken(makeToken());

        configHelper    = new ConfigHelper(context);
        userInterface   = new UserInterface(configHelper.GetValue("host"), data.getToken());
    }

    public Response Login(String inAcc, String inPwd, boolean isSave) throws IOException {
        data.setAccount(inAcc);
        data.setPassword(inPwd);

        Response response = userInterface.Login(data.getAccount(), data.getPassword());
        if (response.isSuccess()) {
            data.setLogin(true);
            data.setName(response.getMessage());
            if (isSave) {
                configHelper.SetValue("account", data.getAccount());
                configHelper.SetValue("password", data.getPassword());
            }

            return new Response(true, 0, R.string.success_login);
        }

        return new Response(false, 0, response.getMessage());
    }

    private String makeToken() {
        String randomString = "abcdefghijklmnopqrstuvwxyz12345";
        String token = "ifafu";
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < 19; i++) {
            token += randomString.charAt(random.nextInt(randomString.length()));
        }
        return token;
    }

    public boolean isLogin() {
        return data.isLogin();
    }

    public void updateData(UserData userData) throws IOException {
        data          = userData;
        userInterface = new UserInterface(configHelper.GetValue("host"), data.getToken());
    }

    public UserData getData() {
        return data;
    }
}
