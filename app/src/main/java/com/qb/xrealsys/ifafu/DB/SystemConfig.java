package com.qb.xrealsys.ifafu.DB;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sky on 25/04/2018.
 */

public class SystemConfig extends RealmObject {

    private String defaultYear;

    private String defaultTerm;

    @PrimaryKey
    private String account;

    public SystemConfig() {

    }

    public String getDefaultYear() {
        return defaultYear;
    }

    public void setDefaultYear(String defaultYear) {
        this.defaultYear = defaultYear;
    }

    public String getDefaultTerm() {
        return defaultTerm;
    }

    public void setDefaultTerm(String defaultTerm) {
        this.defaultTerm = defaultTerm;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
