module.exports = {

  openSesameCreditWebView:function (arg, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "WKWebViewPlugin", "openSesameCreditWebView", [arg]);
  },
  
  openLianPayWebView:function (arg, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "WKWebViewPlugin", "openLianPayWebView", [arg]);
  },

  openAlertWebView:function (arg, successCallback, errorCallback) {
      cordova.exec(successCallback, errorCallback, "WKWebViewPlugin", "openAlertWebView", [arg]);
  }
};
