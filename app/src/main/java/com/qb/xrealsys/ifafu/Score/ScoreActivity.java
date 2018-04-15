package com.qb.xrealsys.ifafu.Score;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.qb.xrealsys.ifafu.Base.BaseActivity;
import com.qb.xrealsys.ifafu.Base.controller.LoadingViewController;
import com.qb.xrealsys.ifafu.MainApplication;
import com.qb.xrealsys.ifafu.R;
import com.qb.xrealsys.ifafu.Score.controller.ScoreAsyncController;
import com.qb.xrealsys.ifafu.Base.controller.TitleBarController;
import com.qb.xrealsys.ifafu.User.controller.UserAsyncController;
import com.qb.xrealsys.ifafu.Base.delegate.TitleBarButtonOnClickedDelegate;
import com.qb.xrealsys.ifafu.Score.delegate.UpdateMainScoreViewDelegate;
import com.qb.xrealsys.ifafu.Base.dialog.ProgressDialog;
import com.qb.xrealsys.ifafu.Score.model.Score;
import com.qb.xrealsys.ifafu.Score.model.ScoreTable;
import com.qb.xrealsys.ifafu.User.model.User;
import com.qb.xrealsys.ifafu.Tool.ConfigHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ScoreActivity extends BaseActivity
        implements
        TitleBarButtonOnClickedDelegate,
        AdapterView.OnItemClickListener,
        UpdateMainScoreViewDelegate,
        View.OnClickListener,
        OptionsPickerView.OnOptionsSelectListener {

    private ScoreAsyncController scoreController;

    private ConfigHelper            configHelper;

    private List<String>            yearOptions;

    private List<List<String>>      termOptions;

    private OptionsPickerView       optionsPickerView;

    private User                    user;

    private TextView                scoreValueFront;

    private TextView                scoreValueBack;

    private TextView                scoreNumberView;

    private TextView                scoreViewTitle;

    private LinearLayout            noDataView;

    private ListView                scoreListView;

    private TextView                scoreViewBottom;

    private boolean                 isUpdate;

    private TitleBarController titleBarController;

    private MainApplication mainApplication;

    private UserAsyncController userController;

    private LoadingViewController loadingViewController;

    private ProgressDialog          progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        progressDialog       = new ProgressDialog(this);
        loadingViewController = new LoadingViewController(this);
        loadingViewController.show();

        getStartUpParams();
        InitElements();
        InitOptionsPickerView();

        scoreViewTitle.setOnClickListener(this);

        isUpdate = true;

        try {
            configHelper    = new ConfigHelper(ScoreActivity.this);
            scoreController = mainApplication.getScoreController();
            scoreController.setUpdateMainScoreViewDelegate(this);
            scoreController.SyncData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void InitOptionsPickerView() {
        optionsPickerView = new OptionsPickerView.Builder(
                ScoreActivity.this,
                this)
                .setLinkage(false)
                .setCancelText("取消")
                .setSubmitText("确定")
                .setTitleSize(13)
                .setTitleText("选择学年/学期")
                .setTitleColor(Color.parseColor("#157efb"))
                .build();
    }

    private void UpdateOptionsPickerView() {
        ScoreTable scoreTable = scoreController.GetData();
        yearOptions = scoreTable.getSearchYearOptions();
        termOptions = new ArrayList<>();
        for (String option: yearOptions) {
            termOptions.add(scoreTable.getSearchTermOptions());
        }

        optionsPickerView.setPicker(yearOptions, termOptions);
        optionsPickerView.setSelectOptions(
                scoreTable.getSelectedYearOption(),
                scoreTable.getSelectedTermOption());
    }

    @Override
    public void onOptionsSelect(int options1, int options2, int options3, View v) {
        try {
            progressDialog.show("正在加载...");
            scoreController.SyncData(options1, options2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void InitElements() {
        scoreViewTitle  = findViewById(R.id.scoreViewTitle);
        scoreNumberView = findViewById(R.id.scoreViewScoreNumber);
        scoreValueFront = findViewById(R.id.scoreViewValueFront);
        scoreValueBack  = findViewById(R.id.scoreViewValueBack);
        noDataView      = findViewById(R.id.noDataView);
        scoreListView   = findViewById(R.id.scoreListView);
        scoreViewBottom = findViewById(R.id.scoreViewBottom);
    }


    @Override
    protected void onStart() {
        super.onStart();
        updateActivity();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scoreViewTitle:
                optionsPickerView.show();
                break;
        }
    }

    @Override
    public void titleBarOnClicked(int id) {
        switch (id) {
            case R.id.headback:
                finish();
                break;
        }
    }

    private void updateActivity() {
        titleBarController = new TitleBarController(this);
        titleBarController
                .setHeadBack()
                .setTwoLineTitle("成绩查询", String.format(
                        Locale.getDefault(), "%s(%s)",
                        user.getName(),
                        user.getAccount()))
                .setOnClickedListener(this);
    }

    private void getStartUpParams() {
        mainApplication = (MainApplication) getApplication();
        userController  = mainApplication.getUserController();
        user            = userController.getData();
    }

    @Override
    public void updateMainScore(ScoreTable scoreTable) {
        final ScoreTable inScoreTable = scoreTable;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreViewTitle.setText(
                        String.format(Locale.getDefault(),
                                getString(R.string.format_main_score_title),
                                inScoreTable.getSearchYearOptions().get(
                                        inScoreTable.getSelectedYearOption()),
                                inScoreTable.getSearchTermOptions().get(
                                        inScoreTable.getSelectedTermOption())));

                scoreNumberView.setText(String.valueOf(inScoreTable.getData().size()));

                try {
                    float  scoreValue  = scoreController.calculateIntellectualEducationScore();
                    String scoreString = String.valueOf(scoreValue);
                    String[] scoreList = scoreString.split("\\.");
                    if (scoreValue != 0 || scoreList.length > 0) {
                        scoreValueFront.setText(scoreList[0]);
                        scoreValueBack.setText(
                                String.format(Locale.getDefault(),
                                        ".%s", scoreList[1].substring(0, 2 % scoreList[1].length())));
                    } else {
                        scoreValueFront.setText("0");
                        scoreValueBack.setText(".0");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                List<Score> scoreList = inScoreTable.getData();
                if (scoreList.size() > 0) {
                    scoreViewBottom.setVisibility(View.VISIBLE);
                    noDataView.setVisibility(View.INVISIBLE);
                } else {
                    scoreViewBottom.setVisibility(View.INVISIBLE);
                    noDataView.setVisibility(View.VISIBLE);
                }

                if (isUpdate) {
                    isUpdate = false;
                    configHelper.SetValue(
                            "lastReadScoreCount",
                            String.valueOf(scoreList.size()));
                }

                List<Map<String, Object>> adapterData = new ArrayList<>();
                for (Score score: scoreList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", score.getCourseName());
                    if (score.isDelayExam()) {
                        map.put("value", "缓考");
                    } else if ((int) score.getScore() == score.getScore()) {
                        map.put("value", (int) score.getScore());
                    } else {
                        map.put("value", score.getScore());
                    }

                    if (score.getScore() < 60) {
                        map.put("icon", R.drawable.icon_nopass);
                    } else {
                        map.put("icon", R.drawable.icon_pass);
                    }

                    adapterData.add(map);
                }

                SimpleAdapter simpleAdapter = new SimpleAdapter(
                        ScoreActivity.this,
                        adapterData,
                        R.layout.gadget_item_score,
                        new String[] {"name", "value", "icon"},
                        new int[] {R.id.scoreItemTitle, R.id.scoreItemValue, R.id.scoreItemIcon});
                scoreListView.setAdapter(simpleAdapter);
                scoreListView.setOnItemClickListener(ScoreActivity.this);

                UpdateOptionsPickerView();
                loadingViewController.cancel();
                progressDialog.cancel();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List<Score> scoreList = scoreController.GetData().getData();
        Intent intent = new Intent(ScoreActivity.this, ScoreInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("score", scoreList.get((int) id));
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
