module.exports = {

  openPage:function (arg, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "WKWebViewPlugin", "openPage", [arg]);
  },
  
  openYhWebView:function (arg, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "WKWebViewPlugin", "openYhWebView", [arg]);
  },

  openAlertWebView:function (arg, successCallback, errorCallback) {
      cordova.exec(successCallback, errorCallback, "WKWebViewPlugin", "openAlertWebView", [arg]);
  }
};
