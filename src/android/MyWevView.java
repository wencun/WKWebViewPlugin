package com.webview.yhck;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * 添加滚动监听的WebView
 * Created by YH_CK on 2017-12-12.
 */

public class MyWevView extends WebView {


  public ScrollInterface mScrollInterface;
  public MyWevView(Context context) {
    super(context);
  }

  public MyWevView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public MyWevView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    mScrollInterface.onSChanged(l, t, oldl, oldt);
  }

  public void setOnCustomScroolChangeListener(ScrollInterface scrollInterface){
    this.mScrollInterface =scrollInterface;
  }
  public interface  ScrollInterface{
    public void onSChanged(int l, int t, int oldl, int oldt);
  }
}
