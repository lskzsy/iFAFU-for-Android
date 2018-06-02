package com.qb.xrealsys.ifafu.Exam;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.qb.xrealsys.ifafu.Base.BaseActivity;
import com.qb.xrealsys.ifafu.Exam.controller.ExamAsyncController;
import com.qb.xrealsys.ifafu.Base.controller.LoadingViewController;
import com.qb.xrealsys.ifafu.MainApplication;
import com.qb.xrealsys.ifafu.R;
import com.qb.xrealsys.ifafu.Base.controller.TitleBarController;
import com.qb.xrealsys.ifafu.Base.delegate.TitleBarButtonOnClickedDelegate;
import com.qb.xrealsys.ifafu.Exam.delegate.UpdateExamTableDelegate;
import com.qb.xrealsys.ifafu.Exam.model.Exam;
import com.qb.xrealsys.ifafu.Exam.model.ExamTable;
import com.qb.xrealsys.ifafu.Tool.GlobalLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExamActivity extends BaseActivity implements
        UpdateExamTableDelegate,
        TitleBarButtonOnClickedDelegate {

    TitleBarController      titleBarController;

    MainApplication         mainApplication;

    ExamAsyncController     examController;

    TextView                viewTitle;

    ListView                listView;

    LinearLayout            blankView;

    LoadingViewController   loadingViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        loadingViewController = new LoadingViewController(this);
        loadingViewController.show();

        InitElement();

        titleBarController = new TitleBarController(this);
        titleBarController
                .setBigPageTitle("学生考试查询")
                .setHeadBack()
                .setOnClickedListener(this);
        mainApplication    = (MainApplication) getApplication();
        examController     = mainApplication.getExamController();
        examController.setUpdateExamTableDelegate(this);
        examController.SyncData();
    }

    private void InitElement() {
        viewTitle = findViewById(R.id.examViewTitle);
        listView  = findViewById(R.id.examViewList);
        blankView = findViewById(R.id.noDataView);
    }

    @Override
    public void titleBarOnClicked(int id) {
        finish();
    }

    @Override
    public void UpdatedExamTable() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ExamTable  examTable    = examController.GetData();
                List<Exam> examList     = examTable.getData();
                String     viewTitle    = String.format(
                        Locale.getDefault(), getString(R.string.format_study_time),
                        examTable.getSearchYearOptions().get(examTable.getSelectedYearOption()),
                        examTable.getSearchTermOptions().get(examTable.getSelectedTermOption()));

                ExamActivity.this.viewTitle.setText(viewTitle);

                if (examList.size() > 0) {
                    blankView.setVisibility(View.INVISIBLE);
                }

                List<Map<String, Object>> adapterData = new ArrayList<>();
                for (Exam exam: examList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("title", exam.getName());
                    map.put("time", exam.getDatetime());
                    map.put("loc", exam.getAddress());
                    map.put("seat", exam.getSeatNumber());
                    map.put("campus", exam.getCampus());
                    map.put("id", exam.getId());

                    String[] timeCompareAnswer = GlobalLib.CompareWithNowTime(exam.getBeginTime());
                    if (timeCompareAnswer[0].equals("-")) {
                        map.put("finish", true);
                        map.put("progress", "已结束");
                    } else {
                        map.put("finish", false);
                        map.put("progress", "剩" + timeCompareAnswer[1] + timeCompareAnswer[2]);
                    }

                    adapterData.add(map);
                }

                ExamItemAdapter examItemAdapter = new ExamItemAdapter(
                        ExamActivity.this, adapterData,
                        R.layout.gadget_item_exam,
                        new String[] {
                                "title", "time", "loc", "seat", "campus", "id", "progress"},
                        new int[] {
                                R.id.examViewItemTitle, R.id.examViewItemTime, R.id.examViewItemLocation,
                                R.id.examViewItemSeat, R.id.examViewItemCampus, R.id.examViewItemId,
                                R.id.examViewItemProgress});
                listView.setAdapter(examItemAdapter);
                int high = (int) GlobalLib.GetRawSize(
                        ExamActivity.this,
                        TypedValue.COMPLEX_UNIT_DIP,
                        examList.size() * 90 + 10);
                listView.setLayoutParams(new RelativeLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        high));

                loadingViewController.cancel();
            }
        });
    }

    public class ExamItemAdapter extends SimpleAdapter {

        public ExamItemAdapter(Context context,
                               List<? extends Map<String, ?>> data,
                               int resource,
                               String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(
                        R.layout.gadget_item_exam, parent, false);
            }
            LinearLayout progressStrokeView =
                    convertView.findViewById(R.id.examViewItemProgressStroke);
            TextView     progressView = convertView.findViewById(R.id.examViewItemProgress);

            Map<String, Object> map = (Map<String, Object>) this.getItem(position);

            if ((boolean) map.get("finish")) {
                progressStrokeView.setBackground(getDrawable(R.drawable.shape_red_stroke));
                progressView.setTextColor(Color.parseColor("#ff0000"));
            } else {
                progressStrokeView.setBackground(getDrawable(R.drawable.shape_green_stroke));
                progressView.setTextColor(Color.parseColor("#00ff00"));
            }

            return super.getView(position, convertView, parent);
        }
    }
}
