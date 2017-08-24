//
//  LDRNVersionManager.h
//  demo
//
//  Created by hong on 2017/8/21.
//  Copyright © 2017年 leadeon. All rights reserved.
//

#import <Foundation/Foundation.h>


typedef void (^SUCCESS_CALLBACK)(NSArray *data, NSURLResponse *response);
typedef void (^FAILED_CALLBACK)(NSError *error, NSURLResponse *response);



@interface LDRNVersionManager : NSObject

+(void) checkRNBundlesVersion: (SUCCESS_CALLBACK) success withFailure: (FAILED_CALLBACK) failure;
+(void) updateReactNativeBundles;

@end
