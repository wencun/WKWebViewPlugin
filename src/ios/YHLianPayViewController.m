//
//  OpenPageViewController.m
//  test
//
//  Created by lv zaiyi on 2017/7/21.
//  Copyright © 2017年 lv zaiyi. All rights reserved.
//

#import "YHLianPayViewController.h"
#import <WebKit/WebKit.h>
#import "SVProgressHUD.h"

@interface YHLianPayViewController ()<WKNavigationDelegate,WKUIDelegate>
@property (nonatomic,strong) WKWebView *webView;
@end

@implementation YHLianPayViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [SVProgressHUD show];
    [self setNav];
    
    self.webView = [[WKWebView alloc] initWithFrame:self.view.bounds];
    self.webView.navigationDelegate = self;
    self.webView.UIDelegate = self;
    [self.webView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:_url]]];
    [self.webView setBackgroundColor:[UIColor whiteColor]];
    [self.view addSubview:self.webView];
    
}

- (void)webView:(WKWebView *)webView decidePolicyForNavigationResponse:(WKNavigationResponse *)navigationResponse decisionHandler:(void (^)(WKNavigationResponsePolicy))decisionHandler{
    
    if ([navigationResponse.response.URL.absoluteString containsString:_successUrl]) {
        [self backLianPay:@"1"];
    }else if ([navigationResponse.response.URL.absoluteString containsString:_backUrl]){
        [self backLianPay:@"0"];
    }
    
    decisionHandler(WKNavigationResponsePolicyAllow);
}

- (void)webView:(WKWebView *)webView didFinishNavigation:(WKNavigation *)navigation{
    [SVProgressHUD dismiss];
    //如果为0则无限期
    if (![@"0" isEqualToString:_sessionExpirationTime]) {
        [NSObject cancelPreviousPerformRequestsWithTarget:self];
        [self performSelector:@selector(backLianPay:) withObject:@"0" afterDelay:[_sessionExpirationTime doubleValue]];
    }
}

- (UIStatusBarStyle)preferredStatusBarStyle{
    return UIStatusBarStyleLightContent;
}

- (void)setNav{
    self.title = _pageTitle;
    if (!_isShowNav) {
        [self.navigationController setNavigationBarHidden:YES];
        return;
    }
    
    self.navigationController.navigationBar.titleTextAttributes = [NSDictionary dictionaryWithObjectsAndKeys:[UIColor whiteColor], NSForegroundColorAttributeName, nil];
    self.navigationController.navigationBar.barStyle = UIBarStyleBlack;
    
    [self.navigationItem setLeftBarButtonItem:[self customBarBtnItemWithImageName:@"back" action:@selector(navBack) frame:CGRectMake(0, 0, 15, 25)]];
    
    //    [self.navigationItem setRightBarButtonItem:[self customBarBtnItemWithImageName:@"news" action:@selector(dismissVC) frame:CGRectMake(0, 0, 25, 25)]];
}

- (UIBarButtonItem *)customBarBtnItemWithImageName:(NSString *)imageName action:(SEL)action frame:(CGRect)rect{
    UIImage* image= [UIImage imageNamed:imageName];//[UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:imageName ofType:@"png"]];
    UIButton *someButton= [[UIButton alloc] initWithFrame:rect];
    [someButton addTarget:self action:action forControlEvents:UIControlEventTouchUpInside];
    [someButton setBackgroundImage:image forState:UIControlStateNormal];
    //    [someButton setShowsTouchWhenHighlighted:YES];
    UIBarButtonItem* leftBarItem= [[UIBarButtonItem alloc] initWithCustomView:someButton];
    return leftBarItem;
}

#pragma mark - WKUIDelegate

- (WKWebView *)webView:(WKWebView *)webView createWebViewWithConfiguration:(WKWebViewConfiguration *)configuration forNavigationAction:(WKNavigationAction *)navigationAction windowFeatures:(WKWindowFeatures *)windowFeatures {
    WKFrameInfo *frameInfo = navigationAction.targetFrame;
    if (![frameInfo isMainFrame]) {
        [webView loadRequest:navigationAction.request];
    }
    return nil;
}


- (void)backLianPay:(NSString *)isSuccessPage{
    if ([self.delegate respondsToSelector:@selector(popLianPayCallback:)]) {
        [self.delegate popLianPayCallback:@{@"isSuccessPage": [isSuccessPage isEqualToString:@"1"]?@1:@0}];
    }
}

- (void)navBack {
    if (self.webView.canGoBack) {
        [self.webView goBack];
    } else {
        [self backLianPay:@"0"];
        //        [self dismissViewControllerAnimated:YES completion:nil];
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end

