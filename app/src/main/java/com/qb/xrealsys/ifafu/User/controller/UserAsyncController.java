package com.qb.xrealsys.ifafu.User.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.qb.xrealsys.ifafu.Base.controller.AsyncController;
import com.qb.xrealsys.ifafu.DB.UserConfig;
import com.qb.xrealsys.ifafu.R;
import com.qb.xrealsys.ifafu.Base.model.Response;
import com.qb.xrealsys.ifafu.Tool.ZFVerify;
import com.qb.xrealsys.ifafu.User.LoginActivity;
import com.qb.xrealsys.ifafu.User.delegate.ModifyPasswordCallbackDelegate;
import com.qb.xrealsys.ifafu.User.model.User;
import com.qb.xrealsys.ifafu.Tool.ConfigHelper;
import com.qb.xrealsys.ifafu.User.web.UserInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.util.Random;
import java.util.concurrent.ExecutorService;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sky on 10/02/2018.
 */

public class UserAsyncController extends AsyncController {

    private User          data;

    private UserInterface userInterface;

    private Context       context;

    private int           reloginCount;

    private ConfigHelper  configHelper;

//    private JSONObject    userList;

    private ZFVerify      zfVerify;

    public UserAsyncController(Context inContext, ExecutorService threadPool, ZFVerify zfVerify) throws IOException {
        super(threadPool);
        this.context            = inContext;

        this.data               = new User();

        this.data.setLogin(false);
        this.zfVerify           = zfVerify;
        this.configHelper       = new ConfigHelper(context);
        this.userInterface      = new UserInterface(configHelper.GetSystemValue("host"), this);
        this.reloginCount       = 0;
//        try {
//            String userListStr = configHelper.GetValue("userList");
//            if (userListStr == null) {
//                userList = new JSONObject("{}");
//            } else {
//                userList = new JSONObject(userListStr);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    public ZFVerify getZfVerify() {
        return zfVerify;
    }

    public String getIndexUrl() {
        return userInterface.getIndexUrl(data.getAccount());
    }

    public boolean ReLogin() {
        boolean ret = true;
        if (this.reloginCount > 3) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("message", "重试超过次数.");
            context.startActivity(intent);
            return true;
        }

        try {
            Response response = Login(data.getAccount(), data.getPassword(), true);
            if (response.isSuccess()) {
                this.reloginCount = 0;
                ret = false;
            } else if (response.getCode() < 0) {
                Intent intent = new Intent(context, LoginActivity.class);
                intent.putExtra("message", response.getMessage(context));
                context.startActivity(new Intent(context, LoginActivity.class));
            }
        } catch (IOException e) {
            ret = false;
        } finally {
            this.reloginCount++;
        }

        return ret;
    }

    public Response Login(String inAcc, String inPwd, boolean isSave) throws IOException {
        data.setAccount(inAcc);
        data.setPassword(inPwd);
        data.setToken(makeToken());

        Response response = userInterface.Login(data.getAccount(), data.getPassword());
        if (response.isSuccess()) {
            data.setLogin(true);
            data.setName(response.getMessage());
            if (isSave) {
                saveUserInfo();
            }

            UserConfig userConfig = Realm.getDefaultInstance().where(UserConfig.class)
                    .equalTo("account", data.getAccount()).findFirst();
            if (userConfig != null) {
                data.setAuthPassword(userConfig.getAuthPassword());
            }
//            data.setAuthPassword();

            return new Response(true, response.getCode(), R.string.success_login);
        }

        return response;
    }

    public void ModifyPassword(final ModifyPasswordCallbackDelegate delegate, final String newPassword) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = userInterface.modifyPassword(data.getAccount(), data.getPassword(), newPassword);
                    if (response.isSuccess()) {
                        data.setPassword(newPassword);
                        saveUserInfo();
                    }
                    delegate.modifyPasswordCallback(response);
                } catch (IOException e) {
                    e.printStackTrace();
                    delegate.modifyPasswordCallback(new Response(false, -1, "网络异常"));
                }
            }
        });
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

    public void updateData(User user) throws IOException {
        data          = user;
        userInterface = new UserInterface(configHelper.GetSystemValue("host"), this);
    }

    public RealmResults<UserConfig> getUserList() {
        return Realm.getDefaultInstance().where(UserConfig.class).findAll();
    }

    public User getData() {
        return data;
    }

    public boolean clearUserInfo(String number) {
        if (configHelper.GetValue("account").equals(number)) {
            return false;
        }

        final String inNumber = number;
        Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {
                RealmResults<UserConfig> results =
                        realm.where(UserConfig.class)
                                .equalTo("account", inNumber).findAll();
                results.deleteAllFromRealm();
            }
        });
//        userList.remove(number);
//        configHelper.SetValue("userList", userList.toString());
        return true;
    }

    public void saveAuthPassword(final String authPassword) {
        data.setAuthPassword(authPassword);

        Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {
                UserConfig userConfig =
                        realm.where(UserConfig.class)
                                .equalTo("account", data.getAccount()).findFirst();
                userConfig.setAuthPassword(authPassword);
                realm.insertOrUpdate(userConfig);
            }
        });
    }

    public void saveUserInfo() {
        configHelper.SetValue("account", data.getAccount());
        configHelper.SetValue("password", data.getPassword());

        Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                UserConfig user = new UserConfig(
                        data.getAccount(),
                        data.getPassword(),
                        data.getName());
                realm.insertOrUpdate(user);
            }
        });
    }

    public boolean haveAuthPassword() {
        return data.getAuthPassword() != null;
    }
}
