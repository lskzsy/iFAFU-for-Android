/*
 * Copyright (c) 2018. RealSys
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.qb.xrealsys.ifafu;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.qb.xrealsys.ifafu.Card.controller.CardController;
import com.qb.xrealsys.ifafu.CommentTeacher.controller.EnvaTeacherController;
import com.qb.xrealsys.ifafu.ElectiveCourse.ElectiveCourseService;
import com.qb.xrealsys.ifafu.ElectiveCourse.controller.ElectiveCourseController;
import com.qb.xrealsys.ifafu.ElectiveCourse.controller.ElectiveCourseTaskController;
import com.qb.xrealsys.ifafu.Exam.controller.ExamAsyncController;
import com.qb.xrealsys.ifafu.Main.controller.UpdateController;
import com.qb.xrealsys.ifafu.Score.controller.ScoreAsyncController;
import com.qb.xrealsys.ifafu.Syllabus.controller.SyllabusAsyncController;
import com.qb.xrealsys.ifafu.Tool.ZFVerify;
import com.qb.xrealsys.ifafu.User.controller.UserAsyncController;
import com.qb.xrealsys.ifafu.Tool.ConfigHelper;
import com.qb.xrealsys.ifafu.Tool.OSSHelper;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by sky on 10/02/2018.
 */

public class MainApplication extends Application {

    private OSSHelper                       ossHelper;

    private ConfigHelper                    configHelper;

    private UserAsyncController             userController;

    private ScoreAsyncController            scoreController;

    private ExamAsyncController             examController;

    private SyllabusAsyncController         syllabusController;

    private UpdateController                updateController;

    private ExecutorService                 cachedThreadPool;

    private EnvaTeacherController           envaTeacherController;

    private CardController                  cardController;

    private ElectiveCourseController        electiveCourseController;

    private ElectiveCourseTaskController    electiveCourseTaskController;

    private NotificationManager             notificationManager;

    private ZFVerify                        zfVerify;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("ifafu.realm").deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfig);

        try {
            cachedThreadPool                = Executors.newCachedThreadPool();
            zfVerify                        = new ZFVerify(getBaseContext());
            userController                  = new UserAsyncController(getBaseContext(), cachedThreadPool, zfVerify);
            configHelper                    = new ConfigHelper(getBaseContext());
            ossHelper                       = new OSSHelper(
                    configHelper.GetSystemValue("ossHost"),
                    configHelper.GetSystemValue("ossKey"));
            cardController                  = new CardController(userController, configHelper);
            scoreController                 = new ScoreAsyncController(userController, configHelper);
            examController                  = new ExamAsyncController(userController, configHelper);
            syllabusController              = new SyllabusAsyncController(userController, configHelper);
            updateController                = new UpdateController(getBaseContext(), ossHelper, configHelper);
            envaTeacherController           = new EnvaTeacherController(userController, configHelper);
            notificationManager             = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            electiveCourseController        = new ElectiveCourseController(userController, configHelper);
            electiveCourseTaskController    = new ElectiveCourseTaskController(electiveCourseController, notificationManager, getBaseContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ElectiveCourseTaskController getElectiveCourseTaskController() {
        return electiveCourseTaskController;
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

    public EnvaTeacherController getEnvaTeacherController() {
        return envaTeacherController;
    }

    public CardController getCardController() {
        return cardController;
    }

    public ZFVerify getZfVerify() {
        return zfVerify;
    }

    public ElectiveCourseController getElectiveCourseController() {
        return electiveCourseController;
    }
}
