package com.wcch.android.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import it.sauronsoftware.base64.Base64;

public class Base64Utils {
    private static final int CACHE_SIZE = 1024;

    public static byte[] decode(String str) throws Exception {
        return Base64.decode(str.getBytes());
    }

    public static String encode(byte[] bArr) throws Exception {
        return new String(Base64.encode(bArr));
    }

    public static String encodeFile(String str) throws Exception {
        return encode(fileToByte(str));
    }

    public static void decodeToFile(String str, String str2) throws Exception {
        byteArrayToFile(decode(str2), str);
    }

    public static byte[] fileToByte(String str) throws Exception {
        byte[] bArr = new byte[0];
        File file = new File(str);
        if (!file.exists()) {
            return bArr;
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(2048);
        byte[] bArr2 = new byte[1024];
        while (true) {
            int read = fileInputStream.read(bArr2);
            if (read != -1) {
                byteArrayOutputStream.write(bArr2, 0, read);
                byteArrayOutputStream.flush();
            } else {
                byteArrayOutputStream.close();
                fileInputStream.close();
                return byteArrayOutputStream.toByteArray();
            }
        }
    }

    public static void byteArrayToFile(byte[] bArr, String str) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
        File file = new File(str);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte[] bArr2 = new byte[1024];
        while (true) {
            int read = byteArrayInputStream.read(bArr2);
            if (read != -1) {
                fileOutputStream.write(bArr2, 0, read);
                fileOutputStream.flush();
            } else {
                fileOutputStream.close();
                byteArrayInputStream.close();
                return;
            }
        }
    }
}
