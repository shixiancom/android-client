package com.shixian.android.client.activities;

import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.shixian.android.client.R;
import com.shixian.android.client.activities.base.SwipeActivity;
import com.shixian.android.client.activities.base.UmengActivity;
import com.shixian.android.client.views.CosmterUrlSpan;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;


/**
 * Created by tangtang on 15/4/1.
 * 点击url后跳转的页面
 */
public class WebActivity extends SwipeActivity  {


    String url = "http://www.shixian.com";


    WebView webView;


    ProgressBar progressBar;



    private Toolbar toolbar;

    String loading = "正在加载...";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web);
        webView= (WebView) findViewById(R.id.webView);
        progressBar= (ProgressBar) findViewById(R.id.progressBar);
        toolbar= (Toolbar) findViewById(R.id.toolbar);

        url=getIntent().getStringExtra("url");

        if(!url.contains("://"))
        {
            url="http://"+url;
        }
        init();


    }


    void init() {


        setSupportActionBar(toolbar);




        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Handle Back Navigation :D
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebActivity.this.onBackPressed();
            }
        });

        toolbar.setNavigationIcon(R.drawable.task_add_cab_back);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {

                                       @Override
                                       public void onProgressChanged(WebView view, int newProgress) {
                                           progressBar.setProgress(newProgress);
                                           if (newProgress == 100) {
                                               // 没有title显示网址
                                               String currentTitle = toolbar.getTitle().toString();
                                               if (loading.equals(currentTitle)) {
                                                   toolbar.setTitle(url);
                                               }

                                               progressBar.setVisibility(View.INVISIBLE);
                                               AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
                                               animation.setDuration(500);
                                               animation.setAnimationListener(new Animation.AnimationListener() {
                                                   @Override
                                                   public void onAnimationStart(Animation animation) {
                                                   }

                                                   @Override
                                                   public void onAnimationEnd(Animation animation) {
                                                       progressBar.setVisibility(View.INVISIBLE);
                                                   }

                                                   @Override
                                                   public void onAnimationRepeat(Animation animation) {
                                                   }
                                               });
                                               progressBar.startAnimation(animation);
                                           } else {
                                               progressBar.setVisibility(View.VISIBLE);
                                           }
                                       }

                                       @Override
                                       public void onReceivedTitle(WebView view, String title) {
                                           toolbar.setTitle(title);
                                       }
                                   }
        );

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        webView.setWebViewClient(new CustomWebViewClient(this));
        webView.loadUrl(url);

        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        webView.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        webView.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
    }


    @Override
    public void onOptionsMenuClosed(Menu menu) {


        super.onOptionsMenuClosed(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_copy:
                String urlString = webView.getUrl();

                ClipboardManager cmb = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(url);


                Toast.makeText(this, urlString + " 已复制", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_browser:
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webView.getUrl()));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "用浏览器打开失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }

    public static class CustomWebViewClient extends WebViewClient {

        Context mContext;

        public CustomWebViewClient(Context context) {
            mContext = context;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return CosmterUrlSpan.openActivityByUri(mContext, url, false, false);
        }
    }
}
