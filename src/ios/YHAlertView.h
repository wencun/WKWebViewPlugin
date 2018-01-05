//
//  YHAlertView.h
//  WebViewAlert_Demo
//
//  Created by Wiley on 2017/11/23.
//  Copyright © 2017年 Wiley. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <WebKit/WebKit.h>

// APP页面尺寸
#define D_WindowRect [UIApplication sharedApplication].keyWindow.bounds
// 内容尺寸
#define D_ContentRect CGRectInset(D_WindowRect,20,60)
// 底部高度
#define D_BottonHeight 50.0
// 不同意按钮颜色
#define D_CancelColor [UIColor colorWithRed:153.0/255.0 green:153.0/255.0 blue:153.0/255.0 alpha:1]
// 同意按钮颜色
#define D_DoneColor [UIColor colorWithRed:237.0/255.0 green:76.0/255.0 blue:89.0/255.0 alpha:1]
// 同意按钮颜色（禁用状态）
#define D_DoneDisableColor [UIColor colorWithRed:204.0/255.0 green:204.0/255.0 blue:204.0/255.0 alpha:1]
// 按钮边框颜色
#define D_LineColor  [UIColor colorWithRed:238.0/255.0 green:238.0/255.0 blue:238.0/255.0 alpha:1]
// 背景透明度
#define D_ShawdowAlpha 0.2
// 圆角
#define D_Radius 8.0
// 取消按钮
#define D_CancelText @"不同意"
// 确定按钮
#define D_DoneText @"同意"


typedef void(^ButtonHandler)(NSString*);

@interface YHAlertView : UIView <WKNavigationDelegate,WKUIDelegate,UIScrollViewDelegate> {
    // 等待框
    UIActivityIndicatorView* _waitView;
}

/**
 弹框主体
 */
@property (nonatomic,weak) UIView *contentView;

/**
 弹框内的 WebView
 */
@property (nonatomic,weak) WKWebView *webView;

/**
 同意按钮
 */
@property (nonatomic,weak) UIButton *doneButt;

/**
 按钮事件 0 :取消 1：确定
 */
@property (copy) ButtonHandler buttBlock;

/**
 执行JS的方法
 */
@property (copy) NSString *runJSCode;

/**
 计时器，循环调用，获取页面高度
 */
@property (nonatomic,strong) NSTimer *timer;

#pragma mark -

// 载入URL
- (void)loadUrl:(NSString*)urlStr;

// 展示页面
- (void)showView;

@end

