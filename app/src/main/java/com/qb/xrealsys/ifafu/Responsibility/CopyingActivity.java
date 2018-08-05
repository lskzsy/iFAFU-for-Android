package com.qb.xrealsys.ifafu.Responsibility;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.qb.xrealsys.ifafu.Base.controller.TitleBarController;
import com.qb.xrealsys.ifafu.Base.delegate.TitleBarButtonOnClickedDelegate;
import com.qb.xrealsys.ifafu.MainApplication;
import com.qb.xrealsys.ifafu.R;
import com.qb.xrealsys.ifafu.Tool.ConfigHelper;

public class CopyingActivity extends AppCompatActivity implements TitleBarButtonOnClickedDelegate {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copying);

        TitleBarController titleBarController  = new TitleBarController(this);
        titleBarController
                .setBigPageTitle("GNU开源协议")
                .setHeadBack()
                .setOnClickedListener(this);

        ConfigHelper configHelper = ((MainApplication)getApplication()).getConfigHelper();
        TextView     copyingView  = findViewById(R.id.copyingView);
        copyingView.setText(configHelper.getCopying());
    }

    @Override
    public void titleBarOnClicked(int id) {
        finish();
    }

    @Override
    public void titleBarOnLongClicked(int id) {

    }
}
