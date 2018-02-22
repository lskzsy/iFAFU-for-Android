package com.qb.xrealsys.ifafu;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.qb.xrealsys.ifafu.tool.GlobalLib;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sky on 23/02/2018.
 */

public class AdController implements View.OnClickListener {

    private Activity        activity;

    private RelativeLayout  adView;

    private Button          adBtn;

    private Bitmap          bitmap;

    private Timer           timer;

    private TimerTask       timerTask;

    private int             adTime;

    public AdController(Activity activity, Bitmap bitmap) {
        this.activity = activity;
        this.bitmap   = bitmap;

        adView = activity.findViewById(R.id.mainAd);
        adBtn  = activity.findViewById(R.id.mainAdBtn);

        adBtn.setEnabled(false);

        if (this.bitmap != null) {
            adView.setBackground(GlobalLib.BitmapToDrawable(activity, this.bitmap));
            adView.setVisibility(View.VISIBLE);
            adBtn.setEnabled(true);
            adBtn.setOnClickListener(this);

            adTime    = 4;
            timer     = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    adTime--;
                    editBtnTime();
                    if (adTime == 0) {
                        endOnUiThread();
                    }
                }
            };

            timer.schedule(timerTask, 0,1000);
        }
    }

    private void endOnUiThread() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                end();
            }
        });
    }

    private void editBtnTime() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adBtn.setText(String.format(Locale.getDefault(), "关闭 %d", adTime));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mainAdBtn:
                end();
                break;
        }
    }

    private void end() {
        adBtn.setEnabled(false);
        adView.setVisibility(View.INVISIBLE);
        timer.cancel();
    }
}
