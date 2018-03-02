package com.qb.xrealsys.ifafu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.qb.xrealsys.ifafu.delegate.TitleBarButtonOnClickedDelegate;
import com.qb.xrealsys.ifafu.model.Score;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ScoreInfoActivity extends BaseActivity implements
        TitleBarButtonOnClickedDelegate {

    private TitleBarController titleBarController;

    private ListView           oneList;

    private ListView           twoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_info);

        titleBarController = new TitleBarController(this);
        titleBarController
                .setHeadBack()
                .setBigPageTitle("成绩详情")
                .setOnClickedListener(this);

        oneList = findViewById(R.id.scoreInfoOneList);
        twoList = findViewById(R.id.scoreInfoTwoList);

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
        twoAdapterData.add(makeAdapterMap("是否重修", String.valueOf(score.isRestudy())));
        twoAdapterData.add(makeAdapterMap("学分", String.valueOf(score.getStudyScore())));
        twoAdapterData.add(makeAdapterMap("绩点", String.valueOf(score.getScorePoint())));
        SimpleAdapter twoSimpleAdapter = new SimpleAdapter(
                this,
                twoAdapterData,
                R.layout.gadget_item_score_info,
                new String[] {"name", "content"},
                new int[] {R.id.scoreInfoItemTitle, R.id.scoreInfoItemContent});
        twoList.setAdapter(twoSimpleAdapter);
    }

    private Map<String, Object> makeAdapterMap(String name, String content) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        if (content.length() == 0) {
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
}
