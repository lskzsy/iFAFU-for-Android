package com.qb.xrealsys.ifafu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qb.xrealsys.ifafu.model.User;
import com.qb.xrealsys.ifafu.model.UserData;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {
    /* if speed enough, slide complete */
    public static final int SNAP_VELOCITY = 100;

    /* menu's right limit */
    private int rightEdge = 0;

    /* finally main stay width */
    private int menuPadding = 300;

    /* menu's left limit */
    private int leftEdge;

    /* x for touch down */
    private float xDown;

    /* x for touch moving */
    private float xMove;

    /* x for touch up */
    private float xUp;

    /* calculating slide speed */
    private VelocityTracker mVelocityTracker;

    private int screenWidth;

    private boolean isMenuVisible;

    private LeftMenuController leftMenuController;

    private LinearLayout mainContent;

    private LinearLayout menu;

    private LinearLayout.LayoutParams menuParams;

    private Button headImg;

    private TextView pageTitle;

    private TextView pageSubTitle;

    private TextView bigHeadImg;

    private TextView studentNumber;

    private TextView isOnline;

    private User   currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitElements();
        InitLeftMenu();
        InitClickListen();
        InitLeftController();

        if (currentUser == null) {
            try {
                currentUser = new User(this.getBaseContext());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!currentUser.isLogin()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, 1000);
        } else {
            updateActivity();
        }
    }

    private void InitElements() {
        mainContent   = (LinearLayout) findViewById(R.id.mainContent);
        menu          = (LinearLayout) findViewById(R.id.leftMenu);
        menuParams    = (LinearLayout.LayoutParams) menu.getLayoutParams();

        headImg       = (Button) findViewById(R.id.headimg);
        pageTitle     = (TextView) findViewById(R.id.pagetitle);
        pageSubTitle  = (TextView) findViewById(R.id.subtitle);
        bigHeadImg    = (TextView) findViewById(R.id.bigHeadImg);
        studentNumber = (TextView) findViewById(R.id.studentNumber);
        isOnline      = (TextView) findViewById(R.id.isOnline);
    }

    private void InitClickListen() {
        headImg.setOnClickListener(this);
    }

    private void InitLeftMenu() {
        mainContent.setOnTouchListener(this);
        WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        screenWidth = window.getDefaultDisplay().getWidth();
        menuParams.width = screenWidth - menuPadding;
        leftEdge = -menuParams.width;
        menuParams.leftMargin = leftEdge;
        mainContent.getLayoutParams().width = screenWidth;
    }

    private void InitLeftController() {
        List<String>                leftMenuUnits       = new ArrayList<>(
                Arrays.asList("信息查询", "实用工具", "软件设置", "关于软件"));

        Map<String, List<String>>   leftMenuTabs        = new HashMap<String, List<String>>() {{
            put("信息查询", Arrays.asList("成绩查询", "选修学分查询", "等级考试查询", "学生考试查询"));
            put("实用工具", Arrays.asList("我的课表", "一键评教", "选修课抢课", "网页模式", "文件库"));
            put("软件设置", Arrays.asList("账号管理", "免验证码", "启动密码"));
            put("关于软件", Arrays.asList("关于iFAFU", "贡献名单", "使用帮助", "隐私条框与免责声明"));
        }};

        Map<String, List<Integer>>  leftMenuTabIcons    = new HashMap<String, List<Integer>>() {{
            put("信息查询", Arrays.asList(
                    R.drawable.icon_100,
                    R.drawable.icon_elective,
                    R.drawable.icon_stepexam,
                    R.drawable.icon_exam));
            put("实用工具", Arrays.asList(
                    R.drawable.icon_syllabus,
                    R.drawable.icon_evaluation,
                    R.drawable.icon_runelective,
                    R.drawable.icon_web,
                    R.drawable.icon_file));
            put("软件设置", Arrays.asList(
                    R.drawable.icon_settingw,
                    R.drawable.icon_verifycode,
                    R.drawable.icon_usepassord));
            put("关于软件", Arrays.asList(
                    R.drawable.icon_about,
                    R.drawable.icon_thanks,
                    R.drawable.icon_help,
                    R.drawable.icon_file
            ));
        }};

        leftMenuController = new LeftMenuController(this, R.id.menuContent);
        leftMenuController.Make(leftMenuUnits, leftMenuTabs, leftMenuTabIcons);
    }

    /**
     * Page Solver
     */
    private void updateActivity() {
        UserData data = currentUser.getData();
        updateNameAndNumber(data.getName(), data.getAccount());
        updateOnlineStatus(data.isLogin());
    }

    private void updateNameAndNumber(String name, String number) {
        headImg.setText(name.substring(name.length() - 2));
        pageTitle.setText(name);
        bigHeadImg.setText(name.substring(name.length() - 2));
        studentNumber.setText(number);
    }

    private void updateOnlineStatus(boolean online) {
        if (online) {
            pageSubTitle.setText("在线");
            isOnline.setText("● 在线");
            isOnline.setTextColor(Color.parseColor("#52fb49"));
        } else {
            pageSubTitle.setText("离线");
            isOnline.setText("● 离线");
            isOnline.setTextColor(Color.parseColor("#ff0000"));
        }
    }

    /**
     * Callback Handler
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();

            switch (requestCode) {
                case 1000:
                    try {
                        currentUser.updateData((UserData) bundle.getSerializable("userObject"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    /**
     * Click Handler
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headimg:
                headImgClicked();
                break;
        }
    }

    private void headImgClicked() {
        if (isMenuVisible) {
            scrollToContent();
        } else {
            scrollToMenu();
        }
    }

    /**
     * Solving Left menu logic
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        createVelocityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDown = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                xMove = event.getRawX();
                int distanceX = (int) (xMove - xDown);
                if (isMenuVisible) {
                    menuParams.leftMargin = distanceX;
                } else {
                    menuParams.leftMargin = leftEdge + distanceX;
                }
                if (menuParams.leftMargin < leftEdge) {
                    menuParams.leftMargin = leftEdge;
                } else if (menuParams.leftMargin > rightEdge) {
                    menuParams.leftMargin = rightEdge;
                }
                menu.setLayoutParams(menuParams);
                break;
            case MotionEvent.ACTION_UP:
                xUp = event.getRawX();
                if (wantToShowMenu()) {
                    if (shouldScrollToMenu()) {
                        scrollToMenu();
                    } else {
                        scrollToContent();
                    }
                } else if (wantToShowContent()) {
                    if (shouldScrollToContent()) {
                        scrollToContent();
                    } else {
                        scrollToMenu();
                    }
                }
                recycleVelocityTracker();
                break;
        }
        return true;
    }

    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    private void scrollToMenu() {
        new ScrollTask().execute(30);
    }

    private void scrollToContent() {
        new ScrollTask().execute(-30);
    }

    private boolean wantToShowContent() {
        return xUp - xDown < 0 && isMenuVisible;
    }

    private boolean wantToShowMenu() {
        return xUp - xDown > 0 && !isMenuVisible;
    }

    private boolean shouldScrollToMenu() {
        return xUp - xDown > menu.getWidth() / 2
                || getScrollVelocity() > SNAP_VELOCITY;
    }

    private boolean shouldScrollToContent() {
        return xDown - xUp + menuPadding > menu.getWidth() / 2
                || getScrollVelocity() > SNAP_VELOCITY;
    }

    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }

    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    class ScrollTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... speed) {
            int leftMargin = menuParams.leftMargin;
            while (true) {
                leftMargin = leftMargin + speed[0];
                if (leftMargin > rightEdge) {
                    leftMargin = rightEdge;
                    break;
                }
                if (leftMargin < leftEdge) {
                    leftMargin = leftEdge;
                    break;
                }
                publishProgress(leftMargin);
                sleep(5);
            }
            if (speed[0] > 0) {
                isMenuVisible = true;
            } else {
                isMenuVisible = false;
            }
            return leftMargin;
        }


        @Override
        protected void onProgressUpdate(Integer... leftMargin) {
            menuParams.leftMargin = leftMargin[0];
            menu.setLayoutParams(menuParams);
        }


        @Override
        protected void onPostExecute(Integer leftMargin) {
            menuParams.leftMargin = leftMargin;
            menu.setLayoutParams(menuParams);
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
