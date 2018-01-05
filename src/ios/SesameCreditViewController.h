//
//  OpenPageViewController.h
//  test
//
//  Created by lv zaiyi on 2017/7/21.
//  Copyright © 2017年 lv zaiyi. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol SesameCreditViewControllerDelegate <NSObject>

- (void)popCallback:(NSDictionary *)dict;

@end

@interface SesameCreditViewController : UIViewController
@property (nonatomic, copy)NSString *url;
@property (nonatomic, copy)NSString *pageTitle;
@property (nonatomic, copy)NSString *host;
@property (nonatomic, weak)id<SesameCreditViewControllerDelegate> delegate;

- (void)dismissVC;

@end


