package com.qb.xrealsys.ifafu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.qb.xrealsys.ifafu.delegate.TitleBarButtonOnClickedDelegate;
import com.qb.xrealsys.ifafu.tool.OSSHelper;

public class ResponsibilityActivity extends BaseActivity implements
        TitleBarButtonOnClickedDelegate {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responsibility);

        TitleBarController  titleBarController  = new TitleBarController(this);
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
