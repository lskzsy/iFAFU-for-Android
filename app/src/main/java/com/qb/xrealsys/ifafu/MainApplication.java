/*
    BSD 3-Clause License

    Copyright (c) 2018, RealSys
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this
      list of conditions and the following disclaimer.

    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.

    * Neither the name of the copyright holder nor the names of its
      contributors may be used to endorse or promote products derived from
      this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
    DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
    FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
    DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
    SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
    CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
    OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
    OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

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

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by sky on 10/02/2018.
 */

public class MainApplication extends Application {

    private OSSHelper                   ossHelper;

    private ConfigHelper                configHelper;

    private UserAsyncController         userController;

    private ScoreAsyncController        scoreController;

    private ExamAsyncController         examController;

    private SyllabusAsyncController     syllabusController;

    private UpdateController            updateController;

    private ExecutorService             cachedThreadPool;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("ifafu.realm").deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfig);

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
