package com.qb.xrealsys.ifafu.model;

import java.io.Serializable;

/**
 * Created by sky on 11/02/2018.
 */

public class UserData implements Serializable {

    private String        name;

    private String        account;

    private String        password;

    private String        token;

    private boolean       isLogin;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
