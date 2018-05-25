package com.webview.yhck;

import android.content.Intent;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class YHWebViewPlugin extends CordovaPlugin {
  private static final int MSG_REQUEST_CODE = 1000;
  private CallbackContext mCallbackContext;
  private String getParam;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    this.getParam = "";
    this.mCallbackContext = callbackContext;
    if (!"".equals(action) || action != null) {
      getParam = args.getJSONObject(0).toString();
      if (action.equals("openAlertWebView")) {
        openDialogActivity();
      } else if (action.equals("openLianPayWebView")) {
        openLianPayActivity();
      } else {
        openActivity();
      }
      return true;
    }
    mCallbackContext.error("error");
    return false;
  }

  private void openDialogActivity() {
    Intent intent = new Intent(cordova.getActivity(), MyWebViewActivity.class);
    intent.putExtra("Param", getParam);
    cordova.startActivityForResult(this, intent, MSG_REQUEST_CODE);
  }

  private void openLianPayActivity() {
    Intent intent = new Intent(cordova.getActivity(), LianPayActivity.class);
    intent.putExtra("Param", getParam);
    cordova.startActivityForResult(this, intent, MSG_REQUEST_CODE);
  }

  private void openActivity() {
    Intent intent = new Intent(cordova.getActivity(), WebViewActivity.class);
    intent.putExtra("Param", getParam);
    cordova.startActivityForResult(this, intent, MSG_REQUEST_CODE);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
    if (requestCode == MSG_REQUEST_CODE && resultCode == 1001) {
      mCallbackContext.success("");
    } else if (requestCode == MSG_REQUEST_CODE && resultCode == 1002) {
      try {
        String result = intent.getStringExtra("status");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", result);
        mCallbackContext.success(jsonObject);
      } catch (JSONException e) {
        e.printStackTrace();
      }
    } else if (requestCode == MSG_REQUEST_CODE && resultCode == 1003) {
      JSONObject jsonObject = new JSONObject();
      try {
        jsonObject.put("isSuccessPage", 1);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      mCallbackContext.success(jsonObject);
    } else if (requestCode == MSG_REQUEST_CODE && resultCode == 1004) {
      JSONObject jsonObject = new JSONObject();
      try {
        jsonObject.put("isSuccessPage", 0);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      mCallbackContext.success(jsonObject);
    }
  }

}
