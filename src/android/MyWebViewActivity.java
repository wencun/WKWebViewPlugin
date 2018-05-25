package com.webview.yhck;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import cn.yonghui.huikaidian.R;

//对话框显示WebView
public class MyWebViewActivity extends Activity implements View.OnClickListener {


  //显示页面URL
  private String mURL;
  //JS方法和参数
  private String mFuncAddParams;
  MyWevView mWebview;
  WebSettings mWebSettings;
  private String statusFlag;
  private Button mCommitBtn;
  private Context mContext;
  private boolean isArrayBottom = false;
  private Dialog mDialog = null;
  private TextView mLoadTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_my_web_view);
    mContext = MyWebViewActivity.this;
    mCommitBtn = (Button) findViewById(R.id.submit_btn);
    String paramJSON = getIntent().getStringExtra("Param");
    try {
      JSONObject jsonObject = new JSONObject(paramJSON);
      mURL = jsonObject.get("URL").toString();
      mFuncAddParams = jsonObject.get("funcAddParam").toString();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    initView();//初始化页面
    Log.d("onPageStarted", "手机SDK：" + Build.VERSION.SDK_INT + "标准版本：" + Build.VERSION_CODES.M);
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      _startDialog();
    }
    initWebView();//初始化WebView
  }

  private void initView() {
    mWebview = (MyWevView) findViewById(R.id.webView);
    //根据屏幕设置WebView的高度
    WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
    DisplayMetrics dm = new DisplayMetrics();
    wm.getDefaultDisplay().getMetrics(dm);
    int width = dm.widthPixels; // 屏幕宽度（像素）
    int height = dm.heightPixels; // 屏幕高度（像素）
    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mWebview.getLayoutParams();
    params.height = (int) (dm.heightPixels * 0.70);
    mWebview.setLayoutParams(params);
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
        sendInfoToJs();
        if (mDialog != null && mDialog.isShowing()) {
          _stopDialog();
        }
        //WebView的总高度
//        float webViewContentHeight = mWebview.getContentHeight() * mWebview.getScale();
//        //WebView的现高度
//        float webViewCurrentHeight = (mWebview.getHeight() + mWebview.getScrollY());
//        if ((webViewContentHeight - webViewCurrentHeight) <= 10) {
//          mCommitBtn.setClickable(true);
//          mCommitBtn.setTextColor(mContext.getResources().getColor(R.color.colorRed));
//          Log.i("KEVIN-MyWebViewActivity", "WebView不能滑动");
//        }
      }
    });

    //添加客户端支持
    mWebview.setWebChromeClient(new WebChromeClient() {
      @Override
      public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        AlertDialog.Builder mAlert = new AlertDialog.Builder(MyWebViewActivity.this);
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
    mWebview.addJavascriptInterface(new MyWebViewActivity.JsInterface(this), "Android");
    webViewScroolChangeListener();
  }

  private void webViewScroolChangeListener() {
    mWebview.setOnCustomScroolChangeListener(new MyWevView.ScrollInterface() {
      @Override
      public void onSChanged(int l, int t, int oldl, int oldt) {
        if (isArrayBottom) {
          return;
        }
        //WebView的总高度
        float webViewContentHeight = mWebview.getContentHeight() * mWebview.getScale();
        //WebView的现高度
        float webViewCurrentHeight = (mWebview.getHeight() + mWebview.getScrollY());
        if ((webViewContentHeight - webViewCurrentHeight) <= 10) {
          mCommitBtn.setClickable(true);
          mCommitBtn.setTextColor(mContext.getResources().getColor(R.color.colorRed));
          isArrayBottom = true;
          Log.i("KEVIN-MyWebViewActivity", "WebView滑动到了底端");
        } else {
          mCommitBtn.setClickable(false);
          mCommitBtn.setTextColor(mContext.getResources().getColor(R.color.colorNoClick));
          Log.i("KEVIN-MyWebViewActivity", "WebView可以滑动");
        }
      }
    });
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.submit_btn:
        statusFlag = "1";
        callBackResult();
        break;
      case R.id.unsubmit_btn:
        statusFlag = "0";
        callBackResult();
        break;
      default:
        break;
    }
  }

  private void callBackResult() {
    Intent intent = new Intent();
    intent.putExtra("status", statusFlag);
    setResult(1002, intent);
    this.finish();
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
        mWebview.loadUrl("javascript:" + mFuncAddParams);
      }
    });
  }

  //点击返回退出浏览器
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      //setResult(1001);
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
      mWebview.clearCache(true);
      ((ViewGroup) mWebview.getParent()).removeView(mWebview);
      mWebview.destroy();
      mWebview = null;
      mDialog = null;
    }
    super.onDestroy();
  }

  private void _startDialog() {
    if (mDialog == null) {
      mDialog = new Dialog(MyWebViewActivity.this, R.style.CustomProgressDialog);
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
