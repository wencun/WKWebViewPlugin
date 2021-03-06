//
//  OpenPageViewController.m
//  test
//
//  Created by lv zaiyi on 2017/7/21.
//  Copyright © 2017年 lv zaiyi. All rights reserved.
//

#import "SesameCreditViewController.h"
#import <WebKit/WebKit.h>
#import "SVProgressHUD.h"

@interface SesameCreditViewController ()<WKNavigationDelegate,WKUIDelegate>
@property (nonatomic,strong) WKWebView *webView;
@end

@implementation SesameCreditViewController

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
    if (!_host) {
        decisionHandler(WKNavigationResponsePolicyAllow);
        return;
    }
    if ([navigationResponse.response.URL.absoluteString containsString:_host]) {
        dispatch_time_t delayTime = dispatch_time(DISPATCH_TIME_NOW, (int64_t)3.0 * NSEC_PER_SEC);
        __weak __typeof(self) weakSelf = self;
        dispatch_after(delayTime, dispatch_get_main_queue(), ^{
            __strong __typeof(weakSelf) strongSelf = weakSelf;
            [strongSelf back];
        });
    }
    decisionHandler(WKNavigationResponsePolicyAllow);
}

- (void)webView:(WKWebView *)webView didFinishNavigation:(null_unspecified WKNavigation *)navigation{
    [SVProgressHUD dismiss];
}

- (UIStatusBarStyle)preferredStatusBarStyle{
    return UIStatusBarStyleLightContent;
}

- (UIImage*) createImageWithColor: (UIColor*) color
{
    CGRect rect=CGRectMake(0.0f, 0.0f, 1.0f, 1.0f);
    UIGraphicsBeginImageContext(rect.size);
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetFillColorWithColor(context, [color CGColor]);
    CGContextFillRect(context, rect);
    UIImage *theImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return theImage;
}

- (void)setNav{
    self.title = _pageTitle;
    
    self.navigationController.navigationBar.titleTextAttributes = [NSDictionary dictionaryWithObjectsAndKeys:[UIColor whiteColor], NSForegroundColorAttributeName, nil];
    self.navigationController.navigationBar.barStyle = UIBarStyleBlack;
    self.navigationController.navigationBar.translucent = NO;
    
    
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


- (void)back{
    if ([self.delegate respondsToSelector:@selector(popCallback:)]) {
        [self.delegate popCallback:nil];
    }
}

- (void)dismissVC{
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)navBack {
    if (self.webView.canGoBack) {
        [self.webView goBack];
    } else {
        [self back];
        //        [self dismissViewControllerAnimated:YES completion:nil];
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end

