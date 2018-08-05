package com.qb.xrealsys.ifafu.CommentTeacher

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.qb.xrealsys.ifafu.Base.controller.LoadingViewController
import com.qb.xrealsys.ifafu.Base.controller.TitleBarController
import com.qb.xrealsys.ifafu.Base.delegate.TitleBarButtonOnClickedDelegate
import com.qb.xrealsys.ifafu.Base.model.Response
import com.qb.xrealsys.ifafu.CommentTeacher.controller.EnvaTeacherController
import com.qb.xrealsys.ifafu.CommentTeacher.delegate.EnvaTeacherAnswerDelegate
import com.qb.xrealsys.ifafu.MainApplication
import com.qb.xrealsys.ifafu.R
import com.qb.xrealsys.ifafu.User.controller.UserAsyncController
import java.util.*

class CommentTeacherActivity :
        AppCompatActivity(),
        TitleBarButtonOnClickedDelegate,
        EnvaTeacherAnswerDelegate {

    private var mainApplication: MainApplication? = null

    private var userController: UserAsyncController? = null

    private var titleBarController: TitleBarController? = null

    private var loadingViewController: LoadingViewController? = null

    private var envaTeacherController: EnvaTeacherController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_teacher)

        this.mainApplication     = this.application as MainApplication
        this.userController      = this.mainApplication!!.userController
        this.titleBarController  = TitleBarController(this)
        this.titleBarController!!
                .setHeadBack()
                .setTwoLineTitle("一键评教中...", String.format(
                        Locale.getDefault(), "%s(%s)",
                        this.userController!!.data.name,
                        this.userController!!.data.account))
                .setOnClickedListener(this)

        this.loadingViewController = LoadingViewController(this)
        this.loadingViewController!!.show()

        this.envaTeacherController = this.mainApplication!!.envaTeacherController
        this.envaTeacherController!!.envaTeacherAnswerDelegate = this
        envaTeacherController!!.accessEnvaTeacher()
    }

    override fun titleBarOnClicked(id: Int) {
        when (id) {
            R.id.headback -> finish()
        }
    }

    override fun titleBarOnLongClicked(id: Int) {

    }

    override fun answerEnvaTeacher(response: Response) {
        runOnUiThread {
            Toast.makeText(
                    this@CommentTeacherActivity,
                    response.message, Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
