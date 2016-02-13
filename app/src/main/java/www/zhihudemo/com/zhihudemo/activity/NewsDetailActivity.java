package www.zhihudemo.com.zhihudemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.widget.Toast;

import www.zhihudemo.com.zhihudemo.R;
import www.zhihudemo.com.zhihudemo.entity.News;
import www.zhihudemo.com.zhihudemo.task.LoadNewsDetailTask;
import www.zhihudemo.com.zhihudemo.utility.NetUtil;

public class NewsDetailActivity extends AppCompatActivity {
    private WebView mWebView;
    private boolean isFavourite = false;
    private News news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mWebView = (WebView) findViewById(R.id.webview);
        setWebView(mWebView);

        news = (News) getIntent().getSerializableExtra("news");
        new LoadNewsDetailTask(mWebView).execute(news.getId());
    }

    private void setWebView(WebView mWebView) {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
    }

    public static void startActivity(Context context, News news) {
        if (NetUtil.netWorkConnection(context)) {
            Intent i = new Intent(context, NewsDetailActivity.class);
            i.putExtra("news", news);
            context.startActivity(i);
        } else {
            Toast.makeText(context, "请打开网络连接", Toast.LENGTH_SHORT).show();
        }
    }
}
