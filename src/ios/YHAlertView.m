//
//  YHAlertView.m
//  WebViewAlert_Demo
//
//  Created by Wiley on 2017/11/23.
//  Copyright © 2017年 Wiley. All rights reserved.
//

#import "YHAlertView.h"

@implementation YHAlertView

- (instancetype)init {
    self = [super init];
    if (self) {
        self.frame = D_WindowRect;
        // 背景视图
        UIView *shawdowView = [[UIView alloc] initWithFrame:D_WindowRect];
        shawdowView.backgroundColor  = [UIColor blackColor];
        shawdowView.alpha = D_ShawdowAlpha;
        [self addSubview:shawdowView];
        // 弹框内容视图
        CGRect contentRect;
        UIView *contentView = [[UIView alloc] initWithFrame:D_ContentRect];
        contentView.layer.cornerRadius = D_Radius;
        contentView.layer.masksToBounds = YES;
        contentView.backgroundColor = [UIColor whiteColor];
        contentView.center = self.center;
        contentRect = contentView.bounds;
        self.contentView = contentView;
        [self addSubview:contentView];
        // WebView
        CGRect webViewRect = contentRect;
        webViewRect.size.height -= D_BottonHeight;
        WKWebView *webView = [[WKWebView alloc] initWithFrame:webViewRect];
        [webView setBackgroundColor:[UIColor whiteColor]];
        webView.scrollView.bounces = NO;
        webView.scrollView.delegate = self;
        webView.navigationDelegate = self;
        webView.UIDelegate = self;
        self.webView = webView;
        [contentView addSubview:webView];
        
        self.scrollCount = 0;

        // 等待框
        UIActivityIndicatorView* waitView = [[UIActivityIndicatorView alloc] initWithFrame:CGRectMake(0,0,30.0,30.0)];
        waitView.center = webView.center;
        waitView.activityIndicatorViewStyle = UIActivityIndicatorViewStyleGray;
        [contentView addSubview:waitView];
        _waitView = waitView;
        // Button
        CGRect buttonRect = contentRect;
        buttonRect.origin.y += webViewRect.size.height;
        buttonRect.size.width = contentRect.size.width/2;
        buttonRect.size.height = D_BottonHeight;
        // 底部边框线
        UIView *line_horizontal = [[UIView alloc] initWithFrame:CGRectMake(11, buttonRect.origin.y+1, D_ContentRect.size.width-22, 1)];
        [line_horizontal setBackgroundColor: D_LineColor];
        UIView *line_vertical = [[UIView alloc] initWithFrame:CGRectMake(contentRect.size.width/2, buttonRect.origin.y+1, 1, D_BottonHeight)];
        [line_vertical setBackgroundColor: D_LineColor];
        [contentView addSubview:line_horizontal];
        [contentView addSubview:line_vertical];
        UIButton *buttCancel = [[UIButton alloc] initWithFrame:buttonRect];
        [buttCancel addTarget:self action:@selector(buttCancel) forControlEvents:UIControlEventTouchDown];
        [buttCancel setTitle:D_CancelText forState:UIControlStateNormal];
        [buttCancel setTitleColor:D_CancelColor forState:UIControlStateNormal];
        [contentView addSubview:buttCancel];
        buttonRect.origin.x += contentRect.size.width/2;
        UIButton *buttDone = [[UIButton alloc] initWithFrame:buttonRect];
        [buttDone addTarget:self action:@selector(buttDone) forControlEvents:UIControlEventTouchDown];
        [buttDone setTitle:D_DoneText forState:UIControlStateNormal];
        [buttDone setTitleColor:D_DoneColor forState:UIControlStateNormal];
        [buttDone setTitleColor:D_DoneDisableColor forState:UIControlStateDisabled];
        buttDone.enabled = false;
        [contentView addSubview:buttDone];
        self.doneButt = buttDone;
    }
    return self;
}

#pragma mark -

