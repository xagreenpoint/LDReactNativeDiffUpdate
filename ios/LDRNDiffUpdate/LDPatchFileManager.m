//
//  PatchFileManager.m
//  demo
//
//  Created by hong on 2017/8/20.
//  Copyright © 2017年 leadeon. All rights reserved.
//

#import "LDPatchFileManager.h"
#import "LDRNBundleList.h"
#import "SSZipArchive.h"
#import "bspatch.h"
#import "FileHash.h"

@implementation LDPatchFileManager


+ (NSString *)getApplicationSupportDirectory
{
    NSString *applicationSupportDirectory = [NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, YES) objectAtIndex:0];
    return applicationSupportDirectory;
}

+ (NSString *) getLDReactNativePath {
    
    return [[LDPatchFileManager getApplicationSupportDirectory] stringByAppendingPathComponent: [LDRNBundleList LDRNRootpath]];
}

+ (void) clearLDReactNativeCache {
    
    NSString *rnPath = [LDPatchFileManager getLDReactNativePath];
    [LDPatchFileManager clearFilesForPath: rnPath];
}

+ (void) goBackOriginal: (NSString *) bundleName {
    
    [[LDRNBundleList folders] enumerateObjectsUsingBlock:^(NSString *  _Nonnull folderName, NSUInteger idx, BOOL * _Nonnull stop) {
        
        if ([folderName isEqualToString:@"origin"]) {
            return ;
        }
        
        NSString *fullPath = [NSString stringWithFormat:@"%@/%@/%@", [LDPatchFileManager getLDReactNativePath], bundleName, folderName];
        [LDPatchFileManager clearFilesForPath: fullPath];
    }];
}

+ (BOOL) clearFilesForPath: (NSString *) fullPath {
    
    NSError *error = nil;
    NSArray *files = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:fullPath error: &error];
    if (error) {
        return NO;
    }
    
    __block BOOL flag = YES;
    
    [files enumerateObjectsUsingBlock:^(id  _Nonnull fileName, NSUInteger idx, BOOL * _Nonnull stop) {
        
        NSString *tmpfile = [fullPath stringByAppendingPathComponent: fileName];
        NSError *error = nil;
        if (![[NSFileManager defaultManager] removeItemAtPath:tmpfile error: &error]) {
            //error
            flag = NO;
            return ;
        }
    }];
    
    return flag;
}


+(BOOL) createFolderAtPath: (NSString *) fullPath {
    
    if ([[NSFileManager defaultManager] fileExistsAtPath: fullPath]) {
        return NO;
    }
    
    NSError *error = nil;
    if (![[NSFileManager defaultManager] createDirectoryAtPath: fullPath withIntermediateDirectories:YES attributes:nil error: &error]) {
        return NO;
    }
    
    return YES;
}

+(BOOL) moveItems: (NSString *) srcPath toPath: (NSString *) destPath {
    
    NSError *error = nil;
    NSArray *files = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:srcPath error: &error];
    if (error) {
        return NO;
    }
    
    __block BOOL flag = YES;
    
    [files enumerateObjectsUsingBlock:^(id  _Nonnull fileName, NSUInteger idx, BOOL * _Nonnull stop) {
        
        NSString *tmpSrcFile = [srcPath stringByAppendingPathComponent: fileName];
        NSString *tempDestFile = [destPath stringByAppendingPathComponent: fileName];
        
        NSError *err = nil;
        if (![[NSFileManager defaultManager] moveItemAtPath:tmpSrcFile toPath:tempDestFile error: &err]) {

            if (err.code != 516) {
                flag = NO;
                return ;
            }
        }
    }];
    
    return flag;
}

+ (void) updateReactNativeLocalPath {
    
    NSString *ldRNFullPath = [LDPatchFileManager getLDReactNativePath];
    if (![[NSFileManager defaultManager] fileExistsAtPath: ldRNFullPath]) {
        
        [LDPatchFileManager createFolderAtPath: ldRNFullPath];
        
        //do not backup
        NSURL *rnRootPath = [NSURL fileURLWithPath: ldRNFullPath];
        [rnRootPath setResourceValue:@YES forKey:NSURLIsExcludedFromBackupKey error:nil];
        
    }
    
    [[LDRNBundleList originBundles] enumerateKeysAndObjectsUsingBlock:^(id  _Nonnull key, id  _Nonnull value, BOOL * _Nonnull stop) {
        
        NSString *bundleName = key;
        
        NSString *bizPath = [ldRNFullPath stringByAppendingPathComponent: bundleName];
        if (![[NSFileManager defaultManager] fileExistsAtPath: bizPath]) {
            
            [LDPatchFileManager createFolderAtPath: bizPath];
        }
        
        [[LDRNBundleList folders] enumerateObjectsUsingBlock:^(id  _Nonnull folder, NSUInteger idx, BOOL * _Nonnull stop) {
            
            NSString *tempPath = [bizPath stringByAppendingPathComponent: folder];
            if (![[NSFileManager defaultManager] fileExistsAtPath: tempPath]) {
                
                [LDPatchFileManager createFolderAtPath: tempPath];
            }
        }];
    }];
}

