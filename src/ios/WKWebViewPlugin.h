//
//  YHContact.h
//  cutePuppyPics
//
//  Created by lv zaiyi on 2017/5/23.
//
//

#import <Cordova/CDV.h>

@interface WKWebViewPlugin : CDVPlugin

//芝麻信用
- (void)openSesameCreditWebView:(CDVInvokedUrlCommand *)command;
//连连支付
- (void)openLianPayWebView:(CDVInvokedUrlCommand *)command;
//Alert样式
- (void)openAlertWebView:(CDVInvokedUrlCommand *)command;

@end

