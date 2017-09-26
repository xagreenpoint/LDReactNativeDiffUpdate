//
//  PatchFileManager.h
//  demo
//
//  Created by hong on 2017/8/20.
//  Copyright © 2017年 leadeon. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface LDPatchFileManager : NSObject

+ (void) clearLDReactNativeCache;
+ (void) goBackOriginal: (NSString *) bundleName;

+ (void) updateReactNativeLocalPath;
+ (void) copyLDBundleToReactNativePath;
+ (void) downloadPatchFile: (NSString *) url withBundle: (NSString *) bundleName;
+ (void) reloadAllJSPatch;

+ (NSString *) jsbundleLocation: (NSString *) bundleName;


@end