+ (void) copyLDBundleToReactNativePath {
    
    NSString *ldRNFullPath = [LDPatchFileManager getLDReactNativePath];
    
    [[LDRNBundleList originBundles] enumerateKeysAndObjectsUsingBlock:^(id  _Nonnull key, id  _Nonnull value, BOOL * _Nonnull stop) {
        
        NSString *bundleName = key;
        
        NSString *srcPath = [[NSBundle mainBundle] pathForResource:bundleName ofType:@"zip"];
        NSString *originPath = [NSString stringWithFormat:@"%@/%@/%@", bundleName, [LDRNBundleList originFolder], [NSString stringWithFormat:@"%@.%@", bundleName, @"zip"]];
        NSString *destPath = [ldRNFullPath stringByAppendingPathComponent:originPath];
        
        [LDPatchFileManager clearFilesForPath: [destPath stringByDeletingLastPathComponent]];
        
        NSError *error = nil;
        if (![[NSFileManager defaultManager] copyItemAtPath:srcPath toPath:destPath error: &error]) {
            //error
            return ;
        }
        
        [SSZipArchive unzipFileAtPath:destPath toDestination: [destPath stringByDeletingLastPathComponent]];
        [[NSFileManager defaultManager] removeItemAtPath:destPath error:nil];
    }];
}

+(BOOL) validBundle:(NSString *) bundleName withFile: (NSString *) destPath withHashKey: (NSString *) hashKey {
    
    NSString *md5 = [FileHash md5HashOfFileAtPath: destPath];
    NSArray *bundleList = [[NSUserDefaults standardUserDefaults] objectForKey: [LDRNBundleList rnSaveKey]];
    
    __block NSDictionary *info = nil;
    [bundleList enumerateObjectsUsingBlock:^(NSDictionary * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        
        if ([bundleName isEqualToString: obj[@"moduleName"]] && [obj[hashKey] isEqualToString: md5]) {
            
            info = obj;
            return ;
        }
    }];
    
    return info ? YES : NO;
}

+(void) downloadPatchFile: (NSString *) url withBundle: (NSString *) bundleName {
    
    NSString *savePath = [NSString stringWithFormat:@"%@/%@/%@", [LDPatchFileManager getLDReactNativePath], bundleName, [LDRNBundleList patchFolder]];
    [LDPatchFileManager download:url withSavePath: savePath withBundleName:bundleName];
}

+ (void)download:(NSString *) fileUrl withSavePath: (NSString *) savePath withBundleName: (NSString *) bundleName {
    
    NSURL *url = [NSURL URLWithString: fileUrl];
    
    NSURLSessionConfiguration *config = [NSURLSessionConfiguration defaultSessionConfiguration];
    NSURLSession *session = [NSURLSession sessionWithConfiguration: config];
    NSURLSessionDownloadTask *task = [session downloadTaskWithURL: url completionHandler:^(NSURL * _Nullable location, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        
        if (!error) {
            
            NSArray *flags = [[NSUserDefaults standardUserDefaults] objectForKey:[LDRNBundleList rnDownloadKey]];
            NSMutableArray *downloadFlags = [NSMutableArray arrayWithArray:flags];
            [downloadFlags addObject: bundleName];
            [[NSUserDefaults standardUserDefaults] setObject:downloadFlags forKey: [LDRNBundleList rnDownloadKey]];
            
            
            NSString *destPath = [savePath stringByAppendingPathComponent: response.suggestedFilename];
            
            [LDPatchFileManager clearFilesForPath: savePath];
            
            [[NSFileManager defaultManager] moveItemAtURL:location toURL:[NSURL fileURLWithPath: destPath] error:nil];
            
            //hash compare
            if (![LDPatchFileManager validBundle: bundleName withFile: destPath withHashKey:@"zipHash"]) {
                return ;
            }
            
            [SSZipArchive unzipFileAtPath:destPath toDestination: [destPath stringByDeletingLastPathComponent]];
            [[NSFileManager defaultManager] removeItemAtPath: destPath error: nil];
            
            [LDPatchFileManager mergePatchFile: bundleName];
        }
        
    }];
    
    [task resume];
}

