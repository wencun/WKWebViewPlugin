//
//  YHContact.m
//  cutePuppyPics
//
//  Created by lv zaiyi on 2017/5/23.
//
//

#import "WKWebViewPlugin.h"
#import "OpenPageViewController.h"
#import "YHLianPayViewController.h"
#import "YHAlertView.h"

@interface WKWebViewPlugin()<OpenPageViewControllerDelegate>

@property (nonatomic, copy) NSString *callbackId;
@property (nonatomic, strong) NSMutableArray *array;
@property (strong, nonatomic) OpenPageViewController *opvc;

@end


@implementation WKWebViewPlugin

#pragma mark - 芝麻信用
- (void)openSesameCreditWebView:(CDVInvokedUrlCommand *)command {
    NSDictionary *dict  = [command argumentAtIndex:0 withDefault:nil];
    if (dict) {
        NSAssert(dict[@"URL"], @"WKWebViewPlugin's url can not be empty");
        NSAssert(dict[@"host"], @"WKWebViewPlugin's host can not be empty");
        
        self.callbackId = [command.callbackId copy];
        self.array = [NSMutableArray array];
        
        _opvc = [[OpenPageViewController alloc] init];
        _opvc.delegate = self;
        _opvc.url = dict[@"URL"];
        _opvc.pageTitle = dict[@"title"];
        
        UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:_opvc];
        [self setStatusBarBackgroundColor:[UIColor blackColor]];
        [self.viewController presentViewController:nav animated:YES completion:nil];
    }
}
#pragma mark - alert样式
- (void)openAlertWebView:(CDVInvokedUrlCommand *)command {
    NSDictionary *dict  = [command argumentAtIndex:0 withDefault:nil];
    if (dict) {
        self.callbackId = [command.callbackId copy];
        NSString *url = dict[@"URL"];
        NSString *runJsCode = dict[@"funcAddParam"];
        
        YHAlertView *wView = [[YHAlertView alloc] init];
        wView.buttBlock = ^(NSString *status) {
            NSDictionary *_dic = [NSDictionary dictionaryWithObjectsAndKeys:status,@"status", nil];
            [self sendResult:_dic];
        };
        [wView setRunJSCode:runJsCode];
        [wView loadUrl:url];
        [wView showView];
    }
}

#pragma mark - 连连支付
- (void)openLianPayWebView:(CDVInvokedUrlCommand *)command {
    NSDictionary *dict  = [command argumentAtIndex:0 withDefault:nil];
    if (dict) {
        NSAssert(dict[@"URL"], @"WKWebViewPlugin's url can not be empty");
        NSAssert(dict[@"successUrl"], @"WKWebViewPlugin's successUrl can not be empty");
        NSAssert(dict[@"backUrl"], @"WKWebViewPlugin's backUrl can not be empty");
        
        YHLianPayViewController *lianPay = [[YHLianPayViewController alloc] init];
        lianPay.delegate = self;
        lianPay.url = dict[@"URL"];
        lianPay.pageTitle = dict[@"title"];
        lianPay.successUrl = dict[@"successUrl"];
        lianPay.backUrl = dict[@"backUrl"];
        lianPay.isShowNav = [dict[@"isShowNav"] boolValue] == NO ? NO : YES;
        
        UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:lianPay];
        [self setStatusBarBackgroundColor:[UIColor blackColor]];
        [self.viewController presentViewController:nav animated:YES completion:nil];
    }
}

//设置状态栏颜色
- (void)setStatusBarBackgroundColor:(UIColor *)color {
    UIView *statusBar = [[[UIApplication sharedApplication] valueForKey:@"statusBarWindow"] valueForKey:@"statusBar"];
    if ([statusBar respondsToSelector:@selector(setBackgroundColor:)]) {
        statusBar.backgroundColor = color;
    }
}

- (void)popCallback:(NSDictionary *)dict{
    [self sendResult:dict];
    [_opvc dismissVC];
}

- (void)sendResult:(NSDictionary*) resultDict{
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultDict];
    [self.commandDelegate sendPluginResult:result callbackId:_callbackId];
}



@end


