package com.qb.xrealsys.ifafu;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.qb.xrealsys.ifafu.delegate.TitleBarButtonOnClickedDelegate;
import com.qb.xrealsys.ifafu.tool.GlobalLib;

import java.util.Locale;

public class AboutActivity extends BaseActivity implements TitleBarButtonOnClickedDelegate {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TitleBarController titleBarController = new TitleBarController(this);
        titleBarController
                .setHeadBack()
                .setBigPageTitle("关于iFAFU")
                .setOnClickedListener(this);
        TextView aboutAppSubName = findViewById(R.id.aboutAppSubName);

        aboutAppSubName.setText(String.format(
                Locale.getDefault(), getString(R.string.app_sub_name),
                GlobalLib.GetLocalVersionName(this)));
    }

    @Override
    public void titleBarOnClicked(int id) {
        finish();
    }
}
