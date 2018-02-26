package com.qb.xrealsys.ifafu;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by sky on 23/02/2018.
 */

public class ElectiveScoreProgressBarController {

    private static RelativeLayout progress;

    private static RelativeLayout finishProgress;

    private static RelativeLayout unFinishProgress;

    private static TextView       score;

    public static void Build(View view, float total, double finish) {
        score               = view.findViewById(R.id.electiveScoreItemScore);
        progress            = view.findViewById(R.id.electiveScoreItemProgress);
        finishProgress      = view.findViewById(R.id.electiveScoreItemFinishProgress);
        unFinishProgress    = view.findViewById(R.id.electiveScoreItemUnFinishProgress);

        create(total, finish);
    }

    public static void create(float total, double finish) {
        score.setTextColor(Color.parseColor("#ffffff"));
        int totalWidth      = progress.getWidth();
        if (finish > total) {
            finishProgress.setLayoutParams(new RelativeLayout.LayoutParams(
                    totalWidth,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            return;
        }

        int finishWidth     = (int) (totalWidth * (finish / total));
        int unFinishWidth   = totalWidth - finishWidth;

        finishProgress.setLayoutParams(new RelativeLayout.LayoutParams(
                finishWidth,
                ViewGroup.LayoutParams.MATCH_PARENT));

        RelativeLayout.LayoutParams unFinishProgressParams = new RelativeLayout.LayoutParams(
                unFinishWidth,
                ViewGroup.LayoutParams.MATCH_PARENT);
        unFinishProgressParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        unFinishProgress.setLayoutParams(unFinishProgressParams);
    }
}
