package com.leadeon.diffupdate.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * Created by Lynn on 2017/8/17.
 */

public class FileMd5Utils {

    static char hexdigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    /**
     * md5文件生成摘要
     *
     * @param filePath
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String MD5File(String filePath) {
        File file=new File(filePath);
        FileInputStream fis = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            byte[] buffer = new byte[2048];
            int length = -1;
            while ((length = fis.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }
            // 32位加密
            byte[] b = md.digest();
            return byteToHexString(b);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    /**
     * @param tmp
     * @return
     * @see [类、类#方法、类#成员]
     */
    private static String byteToHexString(byte[] tmp) {
        String s;
        // 用字节表示就是 16 个字节
        char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
        // 所以表示成 16 进制需要 32 个字符
        // 表示转换结果中对应的字符位置
        int k = 0;
        for (int i = 0; i < 16; i++) {
            // 从第一个字节开始，对 MD5 的每一个字节
            // 转换成 16 进制字符的转换
            // 取第 i 个字节
            byte byte0 = tmp[i];
            // 取字节中高 4 位的数字转换,
            str[k++] = hexdigits[byte0 >>> 4 & 0xf];
            // >>> 为逻辑右移，将符号位一起右移
            // 取字节中低 4 位的数字转换
            str[k++] = hexdigits[byte0 & 0xf];
        }
        // 换后的结果转换为字符串
        s = new String(str);
        return s;
    }




}
