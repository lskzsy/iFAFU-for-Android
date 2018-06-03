package com.qb.xrealsys.ifafu.Card.controller

import android.graphics.Bitmap
import com.qb.xrealsys.ifafu.Base.model.Response
import com.qb.xrealsys.ifafu.Card.delegate.UpdateCardWebAccessStatusDelegate
import com.qb.xrealsys.ifafu.Card.delegate.UpdateMainCardViewDelegate
import com.qb.xrealsys.ifafu.Card.web.CardInterface
import com.qb.xrealsys.ifafu.Tool.ConfigHelper
import com.qb.xrealsys.ifafu.User.controller.UserAsyncController
import com.qb.xrealsys.ifafu.User.model.User
import java.net.CookieHandler
import java.net.CookieManager
import java.util.concurrent.ExecutorService

class CardController(userController: UserAsyncController, configHelper: ConfigHelper) {

    private var userController: UserAsyncController? = null

    private var configHelper: ConfigHelper? = null

    private var threadPool: ExecutorService? = null

    private var cardInterface: CardInterface? = null

    private var isAuth: Boolean = false

    var cookieManager: CookieManager = CookieManager()

    var user: User? = null

    var delegate: UpdateMainCardViewDelegate? = null

    var webUpdateDelegate: UpdateCardWebAccessStatusDelegate? = null

    init {
        this.userController = userController
        this.configHelper   = configHelper
        this.threadPool     = userController.threadPool
        this.cardInterface  = CardInterface()
        this.user           = userController.data

        CookieHandler.setDefault(cookieManager)
    }

    fun SyncData() {
        this.threadPool!!.execute {
            if (!this.isAuth) {
                delegate!!.updateMainCard(Response(false, -1, "还未登录"))
            }

            val response: Response = cardInterface!!.getCredit()
            if (!response.isSuccess) {
                this.isAuth = false
            }
            delegate!!.updateMainCard(response)
        }
    }

    fun downloadVerifyImage() {
        this.threadPool!!.execute {
            val bitmap: Bitmap? = cardInterface!!.getVerifyCode()
            if (bitmap != null) {
                webUpdateDelegate!!.UpdateVerifyImage(bitmap)
            }
        }
    }

    fun login(account: String, password: String, verifyCode: String) {
        this.threadPool!!.execute {
            val response: Response = cardInterface!!.login(account, password, verifyCode)
            if (response.isSuccess) {
                userController!!.saveAuthPassword(password)
            }
            webUpdateDelegate!!.LoginAccessFinish(response)
        }
    }

    fun initRouter() {
        this.threadPool!!.execute {
            webUpdateDelegate!!.InitRouter(cardInterface!!.initRouter())
        }
    }

    fun isAuthentication(): Boolean {
        return isAuth
    }

    fun authentication() {
        isAuth = true
    }
}