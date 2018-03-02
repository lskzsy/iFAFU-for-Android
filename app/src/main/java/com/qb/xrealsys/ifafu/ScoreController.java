package com.qb.xrealsys.ifafu;

import com.qb.xrealsys.ifafu.delegate.UpdateElectiveTargetScoreDelegate;
import com.qb.xrealsys.ifafu.delegate.UpdateMainScoreViewDelegate;
import com.qb.xrealsys.ifafu.delegate.UpdateMakeupExamInfoDelegate;
import com.qb.xrealsys.ifafu.model.MakeupExam;
import com.qb.xrealsys.ifafu.model.Score;
import com.qb.xrealsys.ifafu.model.ScoreTable;
import com.qb.xrealsys.ifafu.tool.ConfigHelper;
import com.qb.xrealsys.ifafu.tool.GlobalLib;
import com.qb.xrealsys.ifafu.web.MakeupInterface;
import com.qb.xrealsys.ifafu.web.ScoreInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sky on 14/02/2018.
 */

public class ScoreController {

    private ScoreTable                          scoreTable;

    private UserController                      userController;

    private ConfigHelper                        configHelper;

    private ScoreInterface                      scoreInterface;

    private MakeupInterface                     makeupInterface;

    private Map<String, Float>                  electiveTargetScore;

    private UpdateMainScoreViewDelegate         updateMainScoreViewDelegate;

    private UpdateElectiveTargetScoreDelegate   updateElectiveTargetScoreDelegate;

    private UpdateMakeupExamInfoDelegate        updateMakeupExamInfoDelegate;

    public ScoreController(UserController userController, ConfigHelper configHelper) {
        this.userController = userController;
        this.configHelper   = configHelper;
        this.scoreTable     = new ScoreTable();
        this.scoreInterface = new ScoreInterface(
                this.configHelper.GetSystemValue("host"),
                userController);
        this.makeupInterface = new MakeupInterface(
                this.configHelper.GetSystemValue("host"),
                userController);
        this.electiveTargetScore = null;
    }

    public void SyncElectiveScore() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (electiveTargetScore == null) {
                        electiveTargetScore = scoreInterface.GetElectiveTargetScore(
                                userController.getData().getAccount(),
                                userController.getData().getName());
                    }
                    scoreTable.setData(scoreInterface.updateScoreTable(
                            userController.getData().getAccount(),
                            userController.getData().getName(),
                            scoreTable.getSearchYearOptions().get(0),
                            scoreTable.getSearchTermOptions().get(0)
                    ));

                    updateElectiveTargetScoreDelegate.UpdatedElectiveTargetScore();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
                    scoreTable.setData(scoreInterface.updateScoreTable(
                            userController.getData().getAccount(),
                            userController.getData().getName(),
                            y, t));
                    updateMainScoreViewDelegate.updateMainScore(scoreTable);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void GetScoreMakeupExam(Score score) {
        final Score queryScore = score;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MakeupExam makeupExam = makeupInterface.GetMakeupExam(
                            userController.getData().getAccount(),
                            userController.getData().getName(),
                            queryScore);
                    updateMakeupExamInfoDelegate.informMakeupExamUpdated(makeupExam);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void SyncData() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    scoreTable = scoreInterface.GetScoreTable(
                            userController.getData().getAccount(),
                            userController.getData().getName());
                    updateMainScoreViewDelegate.updateMainScore(scoreTable);
                    makeupInterface.InitMakeupExam(
                            userController.getData().getAccount(),
                            userController.getData().getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public ScoreTable GetData() {
        return this.scoreTable;
    }

    public List<Score> GetElectiveData() {
        List<Score> scoreList       = scoreTable.getData();
        List<Score> electiveData    = new ArrayList<>();

        try {
            for (Score score: scoreList) {
                if (GlobalLib.CompareUtfWithGbk("任意选修课", score.getCourseType())) {
                    electiveData.add(score);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return electiveData;
    }

    public void SetScoreTableData(List<Score> scoreList) {
        scoreTable.setData(scoreList);
    }

    public void setUpdateMainScoreViewDelegate(
            UpdateMainScoreViewDelegate updateMainScoreViewDelegate) {
        this.updateMainScoreViewDelegate = updateMainScoreViewDelegate;
    }

    public void setUpdateElectiveTargetScoreDelegate(
            UpdateElectiveTargetScoreDelegate updateElectiveTargetScoreDelegate) {
        this.updateElectiveTargetScoreDelegate = updateElectiveTargetScoreDelegate;
    }

    public void setUpdateMakeupExamInfoDelegate(UpdateMakeupExamInfoDelegate updateMakeupExamInfoDelegate) {
        this.updateMakeupExamInfoDelegate = updateMakeupExamInfoDelegate;
    }

    public Map<String, Float> getElectiveTargetScore() {
        return electiveTargetScore;
    }

    public float calculateIntellectualEducationScore() throws IOException {
        List<Score> scoreList = scoreTable.getData();
        float totalScore      = 0;
        float totalStudyScore = 0;
        float totalMinus      = 0;

        for (Score score: scoreList) {
            if (
                    score.isRestudy() ||
                    score.isDelayExam() ||
                    GlobalLib.CompareUtfWithGbk("任意选修课", score.getCourseType()) ||
                    GlobalLib.CompareUtfWithGbk("体育", score.getCourseName())) {
                continue;
            }

            if (score.getScore() < 60) {
                totalMinus += score.getStudyScore();
            }

            if (score.getScore() < 60 && score.getMakeupScore() > 0) {
                totalScore +=
                        (score.getMakeupScore() > 60 ? 60 : score.getMakeupScore() )
                                * score.getStudyScore();
            } else {
                totalScore += score.getScore() * score.getStudyScore();
            }

            totalStudyScore += score.getStudyScore();
        }

        if (totalStudyScore == 0) {
            return 0;
        }

        return totalScore / totalStudyScore - totalMinus;
    }
}
