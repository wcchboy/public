package com.wcch.serialport.utils;

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
}
