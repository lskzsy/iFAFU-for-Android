package com.qb.xrealsys.ifafu.Main.model;

import com.qb.xrealsys.ifafu.Base.model.Model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sky on 05/03/2018.
 */

public class UpdateInf extends Model {

    private int     versionCode;

    private String  versionName;

    private boolean forceUpdating;

    private String  comment;

    public UpdateInf(JSONObject object) {
        try {
            if (object != null) {
                setVersionName(object.getString("versionName"));
                setVersionCode(object.getInt("versionCode"));
                setForceUpdating(object.getBoolean("forceUpdating"));
                setComment(object.getString("comment"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public boolean isForceUpdating() {
        return forceUpdating;
    }

    public void setForceUpdating(boolean forceUpdating) {
        this.forceUpdating = forceUpdating;
    }
}
