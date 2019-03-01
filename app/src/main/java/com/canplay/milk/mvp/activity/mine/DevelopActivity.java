package com.canplay.milk.mvp.activity.mine;


import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.fragment.DataFragment;
import com.canplay.milk.view.NavigationBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 发育评估
 */
public class DevelopActivity extends BaseActivity {


    @BindView(R.id.line)
    View line;
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.webview_webView)
    WebView webView;

    @Override
    public void initViews() {
        setContentView(R.layout.activity_develop);
        ButterKnife.bind(this);
        navigationBar.setNavigationBarListener(this);

    }


    @Override
    public void bindEvents() {


    }


    @Override
    public void initData() {

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() { //通过webView打开链接，不调用系统浏览器

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // Toast.makeText(getApplicationContext(), "网络连接失败 ,请连接网络。", Toast.LENGTH_SHORT).show();
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(false);


        if (null != url) {
            webView.loadUrl(url);
        }

    }
   private String url="http://39.108.15.39:41072/assessment.html";


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                finish();
            }
        }
        return true;
    }


}
