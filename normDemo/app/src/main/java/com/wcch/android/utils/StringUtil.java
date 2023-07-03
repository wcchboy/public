package com.wcch.android.utils;

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

    public static int bytes2Int32Be(byte[] bytes, int offset) {
        int addr = 0;
        addr = bytes[offset + 0] & 0xFF;
        addr = (addr << 8) + (bytes[offset + 1] & 0xFF);
        addr = (addr << 8) + (bytes[offset + 2] & 0xFF);
        addr = (addr << 8) + (bytes[offset + 3] & 0xFF);
        return addr;
    }
    /**
     * int到byte[]
     * @param i
     * @return
     */
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        // 由高位到低位
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }
}
