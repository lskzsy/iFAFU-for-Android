package com.qb.xrealsys.ifafu;

import com.qb.xrealsys.ifafu.delegate.UpdateMainScoreViewDelegate;
import com.qb.xrealsys.ifafu.model.Score;
import com.qb.xrealsys.ifafu.model.ScoreTable;
import com.qb.xrealsys.ifafu.model.User;
import com.qb.xrealsys.ifafu.tool.ConfigHelper;
import com.qb.xrealsys.ifafu.tool.GlobalLib;
import com.qb.xrealsys.ifafu.web.ScoreInterface;

import java.io.IOException;
import java.util.List;

/**
 * Created by sky on 14/02/2018.
 */

public class ScoreController {

    private ScoreTable scoreTable;

    private User user;

    private ConfigHelper configHelper;

    private ScoreInterface scoreInterface;

    private UpdateMainScoreViewDelegate updateMainScoreViewDelegate;

    public ScoreController(User user, ConfigHelper configHelper) {
        this.user           = user;
        this.configHelper   = configHelper;
        this.scoreTable     = new ScoreTable();
        this.scoreInterface = new ScoreInterface(
                configHelper.GetSystemValue("host"),
                user.getToken());
    }

    public void SyncData(int year, int term) throws IOException {
        final String y = scoreTable.getSearchYearOptions().get(year);
        final String t = scoreTable.getSearchTermOptions().get(term);
        scoreTable.setSelectedYearOption(year);
        scoreTable.setSelectedTermOption(term);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    scoreInterface.updateScoreTable(
                            scoreTable,
                            user.getAccount(),
                            user.getName(),
                            y, t);
                    updateMainScoreViewDelegate.updateMainScore(scoreTable);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();;
    }

    public void SyncData() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    scoreTable = scoreInterface.GetScoreTable(user.getAccount(), user.getName());
                    updateMainScoreViewDelegate.updateMainScore(scoreTable);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public ScoreTable GetData() {
        return this.scoreTable;
    }

    public void SetScoreTableData(List<Score> scoreList) {
        scoreTable.setData(scoreList);
    }

    public void setUpdateMainScoreViewDelegate(
            UpdateMainScoreViewDelegate updateMainScoreViewDelegate) {
        this.updateMainScoreViewDelegate = updateMainScoreViewDelegate;
    }

    public float calculateIntellectualEducationScore() throws IOException {
        List<Score> scoreList = scoreTable.getData();
        float totalScore      = 0;
        float totalStudyScore = 0;
        float totalMinus      = 0;

        for (Score score: scoreList) {
            if (
                    score.isDelayExam() ||
                    GlobalLib.CompareUtfWithGbk("任意选修课", score.getCourseType()) ||
                    GlobalLib.CompareUtfWithGbk("体育", score.getCourseName())) {
                continue;
            }

            if (score.getScore() < 60) {
                totalMinus += score.getStudyScore();
            }

            totalScore += score.getScore() * score.getStudyScore();
            totalStudyScore += score.getStudyScore();
        }

        if (totalStudyScore == 0) {
            return 0;
        }

        return totalScore / totalStudyScore - totalMinus;
    }
}
