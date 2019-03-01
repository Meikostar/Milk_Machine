package com.canplay.milk.mvp.activity;

/*
 *	 ______    ______    ______
 *	/\  __ \  /\  ___\  /\  ___\
 *	\ \  __<  \ \  __\_ \ \  __\_
 *	 \ \_____\ \ \_____\ \ \_____\
 *	  \/_____/  \/_____/  \/_____/
 *
 *
 *	Copyright (c) 2013-2014, {Bee} open source community
 *	http://www.bee-framework.com
 *
 *
 *	Permission is hereby granted, free of charge, to any person obtaining a
 *	copy of this software and associated documentation files (the "Software"),
 *	to deal in the Software without restriction, including without limitation
 *	the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *	and/or sell copies of the Software, and to permit persons to whom the
 *	Software is furnished to do so, subject to the following conditions:
 *
 *	The above copyright notice and this permission notice shall be included in
 *	all copies or substantial portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *	FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *	IN THE SOFTWARE.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.view.NavigationBar;


public class WebViewWitesActivitys extends BaseActivity implements NavigationBar.NavigationBarListener {

    public static final String WEBURL = "weburl";
    public static final String WEBTITLE = "webtitle";
    public static final String TYPE = "type";

    private TextView title;
    private ImageView back;
    private NavigationBar navigationBar;
    private ImageView web_back;
    private ImageView goForward;
    private ImageView reload;
    private RelativeLayout mRelativeLayoutbg;
    private Button argee;
    private int type=-1;
    WebView webView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.webviews);

        Intent intent = getIntent();
        String url = intent.getStringExtra(WEBURL);
        String webTitle = intent.getStringExtra(WEBTITLE);
        type = intent.getIntExtra(TYPE,-1);

        title = (TextView) findViewById(R.id.navigationbar_title);
        title.setText(webTitle);
        back = (ImageView) findViewById(R.id.top_view_back);
        navigationBar=(NavigationBar)findViewById(R.id.navigationBar);
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRelativeLayoutbg=(RelativeLayout)findViewById(R.id.rl_rigter);
        argee=(Button)findViewById(R.id.btn_submit);
        if(type!=-1){
            mRelativeLayoutbg.setVisibility(View.VISIBLE);
        }
        final Activity activity = this;
        navigationBar.setNavigationBarListener(this);
        webView = (WebView) findViewById(R.id.webview_webView);
        webView.getSettings().setJavaScriptEnabled(true);
        argee.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        navigationBar.setNavigationBarListener(new NavigationBar.NavigationBarListener() {
            @Override
            public void navigationLeft() {
                finish();
            }

            @Override
            public void navigationRight() {

            }

            @Override
            public void navigationimg() {

            }
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(false);


        if (null != url) {
            webView.loadUrl(url);
        }

        web_back = (ImageView) findViewById(R.id.web_back);
        web_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {

                }
            }
        });


        goForward = (ImageView) findViewById(R.id.goForward);
        goForward.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                webView.goForward();

            }
        });

        reload = (ImageView) findViewById(R.id.reload);
        reload.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                webView.reload();
            }
        });


    }

    @Override
    public void initViews() {

    }

    @Override
    public void bindEvents() {

    }

    @Override
    public void initData() {

    }

    @Override
    protected void onDestroy() {
        webView.destroy();
        super.onDestroy();
    }


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
