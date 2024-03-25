package com.wcch.android.utils;

import com.bumptech.glide.load.Key;

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSAUtils {
    public static final String KEY_ALGORITHM = "RSA";
    private static final int MAX_DECRYPT_BLOCK = 128;
    private static final int MAX_ENCRYPT_BLOCK = 117;
    private static final String PRIVATE_KEY = "RSAPrivateKey";
    public static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC2/HRReixPMyBnUW2q+hUEKD7usf/wLKMjg+bjnzxyxo6ZsUhFzIXqN/ikXew540v1sPFhiiYGUa+RJVmkhwfpiijMK9ouKiSzUz8hfxoeqoqdJsEDsQsMrJciiqGQbxd0sXDOlpb5GSpvz3QzYrmZPy/rskzqAi66+Jhf96WLrQIDAQAB";
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
    public static final String SYNC_CW_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAscjPJqLyalrH8/rVxHH4\nY28YnXxeTvuwmMw42hDaciGh65kKFGeFTBN34N4mUyyHt6Y81bK7H4V33F1A5Z3n\nHcKJOgWb5eR2FnMQxGxNz65dzHvnnxyPb4x/lcdjp+rSI62K3o3FxmZTaOPHcX1q\nnTVfepgyBGOvlhmqPP4hJx4smv96TX0ELDZYJw0kwepIK0dbuxUigx51v2Fw+F+n\nh1bcixQuIZQnBkEj883WwBLuGTalkmhellQtylQEYG2mg6lkl56o3tel/k82jv1l\nyjk+Ht5x5oyyd2TWIVjY54sS/AqJBKfB8uXJECwAPex2NXWt7+270u+HWJ6ryeFh\nJwIDAQAB";

    public static Map<String, Object> genKeyPair() throws Exception {
        KeyPairGenerator instance = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        instance.initialize(1024);
        KeyPair generateKeyPair = instance.generateKeyPair();
        HashMap hashMap = new HashMap(2);
        hashMap.put(PUBLIC_KEY, (RSAPublicKey) generateKeyPair.getPublic());
        hashMap.put(PRIVATE_KEY, (RSAPrivateKey) generateKeyPair.getPrivate());
        return hashMap;
    }

    public static String encryptByPublicKey2(String str, String str2) throws Exception {
        byte[] decode = Base64Utils.decode(str2);
        Cipher instance = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        instance.init(1, (RSAPublicKey) KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(new X509EncodedKeySpec(decode)));
        return new String(Base64Utils.encode(instance.doFinal(str.getBytes(Key.STRING_CHARSET_NAME))));
    }

    public static String sign(byte[] bArr, String str) throws Exception {
        PrivateKey generatePrivate = KeyFactory.getInstance(KEY_ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(Base64Utils.decode(str)));
        Signature instance = Signature.getInstance(SIGNATURE_ALGORITHM);
        instance.initSign(generatePrivate);
        instance.update(bArr);
        return Base64Utils.encode(instance.sign());
    }

    public static boolean verify(byte[] bArr, String str, String str2) throws Exception {
        PublicKey generatePublic = KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(new X509EncodedKeySpec(Base64Utils.decode(str)));
        Signature instance = Signature.getInstance(SIGNATURE_ALGORITHM);
        instance.initVerify(generatePublic);
        instance.update(bArr);
        return instance.verify(Base64Utils.decode(str2));
    }

    public static byte[] decryptByPrivateKey(byte[] bArr, String str) throws Exception {
        byte[] bArr2;
        PKCS8EncodedKeySpec pKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64Utils.decode(str));
        KeyFactory instance = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey generatePrivate = instance.generatePrivate(pKCS8EncodedKeySpec);
        Cipher instance2 = Cipher.getInstance(instance.getAlgorithm());
        instance2.init(2, generatePrivate);
        int length = bArr.length;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i = 0;
        int i2 = 0;
        while (true) {
            int i3 = length - i;
            if (i3 > 0) {
                if (i3 > 128) {
                    bArr2 = instance2.doFinal(bArr, i, 128);
                } else {
                    bArr2 = instance2.doFinal(bArr, i, i3);
                }
                byteArrayOutputStream.write(bArr2, 0, bArr2.length);
                i2++;
                i = i2 * 128;
            } else {
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();
                return byteArray;
            }
        }
    }

    public static byte[] decryptByPublicKey(byte[] bArr, String str) throws Exception {
        byte[] bArr2;
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64Utils.decode(str));
        KeyFactory instance = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey generatePublic = instance.generatePublic(x509EncodedKeySpec);
        Cipher instance2 = Cipher.getInstance(instance.getAlgorithm());
        instance2.init(2, generatePublic);
        int length = bArr.length;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i = 0;
        int i2 = 0;
        while (true) {
            int i3 = length - i;
            if (i3 > 0) {
                if (i3 > 128) {
                    bArr2 = instance2.doFinal(bArr, i, 128);
                } else {
                    bArr2 = instance2.doFinal(bArr, i, i3);
                }
                byteArrayOutputStream.write(bArr2, 0, bArr2.length);
                i2++;
                i = i2 * 128;
            } else {
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();
                return byteArray;
            }
        }
    }

    public static byte[] loadPublicKey(byte[] bArr) throws Exception {
        try {
            return encrypt((RSAPublicKey) KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(new X509EncodedKeySpec(Base64Utils.decode(PUBLIC_KEY))), bArr);
        } catch (NoSuchAlgorithmException unused) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException unused2) {
            throw new Exception("公钥非法");
        } catch (NullPointerException unused3) {
            throw new Exception("公钥数据为空");
        }
    }

    private static byte[] encrypt(RSAPublicKey rSAPublicKey, byte[] bArr) throws Exception {
        if (rSAPublicKey != null) {
            try {
                Cipher instance = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                instance.init(1, rSAPublicKey);
                return instance.doFinal(bArr);
            } catch (NoSuchAlgorithmException unused) {
                throw new Exception("无此加密算法");
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
                return null;
            } catch (InvalidKeyException unused2) {
                throw new Exception("加密公钥非法,请检查");
            } catch (IllegalBlockSizeException unused3) {
                throw new Exception("明文长度非法");
            } catch (BadPaddingException unused4) {
                throw new Exception("明文数据已损坏");
            }
        } else {
            throw new Exception("加密公钥为空, 请设置");
        }
    }

    public static byte[] encryptByPublicKey(byte[] bArr, String str) throws Exception {
        byte[] bArr2;
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64Utils.decode(str));
        KeyFactory instance = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey generatePublic = instance.generatePublic(x509EncodedKeySpec);
        Cipher instance2 = Cipher.getInstance(instance.getAlgorithm());
        instance2.init(1, generatePublic);
        int length = bArr.length;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i = 0;
        int i2 = 0;
        while (true) {
            int i3 = length - i;
            if (i3 > 0) {
                if (i3 > 117) {
                    bArr2 = instance2.doFinal(bArr, i, 117);
                } else {
                    bArr2 = instance2.doFinal(bArr, i, i3);
                }
                byteArrayOutputStream.write(bArr2, 0, bArr2.length);
                i2++;
                i = i2 * 117;
            } else {
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();
                return byteArray;
            }
        }
    }

    public static byte[] encryptByPrivateKey(byte[] bArr, String str) throws Exception {
        byte[] bArr2;
        PKCS8EncodedKeySpec pKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64Utils.decode(str));
        KeyFactory instance = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey generatePrivate = instance.generatePrivate(pKCS8EncodedKeySpec);
        Cipher instance2 = Cipher.getInstance(instance.getAlgorithm());
        instance2.init(1, generatePrivate);
        int length = bArr.length;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i = 0;
        int i2 = 0;
        while (true) {
            int i3 = length - i;
            if (i3 > 0) {
                if (i3 > 117) {
                    bArr2 = instance2.doFinal(bArr, i, 117);
                } else {
                    bArr2 = instance2.doFinal(bArr, i, i3);
                }
                byteArrayOutputStream.write(bArr2, 0, bArr2.length);
                i2++;
                i = i2 * 117;
            } else {
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();
                return byteArray;
            }
        }
    }

    public static String getPrivateKey(Map<String, Object> map) throws Exception {
        return Base64Utils.encode(((java.security.Key) map.get(PRIVATE_KEY)).getEncoded());
    }

    public static String getPublicKey(Map<String, Object> map) throws Exception {
        return Base64Utils.encode(((java.security.Key) map.get(PUBLIC_KEY)).getEncoded());
    }
}
