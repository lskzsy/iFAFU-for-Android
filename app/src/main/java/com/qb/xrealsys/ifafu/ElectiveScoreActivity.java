package com.qb.xrealsys.ifafu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.qb.xrealsys.ifafu.delegate.TitleBarButtonOnClickedDelegate;

import java.util.Locale;

public class ElectiveScoreActivity extends BaseActivity
        implements TitleBarButtonOnClickedDelegate {

    MainApplication         mainApplication;

    UserController          userController;

    TitleBarController      titleBarController;

    ElectiveScoreListController electiveScoreListController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elective_score);

        mainApplication         = (MainApplication) getApplication();
        userController          = mainApplication.getUserController();

        titleBarController      = new TitleBarController(this);
        titleBarController
                .setHeadBack()
                .setTwoLineTitle("选修学分查询", String.format(
                        Locale.getDefault(), getString(R.string.format_subtitle_for_user),
                        userController.getData().getName(), userController.getData().getAccount()))
                .setOnClickedListener(this);
        electiveScoreListController = new ElectiveScoreListController(this);
        electiveScoreListController.SyncListView();
    }

    @Override
    public void titleBarOnClicked(int id) {
        finish();
    }
}
