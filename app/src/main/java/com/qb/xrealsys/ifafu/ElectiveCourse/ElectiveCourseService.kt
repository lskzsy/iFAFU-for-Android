package com.qb.xrealsys.ifafu.ElectiveCourse

import android.app.IntentService
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import com.qb.xrealsys.ifafu.Base.model.Response
import com.qb.xrealsys.ifafu.ElectiveCourse.controller.ElectiveCourseTaskController
import com.qb.xrealsys.ifafu.MainApplication
import com.qb.xrealsys.ifafu.Tool.ConfigHelper
import com.qb.xrealsys.ifafu.User.controller.UserAsyncController
import java.util.concurrent.ExecutorService

private const val ACTION_TASK_RUN = "com.qb.xrealsys.ifafu.ElectiveCourse.action.RUN"
private const val ACTION_TASK_STOP = "com.qb.xrealsys.ifafu.ElectiveCourse.action.STOP"

class ElectiveCourseService : IntentService("ElectiveCourseService") {

    private var electiveCourseTaskController: ElectiveCourseTaskController? = null

    private var userController: UserAsyncController? = null

    private var threadPool: ExecutorService? = null

    private var configHelper: ConfigHelper? = null

    override fun onCreate() {
        super.onCreate()
        val mainApplication: MainApplication = application as MainApplication
        this.electiveCourseTaskController = mainApplication.electiveCourseTaskController
        this.threadPool = mainApplication.cachedThreadPool
        this.userController = mainApplication.userController
        this.configHelper = mainApplication.configHelper
    }

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_TASK_RUN -> {
                handleActionRun()
            }
            ACTION_TASK_STOP -> {
                handleActionStop()
            }
        }
    }

    private fun handleActionRun() {
        if (!running) {
            running = true
            threadPool!!.execute {
                if (!userController!!.isLogin) {
                    val defaultAccount = configHelper!!.GetValue("account")
                    val defaultPassword = configHelper!!.GetValue("password")
                    userController!!.Login(defaultAccount, defaultPassword, true)
                }

                while (running) {
                    val response: Response = electiveCourseTaskController!!.electiveCourse()
                    if (response.isSuccess) {
                        running = false
                    }
                    Thread.sleep(200)
                }
            }
        }
    }

    private fun handleActionStop() {
        running = false
    }

    companion object {
        private var running: Boolean = false

        fun start(context: Context) {
            val intent = Intent(context, ElectiveCourseService::class.java)
            intent.action = ACTION_TASK_RUN
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, ElectiveCourseService::class.java)
            intent.action = ACTION_TASK_STOP
            context.startService(intent)
        }
    }
}
