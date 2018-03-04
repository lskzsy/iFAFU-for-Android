package com.qb.xrealsys.ifafu.Score;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.qb.xrealsys.ifafu.Base.BaseActivity;
import com.qb.xrealsys.ifafu.MainApplication;
import com.qb.xrealsys.ifafu.R;
import com.qb.xrealsys.ifafu.Score.controller.ScoreController;
import com.qb.xrealsys.ifafu.Base.controller.TitleBarController;
import com.qb.xrealsys.ifafu.Base.delegate.TitleBarButtonOnClickedDelegate;
import com.qb.xrealsys.ifafu.Score.delegate.UpdateMakeupExamInfoDelegate;
import com.qb.xrealsys.ifafu.Score.model.MakeupExam;
import com.qb.xrealsys.ifafu.Score.model.Score;
import com.qb.xrealsys.ifafu.Tool.GlobalLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoreInfoActivity extends BaseActivity implements
        UpdateMakeupExamInfoDelegate,
        TitleBarButtonOnClickedDelegate {

    private MainApplication mainApplication;

    private ScoreController scoreController;

    private TitleBarController titleBarController;

    private ListView           oneList;

    private ListView           twoList;

    private ListView           threeList;

    private LinearLayout       threeGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_info);

        mainApplication    = (MainApplication) getApplication();
        scoreController    = mainApplication.getScoreController();
        scoreController.setUpdateMakeupExamInfoDelegate(this);
        titleBarController = new TitleBarController(this);
        titleBarController
                .setHeadBack()
                .setBigPageTitle("成绩详情")
                .setOnClickedListener(this);

        oneList     = findViewById(R.id.scoreInfoOneList);
        twoList     = findViewById(R.id.scoreInfoTwoList);
        threeList   = findViewById(R.id.scoreInfoThreeList);
        threeGroup  = findViewById(R.id.scoreInfoGroup3);

        getStarupParams();
    }

    private void getStarupParams() {
        Bundle bundle = getIntent().getExtras();
        initListView((Score) bundle.getSerializable("score"));
    }

    private void initListView(Score score) {
        List<Map<String, Object>> oneAdapterData = new ArrayList<>();
        oneAdapterData.add(makeAdapterMap("课程名称", score.getCourseName()));
        oneAdapterData.add(makeAdapterMap("所属学期",
                String.format(Locale.getDefault(), getString(R.string.format_study_time),
                        score.getYear(), score.getTerm())));
        oneAdapterData.add(makeAdapterMap("课程性质", score.getCourseType()));
        oneAdapterData.add(makeAdapterMap("课程归属", score.getCourseOwner()));

        SimpleAdapter oneSimpleAdapter = new SimpleAdapter(
                this,
                        oneAdapterData,
                        R.layout.gadget_item_score_info,
                        new String[] {"name", "content"},
                        new int[] {R.id.scoreInfoItemTitle, R.id.scoreInfoItemContent});
        oneList.setAdapter(oneSimpleAdapter);

        List<Map<String, Object>> twoAdapterData = new ArrayList<>();
        twoAdapterData.add(makeAdapterMap("平时成绩", ""));
        twoAdapterData.add(makeAdapterMap("实验成绩", ""));
        twoAdapterData.add(makeAdapterMap("卷面成绩", ""));
        twoAdapterData.add(makeAdapterMap("最终成绩", String.valueOf(score.getScore())));
        twoAdapterData.add(makeAdapterMap("补考成绩", String.valueOf(score.getMakeupScore())));
        twoAdapterData.add(makeAdapterMap("是否重修", score.isRestudy() ? "是" : "否"));
        twoAdapterData.add(makeAdapterMap("学分", String.valueOf(score.getStudyScore())));
        twoAdapterData.add(makeAdapterMap("绩点", String.valueOf(score.getScorePoint())));
        SimpleAdapter twoSimpleAdapter = new SimpleAdapter(
                this,
                twoAdapterData,
                R.layout.gadget_item_score_info,
                new String[] {"name", "content"},
                new int[] {R.id.scoreInfoItemTitle, R.id.scoreInfoItemContent});
        twoList.setAdapter(twoSimpleAdapter);

        if (score.getScore() < 60) {
            threeGroup.setVisibility(View.VISIBLE);
            scoreController.GetScoreMakeupExam(score);
        }
    }

    private Map<String, Object> makeAdapterMap(String name, String content) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        if (content == null || content.length() == 0) {
            map.put("content", "无信息");
        } else {
            map.put("content", content);
        }

        return map;
    }

    @Override
    public void titleBarOnClicked(int id) {
        finish();
    }

    @Override
    public void informMakeupExamUpdated(MakeupExam makeupExam) {
        final MakeupExam exam = makeupExam;

        String timeFormat = "";
        if (makeupExam.getTime() != null) {
            Pattern pattern = Pattern.compile("(\\d+)年(\\d+)月(\\d+)日\\((\\d+):(\\d+)-\\d+:\\d+\\)");
            Matcher matcher = pattern.matcher(makeupExam.getTime());
            List<Integer> timeArray = new ArrayList<>();
            if (matcher.find()) {
                for (int i = 1; i <= 5; i++) {
                    timeArray.add(Integer.parseInt(matcher.group(i)));
                }
            }
            String[] answer = GlobalLib.CompareWithNowTime(timeArray);
            String timeString = "已结束";
            if (answer[0].contains("+")) {
                timeString = String.format(Locale.getDefault(), "剩%s%s", answer[1], answer[2]);
            }
            timeFormat = String.format(Locale.getDefault(),
                    "%s-%s", exam.getTime(), timeString);
        }
        final String finalTimeFormat = timeFormat;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<Map<String, Object>> threeAdapterData = new ArrayList<>();
                threeAdapterData.add(makeAdapterMap("考试时间", finalTimeFormat));
                threeAdapterData.add(makeAdapterMap("考试地点", exam.getLocation()));
                threeAdapterData.add(makeAdapterMap("座位号", exam.getSeatNumber()));
                threeAdapterData.add(makeAdapterMap("考试形式", exam.getMethod()));

                SimpleAdapter threeSimpleAdapter = new SimpleAdapter(
                        ScoreInfoActivity.this,
                        threeAdapterData,
                        R.layout.gadget_item_score_info,
                        new String[] {"name", "content"},
                        new int[] {R.id.scoreInfoItemTitle, R.id.scoreInfoItemContent});
                threeList.setAdapter(threeSimpleAdapter);
            }
        });
    }
}
