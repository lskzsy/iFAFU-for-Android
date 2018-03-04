package com.qb.xrealsys.ifafu.Responsibility;

import android.os.Bundle;

import com.qb.xrealsys.ifafu.Base.BaseActivity;
import com.qb.xrealsys.ifafu.Base.controller.TitleBarController;
import com.qb.xrealsys.ifafu.Base.delegate.TitleBarButtonOnClickedDelegate;
import com.qb.xrealsys.ifafu.R;

public class ResponsibilityActivity extends BaseActivity implements
        TitleBarButtonOnClickedDelegate {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responsibility);

        TitleBarController titleBarController  = new TitleBarController(this);
        titleBarController
                .setBigPageTitle("iFAFU隐私条款及免责说明")
                .setHeadBack()
                .setOnClickedListener(this);
    }

    @Override
    public void titleBarOnClicked(int id) {
        finish();
    }
}
