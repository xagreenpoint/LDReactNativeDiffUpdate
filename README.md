# LDReactNativeDiffUpdate
React native增量更新，支持多入口多业务。

## 增量更新实现机制

目前增量更新采用native实现，后续会暴露功能接口，供js控制更新流程。

为了更好的用户体验，所有业务包进行工程内置，增量包后台下载合并操作，App下次启动更新生效（后续增加立即生效功能）。

增量算法采用bsdiff差异算法。

## 安装和使用

#### iOS安装增量更新

工程根目录执行：

`npm install --save react-native-diff-update`
`react-native link react-native-DebugServerHost`

打开工程可以看到`LLibraries`下`LDRNDiffUpdate`就是增量更新模块。


#### iOS使用

首先进行相关配置，配置信息在`LDRNBundleList.m`文件中，核心设置有：

```
appKey   				//应用唯一标识
rnVersion 				//当前工程使用的React Native版本
originBundles 			//工程内置的业务bundle
originBundlesHash		//业务bundle的md5 (可选)
entryJSName				//bundle入口文件名称
patchFileName			//补丁bundle入口文件名称
versionUrl				//版本请求接口地址
```

配置好信息后，在自己的需要加载bundle的`viewController`中引入头文件`#import "LDRNDiffUpdate.h"`，然后指定jsBundle文件名即可：

```OC
NSURL *jsCodeLocation = [LDRNDiffUpdate jsBundleUrl:@"LDBusinessEntry"];
```


#### android安装增量更新

#### android相关配置

## 接口说明

#### 版本请求接口

## 安全



