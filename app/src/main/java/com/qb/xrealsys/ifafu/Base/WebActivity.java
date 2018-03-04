package com.qb.xrealsys.ifafu.Base;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.qb.xrealsys.ifafu.Base.BaseActivity;
import com.qb.xrealsys.ifafu.Base.controller.LoadingViewController;
import com.qb.xrealsys.ifafu.Base.controller.TitleBarController;
import com.qb.xrealsys.ifafu.Base.delegate.TitleBarButtonOnClickedDelegate;
import com.qb.xrealsys.ifafu.R;

public class WebActivity extends BaseActivity implements TitleBarButtonOnClickedDelegate {

    private WebView                 webView;

    private String                  loadUrl;

    private TitleBarController titleBarController;

    private LoadingViewController loadingViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        loadingViewController = new LoadingViewController(this);
        loadingViewController.show();

        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                loadingViewController.cancel();
            }
        });

        setWebViewSetting();
        getStartUpParams();
    }


    @Override
    protected void onStart() {
        super.onStart();

        webView.loadUrl(loadUrl);
    }

    private void setWebViewSetting() {
        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(
                getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath());
    }

    private void getStartUpParams() {
        Bundle bundle = getIntent().getExtras();
        loadUrl = bundle.getString("loadUrl");

        titleBarController = new TitleBarController(this);
        titleBarController
                .setBigPageTitle(bundle.getString("pageTitle"))
                .setHeadBack()
                .setOnClickedListener(this);
    }

    @Override
    public void titleBarOnClicked(int id) {
        switch (id) {
            case R.id.headback:
                webView.destroy();
                finish();
                break;
        }
    }
}
