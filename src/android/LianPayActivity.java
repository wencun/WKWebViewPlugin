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
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.yonghui.huikaidian.R;

public class LianPayActivity extends Activity {

  //标题布局
  private RelativeLayout mRelativeLayout;
  //返回按纽
  private ImageView mBackView;
  //消息列表
  private ImageView mNewsView;
  //标题
  private TextView mTitleView;
  //显示标题
  private String mTitle;
  //结果页面的host
  private String urlHost;
  //显示页面URL
  private String mURL;
  //成功页面URL
  private String mSuccessURL;
  //返回页面的URL
  private String mBackURL;
  WebView mWebview;
  WebSettings mWebSettings;
  //是否显示标题
  private String isShowTitle = "1";
  private Dialog mDialog = null;
  private TextView mLoadTextView;
  private int mSessionExpirationTime = -1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // 启用窗口特征，启用带进度和不带进度的进度条
    requestWindowFeature(Window.FEATURE_PROGRESS);
    setContentView(R.layout.activity_webview);
    mBackView = (ImageView) findViewById(R.id.back_view);
    mTitleView = (TextView) findViewById(R.id.title_view);
    mNewsView = (ImageView) findViewById(R.id.news_view);
    mRelativeLayout = (RelativeLayout) findViewById(R.id.title_layout);


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
        LianPayActivity.this.finish();
      }
    });

    //打开消息列表页面（暂时未开放）
    mNewsView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        setResult(1001);
        LianPayActivity.this.finish();
      }
    });
  }

  private void initView() {
    String paramJSON = getIntent().getStringExtra("Param");
    try {
      JSONObject jsonObject = new JSONObject(paramJSON);
      mTitle = jsonObject.getString("title");
      mURL = jsonObject.getString("URL");
      mSuccessURL = jsonObject.getString("successUrl");
      mBackURL = jsonObject.getString("backUrl");
      isShowTitle = jsonObject.getString("isShowNav");
      mSessionExpirationTime = jsonObject.getInt("sessionExpirationTime");
    } catch (JSONException e) {
      e.printStackTrace();
    }

    if (isShowTitle.equals("0")) {
      mRelativeLayout.setVisibility(View.GONE);
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
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          _startDialog();
        }
        if (url.indexOf(mSuccessURL) != -1) {
          setResult(1003);
          LianPayActivity.this.finish();
        } else if (url.indexOf(mBackURL) != -1) {
          setResult(1004);
          LianPayActivity.this.finish();
        }
      }

      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
      }

      @Override
      public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();// 接受所有网站的证书
      }

      @Override
      public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (url.equals(mURL) && mSessionExpirationTime != 0) {
          new Thread() {
            public void run() {
              try {
                Thread.sleep(mSessionExpirationTime * 1000);
                setResult(1004);
                LianPayActivity.this.finish();
              } catch (InterruptedException e) {
              }
            }
          }.start();     //这种内部匿名类的写法，快速生成一个线程对象，也有利于快速垃圾回收
        }
        if (mDialog != null && mDialog.isShowing()) {
          _stopDialog();
        }
      }
    });

    //添加客户端支持
    mWebview.setWebChromeClient(new WebChromeClient() {
      @Override
      public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        AlertDialog.Builder mAlert = new AlertDialog.Builder(LianPayActivity.this);
        mAlert.setTitle("Alert");
        mAlert.setMessage(message);
        mAlert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            result.confirm();
          }
        });
        mAlert.setCancelable(false);
        mAlert.create().show();
        return true;
      }
    });
    mWebview.addJavascriptInterface(new LianPayActivity.JsInterface(this), "Android");
  }

  private class JsInterface {
    private Context mContext;

    public JsInterface(Context context) {
      this.mContext = context;
    }

    //在js中调用window.Android.showInfoFromJs(name)，便会触发此方法。
    @JavascriptInterface
    public void showInfoFromJs(String name) {
      Toast.makeText(mContext, name, Toast.LENGTH_SHORT).show();
    }
  }

  //在java中调用js代码
  public void sendInfoToJs() {
    final String jsonData = "在java中调用js代码";
    //必须另开线程进行JS方法调用(否则无法调用)
    mWebview.post(new Runnable() {
      @Override
      public void run() {
        // 注意调用的JS方法名要对应上
        // 调用javascript的callJS()方法
        // mWebview.loadUrl("javascript:callJS('"+jsonData+"')");
      }
    });
  }

  //点击返回退出浏览器
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      return true;
    }
    return false;
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
    } else {
      mDialog.show();
    }
  }

  private void _stopDialog() {
    if (mDialog != null) {
      mDialog.dismiss();
    }
  }
}
