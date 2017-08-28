package com.yyh.lib.bsdiff.utils;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtil {

    private static final boolean DEBUG = true;

    public static boolean copyFile(String source, String dest) {
        try {
            return copyFile(new FileInputStream(new File(source)), dest);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean copyFile(final InputStream inputStream, String dest) {
        FileOutputStream oputStream = null;
        try {
            File destFile = new File(dest);
            destFile.getParentFile().mkdirs();
            destFile.createNewFile();

            oputStream = new FileOutputStream(destFile);
            byte[] bb = new byte[10 * 1024];
            int len = 0;
            while ((len = inputStream.read(bb)) != -1) {
                oputStream.write(bb, 0, len);
            }
            oputStream.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (oputStream != null) {
                try {
                    oputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    public static synchronized void unZipAll(String zipFile, String aimdir) {
        File tempDir=new File(aimdir);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        ZipFile zfile = null;
        BufferedOutputStream fos = null;
        BufferedInputStream bis = null;

        LogUtils.writeLog("开始解压");
        try {
            zfile = new ZipFile(zipFile);
            ZipEntry ze = null;
            Enumeration zList = zfile.entries();
            while (zList.hasMoreElements()) {
                ze = (ZipEntry) zList.nextElement();
                String relativePath = ze.getName();

                if (ze.isDirectory()) {
                    File folder = new File(tempDir, relativePath);
                    if (DEBUG) {
                        LogUtils.writeLog("isDirectory :  "
                                + folder.getAbsolutePath());
                    }
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }

                } else {
                    File targetFile = new File(tempDir, relativePath);
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    targetFile.createNewFile();

                    fos = new BufferedOutputStream(new FileOutputStream(
                            targetFile));
                    bis = new BufferedInputStream(zfile.getInputStream(ze));
                    byte[] buffer = new byte[2 * 1024];
                    int count = -1;
                    while ((count = bis.read(buffer)) != -1) {
                        fos.write(buffer, 0, count);
                        fos.flush();
                    }
                    fos.close();
                    fos = null;
                    bis.close();
                    bis = null;

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.writeLog("解压出现异常");
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (zfile != null) {
                try {
                    zfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static synchronized void unZipRes(String zipFile, String aimdir) {
        File tempDir=new File(aimdir);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        ZipFile zfile = null;
        BufferedOutputStream fos = null;
        BufferedInputStream bis = null;

        LogUtils.writeLog("开始解压");
        try {
            zfile = new ZipFile(zipFile);
            ZipEntry ze = null;
            Enumeration zList = zfile.entries();
            while (zList.hasMoreElements()) {
                ze = (ZipEntry) zList.nextElement();
                String relativePath = ze.getName();

                if (relativePath.startsWith("index")) {
                    if (DEBUG) {
                        LogUtils.writeLog("relativePath :  " + relativePath);
                    }
                    continue;
                }

                if (ze.isDirectory()) {
                    File folder = new File(tempDir, relativePath);
                    if (DEBUG) {
                        LogUtils.writeLog("isDirectory :  "
                                + folder.getAbsolutePath());
                    }
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }

                } else {
                    File targetFile = new File(tempDir, relativePath);
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    targetFile.createNewFile();

                    fos = new BufferedOutputStream(new FileOutputStream(
                            targetFile));
                    bis = new BufferedInputStream(zfile.getInputStream(ze));
                    byte[] buffer = new byte[2 * 1024];
                    int count = -1;
                    while ((count = bis.read(buffer)) != -1) {
                        fos.write(buffer, 0, count);
                        fos.flush();
                    }
                    fos.close();
                    fos = null;
                    bis.close();
                    bis = null;

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.writeLog("解压出现异常");
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (zfile != null) {
                try {
                    zfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static synchronized void unZipPatch(String apkFile, String aimdir) {
        File tempDir=new File(aimdir);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        ZipFile zfile = null;
        BufferedOutputStream fos = null;
        BufferedInputStream bis = null;

        LogUtils.writeLog("开始解压");
        try {
            zfile = new ZipFile(apkFile);
            ZipEntry ze = null;
            Enumeration zList = zfile.entries();
            while (zList.hasMoreElements()) {
                ze = (ZipEntry) zList.nextElement();
                String relativePath = ze.getName();
                if (!ze.isDirectory()) {
                    if (relativePath.endsWith("patch")) {
                        File targetFile = new File(tempDir, relativePath);

                        if (!targetFile.getParentFile().exists()) {
                            targetFile.getParentFile().mkdirs();
                        }
                        targetFile.createNewFile();

                        fos = new BufferedOutputStream(new FileOutputStream(
                                targetFile));
                        bis = new BufferedInputStream(zfile.getInputStream(ze));
                        byte[] buffer = new byte[2 * 1024];
                        int count = -1;
                        while ((count = bis.read(buffer)) != -1) {
                            fos.write(buffer, 0, count);
                            fos.flush();
                        }
                        fos.close();
                        fos = null;
                        bis.close();
                        bis = null;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

            LogUtils.writeLog("解压出现异常");
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (zfile != null) {
                try {
                    zfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    /**
     * 从文本文件中读取文本
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static String readFile(String path) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        File file = new File(path);
        if (file.exists()) {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader reader = new BufferedReader(inStreamReader);
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line);
            }
            reader.close();
            inStreamReader.close();
        }
        return contentBuilder.toString().trim();
    }


    /**
     *  向文本文件中写入文本
     * @param path
     * @return
     * @throws IOException
     */
    public static void writeFile(String path, String content) throws IOException {
        if (!TextUtils.isEmpty(content)) {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            } else {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
        }
    }


    /**
     * 递归删除文件及文件夹
     * @param path
     * @return
     */
    public static boolean deleteAll(String path) {
        File file=new File(path);
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles != null && childFiles.length > 0) {
                for (int i = 0; i < childFiles.length; i++) {
                    deleteAll(childFiles[i].getAbsolutePath());
                }
            }
        }
        return file.delete();
    }
}
