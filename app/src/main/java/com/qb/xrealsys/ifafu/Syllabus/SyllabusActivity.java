package com.qb.xrealsys.ifafu.Syllabus;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.qb.xrealsys.ifafu.Base.BaseActivity;
import com.qb.xrealsys.ifafu.Base.controller.LoadingViewController;
import com.qb.xrealsys.ifafu.MainApplication;
import com.qb.xrealsys.ifafu.R;
import com.qb.xrealsys.ifafu.Syllabus.controller.SyllabusAsyncController;
import com.qb.xrealsys.ifafu.Base.controller.TitleBarController;
import com.qb.xrealsys.ifafu.Base.delegate.TitleBarButtonOnClickedDelegate;
import com.qb.xrealsys.ifafu.Score.dialog.CourseInfoDialog;
import com.qb.xrealsys.ifafu.Syllabus.model.Course;
import com.qb.xrealsys.ifafu.Tool.ConfigHelper;
import com.qb.xrealsys.ifafu.Tool.GlobalLib;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class SyllabusActivity extends BaseActivity implements
        View.OnClickListener,
        TitleBarButtonOnClickedDelegate,
        OptionsPickerView.OnOptionsSelectListener {

    private static final int horizontalTabNum = 7;

    private static final int verticalTabNum   = 12;

    private static final String[][] studyBeginTime = new String[][] {{
            "8:00", "8:50", "9:55", "10:45", "11:35",
            "14:00", "14:50", "15:50", "16:40",
            "18:25", "19:15", "20:05"}, {
            "8:30", "9:20","10:25","11:15","12:05",
            "14:00", "14:50", "15:50", "16:40",
            "18:25", "19:15", "20:05"}};

    private static final String[] weekDayName = new String[] {
            "周日", "周一", "周二", "周三", "周四", "周五", "周六",};

    private static final String[] baseColors = new String[] {
            "#fa474b", "#0273fe", "#fe7f02", "#b956f8", "#38d3a9",
            "#48d9f8", "#f0c83c", "#a9d53c", "#fcb304", "#f784e3",
            "#b8773a", "#2f2f2f", "#8e7fa7", "#6493b5", "#66a752"};

    private MainApplication         mainApplication;

    private SyllabusAsyncController syllabusController;

    private TitleBarController      titleBarController;

    private ConfigHelper            configHelper;

    private LoadingViewController   loadingViewController;

    private RelativeLayout          syllbusContent;

    private LinearLayout            noDataView;

    private boolean                 isDraw;

    private int                     selectedWeek;

    private int                     nowWeek;

    private List<View>              coursesView;

    private Map<String, String>     mapNameToColor;

    private int[]                   baseColorIndex;

    private Map<Integer, Course>    mapIdToCourse;

    private OptionsPickerView       optionsPickerView;

    private CourseInfoDialog        courseInfoDialog;

    private List<String>            options;

    private TextView                pageTitle;

    private int                     contentWidth;

    private int                     contentHeight;

    private int                     titleWidth;

    private int                     titleHeight;

    private int                     tabWidth;

    private int                     tabHeight;

    private int                     backToNowWeekBtnId;

    private int                     settingBtnId;

    private boolean                 isInit;

    private int                     baseColorSwitchLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus);

        isDraw          = true;
        isInit          = true;
        noDataView      = findViewById(R.id.noDataView);
        pageTitle       = findViewById(R.id.pagetitle);
        syllbusContent  = findViewById(R.id.syllabusContent);
        syllbusContent.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        if (isDraw) {
                            drawSyllabus();
                            loadingViewController.cancel();
                            isDraw = false;
                        }
                    }
                });

        mainApplication         = (MainApplication) getApplication();
        syllabusController      = mainApplication.getSyllabusController();
        loadingViewController   = new LoadingViewController(this);
        titleBarController      = new TitleBarController(this);
        coursesView             = new ArrayList<>();
        mapNameToColor          = new HashMap<>();
        mapIdToCourse           = new HashMap<>();
        courseInfoDialog        = new CourseInfoDialog(this);
        baseColorIndex          = new int[baseColors.length];
        baseColorSwitchLimit    = baseColors.length;
        for (int i = 0; i < baseColors.length; i++) {
            baseColorIndex[i] = i;
        }

        configHelper    = mainApplication.getConfigHelper();
        selectedWeek    = GlobalLib.GetNowWeek(configHelper.GetValue("nowTermFirstWeek"));
        nowWeek         = selectedWeek;

        titleBarController
                .setHeadBack()
                .setTwoLineTitle(
                        "课表",
                        syllabusController.GetNowStudyTime(getString(R.string.format_study_time)))
                .setRightBtn(R.drawable.icon_pushpin_1)
                .setOnClickedListener(this);

        initOptionsPickerView();
        drawBackNowWeekBtn();
        loadingViewController.show();

        setPageTitle(nowWeek);
    }

    private void setPageTitle(int week) {
        String title;
        if (week < 1 || week > 24) {
            title = "放假中";
        } else {
            title = options.get(week - 1);
        }

        pageTitle.setText(title);
    }

    private void initOptionsPickerView() {
        pageTitle.setOnClickListener(this);
        optionsPickerView = new OptionsPickerView.Builder(
                SyllabusActivity.this,
                this)
                .setLinkage(false)
                .setSubmitText("确定")
                .setTitleSize(13)
                .setTitleText("选择目标周")
                .setTitleColor(Color.parseColor("#157efb"))
                .build();

        options = new ArrayList<>();
        for (int i = 1; i <= 24; i++) {
            options.add(String.format(Locale.getDefault(), "第%d周", i));
        }
        optionsPickerView.setPicker(options);
        if (nowWeek > 0) {
            optionsPickerView.setSelectOptions(nowWeek - 1);
        }
    }

    private String[] makeDateOnWeek() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "MM-dd", Locale.getDefault());
        String[] answer     = new String[7];
        long plus = (selectedWeek - nowWeek) *  7L * 24L * 3600000L;
        long time = System.currentTimeMillis() + plus;

        calendar.setTime(new Date(time));
        for (int i = 0; i < 7; i++) {
            calendar.set(Calendar.DAY_OF_WEEK, i + 1);
            Date date = calendar.getTime();
            answer[i] = simpleDateFormat.format(date.getTime());
        }

        return answer;
    }

    private void drawBackNowWeekBtn() {
        Button backNowWeekBtn = new Button(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.setMarginEnd(
                (int) GlobalLib.GetRawSize(this, TypedValue.COMPLEX_UNIT_DIP, 10));
        backNowWeekBtn.setLayoutParams(layoutParams);
        backNowWeekBtn.setTextColor(Color.parseColor("#ffffff"));
        backNowWeekBtn.setText("回到本周");
        backNowWeekBtn.setBackgroundResource(R.drawable.shape_white_stroke);
        backToNowWeekBtnId = View.generateViewId();
        backNowWeekBtn.setId(backToNowWeekBtnId);
        backNowWeekBtn.setOnClickListener(this);

        RelativeLayout titleBarView = findViewById(R.id.titleBarView);
        titleBarView.addView(backNowWeekBtn);
    }

    private void drawSyllabus() {
        if (isInit) {
            contentWidth    = syllbusContent.getWidth();
            contentHeight   = syllbusContent.getHeight();
            titleWidth      = contentWidth / (horizontalTabNum * 2 + 1);
            titleHeight     = titleWidth / 4 * 5;
            tabWidth        = titleWidth * 2;
            tabHeight       = (contentHeight - titleHeight) / verticalTabNum;

            isInit = false;
        }

        for (View view: coursesView) {
            syllbusContent.removeView(view);
        }
        coursesView.clear();

        drawTitle(titleWidth, titleHeight, tabWidth, tabHeight);
        drawContent(titleWidth, titleHeight, tabWidth, tabHeight);
    }

    private void drawContent(int titleWidth, int titleHeight, int tabWidth, int tabHeight) {
        if (selectedWeek < 1 || selectedWeek > 24) {
            noDataView.setVisibility(View.VISIBLE);
            return;
        }
        noDataView.setVisibility(View.INVISIBLE);

        List<List<Course>> data = syllabusController.GetCourseInfoByWeek(selectedWeek);
        Random random = new Random(System.currentTimeMillis());

        boolean isEmpty = true;
        for (int i = 0; i < 7; i++) {
            List<Course> courseList = data.get(i);
            for (Course course: courseList) {
                isEmpty = false;
                int courseBegin  = course.getBegin();
                int courseLength = course.getEnd() - course.getBegin() + 1;

                //  Switch a Color
                String color;
                if (mapNameToColor.containsKey(course.getName())) {
                    color = mapNameToColor.get(course.getName());
                } else {
                    int index = random.nextInt(baseColorSwitchLimit);
                    color = baseColors[baseColorIndex[index]];
                    baseColorIndex[index] = baseColorIndex[baseColorSwitchLimit - 1];
                    baseColorSwitchLimit--;
                    mapNameToColor.put(course.getName(), color);
                }

                drawCourseView(
                        tabWidth, tabHeight * courseLength,
                        titleWidth + tabWidth * i,
                        titleHeight + tabHeight * (courseBegin - 1),
                        String.format(Locale.getDefault(), "%s\n@%s",
                                course.getName(), course.getAddress()),
                        color, course);
            }
        }

        if (isEmpty) {
            noDataView.setVisibility(View.VISIBLE);
        }
    }

    private void drawTitle(int titleWidth, int titleHeight, int tabWidth, int tabHeight) {
        //  draw setting button
        drawSettingBtn(titleWidth,titleHeight);

        //  draw horizontal title
        String[] date = makeDateOnWeek();
        for (int i = 0; i < horizontalTabNum; i++) {
            drawTitleItem(
                    tabWidth, titleHeight,
                    titleWidth + i * tabWidth, 0,
                    drawTitleTextView(weekDayName[i], true, 12, "#000000"),
                    drawTitleTextView(date[i], false, 10, "#aaaaaa"));
        }

        //  draw vertical title
        int campus = syllabusController.GetData().getCampus();
        for (int i = 0; i < verticalTabNum; i++) {
            drawTitleItem(
                    titleWidth, tabHeight - 1,
                    0, titleHeight + i * tabHeight,
                    drawTitleTextView(studyBeginTime[campus][i], false, 8, "#aaaaaa"),
                    drawTitleTextView(String.valueOf(i + 1), false, 14, "#aaaaaa"));
        }
    }

    private void drawCourseView(
            int width, int height,
            int x, int y,
            String content, String color,
            Course course) {
        TextView courseView = new TextView(this);
        RelativeLayout.LayoutParams courseViewParams
                = new RelativeLayout.LayoutParams(width, height);
        courseViewParams.topMargin = y;
        courseViewParams.setMarginStart(x);
        courseView.setLayoutParams(courseViewParams);
        courseView.setGravity(Gravity.CENTER);
        courseView.setTextColor(Color.parseColor("#ffffff"));
        courseView.setTextSize(10);
        courseView.setText(content);
        courseView.setBackgroundColor(Color.parseColor(color));
        int newId = View.generateViewId();
        courseView.setId(newId);
        courseView.setOnClickListener(this);
        mapIdToCourse.put(newId, course);

        syllbusContent.addView(courseView);
        coursesView.add(courseView);
    }

    private void drawTitleItem(
            int width, int height,
            int x, int y,
            TextView oneView, TextView twoView) {
        LinearLayout titleItem = new LinearLayout(this);
        RelativeLayout.LayoutParams titleItemParams
                = new RelativeLayout.LayoutParams(width, height);
        titleItemParams.setMarginStart(x);
        titleItemParams.topMargin = y;
        titleItem.setLayoutParams(titleItemParams);
        titleItem.setBackgroundColor(Color.parseColor("#ffffff"));
        titleItem.setOrientation(LinearLayout.VERTICAL);
        titleItem.setGravity(Gravity.CENTER_HORIZONTAL);
        titleItem.addView(oneView);
        titleItem.addView(twoView);

        syllbusContent.addView(titleItem);
        coursesView.add(titleItem);
    }

    private void drawSettingBtn(int width, int height) {
        LinearLayout settingBtnContent = new LinearLayout(this);
        settingBtnContent.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
        settingBtnContent.setGravity(Gravity.CENTER);
        settingBtnContent.setBackgroundColor(Color.parseColor("#ffffff"));

        ImageView settingBtn = new ImageView(this);
        settingBtn.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
        settingBtn.setImageResource(R.drawable.icon_setting);
        settingBtn.setScaleX(0.8f);
        settingBtn.setScaleY(0.8f);

        settingBtnContent.addView(settingBtn);
        syllbusContent.addView(settingBtnContent);
    }

    private TextView drawTitleTextView(String text, boolean isBold, int size, String color) {
        TextView oneView = new TextView(this);
        oneView.setTextColor(Color.parseColor(color));
        oneView.getPaint().setFakeBoldText(isBold);
        oneView.setTextSize(size);
        oneView.setText(text);
        oneView.setGravity(Gravity.CENTER);

        return oneView;
    }

    @Override
    public void titleBarOnClicked(int id) {
        switch (id) {
            case R.id.headback:
                finish();
                break;
        }
    }

    @Override
    public void onOptionsSelect(int options1, int options2, int options3, View v) {
        selectedWeek = options1 + 1;
        isDraw = true;
        pageTitle.setText(options.get(options1));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.pagetitle:
                optionsPickerView.show();
                break;
            default:
                if (id == backToNowWeekBtnId) {
                    if (selectedWeek != nowWeek) {
                        selectedWeek = nowWeek;
                        optionsPickerView.setSelectOptions(nowWeek - 1);
                        setPageTitle(nowWeek);
                        drawSyllabus();
                    }
                } else {
                    Course course = mapIdToCourse.get(id);
                    courseInfoDialog.show(course);
                }
                break;
        }
    }
}
