package com.qb.xrealsys.ifafu.Base.controller;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qb.xrealsys.ifafu.R;
import com.qb.xrealsys.ifafu.Base.delegate.TitleBarButtonOnClickedDelegate;

import org.jetbrains.annotations.NotNull;

/**
 * Created by sky on 22/02/2018.
 */

public class TitleBarController implements View.OnClickListener {

    private Activity        activity;

    private Button          headImg;

    private Button          headBack;

    private TextView        bigPageTitle;

    private LinearLayout    twoLinePageTitle;

    private TextView        pageTitle;

    private TextView        pageSubTitle;

    private ProgressBar     rightProgress;

    private Button          rightBtn;

    private TitleBarButtonOnClickedDelegate titleBarButtonOnClickedDelegate;

    public TitleBarController(Activity activity) {
        this.activity = activity;
        getAllElements();
        InitAllElements();
    }

    private void getAllElements() {
        this.headImg  = activity.findViewById(R.id.headimg);
        this.headBack = activity.findViewById(R.id.headback);

        this.bigPageTitle       = activity.findViewById(R.id.bigPageTitle);
        this.twoLinePageTitle   = activity.findViewById(R.id.twoLinePageTitle);

        this.pageTitle      = activity.findViewById(R.id.pagetitle);
        this.pageSubTitle   = activity.findViewById(R.id.subtitle);
        this.rightProgress  = activity.findViewById(R.id.rightProgress);
        this.rightBtn       = activity.findViewById(R.id.rightBtn);
    }

    private void InitAllElements() {
        this.headImg.setVisibility(View.INVISIBLE);
        this.headBack.setVisibility(View.INVISIBLE);
        this.bigPageTitle.setVisibility(View.INVISIBLE);
        this.twoLinePageTitle.setVisibility(View.INVISIBLE);
    }

    public TitleBarController setHeadImg(String name) {
        this.headImg.setVisibility(View.VISIBLE);
        this.headImg.setOnClickListener(this);
        headImg.setText(name);
        return this;
    }

    public TitleBarController setHeadBack() {
        this.headBack.setVisibility(View.VISIBLE);
        this.headBack.setOnClickListener(this);
        return this;
    }

    public TitleBarController setBigPageTitle(String title) {
        this.bigPageTitle.setVisibility(View.VISIBLE);
        this.bigPageTitle.setText(title);
        return this;
    }

    public TitleBarController setRightBtn(int drawableId) {
        this.rightBtn.setVisibility(View.VISIBLE);
        this.rightBtn.setBackgroundResource(drawableId);
        return this;
    }

    public boolean isRightProgressRunning() {
        return this.rightProgress.getVisibility() == View.VISIBLE;
    }

    public TitleBarController setRightProgress(int visible) {
        this.rightProgress.setVisibility(visible);
        return this;
    }

    public TitleBarController setBigPageTitleWithDrawable(String title, int drawableId, boolean inStart) {
        this.bigPageTitle.setVisibility(View.VISIBLE);
        this.bigPageTitle.setText(title);

//        Drawable drawable = activity.getDrawable(drawableId);
//        if (drawable != null) {
////            drawable.setBounds(0, 0, this.bigPageTitle.get, this.bigPageTitle.getHeight());
//            if (inStart) {
//                this.bigPageTitle.setCompoundDrawablesRelative(
//                        drawable, null, null, null);
//            } else {
//                this.bigPageTitle.setCompoundDrawablesRelative(
//                        null, null, drawable, null);
//            }
//        }

        return this;
    }

    public TitleBarController setTwoLineTitle(String title, String subTitle) {
        this.twoLinePageTitle.setVisibility(View.VISIBLE);
        this.pageTitle.setText(title);
        this.pageSubTitle.setText(subTitle);
        return this;
    }

    public TitleBarController setOnClickedListener(
            TitleBarButtonOnClickedDelegate titleBarButtonOnClickedDelegate) {
        this.titleBarButtonOnClickedDelegate = titleBarButtonOnClickedDelegate;
        return this;
    }

    @Override
    public void onClick(View v) {
        titleBarButtonOnClickedDelegate.titleBarOnClicked(v.getId());
    }
}
