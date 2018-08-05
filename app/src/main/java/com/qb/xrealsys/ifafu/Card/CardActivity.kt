package com.qb.xrealsys.ifafu.Card

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.*
import com.qb.xrealsys.ifafu.Base.controller.LoadingViewController
import com.qb.xrealsys.ifafu.Base.controller.TitleBarController
import com.qb.xrealsys.ifafu.Base.delegate.TitleBarButtonOnClickedDelegate
import com.qb.xrealsys.ifafu.Card.controller.CardController
import com.qb.xrealsys.ifafu.MainApplication
import com.qb.xrealsys.ifafu.R
import java.net.CookieStore
import java.net.HttpCookie

class CardActivity : AppCompatActivity(), TitleBarButtonOnClickedDelegate {

    private var cardController: CardController? = null

    private var loadingViewController: LoadingViewController? = null

    private var titleBarController: TitleBarController? = null

    private var webView: WebView? = null

    private var loadUrl: String = "http://app.fafu.edu.cn/publicapp/sys/myyktzd/mobile/oneCard/index.html#!/"

    private var uesUrl: String = "http://app.fafu.edu.cn"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        loadingViewController = LoadingViewController(this)
        loadingViewController!!.show()

        titleBarController = TitleBarController(this)
        titleBarController!!
                .setBigPageTitle("校园一卡通")
                .setHeadBack()
                .setOnClickedListener(this)

        cardController = (application as MainApplication).cardController

        webView = findViewById(R.id.webView)
        webView!!.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                view.loadUrl(request.url.toString())
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)

                loadingViewController!!.cancel()
            }
        }

        setWebViewSetting()
    }

    override fun onStart() {
        super.onStart()

        val cookieManager: CookieManager = CookieManager.getInstance()
        val cookieStore: CookieStore = cardController!!.cookieManager.cookieStore
        val cookies: MutableList<HttpCookie> = cookieStore.cookies
        for (cookie: HttpCookie in cookies) {
            val setCookie: String = String.format("%s; domain=%s; path=",
                    cookie.toString(), cookie.domain, cookie.path)
            cookieManager.setCookie(uesUrl, setCookie)
        }

        webView!!.loadUrl(loadUrl)
    }

    private fun setWebViewSetting() {
        val webSettings = webView!!.settings

        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings!!.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.loadsImagesAutomatically = true
        webSettings.domStorageEnabled = true
        webSettings.setAppCacheEnabled(true)
        webSettings.setAppCachePath(
                applicationContext.getDir("cache", Context.MODE_PRIVATE).path)
    }

    override fun titleBarOnClicked(id: Int) {
        when (id) {
            R.id.headback -> {
                webView!!.destroy()
                finish()
            }
        }
    }

    override fun titleBarOnLongClicked(id: Int) {
    }
}
