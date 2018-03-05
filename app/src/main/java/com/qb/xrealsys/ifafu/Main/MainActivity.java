package com.qb.xrealsys.ifafu.Main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qb.xrealsys.ifafu.About.AboutActivity;
import com.qb.xrealsys.ifafu.Main.controller.AdController;
import com.qb.xrealsys.ifafu.Base.BaseActivity;
import com.qb.xrealsys.ifafu.Base.controller.TitleBarController;
import com.qb.xrealsys.ifafu.Exam.ExamActivity;
import com.qb.xrealsys.ifafu.Main.controller.LeftMenuController;
import com.qb.xrealsys.ifafu.Main.controller.UpdateController;
import com.qb.xrealsys.ifafu.Main.delegate.UpdateQueryCallbackDelegate;
import com.qb.xrealsys.ifafu.Main.dialog.UpdateDialog;
import com.qb.xrealsys.ifafu.Main.model.UpdateInf;
import com.qb.xrealsys.ifafu.MainApplication;
import com.qb.xrealsys.ifafu.Base.ProtectActivity;
import com.qb.xrealsys.ifafu.R;
import com.qb.xrealsys.ifafu.Responsibility.ResponsibilityActivity;
import com.qb.xrealsys.ifafu.Score.ElectiveScoreActivity;
import com.qb.xrealsys.ifafu.Score.ScoreActivity;
import com.qb.xrealsys.ifafu.Score.controller.ScoreController;
import com.qb.xrealsys.ifafu.Syllabus.SyllabusActivity;
import com.qb.xrealsys.ifafu.Syllabus.controller.SyllabusController;
import com.qb.xrealsys.ifafu.User.LoginActivity;
import com.qb.xrealsys.ifafu.User.controller.UserController;
import com.qb.xrealsys.ifafu.Base.WebActivity;
import com.qb.xrealsys.ifafu.Main.delegate.LeftMenuClickedDelegate;
import com.qb.xrealsys.ifafu.User.delegate.ReplaceUserDelegate;
import com.qb.xrealsys.ifafu.Base.delegate.TitleBarButtonOnClickedDelegate;
import com.qb.xrealsys.ifafu.Score.delegate.UpdateMainScoreViewDelegate;
import com.qb.xrealsys.ifafu.Syllabus.delegate.UpdateMainSyllabusViewDelegate;
import com.qb.xrealsys.ifafu.Syllabus.delegate.UpdateMainUserViewDelegate;
import com.qb.xrealsys.ifafu.User.dialog.AccountSettingDialog;
import com.qb.xrealsys.ifafu.Base.dialog.ProgressDialog;
import com.qb.xrealsys.ifafu.Syllabus.model.Course;
import com.qb.xrealsys.ifafu.Score.model.Score;
import com.qb.xrealsys.ifafu.Score.model.ScoreTable;
import com.qb.xrealsys.ifafu.Syllabus.model.Syllabus;
import com.qb.xrealsys.ifafu.User.model.User;
import com.qb.xrealsys.ifafu.Tool.ConfigHelper;
import com.qb.xrealsys.ifafu.Tool.GlobalLib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends BaseActivity
        implements
        View.OnTouchListener,
        View.OnClickListener,
        LeftMenuClickedDelegate,
        UpdateMainUserViewDelegate,
        UpdateMainScoreViewDelegate,
        UpdateMainSyllabusViewDelegate,
        TitleBarButtonOnClickedDelegate,
        ReplaceUserDelegate {
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

    private long firstClickBack;

    private int screenWidth;

    private boolean isMenuVisible;

    private LeftMenuController leftMenuController;

    private LinearLayout mainContent;

    private LinearLayout menu;

    private LinearLayout.LayoutParams menuParams;

    private TextView bigHeadImg;

    private TextView studentNumber;

    private TextView isOnline;

    private TextView mainUserNumber;

    private TextView mainUserInstitute;

    private TextView mainUserEnrollment;

    private TextView mainUserClas;

    private TextView mainScoreTitle;

    private TextView mainScoreContent;

    private TextView mainSyllabusTitle;

    private TextView mainSyllabusTime;

    private TextView mainSyllabusContent;

    private LinearLayout mainScore;

    private UserController currentUserController;

    private SyllabusController syllabusController;

    private ScoreController scoreController;

    private ConfigHelper configHelper;

    private UpdateController updateController;

    private TitleBarController titleBarController;

    private AdController adController;

    private MainApplication mainApplication;

    private AccountSettingDialog accountSettingDialog;

    private ProgressDialog progressDialog;

    private UpdateDialog   updateDialog;

    private SpannableString mainSyllabusBlankString;

    private boolean isWelcome;

    private boolean isAd;

    private boolean isOnce;

    private boolean isInitLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isWelcome   = true;
        isAd        = false;
        isInitLoad  = true;

        firstClickBack = 0;

        mainApplication       = (MainApplication) getApplicationContext();
        currentUserController = mainApplication.getUserController();
        configHelper          = mainApplication.getConfigHelper();
        scoreController       = mainApplication.getScoreController();
        syllabusController    = mainApplication.getSyllabusController();
        updateController      = mainApplication.getUpdateController();

        progressDialog          = new ProgressDialog(this);
        updateDialog            = new UpdateDialog(this, updateController, updateController);
        mainSyllabusBlankString = new SpannableString("0今天没有课");
        mainSyllabusBlankString.setSpan(new ImageSpan(this, R.drawable.drawable_superman),
                0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        InitElements();
        InitLeftMenu();
        InitLeftController();
        InitClickListen();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!StartupProcess()) {
            return;
        }


        if (!updateController.isChecked()) {
            //  Open update app dialog
            UpdateInf updateInf = updateController.CheckUpdate();
            if (updateInf != null) {
                updateDialog.show(updateInf);
            }
        }

        InitBackground();
        UpdateAndVerifyUser();
    }

    private boolean StartupProcess() {
        if (isOnce) {
            isOnce = false;
            gotoProtectActivity(true);
            return false;
        }

        if (isAd) {
            isAd = false;
            adController = new AdController(this, mainApplication.getOssHelper().getAd());
        }

        if (isWelcome) {
            isWelcome = false;
            isAd      = true;
            if (Boolean.valueOf(configHelper.GetValue("verify"))) {
                isOnce = true;
            }

            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
            return false;
        }

        return true;
    }

    private void InitBackground() {
        Bitmap background = mainApplication.getOssHelper().getBackground();
        if (background != null) {
            mainContent.setBackground(GlobalLib.BitmapToDrawable(this, background));
        }
    }

    private void UpdateAndVerifyUser() {
        if (!currentUserController.isLogin()) {
            final String defaultAccount  = configHelper.GetValue("account");
            final String defaultPassword = configHelper.GetValue("password");

            if (defaultAccount.equals("") && defaultPassword.equals("")) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            currentUserController.Login(defaultAccount, defaultPassword, true);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateActivity();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        } else {
            updateActivity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if (System.currentTimeMillis() - firstClickBack > 2000) {
                Toast.makeText(this, "再次按下返回键退出程序", Toast.LENGTH_SHORT).show();
                firstClickBack = System.currentTimeMillis();
            }else{
                finish();
                System.exit(0);
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void InitElements() {
        mainContent   = findViewById(R.id.mainContent);
        menu          = findViewById(R.id.leftMenu);
        menuParams    = (LinearLayout.LayoutParams) menu.getLayoutParams();
        mainScore     = findViewById(R.id.mainScore);

        bigHeadImg       = findViewById(R.id.bigHeadImg);
        studentNumber    = findViewById(R.id.studentNumber);
        isOnline         = findViewById(R.id.isOnline);

        mainUserNumber      = findViewById(R.id.main_user_number);
        mainUserEnrollment  = findViewById(R.id.main_user_enrollment);
        mainUserInstitute   = findViewById(R.id.main_user_institute);
        mainUserClas        = findViewById(R.id.main_user_clas);
        mainScoreTitle      = findViewById(R.id.main_score_title);
        mainScoreContent    = findViewById(R.id.main_score_content);
        mainSyllabusTitle   = findViewById(R.id.main_syllabus_title);
        mainSyllabusTime    = findViewById(R.id.main_syllabus_time);
        mainSyllabusContent = findViewById(R.id.main_syllabus_content);
    }

    private void InitClickListen() {
//        mainScore.setOnClickListener(this);
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
        final String systemSettingVerifyOption
                = isOnce ? "关闭密码" : "启动密码";

        List<String>                leftMenuUnits       = new ArrayList<>(
                Arrays.asList("信息查询", "实用工具", "软件设置", "关于软件"));

        Map<String, List<String>>   leftMenuTabs        = new HashMap<String, List<String>>() {{
            put("信息查询", Arrays.asList("成绩查询", "选修学分查询", "等级考试查询", "学生考试查询"));
            put("实用工具", Arrays.asList("我的课表", "一键评教", "选修课抢课", "网页模式", "文件库"));
            put("软件设置", Arrays.asList("账号管理", "免验证码", systemSettingVerifyOption));
            put("关于软件", Arrays.asList("关于iFAFU", "贡献名单", "使用帮助", "隐私条款与免责声明"));
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
        leftMenuController.setClickedDelegate(this);
    }

    @Override
    public void onTabClick(int tabIndex) {
        switch (tabIndex) {
            case 0:
                gotoScoreActivity();
                break;
            case 1:
                gotoElectiveScoreActivity();
                break;
            case 2:
                Toast.makeText(
                        this,
                        getString(R.string.error_grade_exam_query),
                        Toast.LENGTH_SHORT).show();
                break;
            case 3:
                gotoExamActivity();
                break;
            case 4:
                gotoSyllabusActivity();
                break;
            case 7:
                gotoBrower("网页模式", currentUserController.getIndexUrl());
                break;
            case 8:
                gotoBrower("iFAFU文件库", configHelper.GetSystemValue("iFAFUFileUrl"));
                break;
            case 9:
                AccountSetting();
                break;
            case 10:
                Toast.makeText(
                        this,
                        "暂不支持修改！",
                        Toast.LENGTH_SHORT).show();
                break;
            case 11:
                gotoProtectActivity(false);
                break;
            case 12:
                gotoAboutActivity();
                break;
            case 14:
                Toast.makeText(
                        this,
                        "暂无帮助信息！",
                        Toast.LENGTH_SHORT).show();
                break;
            case 15:
                gotoResponsibilityActivity();
                break;
            default:
                Toast.makeText(this, "正在开发中...", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void updateData() {
        try {
            syllabusController.SyncData();
            scoreController.SyncData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Go to other activity
     */
    private void gotoSyllabusActivity() {
        Intent intent = new Intent(MainActivity.this, SyllabusActivity.class);
        startActivity(intent);
    }

    private void gotoProtectActivity(boolean isVerify) {
        Intent intent = new Intent(MainActivity.this, ProtectActivity.class);
        if (!isVerify) {
            if (Boolean.parseBoolean(configHelper.GetValue("verify"))) {
                intent.putExtra("mode", 2);
            } else {
                intent.putExtra("mode", 1);
            }
        }
        startActivity(intent);
    }

    @Override
    public void ReplaceUser() {
        isInitLoad = true;
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.putExtra("isKill", false);
        startActivity(intent);
    }

    @Override
    public void ReplaceUser(String account, String password) {
        final String defaultAccount     = account;
        final String defaultPassword    = password;

        isInitLoad = true;
        progressDialog.show("正在切换账号...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    currentUserController.Login(defaultAccount, defaultPassword, true);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateActivity();
                            progressDialog.cancel();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void AccountSetting() {
        accountSettingDialog = new AccountSettingDialog(this, this);
        accountSettingDialog.show();
    }

    private void gotoResponsibilityActivity() {
        Intent intent = new Intent(MainActivity.this, ResponsibilityActivity.class);
        startActivity(intent);
    }

    private void gotoAboutActivity() {
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    private void gotoScoreActivity() {
        Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
        startActivity(intent);
    }

    private void gotoBrower(String title, String url) {
        Intent intent = new Intent(MainActivity.this, WebActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("pageTitle", title);
        bundle.putString("loadUrl", url);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void gotoElectiveScoreActivity() {
        Intent intent = new Intent(MainActivity.this, ElectiveScoreActivity.class);
        startActivity(intent);
    }

    private void gotoExamActivity() {
        Intent intent = new Intent(MainActivity.this, ExamActivity.class);
        startActivity(intent);
    }

    /**
     * Page Solver
     */
    private void updateActivity() {
        User data = currentUserController.getData();

        syllabusController.setUpdateMainUserViewDelegate(this);
        syllabusController.setUpdateMainSyllabusViewDelegate(this);
        scoreController.setUpdateMainScoreViewDelegate(this);

        titleBarController = new TitleBarController(MainActivity.this);
        titleBarController
                .setTwoLineTitle(
                        data.getName(),
                        data.isLogin() ? "在线" : "离线")
                .setHeadImg(data.getName().substring(data.getName().length() - 2))
                .setOnClickedListener(this);

        updateNameAndNumber(data.getName(), data.getAccount());
        updateOnlineStatus(data.isLogin());

        if (isInitLoad) {
            updateData();
            isInitLoad = false;
        } else {
            updateMainUser(currentUserController.getData());
            updateMainScore(scoreController.GetData());
            updateMainSyllabus(syllabusController.GetData());
        }
    }

    private void updateNameAndNumber(String name, String number) {
        bigHeadImg.setText(name.substring(name.length() - 2));
        studentNumber.setText(number);
    }

    private void updateOnlineStatus(boolean online) {
        if (online) {
            isOnline.setText("● 在线");
            isOnline.setTextColor(Color.parseColor("#52fb49"));
        } else {
            isOnline.setText("● 离线");
            isOnline.setTextColor(Color.parseColor("#ff0000"));
        }
    }

    @Override
    public void updateMainSyllabus(Syllabus syllabus) {
        final Syllabus inSyllabus = syllabus;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainSyllabusTitle.setText(
                        String.format(Locale.getDefault(),
                                getString(R.string.format_main_syllabus_title),
                                inSyllabus.getSearchYearOptions().get(
                                        inSyllabus.getSelectedYearOption()),
                                inSyllabus.getSearchTermOptions().get(
                                        inSyllabus.getSelectedTermOption())));

                String[] studyTime = GlobalLib.GetStudyTime(
                        configHelper.GetValue("nowTermFirstWeek"));
                mainSyllabusTime.setText(studyTime[0]);
                int nowWeek = Integer.parseInt(studyTime[1]);
                int weekDay = Integer.parseInt(studyTime[2]);

                if (nowWeek < 1 || nowWeek > 24) {
                    mainSyllabusContent.setText(mainSyllabusBlankString);
                    return;
                }

                List<Course> courseList
                        = syllabusController.GetCourseInfoByWeekAndWeekday(nowWeek, weekDay);
                if (courseList.size() < 1) {
                    mainSyllabusContent.setText(mainSyllabusBlankString);
                } else {
                    String display = String.format(
                            Locale.getDefault(),"今天有%d节课\n", courseList.size());
                    String willStudyTime = syllabusController.GetWillStudyTime(courseList);
                    if (willStudyTime == null) {
                        display += "无待上课程";
                    } else {
                        display += willStudyTime;
                    }
                    mainSyllabusContent.setText(display);
                }
            }
        });
    }

    @Override
    public void updateMainUser(User user) {
        final User contentUser = user;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainUserNumber.setText(
                        String.format(Locale.getDefault(),"%s%s",
                                getString(R.string.default_main_s_number),
                                contentUser.getAccount()));
                mainUserEnrollment.setText(
                        String.format(Locale.getDefault(), "%s%d",
                                getString(R.string.default_main_s_enrollment),
                                contentUser.getEnrollment()));
                mainUserInstitute.setText(
                        String.format(Locale.getDefault(), "%s%s",
                                getString(R.string.default_main_s_institute),
                                contentUser.getInstitute()));
                mainUserClas.setText(
                        String.format(Locale.getDefault(), "%s%s",
                                getString(R.string.default_main_s_clas),
                                contentUser.getClas()));
            }
        });
    }

    @Override
    public void updateMainScore(ScoreTable scoreTable) {
        final ScoreTable inScoreTable = scoreTable;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<String> yearOptions = inScoreTable.getSearchYearOptions();
                List<String> termOptions = inScoreTable.getSearchTermOptions();
                mainScoreTitle.setText(
                        String.format(Locale.getDefault(), getString(R.string.format_main_score_title),
                                yearOptions.get(inScoreTable.getSelectedYearOption()),
                                termOptions.get(inScoreTable.getSelectedTermOption())
                        ));

                List<Score> scoreList   = inScoreTable.getData();
                int         scoreCount  = scoreList.size();
                int         lastRead    = 0;

                String lastReadScoreCount  = configHelper.GetValue("lastReadScoreCount");
                if (lastReadScoreCount == null) {
                    lastRead = 0;
                } else {
                    lastRead = Integer.parseInt(lastReadScoreCount);
                }

                if (scoreCount > lastRead) {
                    mainScoreContent.setText(
                            String.format(Locale.getDefault(),
                                    getString(R.string.format_main_score_content_new),
                                    scoreCount,
                                    scoreCount - lastRead));
                } else {
                    mainScoreContent.setText(
                            String.format(Locale.getDefault(),
                                    getString(R.string.format_main_score_content),
                                    scoreCount));
                }
//                configHelper.SetValue("lastReadScoreCount", String.valueOf(scoreCount));
            }
        });
    }

    @Override
    public void updateError(String error) {
        final String  errorMsg = error;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Click Handler
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mainScore:
                gotoScoreActivity();
                break;
        }
    }

    @Override
    public void titleBarOnClicked(int id) {
        switch (id) {
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
