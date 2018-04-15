package com.qb.xrealsys.ifafu;

import android.app.Application;

import com.qb.xrealsys.ifafu.Exam.controller.ExamAsyncController;
import com.qb.xrealsys.ifafu.Main.controller.UpdateController;
import com.qb.xrealsys.ifafu.Score.controller.ScoreAsyncController;
import com.qb.xrealsys.ifafu.Syllabus.controller.SyllabusAsyncController;
import com.qb.xrealsys.ifafu.User.controller.UserAsyncController;
import com.qb.xrealsys.ifafu.Tool.ConfigHelper;
import com.qb.xrealsys.ifafu.Tool.OSSHelper;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sky on 10/02/2018.
 */

public class MainApplication extends Application {

    private OSSHelper           ossHelper;

    private ConfigHelper        configHelper;

    private UserAsyncController userController;

    private ScoreAsyncController scoreController;

    private ExamAsyncController examController;

    private SyllabusAsyncController syllabusController;

    private UpdateController    updateController;

    private ExecutorService     cachedThreadPool;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            cachedThreadPool    = Executors.newCachedThreadPool();
            userController      = new UserAsyncController(getBaseContext(), cachedThreadPool);
            configHelper        = new ConfigHelper(getBaseContext());
            ossHelper           = new OSSHelper(
                    configHelper.GetSystemValue("ossHost"),
                    configHelper.GetSystemValue("ossKey"));
            scoreController     = new ScoreAsyncController(userController, configHelper);
            examController      = new ExamAsyncController(userController, configHelper);
            syllabusController  = new SyllabusAsyncController(userController, configHelper);
            updateController    = new UpdateController(getBaseContext(), ossHelper, configHelper);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UpdateController getUpdateController() {
        return updateController;
    }

    public OSSHelper getOssHelper() {
        return ossHelper;
    }

    public UserAsyncController getUserController() {
        return userController;
    }

    public ConfigHelper getConfigHelper() {
        return configHelper;
    }

    public ScoreAsyncController getScoreController() {
        return scoreController;
    }

    public ExamAsyncController getExamController() {
        return examController;
    }

    public SyllabusAsyncController getSyllabusController() {
        return syllabusController;
    }

    public ExecutorService getCachedThreadPool() {
        return cachedThreadPool;
    }
}
