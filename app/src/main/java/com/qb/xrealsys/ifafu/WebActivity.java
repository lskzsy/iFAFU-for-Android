package com.qb.xrealsys.ifafu;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qb.xrealsys.ifafu.delegate.TitleBarButtonOnClickedDelegate;

import java.io.IOException;

public class WebActivity extends BaseActivity implements TitleBarButtonOnClickedDelegate {

    private WebView                 webView;

    private String                  loadUrl;

    private TitleBarController      titleBarController;

    private LodingViewController    lodingViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        lodingViewController = new LodingViewController(this);
        lodingViewController.show();

        webView = (WebView) findViewById(R.id.webView);
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

                lodingViewController.cancel();
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
        webSettings.setLoadsImagesAutomatically(true);
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
