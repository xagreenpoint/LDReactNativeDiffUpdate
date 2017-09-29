//
//  LDRNDiffUpdate.m
//  demo
//
//  Created by hong on 2017/8/22.
//  Copyright © 2017年 leadeon. All rights reserved.
//

#import "LDRNDiffUpdate.h"

#import "LDPatchFileManager.h"
#import "LDRNVersionManager.h"
#import "LDRNBundleList.h"

@implementation LDRNDiffUpdate

+(NSURL *) jsBundleUrl: (NSString *) bundleName {
    
    NSDictionary *infoDictionary = [[NSBundle mainBundle] infoDictionary];
    NSString *appCurVersion = [infoDictionary objectForKey:@"CFBundleShortVersionString"];
    
    NSDictionary *appInfo = [[NSUserDefaults standardUserDefaults] objectForKey: [LDRNBundleList appInfoKey]];
    if (appInfo == nil || appInfo[@"appVersion"] == nil || ![appInfo[@"appVersion"] isEqualToString: appCurVersion]) {
        
        //first
        appInfo = @{
                    @"appVersion": appCurVersion
                    };
        [[NSUserDefaults standardUserDefaults] setObject:appInfo forKey: [LDRNBundleList appInfoKey]];
        [[NSUserDefaults standardUserDefaults] synchronize];
        
        [LDPatchFileManager clearLDReactNativeCache];
        [LDPatchFileManager updateReactNativeLocalPath];
        [LDPatchFileManager copyLDBundleToReactNativePath];
    } else {
        
        //not first
        [LDRNVersionManager gobackOriginalIfNeed];
        [LDPatchFileManager updateReactNativeLocalPath];
        [LDPatchFileManager reloadAllJSPatch];
    }
    
    NSURL *jslocation = [NSURL fileURLWithPath: [LDPatchFileManager jsbundleLocation: bundleName]];
    [LDRNVersionManager updateReactNativeBundles];
    return jslocation;
}


+(NSURL *) jsBundleUrlDefault {
    NSString *commonBundle = @"LDCommon";
    return [LDRNDiffUpdate jsBundleUrl: commonBundle];
}

@end
