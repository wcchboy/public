package com.wcch.android.utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * serialport
 * <p>
 * Created by RyanWang on 2022/10/31
 * Copyright © 2022年 IGRS. All rights reserved.
 * <p>
 * Describe:
 */
public class StringUtil {
    public static boolean isNotEmpty(String s){
        if(s == null || s.isEmpty()){
            return false;
        }
        return true;
    }
    public boolean isEmpty(String s){
        return !isNotEmpty(s);
    }
    /**
     * Return whether the string is null or white space.
     *
     * @param s The string.
     * @return {@code true}: yes<br> {@code false}: no
     */
    public static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static final char[] HEX_DIGITS_UPPER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final char[] HEX_DIGITS_LOWER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    /**
     * Bytes to hex string.
     * <p>e.g. bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns "00A8"</p>
     *
     * @param bytes The bytes.
     * @return hex string
     */
    public static String bytes2HexString(final byte[] bytes) {
        return bytes2HexString(bytes, true);
    }

    /**
     * Bytes to hex string.
     * <p>e.g. bytes2HexString(new byte[] { 0, (byte) 0xa8 }, true) returns "00A8"</p>
     *
     * @param bytes       The bytes.
     * @param isUpperCase True to use upper case, false otherwise.
     * @return hex string
     */
    public static String bytes2HexString(final byte[] bytes, boolean isUpperCase) {
        if (bytes == null) return "";
        char[] hexDigits = isUpperCase ? HEX_DIGITS_UPPER : HEX_DIGITS_LOWER;
        int len = bytes.length;
        if (len <= 0) return "";
        char[] ret = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = hexDigits[bytes[i] >> 4 & 0x0f];
            ret[j++] = hexDigits[bytes[i] & 0x0f];
        }
        return new String(ret);
    }

    /**
     * 打印串口信息-22一组分开
     *
     * @param bytes 出入的byte数组
     * @return 返回一个22一组的string
     */
    public static String bytes2hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String tmp;
        for (byte b : bytes) {
            // 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1) {
                //只有一位的前面补个0
                tmp = "0" + tmp;
            }
            //每个字节用空格断开
            //sb.append(tmp).append(" ");
            sb.append(tmp);
        }
        //删除最后一个字节后面对于的空格
        //sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }
    /**
     * 返回对象的唯一标识符
     * @param obj 需要取得唯一标识符的对象
     * @return className@hashcode 形式的唯一标识符。
     */
    public static String identityToString(Object obj){
        return obj.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(obj));
    }
    public static String getNowTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss z");
        return sdf.format(new Date());
    }
    public static int getIntByByte(byte pd,int i){
        return (pd & 0x1f);
    }

    public static String replaceByPosition(String src, int start, int length, String target)
    {
        //替换指定字串
        String begin = src.substring(0, start);
        String tail = src.substring(start+length);
        return begin + target + tail;
    }

    public static String getStringFilledBy(int len, char c)
    {
        char[] chars = new char[len];
        Arrays.fill(chars, c);
        String str = new String(chars);
        return str;
    }
}
