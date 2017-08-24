//
//  LDRNBundleList.h
//  demo
//
//  Created by hong on 2017/8/20.
//  Copyright © 2017年 leadeon. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface LDRNBundleList : NSObject

+(NSString *) appKey;
+(NSString *) versionUrl;
+(NSString *) rnVersion;

+(NSString *) LDRNRootpath;
+(NSDictionary *) originBundles;
+(NSDictionary *) originBundlesHash;

+(NSString *) entryJSName;
+(NSString *) patchFileName;

+(NSString *) resFolder;

+(NSArray *) folders;

+(NSString *) indexFolder;
+(NSString *) mergeFolder;
+(NSString *) patchFolder;
+(NSString *) originFolder;

+(NSString *) rnSaveKey;
+(NSString *) rnDownloadKey;
+(NSString *) appInfoKey;


@end
