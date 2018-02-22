package com.qb.xrealsys.ifafu;

import android.app.Application;

import com.qb.xrealsys.ifafu.tool.ConfigHelper;
import com.qb.xrealsys.ifafu.tool.OSSHelper;

import java.io.IOException;

/**
 * Created by sky on 10/02/2018.
 */

public class MainApplication extends Application {

    private OSSHelper    ossHelper;

    private ConfigHelper configHelper;

    private static MainApplication instance = null;

    public static MainApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            if (configHelper == null) {
                configHelper = new ConfigHelper(getBaseContext());
            }

            ossHelper = new OSSHelper(configHelper.GetSystemValue("ossHost"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public OSSHelper getOssHelper() {
        return ossHelper;
    }
}
