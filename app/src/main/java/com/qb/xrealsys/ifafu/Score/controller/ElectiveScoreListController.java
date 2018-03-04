package com.qb.xrealsys.ifafu.Score.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.qb.xrealsys.ifafu.Base.controller.LoadingViewController;
import com.qb.xrealsys.ifafu.MainApplication;
import com.qb.xrealsys.ifafu.R;
import com.qb.xrealsys.ifafu.Score.ScoreInfoActivity;
import com.qb.xrealsys.ifafu.Score.delegate.UpdateElectiveTargetScoreDelegate;
import com.qb.xrealsys.ifafu.Score.model.ElectiveScoreItem;
import com.qb.xrealsys.ifafu.Score.model.Score;
import com.qb.xrealsys.ifafu.Tool.GlobalLib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by sky on 23/02/2018.
 */

public class ElectiveScoreListController implements
        AdapterView.OnItemClickListener,
        View.OnClickListener,
        UpdateElectiveTargetScoreDelegate {

    private Activity          activity;

    private ElectiveScoreItem itemAll;

    private ElectiveScoreItem itemNature;

    private ElectiveScoreItem itemSocial;

    private ElectiveScoreItem itemArt;

    private ElectiveScoreItem itemLetter;

    private ElectiveScoreItem itemInnovate;

    private List<ElectiveScoreItem> items;

    private MainApplication mainApplication;

    private ScoreController scoreController;

    private Map<String, Integer>    mapStringToIndex;

    private List<ArrayList<Score>>  electiveScores;

    private List<Double>            electiveTotalScore;

    private List<Integer>           icons;

    private List<Integer>           titles;

    private List<Integer>           listIcons;

    private Map<String, Float>      electiveTargetScore;

    private Map<Integer, Integer>   mapIdToIndex;

    private LoadingViewController loadingViewController;

    public ElectiveScoreListController(Activity activity) {
        this.activity = activity;

        itemAll      = new ElectiveScoreItem(activity.findViewById(R.id.electiveScoreItemAll));
        itemNature   = new ElectiveScoreItem(activity.findViewById(R.id.electiveScoreItemNature));
        itemSocial   = new ElectiveScoreItem(activity.findViewById(R.id.electiveScoreItemSocial));
        itemArt      = new ElectiveScoreItem(activity.findViewById(R.id.electiveScoreItemArt));
        itemLetter   = new ElectiveScoreItem(activity.findViewById(R.id.electiveScoreItemLetter));
        itemInnovate = new ElectiveScoreItem(activity.findViewById(R.id.electiveScoreItemInnovate));

        loadingViewController = new LoadingViewController(activity);
        loadingViewController.show();

        this.items   =
                Arrays.asList(itemAll, itemNature, itemSocial, itemArt, itemLetter, itemInnovate);
        this.electiveScores =
                Arrays.asList(
                        new ArrayList<Score>(),
                        new ArrayList<Score>(),
                        new ArrayList<Score>(),
                        new ArrayList<Score>(),
                        new ArrayList<Score>(),
                        new ArrayList<Score>());
        this.mapStringToIndex   = new HashMap<>();
        this.mapIdToIndex       = new HashMap<Integer, Integer>() {{
            put(R.id.electiveScoreItemAll, 0);
            put(R.id.electiveScoreItemNature, 1);
            put(R.id.electiveScoreItemSocial, 2);
            put(R.id.electiveScoreItemArt, 3);
            put(R.id.electiveScoreItemLetter, 4);
            put(R.id.electiveScoreItemInnovate, 5);
        }};
        this.electiveTotalScore = Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

        InitData();
        InitListView();

        mainApplication = (MainApplication) activity.getApplication();
        scoreController = mainApplication.getScoreController();
        scoreController.setUpdateElectiveTargetScoreDelegate(this);
    }

    public void SyncListView() {
        scoreController.SyncElectiveScore();
    }

    private void InitData() {
        icons = Arrays.asList(
                R.drawable.icon_all_elective_score,
                R.drawable.icon_nature_elective_score,
                R.drawable.icon_social_elective_score,
                R.drawable.icon_art_elective_score,
                R.drawable.icon_letter_elective_score,
                R.drawable.icon_innovate_elective_score);

        listIcons = Arrays.asList(
                R.drawable.icon_nature_elective,
                R.drawable.icon_social_elective,
                R.drawable.icon_art_elective,
                R.drawable.icon_letter_elective,
                R.drawable.icon_innovate_elective);

        titles = Arrays.asList(
                R.string.display_elective_score_all,
                R.string.display_elective_score_nature,
                R.string.display_elective_score_social,
                R.string.display_elective_score_art,
                R.string.display_elective_score_letter,
                R.string.display_elective_score_innovate);
    }

    private void InitListView() {
        Iterator<Integer>           iconIter  = icons.iterator();
        Iterator<Integer>           titleIter = titles.iterator();
        Iterator<ElectiveScoreItem> itemIter  = items.iterator();

        int i = 0;
        while (itemIter.hasNext() && titleIter.hasNext() && iconIter.hasNext()) {
            ElectiveScoreItem item = itemIter.next();
            int               icon = iconIter.next();
            int              title = titleIter.next();

            mapStringToIndex.put(activity.getString(title), i);
            item.getTitle().setText(String.format(
                    Locale.getDefault(),
                    activity.getString(R.string.format_elective_score_item_title),
                    activity.getString(title), "无要求", 0));
            item.getIcon().setImageResource(icon);
            item.getSelf().setOnClickListener(this);
            i++;
        }
    }

    private void updateActivity() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 6; i++) {
                    ElectiveScoreItem item = items.get(i);
                    List<Score>  scoreList = electiveScores.get(i);
                    Double      totalScore = electiveTotalScore.get(i);
                    String           title = activity.getString(titles.get(i));
                    String         content = "无要求";

                    if (electiveTargetScore.get(title) > 0) {
                        content = String.format(
                                Locale.getDefault(),
                                activity.getString(
                                        R.string.format_elective_score_item_title_content),
                                electiveTargetScore.get(title).toString());

                        ElectiveScoreProgressBarController.Build(
                                        item.getSelf(),
                                        electiveTargetScore.get(title),
                                        totalScore);
                    }

                    item.getTitle().setText(String.format(
                            Locale.getDefault(),
                            activity.getString(R.string.format_elective_score_item_title),
                            title, content, scoreList.size()
                    ));

                    String scoreStr = totalScore.toString();
                    item.getScore().setText(String.format(
                            Locale.getDefault(), "%s", scoreStr));
                }

                loadingViewController.cancel();
            }
        });
    }

    @Override
    public void UpdatedElectiveTargetScore() {
        List<Score> electiveScore = scoreController.GetElectiveData();
        for (Score score: electiveScore) {
            electiveScores.get(0).add(score);
            electiveScores.get(mapStringToIndex.get(score.getCourseOwner())).add(score);

            if (score.getScore() >= 60) {
                int index = mapStringToIndex.get(score.getCourseOwner());
                electiveTotalScore.set(
                        index,
                        electiveTotalScore.get(index) + score.getStudyScore());
                electiveTotalScore.set(
                        0,
                        electiveTotalScore.get(0) + score.getStudyScore());
            }
        }

        electiveTargetScore = scoreController.getElectiveTargetScore();
        updateActivity();
    }

    @Override
    public void onClick(View v) {
        int index = mapIdToIndex.get(v.getId());
        ElectiveScoreItem item = items.get(index);
        ImageView          btn = item.getButton();
        ListView          list = item.getList();
        List<Score>  scoreList = electiveScores.get(index);

        btn.setPivotX(btn.getWidth() / 2);
        btn.setPivotY(btn.getHeight() / 2);

        if (scoreList.size() < 1) {
            Toast.makeText(
                    activity,
                    activity.getString(R.string.error_elective_score_list),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (list.getAdapter() != null && list.getAdapter().getCount() != 0) {
            item.getButton().setRotation(0);
            list.setAdapter(null);
            list.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        } else {
            item.getButton().setRotation(90);
            List<Map<String, Object>> adaptData = new ArrayList<>();

            for (Score score: scoreList) {
                Map<String, Object> map = new HashMap<>();
                map.put("icon", listIcons.get(mapStringToIndex.get(score.getCourseOwner()) - 1));
                map.put("title", score.getCourseName());
                map.put("score", String.valueOf(score.getStudyScore()));
                map.put("self", score);
                if (score.getScore() >= 60) {
                    map.put("status", R.drawable.icon_green);
                } else {
                    map.put("status", R.drawable.icon_red);
                }
                adaptData.add(map);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(
                    activity,
                    adaptData,
                    R.layout.gadget_item_elective_score_list,
                    new String[] {"icon", "title", "score", "status"},
                    new int[]{
                            R.id.electiveScoreListItemIcon,
                            R.id.electiveScoreListItemTitle,
                            R.id.electiveScoreListItemScore,
                            R.id.electiveScoreListItemStatus});
            list.setAdapter(simpleAdapter);

            int heigh = scoreList.size() * 50;
            list.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) GlobalLib.GetRawSize(activity, TypedValue.COMPLEX_UNIT_DIP, heigh)));
            list.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, Object> item =
                (HashMap<String, Object>) parent.getAdapter().getItem(position);

        Intent intent = new Intent(activity, ScoreInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("score", (Score) item.get("self"));
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }
}
