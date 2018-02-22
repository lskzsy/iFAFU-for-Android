package com.qb.xrealsys.ifafu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.qb.xrealsys.ifafu.tool.OSSHelper;

import java.io.IOException;

public class WelcomeActivity extends AppCompatActivity {

    private MainApplication mainApplication;

    private OSSHelper ossHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mainApplication = (MainApplication) getApplicationContext();
        ossHelper = mainApplication.getOssHelper();
    }

    @Override
    protected void onStart() {
        super.onStart();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ossHelper.syncData();
                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
