package com.qb.xrealsys.ifafu.tool;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Created by sky on 10/02/2018.
 */

public class ConfigHelper {

    private Properties properties;

    private Context    context;

    public ConfigHelper(Context inContext) throws IOException {
        context    = inContext;
        properties = new Properties();
        properties.load(context.getAssets().open("config.propertise"));
    }

    public String GetValue(String key) {
        return properties.getProperty(key);
    }

    public void SetValue(String key, String value) {
        properties.setProperty(key, value);
        OutputStream fos;
        try {
            fos = context.openFileOutput("property.properties", Context.MODE_PRIVATE);
            properties.store(fos, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
