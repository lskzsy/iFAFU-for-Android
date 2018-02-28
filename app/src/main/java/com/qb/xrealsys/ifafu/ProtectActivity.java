package com.qb.xrealsys.ifafu;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qb.xrealsys.ifafu.tool.ConfigHelper;
import com.qb.xrealsys.ifafu.tool.GestureLockViewGroup;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProtectActivity extends AppCompatActivity implements View.OnClickListener {

    private GestureLockViewGroup gestureLockViewGroup;

    private TextView             protectTitle;

    private TextView             protectSubTitle;

    private ImageView            finishBtn;

    private int                  optionMode;

    private MainApplication      mainApplication;

    private ConfigHelper         configHelper;

    private boolean              pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protect);

        gestureLockViewGroup = findViewById(R.id.gestureLockViewGroup);
        protectTitle         = findViewById(R.id.protectTitle);
        protectSubTitle      = findViewById(R.id.protectSubTitle);
        finishBtn            = findViewById(R.id.protectFinishBtn);

        mainApplication = (MainApplication) getApplication();
        configHelper    = mainApplication.getConfigHelper();
        pass            = false;

        getStartupParams();
    }

    private void getStartupParams() {
        optionMode = getIntent().getIntExtra("mode",0);
        switch (optionMode) {
            case 0:
                // verify password
                protectTitle.setText("验证密码以继续访问");
                protectSubTitle.setText("请绘制手势密码");
                finishBtn.setVisibility(View.INVISIBLE);
                gestureLockViewGroup.setOnGestureLockViewListener(new VerifyListener());
                break;
            case 1:
                // set password
                protectTitle.setText("设置启动密码");
                protectSubTitle.setText("请绘制手势密码");
                finishBtn.setVisibility(View.VISIBLE);
                finishBtn.setOnClickListener(this);
                gestureLockViewGroup.setOnGestureLockViewListener(new SetListener());
                break;
            case 2:
                // quit password
                protectTitle.setText("需要验证密码以关闭启动密码");
                protectSubTitle.setText("请绘制手势密码");
                finishBtn.setVisibility(View.VISIBLE);
                finishBtn.setOnClickListener(this);
                gestureLockViewGroup.setOnGestureLockViewListener(new QuitListener());
                break;
        }
    }

    private int[] getAnswer() {
        String answerStr = configHelper.GetValue("verifyAnswer");
        if (answerStr != null) {
            String[] answerStrArray = answerStr
                    .substring(1, answerStr.length() - 1).split(", ");
            int[]    answer         = new int[answerStrArray.length];

            int i = 0;
            for (String answerS: answerStrArray) {
                answer[i++] = Integer.parseInt(answerS);
            }
            return answer;
        }

        return null;
    }

    private void setAnswer(List<Integer> answer) {
        String answerString = answer.toString();
        configHelper.SetValue("verifyAnswer", answerString);
        configHelper.SetValue("verify", String.valueOf(true));
    }

    private void resetGestureLockViewGroup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gestureLockViewGroup.reset();
            }
        }, 500);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (optionMode == 0 && !pass) {
            System.exit(0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.protectFinishBtn:
                finish();
                break;
        }
    }

    private class VerifyListener implements GestureLockViewGroup.OnGestureLockViewListener {

        public VerifyListener() {
            gestureLockViewGroup.setAnswer(getAnswer());
        }

        @Override
        public void onBlockSelected(int cId) {

        }

        @Override
        public void onGestureEvent(boolean matched) {
            if (matched) {
                pass = true;
                finish();
            } else {
                protectSubTitle.setText(String.format(
                        Locale.getDefault(),
                        "验证失败，还有%d次机会，请重试",
                        gestureLockViewGroup.getmTryTimes()));
            }

            resetGestureLockViewGroup();
        }

        @Override
        public void onUnmatchedExceedBoundary() {
            finish();
        }
    }

    private class SetListener implements GestureLockViewGroup.OnGestureLockViewListener {

        private int             inputCount;

        private List<Integer>   choose;

        public SetListener() {
            inputCount = 0;
        }

        @Override
        public void onBlockSelected(int cId) {

        }

        @Override
        public void onGestureEvent(boolean matched) {
            if (inputCount == 0) {
                choose = new ArrayList<>(gestureLockViewGroup.getmChoose());
                protectSubTitle.setText("请再绘制一次");
                inputCount++;
            } else {
                if (choose.equals(gestureLockViewGroup.getmChoose())) {
                    setAnswer(choose);
                    finish();
                } else {
                    protectSubTitle.setText("与上次绘制的手势密码不匹配，请重新绘制");
                    inputCount = 0;
                }
            }

            resetGestureLockViewGroup();
        }

        @Override
        public void onUnmatchedExceedBoundary() {

        }
    }

    private class QuitListener implements GestureLockViewGroup.OnGestureLockViewListener {

        public QuitListener() {
            gestureLockViewGroup.setAnswer(getAnswer());
        }

        @Override
        public void onBlockSelected(int cId) {
        }

        @Override
        public void onGestureEvent(boolean matched) {
            if (matched) {
                Toast.makeText(
                        ProtectActivity.this,
                        "关闭成功",
                        Toast.LENGTH_SHORT).show();
                configHelper.SetValue("verify", String.valueOf(false));
                finish();
            } else {
                protectSubTitle.setText(String.format(
                        Locale.getDefault(),
                        "验证失败，还有%d次机会，请重试",
                        gestureLockViewGroup.getmTryTimes()));
            }

            resetGestureLockViewGroup();
        }

        @Override
        public void onUnmatchedExceedBoundary() {
            finish();
        }
    }
}
