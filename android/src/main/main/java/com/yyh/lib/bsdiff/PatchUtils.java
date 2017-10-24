package com.yyh.lib.bsdiff;

import com.leadeon.diffupdate.utils.LogUtils;
import com.leadeon.diffupdate.utils.RNFilePathUtils;

import java.io.File;
import java.io.IOException;

/**
 * APK Patch工具类
 * 
 * @author yuyuhang
 * @date 2016-1-26 下午1:10:40
 */
public class PatchUtils {

	private static PatchUtils instance;

	public static PatchUtils getInstance() {
		if (instance == null)
			instance = new PatchUtils();
		return instance;
	}

	static {
		System.loadLibrary("ApkPatchLibrary");
	}

	public synchronized int mergePatch(String oldbundlePath, String newbundlePath, String patchPath){

		File destFile = new File(newbundlePath);

		destFile.getParentFile().mkdirs();
		try {
			LogUtils.writeLog("创建merage文件");
			if(destFile.exists()){
				destFile.delete();
			}
			destFile.createNewFile();
			LogUtils.writeLog("创建merage文件夹成功");
		} catch (IOException e) {
			e.printStackTrace();
			LogUtils.writeLog("创建merage文件夹失败"+e.getMessage());
		}
		return patch(oldbundlePath,newbundlePath,patchPath);
	}



	/**
	 * native方法 使用路径为oldApkPath的apk与路径为patchPath的补丁包，合成新的apk，并存储于newApkPath
	 * 返回：0，说明操作成功
	 * @param oldbundlePath
	 *            示例:/sdcard/old.apk
	 * @param newbundlePath
	 *            示例:/sdcard/new.apk
	 * @param patchPath
	 *            示例:/sdcard/xx.patch
	 * @return
	 */
	public native int patch(String oldbundlePath, String newbundlePath, String patchPath);
}