package com.qb.xrealsys.ifafu;

import android.app.Application;

import com.qb.xrealsys.ifafu.Exam.controller.ExamController;
import com.qb.xrealsys.ifafu.Score.controller.ScoreController;
import com.qb.xrealsys.ifafu.Syllabus.controller.SyllabusController;
import com.qb.xrealsys.ifafu.User.controller.UserController;
import com.qb.xrealsys.ifafu.Tool.ConfigHelper;
import com.qb.xrealsys.ifafu.Tool.OSSHelper;

import java.io.IOException;

/**
 * Created by sky on 10/02/2018.
 */

public class MainApplication extends Application {

    private OSSHelper           ossHelper;

    private ConfigHelper        configHelper;

    private UserController userController;

    private ScoreController scoreController;

    private ExamController examController;

    private SyllabusController syllabusController;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            userController      = new UserController(getBaseContext());
            configHelper        = new ConfigHelper(getBaseContext());
            ossHelper           = new OSSHelper(
                    configHelper.GetSystemValue("ossHost"),
                    configHelper.GetSystemValue("ossKey"));
            scoreController     = new ScoreController(userController, configHelper);
            examController      = new ExamController(userController, configHelper);
            syllabusController  = new SyllabusController(userController, configHelper);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public OSSHelper getOssHelper() {
        return ossHelper;
    }

    public UserController getUserController() {
        return userController;
    }

    public ConfigHelper getConfigHelper() {
        return configHelper;
    }

    public ScoreController getScoreController() {
        return scoreController;
    }

    public ExamController getExamController() {
        return examController;
    }

    public SyllabusController getSyllabusController() {
        return syllabusController;
    }
}
