package com.qb.xrealsys.ifafu;

import com.qb.xrealsys.ifafu.delegate.UpdateMainSyllabusViewDelegate;
import com.qb.xrealsys.ifafu.delegate.UpdateMainUserViewDelegate;
import com.qb.xrealsys.ifafu.model.Model;
import com.qb.xrealsys.ifafu.model.Syllabus;
import com.qb.xrealsys.ifafu.model.User;
import com.qb.xrealsys.ifafu.tool.ConfigHelper;
import com.qb.xrealsys.ifafu.web.SyllabusInterface;

import java.io.IOException;
import java.util.Map;

/**
 * Created by sky on 12/02/2018.
 */

public class SyllabusController {

    private Syllabus        syllabus;

    private User            user;

    private UserController  userController;

    private ConfigHelper    configHelper;

    private UpdateMainUserViewDelegate updateMainUserViewDelegate;

    private UpdateMainSyllabusViewDelegate updateMainSyllabusViewDelegate;

    public SyllabusController(UserController userController, ConfigHelper configHelper) {
        this.userController = userController;
        this.user           = userController.getData();
        this.syllabus       = new Syllabus();
        this.configHelper   = configHelper;
    }

    public void SyncData() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SyllabusInterface syllabusInterface = new SyllabusInterface(
                            configHelper.GetSystemValue("host"),
                            userController);
                    Map<String, Model> answer = syllabusInterface.GetSyllabus(user.getAccount(), user.getName());
                    if (answer == null) {
                        updateMainUserViewDelegate.updateError("获取失败");
                        return;
                    }

                    User answerUser = (User) answer.get("user");
                    user.setClas(answerUser.getClas());
                    user.setEnrollment(answerUser.getEnrollment());
                    user.setInstitute(answerUser.getInstitute());
                    updateMainUserViewDelegate.updateMainUser(user);

                    syllabus = (Syllabus) answer.get("syllabus");
                    updateMainSyllabusViewDelegate.updateMainSyllabus(syllabus);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public Syllabus GetData() {
        return this.syllabus;
    }

    public void setUpdateMainUserViewDelegate(UpdateMainUserViewDelegate updateMainUserViewDelegate) {
        this.updateMainUserViewDelegate = updateMainUserViewDelegate;
    }

    public void setUpdateMainSyllabusViewDelegate(UpdateMainSyllabusViewDelegate updateMainSyllabusViewDelegate) {
        this.updateMainSyllabusViewDelegate = updateMainSyllabusViewDelegate;
    }
}
