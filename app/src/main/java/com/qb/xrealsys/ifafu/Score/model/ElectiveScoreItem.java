package com.qb.xrealsys.ifafu.Score.model;

import android.app.Activity;
import android.text.Layout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.qb.xrealsys.ifafu.R;

/**
 * Created by sky on 23/02/2018.
 */

public class ElectiveScoreItem {

    private LinearLayout self;

    private ImageView icon;

    private TextView  title;

    private TextView  score;

    private ImageView button;

    private ListView  list;

    public ElectiveScoreItem(View view) {
        self    = (LinearLayout) view;
        icon    = view.findViewById(R.id.electiveScoreItemIcon);
        title   = view.findViewById(R.id.electiveScoreItemTitle);
        score   = view.findViewById(R.id.electiveScoreItemScore);
        list    = view.findViewById(R.id.electiveScoreItemList);
        button  = view.findViewById(R.id.electiveScoreItemBtn);
    }

    public LinearLayout getSelf() {
        return self;
    }

    public void setSelf(LinearLayout self) {
        this.self = self;
    }

    public ImageView getButton() {
        return button;
    }

    public void setButton(ImageView button) {
        this.button = button;
    }

    public ImageView getIcon() {
        return icon;
    }

    public void setIcon(ImageView icon) {
        this.icon = icon;
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    public TextView getScore() {
        return score;
    }

    public void setScore(TextView score) {
        this.score = score;
    }

    public ListView getList() {
        return list;
    }

    public void setList(ListView list) {
        this.list = list;
    }
}
