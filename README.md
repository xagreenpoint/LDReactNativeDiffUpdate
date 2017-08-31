# LDReactNativeDiffUpdate
React native增量更新，支持多入口多业务。

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


#### iOS使用方式

首先进行相关配置，配置信息在`LDRNBundleList.m`文件中，核心设置有：

```json
appKey				    //应用唯一标识
rnVersion 				//当前工程使用的React Native版本
originBundles 			    //工程内置的业务bundle
originBundlesHash		    //业务bundle的md5 (可选)
entryJSName				//bundle入口文件名称
patchFileName			    //补丁bundle入口文件名称
versionUrl				//版本请求接口地址
```

配置好信息后，在自己的需要加载bundle的`viewController`中引入头文件`#import "LDRNDiffUpdate.h"`，然后指定jsBundle文件名即可（每个jsBundle代表一个独立的业务）：

```OC
NSURL *jsCodeLocation = [LDRNDiffUpdate jsBundleUrl:@"LDBusinessEntry"];
```

#### android相关方式

在应用启动时启动RnModuleDiffUpdateService这个service,当应用关闭时请停止此service。

## 版本请求接口说明

每次启动app时，检测本地所有jsBundle是否有更新，如果有更新则根据下载条件浸没下载，再根据加载策略进行实时更新还是下次启动更新RN业务。也可以对有问题的jsBundle业务进行回滚操作。

```js
接口名称[POST]：/RN/patchVersion

请求body:
{
appKey: "",                   //app唯一标识
appVersion: "",               //app当前版本
rnVersion: "",                //react native集成版本
resBody: { 
        "LDBizName1": "1.2",  //业务名称: 版本号
        "LDBizName2": "1.1", 
        ......
    } 
}

相应body:

```json
{
    retCode: '000000',      //响应码：000000代表成功，其他代表失败
    retDesc: 'xxx',         //失败原因描述
    rspBody: {
        patchs:[   
            { 
                "zipPath": "https://xx.xx.com/patchzip/LDBizModuleName1.zip",   //下载路径
                "version": "1.3",                                               //业务版本号
                "moduleName": "LDBizName1",                                     //jsBundle名称
                "zipHash": "xxxxxx",                                            //zip文件md5值
                "jsbundleHash": "xxxxxx",                                       //差异合并后js文件md5值
                "downloadNow": "3",                                             //0：总是下载, 1:wifi下载，2: 4g和wifi下载
                "uploadNow": "true",                                            //true:即刻更新，false:下次启动更新
                "needGoBack": "false",                                          //是否需要回退版本
            },

            ......        
          ]
    }
}

```

## 安全策略

增量更新安全主要涉及到jsBundle，jsBundle只是js代码，虽然经过混淆处理，但依然是纯明文，虽然可以对js做加密处理，但这种行为不太明智。

主要考虑以下安全因素：

1. 网络接口安全。这点其实和增量更新关系不大，版本接口复用原有后台安全策略即可。

2. jsBundle篡改安全。为防止篡改js入侵app业务，需对jsBundle做签名校验，一是下载文件后校验其完整性，二是每次加载jsBundle时校验，这样就防止了整条链路篡改风险。

3. jsBundle业务安全。因为jsBundle是明文，所以业务中需要进行加解密等敏感措施就不能在js侧实现，解决的办法是native实现加解密暴露给js接口调用。



