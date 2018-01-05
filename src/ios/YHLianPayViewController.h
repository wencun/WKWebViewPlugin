//
//  YHWebViewController.h
//  WebViewPlugin
//
//  Created by Wiley on 2017/9/11.
//  Copyright © 2017年 MrWiley. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol YHLianPayViewControllerDelegate <NSObject>

- (void)popCallback:(NSDictionary *)dict;

@end

@interface YHLianPayViewController : UIViewController

@property (nonatomic, copy)NSString *url;
@property (nonatomic, copy)NSString *pageTitle;
@property (nonatomic, copy)NSString *successUrl;
@property (nonatomic, copy)NSString *backUrl;
@property (nonatomic, assign)BOOL isShowNav;
@property (nonatomic, weak)id<YHLianPayViewControllerDelegate> delegate;

- (void)dismissVC;
@end
