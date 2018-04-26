package com.qb.xrealsys.ifafu.DB;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sky on 25/04/2018.
 */

public class UserConfig extends RealmObject {

    @PrimaryKey
    private String account;

    private String password;

    private String name;

    public UserConfig() {
    }

    public UserConfig(String account, String password, String name) {
        this.account = account;
        this.password = password;
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
