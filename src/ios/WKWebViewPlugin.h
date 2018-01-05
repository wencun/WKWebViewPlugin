//
//  YHContact.h
//  cutePuppyPics
//
//  Created by lv zaiyi on 2017/5/23.
//
//

#import <Cordova/CDV.h>

@interface WKWebViewPlugin : CDVPlugin

- (void)openPage:(CDVInvokedUrlCommand *)command;

- (void)openYhWebView:(CDVInvokedUrlCommand *)command;

- (void)openAlertWebView:(CDVInvokedUrlCommand *)command;

@end
