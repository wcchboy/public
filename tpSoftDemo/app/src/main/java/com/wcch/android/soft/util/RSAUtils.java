package com.wcch.android.soft.util;

import android.content.Context;

import com.wcch.android.utils.Base64Utils;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSAUtils {

    /**RSA算法*/
    public static final String RSA = "RSA";
    /**加密方式，android的*/
    //public static final String TRANSFORMATION = "RSA/None/NoPadding";
    /**加密方式，标准jdk的*/
    public static final String TRANSFORMATION = "RSA/None/PKCS1Padding";

    private static String PUB_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDegLmF7tIxTcqlogaH4Skl3V499rSQZynN0BMlnocAb/rkSzScyHn3dcfjMjXYtxRYx1csweemi0J2hpx6yP3UHbUZyTzfS887oR9QS3s1GjFPYLEH7gThVWhNxtoo+K1l8iXltyjH0+o/TgT8xv2MVrYYO0FV5x/8vhm/ruVtjwIDAQAB";



    public static String getAuthorization(Context ctx) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String str = String.format("%sigrs%s", dateFormat.format(date), ctx.getPackageName());
        String authorization = null;
        try {
            authorization = encrypt(str, PUB_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return authorization;
    }

    private static String encrypt(String str, String publicKey) throws Exception {
        //base64编码的公钥
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }


//
//    public static String getAuthorization(Context ctx) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
//        String str = String.format("%sigrs%s", dateFormat.format(new Date()), ctx.getPackageName());
//        L.i("data:"+str);
//        String authorization = null;
//        try {
//            authorization = encrypt(str, PUB_KEY);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return authorization;
//    }
//
//    public static String encrypt(String str, String publicKey) throws Exception {
//        //base64编码的公钥
//        byte[] decoded = android.util.Base64.decode(publicKey, android.util.Base64.DEFAULT);
//        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
//        //RSA加密
//        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
//        byte[] result = cipher.doFinal(str.getBytes());
//        return android.util.Base64.encodeToString(result, android.util.Base64.DEFAULT);
//    }
    /**
     * RSA私钥解密
     *
     * @param str        加密字符串
     * @param publicKey 私钥
     * @return 铭文
     * @throws Exception 解密过程中的异常信息
     */
    public static String decrypt(String str, String publicKey) throws Exception {
//        //64位解码加密后的字符串
        //byte[] inputByte = Base64.decodeBase64(str.getBytes());
        //byte[] inputByte = Base64.decodeBase64(str.getBytes());
        byte[] inputByte = str.getBytes();

        //base64编码的公钥
        byte[] decoded = android.util.Base64.decode(publicKey, android.util.Base64.DEFAULT);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));

        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        //Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, pubKey);
        byte[] result = cipher.doFinal(str.getBytes());
        // return new String(result);
        return android.util.Base64.encodeToString(result, android.util.Base64.DEFAULT);
        //return Base64.encodeBase64String(result);
    }

    /** 使用公钥加密 */
    public static byte[] encByPublicKey(byte[] data, byte[] publicKey) throws Exception {
        // 得到公钥对象
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        // 加密数据
        Cipher cp = Cipher.getInstance(TRANSFORMATION);
        cp.init(Cipher.ENCRYPT_MODE, pubKey);
        return cp.doFinal(data);
    }
    /** 使用公钥加密 */
    public static byte[] decByPublicKey(byte[] data, byte[] publicKey) throws Exception {
        // 得到公钥对象
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        // 加密数据
        Cipher cp = Cipher.getInstance(TRANSFORMATION);
        cp.init(Cipher.DECRYPT_MODE, pubKey);
        return cp.doFinal(data);
    }


    /** 使用私钥解密 */
    public static byte[] decryptByPrivateKey(byte[] encrypted, byte[] privateKey) throws Exception {
        // 得到私钥对象
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory kf = KeyFactory.getInstance(RSA);
        PrivateKey keyPrivate = kf.generatePrivate(keySpec);
        // 解密数据
        Cipher cp = Cipher.getInstance(TRANSFORMATION);
        cp.init(Cipher.DECRYPT_MODE, keyPrivate);
        byte[] arr = cp.doFinal(encrypted);
        return arr;
    }
    /** 生成密钥对，即公钥和私钥。key长度是512-2048，一般为1024 */
    public static KeyPair generateRSAKeyPair(int keyLength) throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
        kpg.initialize(keyLength);
        return kpg.genKeyPair();
    }

    /** 获取公钥，打印为48-12613448136942-12272-122-913111503-126115048-12...等等一长串用-拼接的数字 */
    public static byte[] getPublicKey(KeyPair keyPair) {
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        return rsaPublicKey.getEncoded();
    }

    /** 获取私钥，同上 */
    public static byte[] getPrivateKey(KeyPair keyPair) {
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        return rsaPrivateKey.getEncoded();
    }




    public static void main(String[] args) {


    }

    public static String getRSAPublicString (byte[] publickey, String str) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        //公钥加密
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publickey);
//        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64Utils.decode("MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKZMDp6b33uFPbZ8BsuutyI/PrMUejgYynY6ikQQnuTVUBLO09pY0+CQr3iBl3XNLGZ3+RiDfVYMrvA1vTnlO5cCAwEAAQ=="));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] result = cipher.doFinal(str.getBytes());
//        System.out.println("Public Key : " + publickey);
//        System.out.println("公钥加密，私钥解密 --加密: " +Base64Utils.encode(result));
        return Base64Utils.encode(result);
    }


}
