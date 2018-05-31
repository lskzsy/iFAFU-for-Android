package com.qb.xrealsys.ifafu.CommentTeacher.controller

import android.util.Log
import com.qb.xrealsys.ifafu.Base.controller.AsyncController
import com.qb.xrealsys.ifafu.Base.model.Response
import com.qb.xrealsys.ifafu.CommentTeacher.delegate.EnvaTeacherAnswerDelegate
import com.qb.xrealsys.ifafu.CommentTeacher.web.EnvaTeacherInterface
import com.qb.xrealsys.ifafu.Tool.ConfigHelper
import com.qb.xrealsys.ifafu.User.controller.UserAsyncController
import com.qb.xrealsys.ifafu.User.model.User
import java.io.IOException

class EnvaTeacherController(
        userController: UserAsyncController,
        configHelper: ConfigHelper): AsyncController(userController.threadPool) {

    private var envaTeacherInterface: EnvaTeacherInterface? = null

    private var user: User? = null

    var envaTeacherAnswerDelegate: EnvaTeacherAnswerDelegate? = null

    init {
        envaTeacherInterface = EnvaTeacherInterface(
                configHelper.GetSystemValue("host"),
                userController)
        user = userController.data
    }

    fun accessEnvaTeacher() {
        threadPool.execute {
            try {
                val response: Response = envaTeacherInterface!!.accessEnvaTeacher(
                        user!!.account, user!!.name)
                Log.d("debug", response.message)
                envaTeacherAnswerDelegate!!.answerEnvaTeacher(response)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}