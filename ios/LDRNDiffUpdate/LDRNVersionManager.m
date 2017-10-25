//
//  LDRNVersionManager.m
//  demo
//
//  Created by hong on 2017/8/21.
//  Copyright © 2017年 leadeon. All rights reserved.
//

#import "LDRNVersionManager.h"
#import "LDRNBundleList.h"
#import "LDPatchFileManager.h"


@implementation LDRNVersionManager


+(BOOL) validResBody: (NSDictionary *) res {
    
    if (res == nil
        || res[@"retCode"] == nil
        || ![res[@"retCode"] isEqualToString:@"000000"]
        || res[@"resBody"] == nil) {
        
        return NO;
    }
    
    return YES;
}

+(void) checkRNBundlesVersion: (SUCCESS_CALLBACK) success withFailure: (FAILED_CALLBACK) failure {
    
    NSURL *url = [NSURL URLWithString: [LDRNBundleList versionUrl]];
    
    NSURLSessionConfiguration *config = [NSURLSessionConfiguration defaultSessionConfiguration];
    NSURLSession *session = [NSURLSession sessionWithConfiguration: config];
    
    NSArray *patchs = [[NSUserDefaults standardUserDefaults] objectForKey:[LDRNBundleList rnSaveKey]];
    
    __block NSDictionary *body = [LDRNBundleList originBundles];
    if (patchs && patchs.count > 0) {
        
        NSMutableDictionary *bundles = [NSMutableDictionary dictionary];
        [patchs enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            
            [bundles setObject:obj[@"version"] forKey:obj[@"moduleName"]];
        }];
        
        body = bundles;
    }
    
    NSDictionary *infoDictionary = [[NSBundle mainBundle] infoDictionary];
    NSString *appVersion = [infoDictionary objectForKey:@"CFBundleShortVersionString"];
    NSDictionary *reqObj = @{
                             @"appKey": [LDRNBundleList appKey],
                             @"appVersion": appVersion,
                             @"rnVersion": [LDRNBundleList rnVersion],
                             @"platform": @"ios",
                             @"reqBody": body,
                             };
    
    NSData *reqData = [NSJSONSerialization dataWithJSONObject:reqObj options:kNilOptions error:nil];
    
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL: url];
    request.HTTPMethod = @"POST";
    [request setValue:@"application/Json" forHTTPHeaderField:@"Content-Type"];
    request.HTTPBody = reqData;
    NSURLSessionTask *task = [session dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        
        if (error) {
            failure(error, response);
            return ;
        }
        
        NSError *err = nil;
        NSDictionary *res = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error: &err];
        
        if (err) {
            failure(err, response);
            return ;
        }
        
        NSError *customError = [NSError errorWithDomain:@"custom.rn.errdomain" code: -3 userInfo:@{NSLocalizedDescriptionKey: @"response data format not well"}];
        if ([LDRNVersionManager validResBody: res]) {
            
            failure(customError, response);
            return ;
        }
        
        NSArray *patchs = res[@"rspBody"][@"patchs"];
        if (patchs == nil) {
            
            failure(customError, response);
            return ;
        }
        
        if (patchs.count > 0) {
            
            [[NSUserDefaults standardUserDefaults] setObject:patchs forKey: [LDRNBundleList rnSaveKey]];
            [[NSUserDefaults standardUserDefaults] setObject:@[] forKey: [LDRNBundleList rnDownloadKey]];
            [[NSUserDefaults standardUserDefaults] synchronize];
        }
        
        success(patchs, response);
    }];
    
    [task resume];
}

+(NSArray *) unDownloadBundles {
    
    
    NSArray *flags = [[NSUserDefaults standardUserDefaults] objectForKey:[LDRNBundleList rnDownloadKey]];
    NSArray *patchs = [[NSUserDefaults standardUserDefaults] objectForKey:[LDRNBundleList rnSaveKey]];
    
    __block NSMutableArray *arrs = [NSMutableArray array];
    [patchs enumerateObjectsUsingBlock:^(NSDictionary * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        
        NSString *bundleName = obj[@"moduleName"];
        if (flags != nil && ![flags containsObject: bundleName]) {
            [arrs addObject: obj];
        }
    }];
    
    return arrs;
}

+(void) updateReactNativeBundles {
    
    NSArray *bundles = [LDRNVersionManager unDownloadBundles];
    if ([bundles count] > 0) {
        
        //continue download
        [bundles enumerateObjectsUsingBlock:^(NSDictionary *  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            
            NSString *downloadUrl = obj[@"zipPath"];
            NSString *bundleName = obj[@"moduleName"];
            NSString *needGoback = obj[@"needGoBack"];
            
            if ([needGoback isEqualToString:@"true"]) {
                return ;
            }
            
            [LDPatchFileManager downloadPatchFile:downloadUrl withBundle:bundleName];
        }];
        
        return;
    }
    
    [LDRNVersionManager checkRNBundlesVersion:^(NSArray *bundlesInfo, NSURLResponse *response) {
        
        [bundlesInfo enumerateObjectsUsingBlock:^(NSDictionary   * _Nonnull info, NSUInteger idx, BOOL * _Nonnull stop) {
            
            NSString *zipUrl = info[@"zipPath"];
            NSString *bundleName = info[@"moduleName"];
            NSString *needGoback = info[@"needGoBack"];
            
            if ([needGoback isEqualToString:@"true"]) {
                return ;
            }
            
            if (zipUrl != nil && bundleName != nil) {
                [LDPatchFileManager downloadPatchFile: zipUrl withBundle: bundleName];
            }
            
        }];
        
    } withFailure:^(NSError *error, NSURLResponse *response) {
        
    }];
}

+ (void) gobackOriginalIfNeed {
    
    NSArray *patchs = [[NSUserDefaults standardUserDefaults] objectForKey:[LDRNBundleList rnSaveKey]];
    
    [patchs enumerateObjectsUsingBlock:^(NSDictionary * _Nonnull info, NSUInteger idx, BOOL * _Nonnull stop) {
        
        NSString *bundleName = info[@"moduleName"];
        NSString *needGoback = info[@"needGoBack"];
        
        if ([needGoback isEqualToString:@"true"]) {
            
            [LDPatchFileManager goBackOriginal: bundleName];
            return ;
        }
    }];
}

@end
