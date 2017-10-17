# LDReactNativeDiffUpdate
React native增量更新，支持多入口多业务。

#### 开源的目的

目前业界还没有一款足够适应大部分增量更新场景的方案，要么不支持多业务入口，要么没有开源，要么没有开放后台源码。基于这些需求，我们想通过开源社区的力量，大家一起打造出比较完善易用的增量更新框架，因此，需要大家的参与及支持，为RN的生态系统做点儿小贡献（为了项目的灵活自由发展，我们采用了MIT许可）。

* QQ交流群：**539533937**  <a target="_blank" href="//shang.qq.com/wpa/qunwpa?idkey=310cb3000d666c23a0f3757a9d2a3548f0a83aecf2121a214790f96718f07157"><img border="0" src="http://pub.idqqimg.com/wpa/images/group.png" alt="ReactNative热更新" title="ReactNative热更新"></a> 


![](./doc/image/ReactNativeDiffUpdate.png)



## 增量更新实现机制

目前增量更新采用native实现，后续会暴露功能接口，供js控制更新流程。

为了更好的用户体验，所有业务包进行工程内置，增量包后台下载合并操作，App下次启动更新生效（后续增加立即生效功能）。

增量算法采用bsdiff差异算法。

## 安装和使用

客户端与后台相对独立，可集成客户端增量更新模块，自己构建后台版本接口，也可采用我们后台框架搭建（暂时还未开源）。

#### 安装增量更新

工程根目录执行：

`npm install --save react-native-diff-update`

`react-native link react-native-diff-update`

打开工程可以看到`Libraries`下`LDRNDiffUpdate`就是增量更新模块。

##### iOS Pod集成方式：

`npm install --save react-native-diff-update`

在工程`Podfile`文件中加入：

`pod 'LDRNDiffUpdate', :path => '../node_modules/react-native-diff-update'`

然后执行：`pod install`

##### SSZipArchive库冲突问题

假如原有工程已经存在SSZipArchive库，与本pod spec冲突，建议采用`v1.0.5`版本，此版本没有将SSZipArchive进行分离，因此更容易处理冲突问题，install时增加版本号：

`npm install --save react-native-diff-update@v1.0.5`

这样集成到工程中时，只需要把原有工程SSZipArchive删除即可。至于SSZipArchive工程分离方式，目前没有找到比较好的方法，可以参见`code push`的处理方式也是这样。

<https://github.com/Microsoft/react-native-code-push/issues/241>

#### iOS使用方式

首先进行相关配置，配置信息在`LDRNBundleList.m`文件中，核心设置有：

```json
appKey                  //应用唯一标识
rnVersion               //当前工程使用的React Native版本
originBundles           //工程内置的业务bundle
originBundlesHash       //业务bundle的md5 (可选)
entryJSName             //bundle入口文件名称
patchFileName           //补丁bundle入口文件名称
versionUrl              //版本请求接口地址
```

配置信息不应该直接修改`LDRNBundleList.m`源文件，更合理的方式是创建`LDRNBundleList的分类`，当然只需设置需要项:

```objective-c
@implementation LDRNBundleList (setting)

+(NSString *) appKey {
    return @"11111111";
}

+(NSString *) rnVersion {
    return @"0.48.3";
}

+(NSDictionary *) originBundles {
    return @{
                //首次集成置空
             };
}

@end
```

> 注意首次集成时originBundles设置为空字典，否则在工程中找不到zip文件，会引起程序崩溃。等增加zip文件后再进行配置。

配置好信息后，在自己的需要加载bundle的`viewController`中引入头文件`#import "LDRNDiffUpdate.h"`，然后指定jsBundle文件名即可（每个jsBundle代表一个独立的业务）：

```OC
NSURL *jsCodeLocation = [LDRNDiffUpdate jsBundleUrl:@"LDBusinessEntry"];
```

#### android使用方式

在Android中有时候npm 的link会不成功，这时可通过如下步骤进行配置：

找到 android/settings.gradle文件并添加:

```java
include ':react-native-diff-update'
project(':react-native-diff-update').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-diff-update/android')
```

找到 android/app/build.gradle 文件并添加:

```java
dependencies {
    compile project(':react-native-diff-update')
}
```

配置完后需要在应用启动的第一个Activity的onCreate方法中调用

```java
LeadeonDiff.init(this, this);
```

> 第一个参数是应用程序的上下文，不能为null,第二个参数是CopyCompletedCallback接口的实现，当首次复制完assets目录下的rn模块后会回调此接口。



## 版本请求接口说明

每次启动app时，检测本地所有jsBundle是否有更新，如果有更新则根据下载条件浸没下载，再根据加载策略进行实时更新还是下次启动更新RN业务。也可以对有问题的jsBundle业务进行回滚操作。

```js
接口名称[POST]：/RN/patchVersion

请求body:
{
appKey: "",                   //app唯一标识
appVersion: "",               //app当前版本
rnVersion: "",                //react native集成版本
platform: "",                 //平台ios android
resBody: { 
        "LDBizName1": "1.2.0",  //业务名称: 版本号
        "LDBizName2": "1.1.0", 
        ...
    } 
}

响应body:

```json
{
    retCode: '000000',      //响应码：000000代表成功，其他代表失败
    retDesc: 'xxx',         //失败原因描述
    rspBody: {
        patchs:[   
            { 
                "loadType": "ReactNative",                                       //业务类型：ReactNative、HybridApp
                "zipPath": "https://xx.xx.com/patchzip/LDBizModuleName1.zip",   //下载路径
                "version": "1.3.0",                                               //业务版本号
                "moduleName": "LDBizName1",                                     //jsBundle名称
                "zipHash": "xxxxxx",                                            //zip文件md5值
                "jsbundleHash": "xxxxxx",                                       //差异合并后js文件md5值
                "downloadNow": "3",                                             //0：总是下载, 1:wifi下载，2: 4g和wifi下载
                "loadNow": "true",                                              //true:即刻更新，false:下次启动更新
                "needGoBack": "false",                                          //是否需要回退版本
            },

            ...
          ]
    }
}

```

## 安全策略

增量更新安全主要涉及到jsBundle，jsBundle只是js代码，虽然经过混淆处理，但依然是纯明文，虽然可以对js做加密处理，但这种行为不太明智。

主要考虑以下安全因素：

1. 网络接口安全。这点其实和增量更新关系不大，版本接口复用原有后台安全策略即可。

2. jsBundle篡改安全。为防止篡改js入侵app业务，需对jsBundle做签名校验，一是下载文件后校验其完整性，二是每次加载jsBundle时校验，这样就防止了整条链路篡改风险。

3. jsBundle业务安全。因为jsBundle是明文，所以业务中需要进行加解密等敏感措施就不能在js侧实现，解决的办法是native实现加解密暴露给js接口调用。加解密可作为RN独立module实现，不在增量更新体现。


## Licence

(MIT)