- (void)loadUrl:(NSString*)urlStr {
    NSURLRequest *urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:urlStr]];
    [self.webView loadRequest: urlRequest];
}

- (void)showView {
    // 第一步：将view宽高缩至无限小（点）
    self.contentView.transform = CGAffineTransformScale(CGAffineTransformIdentity, CGFLOAT_MIN, CGFLOAT_MIN);
    [UIView animateWithDuration:0.2 animations:^{
        self.contentView.transform = CGAffineTransformIdentity;
    }];
    [[self topViewController].view addSubview:self];
}

- (void)buttCancel {
    [self dismiss];
    self.buttBlock(@"0");
}

- (void)buttDone {
    [self dismiss];
    self.buttBlock(@"1");
}

- (void)dismiss {
    [self removeFromSuperview];
}

#pragma mark - WKNavigationDelegate

// 页面开始加载时调用
- (void)webView:(WKWebView *)webView didStartProvisionalNavigation:(WKNavigation *)navigation {
    [_waitView startAnimating];
}

// 当内容开始返回时调用
- (void)webView:(WKWebView *)webView didCommitNavigation:(WKNavigation *)navigation {
    [_waitView stopAnimating];
}

// 页面加载完成之后调用
- (void)webView:(WKWebView *)webView didFinishNavigation:(WKNavigation *)navigation {
    [self exeJavaScript:self.runJSCode];
    self.timer = [NSTimer scheduledTimerWithTimeInterval:0.5 target:self selector:@selector(delayMethod) userInfo:nil repeats:YES];
}

- (void)delayMethod {
    if (self.webView.scrollView.contentSize.height > 0) {
        [self.timer invalidate];
        self.timer = nil;
    }
    if (self.webView.scrollView.contentSize.height <= self.webView.frame.size.height) {
        self.doneButt.enabled = true;
        self.webView.scrollView.delegate = nil;
    }
}

// 页面加载失败时调用
- (void)webView:(WKWebView *)webView didFailProvisionalNavigation:(WKNavigation *)navigation {
    [_waitView stopAnimating];
}

// 执行js代码
- (void)exeJavaScript:(NSString*)exeJS {
    [self.webView evaluateJavaScript:exeJS completionHandler:^(id _Nullable id, NSError * _Nullable error) {}];
}

#pragma mark -

- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    //解决第一次打开的时候，偶尔会出现执行此方法的情况，即scrollView.contentOffset会变动
    if (self.scrollCount < 2) {//测试出来2次
        if (scrollView.contentOffset.y > 100){//测试出来scrollView.contentOffset.y 会偏移 400多，所以取一个大概值即可，还有一个原因是，如果是手动滑动的话，scrollView.contentOffset.y变化幅度不会超过10
            self.scrollCount++;
            return;
        }else{
            self.scrollCount = 2;
        }
    }
    CGFloat offset = (scrollView.contentSize.height-self.webView.frame.size.height) - scrollView.contentOffset.y;
    if (offset <= 10.0) {
        self.doneButt.enabled = true;
        self.webView.scrollView.delegate = nil;
    }
}

#pragma mark - 获取当前页面的 ViewController

- (UIViewController *)topViewController {
    UIViewController *resultVC;
    resultVC = [self _topViewController:[[UIApplication sharedApplication].keyWindow rootViewController]];
    while (resultVC.presentedViewController) {
        resultVC = [self _topViewController:resultVC.presentedViewController];
    }
    return resultVC;
}

- (UIViewController *)_topViewController:(UIViewController *)vc {
    if ([vc isKindOfClass:[UINavigationController class]]) {
        return [self _topViewController:[(UINavigationController *)vc topViewController]];
    } else if ([vc isKindOfClass:[UITabBarController class]]) {
        return [self _topViewController:[(UITabBarController *)vc selectedViewController]];
    } else {
        return vc;
    }
    return nil;
}

@end

