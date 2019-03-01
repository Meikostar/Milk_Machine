package com.canplay.milk.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mykar on 17/4/10.
 */
public class DataFragment extends BaseFragment {


    @BindView(R.id.webview_webView)
    WebView webView;
    private String user_class;

    private int type = 1;

    public DataFragment(int type) {
        this.type = type;
    }

    public DataFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.data_fragment, null);
        ButterKnife.bind(this, view);


        initView();
        return view;
    }

    private void initView() {

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

    private  String url="https://www.baidu.com";
    @Override
    public void onDestroy() {
        super.onDestroy();
       if(webView!=null){
           webView.destroy();
       }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();



    }


}
