package com.qb.xrealsys.ifafu;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by sky on 26/02/2018.
 */

public class LodingViewController {

    private ImageView animIcon;

    private ImageView animShadow;

    private Animation animation1;

    private Animation animation2;

    private RelativeLayout view;

    private boolean   run;

    public LodingViewController(Activity activity) {
        this.view   = activity.findViewById(R.id.loadingView);

        animIcon    = view.findViewById(R.id.loadingIcon);
        animShadow  = view.findViewById(R.id.loadingShadow);

        animation1  = AnimationUtils.loadAnimation(activity, R.anim.anim_loading_1);
        animation1.setAnimationListener(new ReAnimationLinstener());
        animation2  = AnimationUtils.loadAnimation(activity, R.anim.anim_loading_2);
        animation2.setAnimationListener(new ReAnimationLinstener());
    }

    public void show() {
        run = true;
        this.view.setVisibility(View.VISIBLE);
        animIcon.startAnimation(animation1);
        animShadow.startAnimation(animation2);
    }

    public void cancel() {
        if (run) {
            run = false;
            this.view.setVisibility(View.INVISIBLE);
            animIcon.clearAnimation();
            animShadow.clearAnimation();
        }
    }

    public class ReAnimationLinstener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            animation.reset();
            animation.setAnimationListener(new ReAnimationLinstener());
            animation.start();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
