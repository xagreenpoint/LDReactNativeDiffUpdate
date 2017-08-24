//
//  LDRNBundleList.m
//  demo
//
//  Created by hong on 2017/8/20.
//  Copyright © 2017年 leadeon. All rights reserved.
//

#import "LDRNBundleList.h"

@implementation LDRNBundleList

+(NSString *) appKey {
  return @"xxxxxxxxxxxx";
}

+(NSString *) LDRNRootpath {
  return @"rn_res";
}

+(NSString *) rnVersion {
  return @"0.46.4";
}

+(NSDictionary *) originBundles {
  return @{
           @"LDBusinessEntry": @"1.0.1",
           @"LDCommon": @"1.0.0",
           };
}

+(NSDictionary *) originBundlesHash {
  return @{
           @"LDBusinessEntry": @"6c36ebdd7fbde18b26be726d2d3a7254",
           @"LDCommon": @"4f2601d5e4dbd4a8eea9d4110561dca4",
           };
}


+(NSString *) entryJSName {
  return @"main.jsbundle";
}

+(NSString *) patchFileName {
  return @"patch.jsbundle";
}

+(NSString *) resFolder {
  return @"assets";
}

+(NSArray *) folders {
  return @[
           @"index",
           @"merge",
           @"patch",
           @"origin",
           ];
}

+(NSString *) indexFolder {
  return [[LDRNBundleList folders] objectAtIndex: 0];
}

+(NSString *) mergeFolder {
  return [[LDRNBundleList folders] objectAtIndex: 1];
}

+(NSString *) patchFolder {
  return [[LDRNBundleList folders] objectAtIndex: 2];
}

+(NSString *) originFolder {
  
  return [[LDRNBundleList folders] lastObject];
}

+(NSString *) versionUrl {
  return @"http://192.168.6.30:3000/biz-orange/RN/patchVersion";
}

+(NSString *) rnSaveKey {
  return @"_reactNativeBundlesVersion";
}

+(NSString *) rnDownloadKey {
  return @"_reactNativeDownloadFlag";
}

+(NSString *) appInfoKey {
  return @"_reactNativeAppInfo";
}



@end
