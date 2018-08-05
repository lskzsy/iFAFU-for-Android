package com.qb.xrealsys.ifafu.Score.controller;

import com.qb.xrealsys.ifafu.Base.controller.AsyncController;
import com.qb.xrealsys.ifafu.Base.model.Model;
import com.qb.xrealsys.ifafu.Base.model.Response;
import com.qb.xrealsys.ifafu.Syllabus.delegate.UpdateMainUserViewDelegate;
import com.qb.xrealsys.ifafu.User.controller.UserAsyncController;
import com.qb.xrealsys.ifafu.Score.delegate.UpdateElectiveTargetScoreDelegate;
import com.qb.xrealsys.ifafu.Score.delegate.UpdateMainScoreViewDelegate;
import com.qb.xrealsys.ifafu.Score.delegate.UpdateMakeupExamInfoDelegate;
import com.qb.xrealsys.ifafu.Score.model.MakeupExam;
import com.qb.xrealsys.ifafu.Score.model.Score;
import com.qb.xrealsys.ifafu.Score.model.ScoreTable;
import com.qb.xrealsys.ifafu.Tool.ConfigHelper;
import com.qb.xrealsys.ifafu.Tool.GlobalLib;
import com.qb.xrealsys.ifafu.Score.web.MakeupInterface;
import com.qb.xrealsys.ifafu.Score.web.ScoreInterface;
import com.qb.xrealsys.ifafu.User.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sky on 14/02/2018.
 */

public class ScoreAsyncController extends AsyncController {

    private ScoreTable                          scoreTable;

    private UserAsyncController                 userController;

    private User                                user;

    private ConfigHelper                        configHelper;

    private ScoreInterface                      scoreInterface;

    private MakeupInterface                     makeupInterface;

    private Map<String, Float>                  electiveTargetScore;

    private UpdateMainScoreViewDelegate         updateMainScoreViewDelegate;

    private UpdateElectiveTargetScoreDelegate   updateElectiveTargetScoreDelegate;

    private UpdateMakeupExamInfoDelegate        updateMakeupExamInfoDelegate;

    private UpdateMainUserViewDelegate          updateMainUserViewDelegate;

    public ScoreAsyncController(UserAsyncController userController, ConfigHelper configHelper) {
        super(userController.getThreadPool());
        this.userController = userController;
        this.user           = userController.getData();
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
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (electiveTargetScore == null) {
                        electiveTargetScore = scoreInterface.GetElectiveTargetScore(
                                user.getAccount(),
                                user.getName());
                    }
                    scoreTable.setData(scoreInterface.updateScoreTable(
                            user.getAccount(),
                            user.getName(),
                            scoreTable.getSearchYearOptions().get(0),
                            scoreTable.getSearchTermOptions().get(0)
                    ));

                    updateElectiveTargetScoreDelegate.UpdatedElectiveTargetScore();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void SyncData(int year, int term) throws IOException {
        final String y = scoreTable.getSearchYearOptions().get(year);
        final String t = scoreTable.getSearchTermOptions().get(term);
        scoreTable.setSelectedYearOption(year);
        scoreTable.setSelectedTermOption(term);

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    scoreTable.setData(scoreInterface.updateScoreTable(
                            user.getAccount(),
                            user.getName(),
                            y, t));
                    updateMainScoreViewDelegate.updateMainScore(scoreTable);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void GetScoreMakeupExam(Score score) {
        final Score queryScore = score;

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    MakeupExam makeupExam = makeupInterface.GetMakeupExam(
                            user.getAccount(),
                            user.getName(),
                            queryScore);
                    updateMakeupExamInfoDelegate.informMakeupExamUpdated(makeupExam);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void SyncData() throws IOException {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, Model> answer = scoreInterface.GetScoreTable(
                            user.getAccount(),
                            user.getName());

                    if (answer == null) {
                        updateMainUserViewDelegate.updateError("获取失败");
                        return;
                    }

                    if (answer.containsKey("error")) {
                        Response response = (Response) answer.get("error");
                        updateMainUserViewDelegate.updateError(response.getMessage());
                        return;
                    }

                    User answerUser = (User) answer.get("user");
                    user.setClas(answerUser.getClas());
                    user.setEnrollment(answerUser.getEnrollment());
                    user.setInstitute(answerUser.getInstitute());
                    updateMainUserViewDelegate.updateMainUser(user);

                    scoreTable = (ScoreTable) answer.get("scoreTable");
                    scoreTable.updateDefaultData();
                    updateMainScoreViewDelegate.updateMainScore(scoreTable);
                    makeupInterface.InitMakeupExam(
                            userController.getData().getAccount(),
                            userController.getData().getName());
                } catch (IOException e) {
                    updateMainUserViewDelegate.updateError("获取失败");
                    e.printStackTrace();
                }
            }
        });
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

    public void setUpdateMainUserViewDelegate(UpdateMainUserViewDelegate updateMainUserViewDelegate) {
        this.updateMainUserViewDelegate = updateMainUserViewDelegate;
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