+ (void) reloadAllJSPatch {
    
    NSString *ldRNFullPath = [LDPatchFileManager getLDReactNativePath];
    if (![[NSFileManager defaultManager] fileExistsAtPath: ldRNFullPath]) {
        
        [LDPatchFileManager copyLDBundleToReactNativePath];
        return;
    }
    
    [[LDRNBundleList originBundles] enumerateKeysAndObjectsUsingBlock:^(id  _Nonnull key, id  _Nonnull value, BOOL * _Nonnull stop) {
        
        [LDPatchFileManager moveMergeFilesToIndex: key];
    }];
}

+ (void) mergePatchFile: (NSString *) bundleName {
    
    NSString *absoluteRoot = [[LDPatchFileManager getLDReactNativePath] stringByAppendingPathComponent: bundleName];
    
    NSString *originfile = [NSString stringWithFormat:@"%@/%@/%@", absoluteRoot, [LDRNBundleList originFolder], [LDRNBundleList entryJSName]];
    NSString *patchfile = [NSString stringWithFormat:@"%@/%@/%@", absoluteRoot, [LDRNBundleList patchFolder], [LDRNBundleList patchFileName]];
    NSString *destfile = [NSString stringWithFormat:@"%@/%@/%@", absoluteRoot, [LDRNBundleList mergeFolder], [LDRNBundleList entryJSName]];
    
    int ret = bspatch(originfile.UTF8String, destfile.UTF8String, patchfile.UTF8String);
    if (ret == -1) {
        //error
        [LDPatchFileManager clearFilesForPath: [destfile stringByDeletingLastPathComponent]];
        return;
    }
    
    [[NSFileManager defaultManager] removeItemAtPath:patchfile error:nil];
    if (![LDPatchFileManager moveItems:[patchfile stringByDeletingLastPathComponent] toPath:[destfile stringByDeletingLastPathComponent]]) {
        //error
        [LDPatchFileManager clearFilesForPath: [patchfile stringByDeletingLastPathComponent]];
        [LDPatchFileManager clearFilesForPath: [destfile stringByDeletingLastPathComponent]];
        return;
    }
    
    if (![LDPatchFileManager validBundle: bundleName withFile:destfile withHashKey:@"jsbundleHash"]) {
        
        [LDPatchFileManager clearFilesForPath: [patchfile stringByDeletingLastPathComponent]];
        [LDPatchFileManager clearFilesForPath: [destfile stringByDeletingLastPathComponent]];
        return;
    }
}

+ (void) moveMergeFilesToIndex: (NSString *) bundleName {
    
    NSString *absoluteRoot = [[LDPatchFileManager getLDReactNativePath] stringByAppendingPathComponent: bundleName];
    
    NSString *mergePath = [NSString stringWithFormat:@"%@/%@", absoluteRoot, [LDRNBundleList mergeFolder]];
    NSString *indexPath = [NSString stringWithFormat:@"%@/%@", absoluteRoot, [LDRNBundleList indexFolder]];
    
    NSString *entryJSfile = [mergePath stringByAppendingPathComponent: [LDRNBundleList entryJSName]];
    if (![[NSFileManager defaultManager] fileExistsAtPath: entryJSfile]) {
        //not fount
        return ;
    }
    
    [LDPatchFileManager clearFilesForPath: indexPath];
    [LDPatchFileManager moveItems:mergePath toPath:indexPath];
}

+ (NSString *) jsbundleLocation: (NSString *) bundleName {
    
    NSString *absoluteRoot = [[LDPatchFileManager getLDReactNativePath] stringByAppendingPathComponent: bundleName];
    
    NSString *originfile = [NSString stringWithFormat:@"%@/%@/%@", absoluteRoot, [LDRNBundleList originFolder], [LDRNBundleList entryJSName]];
    NSString *indexfile = [NSString stringWithFormat:@"%@/%@/%@", absoluteRoot, [LDRNBundleList indexFolder], [LDRNBundleList entryJSName]];
    
    if ([[NSFileManager defaultManager] fileExistsAtPath: indexfile] ||
        [LDPatchFileManager validBundle: bundleName withFile: indexfile withHashKey:@"jsbundleHash"]) {
        return indexfile;
    }
    
    if ([[NSFileManager defaultManager] fileExistsAtPath: originfile]) {
        
        if ([[LDRNBundleList originBundlesHash] count] < 1 ||
            [[[LDRNBundleList originBundlesHash] objectForKey:@"bundleName"] isEqualToString: [FileHash md5HashOfFileAtPath: originfile]]) {
            
            return originfile;
        }
    }
    
    [LDPatchFileManager copyLDBundleToReactNativePath];
    return originfile;
}


@end
