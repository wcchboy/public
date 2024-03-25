package com.wcch.android.utils;

public class ByteUtil {
    private static int hexChar2byte(char c) {
        switch (c) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            default:
                switch (c) {
                    case 'A':
                        return 10;
                    case 'B':
                        return 11;
                    case 'C':
                        return 12;
                    case 'D':
                        return 13;
                    case 'E':
                        return 14;
                    case 'F':
                        return 15;
                    default:
                        switch (c) {
                            case 'a':
                                return 10;
                            case 'b':
                                return 11;
                            case 'c':
                                return 12;
                            case 'd':
                                return 13;
                            case 'e':
                                return 14;
                            case 'f':
                                return 15;
                            default:
                                return -1;
                        }
                }
        }
    }

    public static String bytes2HexStr(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        if (bArr == null || bArr.length <= 0) {
            return "";
        }
        char[] cArr = new char[2];
        for (int i = 0; i < bArr.length; i++) {
            cArr[0] = Character.forDigit((bArr[i] >>> 4) & 15, 16);
            cArr[1] = Character.forDigit(bArr[i] & 15, 16);
            sb.append(cArr);
        }
        return sb.toString().toUpperCase();
    }

    public static String bytes2HexStr(byte[] bArr, int i, int i2) {
        byte[] bArr2 = new byte[i2];
        System.arraycopy(bArr, i, bArr2, 0, i2);
        return bytes2HexStr(bArr2);
    }

    public static long hexStr2decimal(String str) {
        return Long.parseLong(str, 16);
    }

    public static String decimal2fitHex(long j) {
        String upperCase = Long.toHexString(j).toUpperCase();
        if (upperCase.length() % 2 == 0) {
            return upperCase.toUpperCase();
        }
        return "0" + upperCase;
    }

    public static String decimal2fitHex(long j, int i) {
        StringBuilder sb = new StringBuilder(decimal2fitHex(j));
        while (sb.length() < i) {
            sb.insert(0, '0');
        }
        return sb.toString();
    }

    public static String fitDecimalStr(int i, int i2) {
        StringBuilder sb = new StringBuilder(String.valueOf(i));
        while (sb.length() < i2) {
            sb.insert(0, "0");
        }
        return sb.toString();
    }

    public static String str2HexString(String str) {
        byte[] bArr;
        char[] charArray = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder();
        try {
            bArr = str.getBytes("utf8");
        } catch (Exception e) {
            e.printStackTrace();
            bArr = null;
        }
        for (int i = 0; i < bArr.length; i++) {
            sb.append(charArray[(bArr[i] & 240) >> 4]);
            sb.append(charArray[bArr[i] & 15]);
        }
        return sb.toString();
    }

    public static byte[] hexStr2bytes(String str) {
        int length = str.length() / 2;
        byte[] bArr = new byte[length];
        char[] charArray = str.toUpperCase().toCharArray();
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            bArr[i] = (byte) (hexChar2byte(charArray[i2 + 1]) | (hexChar2byte(charArray[i2]) << 4));
        }
        return bArr;
    }
}
