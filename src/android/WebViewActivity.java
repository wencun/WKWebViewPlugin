package com.webview.yhck;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;

import cn.yonghui.huikaidian.R;

public class WebViewActivity extends Activity {

  //返回按纽
  private ImageView mBackView;
  //消息列表
  private ImageView mNewsView;
  //标题
  private TextView mTitleView;
  //显示标题
  private String mTitle;
  //成功页面的host
  private String urlHost;
  //显示页面URL
  private String mURL;
  WebView mWebview;
  WebSettings mWebSettings;
  private Dialog mDialog = null;
  private TextView mLoadTextView;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_webview);
    mBackView = (ImageView) findViewById(R.id.back_view);
    mTitleView = (TextView) findViewById(R.id.title_view);
    mNewsView = (ImageView) findViewById(R.id.news_view);

    initView();//初始化页面
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      _startDialog();
    }
    mWebview = (WebView) findViewById(R.id.webView);
    initWebView();//初始化WebView

    //返回按纽，关闭WebVIew
    mBackView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        setResult(1001);
        WebViewActivity.this.finish();
      }
    });

    //打开消息列表页面（暂时未开放）
    mNewsView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        setResult(1001);
        WebViewActivity.this.finish();
      }
    });
  }

  private void initView() {
    String paramJSON = getIntent().getStringExtra("Param");
    try {
      JSONObject jsonObject = new JSONObject(paramJSON);
       mTitle= jsonObject.getString("title");
      urlHost= jsonObject.getString("host");
      mURL = jsonObject.getString("URL");
    } catch (JSONException e) {
      e.printStackTrace();
    }

    mTitleView.setText(mTitle);
  }

  private void initWebView() {
    mWebSettings = mWebview.getSettings();
    //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
    mWebSettings.setJavaScriptEnabled(true);
    //不使用缓存:
    mWebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
    // 设置允许JS弹窗
    mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    mWebview.loadUrl(mURL);
    // android 5.0以上默认不支持Mixed Content
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      mWebview.getSettings().setMixedContentMode(
        WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
    }

    //复写shouldOverrideUrlLoading()方法，使得打开网页时不调用系统浏览器， 而是在本WebView中显示
    mWebview.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);

        if (url.indexOf(urlHost) != -1) {
          new Thread() {
            public void run() {
              try {
                Thread.sleep(3000);
                setResult(1001);
                WebViewActivity.this.finish();
              } catch (InterruptedException e) {
              }
            }
          }.start();     //这种内部匿名类的写法，快速生成一个线程对象，也有利于快速垃圾回收
        }

        return true;
      }

      @Override
      public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();// 接受所有网站的证书
      }

      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          _startDialog();
        }
      }

      @Override
      public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (mDialog != null && mDialog.isShowing()) {
          _stopDialog();
        }
      }
    });
  }



  //点击返回退出浏览器
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      setResult(1001);
      this.finish();
      return false;
    }
    return super.onKeyDown(keyCode, event);
  }

  //销毁Webview
  @Override
  protected void onDestroy() {
    if (mWebview != null) {
      mWebview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
      mWebview.clearHistory();
      ((ViewGroup) mWebview.getParent()).removeView(mWebview);
      mWebview.destroy();
      mWebview = null;
      mDialog = null;
    }
    super.onDestroy();
  }

  private void _startDialog() {
    if (mDialog == null) {
      mDialog = new Dialog(this, R.style.CustomProgressDialog);
      mDialog.setContentView(R.layout.custom_dialog_progress);
      mDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
      mDialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
      mDialog.setCancelable(false);// 设置按返回键是否关闭dialog
      ImageView imageView = (ImageView) mDialog.findViewById(R.id.loadingImageView);
      mLoadTextView = (TextView) mDialog.findViewById(R.id.id_tv_loadingmsg);
      mLoadTextView.setText("");
      AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
      animationDrawable.start();
      mDialog.show();
    }else {
      mDialog.show();
    }
  }

  private void _stopDialog() {
    if (mDialog != null) {
      mDialog.dismiss();
    }
  }
}
