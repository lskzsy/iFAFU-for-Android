package com.qb.xrealsys.ifafu;

import com.qb.xrealsys.ifafu.delegate.UpdateExamTableDelegate;
import com.qb.xrealsys.ifafu.model.ExamTable;
import com.qb.xrealsys.ifafu.tool.ConfigHelper;
import com.qb.xrealsys.ifafu.web.ExamInterface;

import java.io.IOException;

/**
 * Created by sky on 24/02/2018.
 */

public class ExamController {

    private UserController      userController;

    private ConfigHelper        configHelper;

    private ExamTable           examTable;

    private ExamInterface       examInterface;

    private UpdateExamTableDelegate updateExamTableDelegate;

    public ExamController(UserController userController, ConfigHelper configHelper) {
        this.userController = userController;
        this.configHelper   = configHelper;
        this.examTable      = new ExamTable();
        this.examInterface  = new ExamInterface(
                this.configHelper.GetSystemValue("host"),
                userController);
    }

    public void SyncData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    examTable = examInterface.GetExamTable(
                            userController.getData().getAccount(),
                            userController.getData().getName());
                    updateExamTableDelegate.UpdatedExamTable();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public ExamTable GetData() {
        return examTable;
    }

    public void setUpdateExamTableDelegate(UpdateExamTableDelegate updateExamTableDelegate) {
        this.updateExamTableDelegate = updateExamTableDelegate;
    }
}
