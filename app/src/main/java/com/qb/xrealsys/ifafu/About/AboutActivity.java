package com.qb.xrealsys.ifafu.About;

import android.os.Bundle;
import android.widget.TextView;

import com.qb.xrealsys.ifafu.Base.BaseActivity;
import com.qb.xrealsys.ifafu.Base.controller.TitleBarController;
import com.qb.xrealsys.ifafu.Base.delegate.TitleBarButtonOnClickedDelegate;
import com.qb.xrealsys.ifafu.R;
import com.qb.xrealsys.ifafu.Tool.GlobalLib;

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
