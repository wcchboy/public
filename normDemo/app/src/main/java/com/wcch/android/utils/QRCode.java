package com.wcch.android.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.bumptech.glide.load.Key;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Hashtable;

public class QRCode {
    public static Bitmap createQRCode(String str, int i) {
        try {
            Hashtable hashtable = new Hashtable();
            hashtable.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hashtable.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hashtable.put(EncodeHintType.MARGIN, 1);
            BitMatrix deleteWhite = deleteWhite(new QRCodeWriter().encode(str, BarcodeFormat.QR_CODE, i, i, hashtable));
            int width = deleteWhite.getWidth();
            int[] iArr = new int[(width * width)];
            for (int i2 = 0; i2 < width; i2++) {
                for (int i3 = 0; i3 < width; i3++) {
                    if (deleteWhite.get(i3, i2)) {
                        iArr[(i2 * width) + i3] = -16777216;
                    } else {
                        iArr[(i2 * width) + i3] = -1;
                    }
                }
            }
            Bitmap createBitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
            createBitmap.setPixels(iArr, 0, width, 0, 0, width, width);
            return createBitmap;
        } catch (Exception unused) {
            return null;
        }
    }

    private static BitMatrix deleteWhite(BitMatrix bitMatrix) {
        int[] enclosingRectangle = bitMatrix.getEnclosingRectangle();
        int i = enclosingRectangle[2] + 1;
        int i2 = enclosingRectangle[3] + 1;
        BitMatrix bitMatrix2 = new BitMatrix(i, i2);
        bitMatrix2.clear();
        for (int i3 = 0; i3 < i; i3++) {
            for (int i4 = 0; i4 < i2; i4++) {
                if (bitMatrix.get(enclosingRectangle[0] + i3, enclosingRectangle[1] + i4)) {
                    bitMatrix2.set(i3, i4);
                }
            }
        }
        return bitMatrix2;
    }

    public static Bitmap createQRCodeWithLogo(String str, int i, Bitmap bitmap) {
        if (str != null) {
            try {
                if (!"".equals(str)) {
                    HashMap hashMap = new HashMap();
                    hashMap.put(EncodeHintType.CHARACTER_SET, Key.STRING_CHARSET_NAME);
                    hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
                    hashMap.put(EncodeHintType.MARGIN, 4);
                    BitMatrix encode = new QRCodeWriter().encode(new String(str.getBytes(Key.STRING_CHARSET_NAME), "ISO-8859-1"), BarcodeFormat.QR_CODE, i, i);
                    int[] iArr = new int[(i * i)];
                    for (int i2 = 0; i2 < i; i2++) {
                        for (int i3 = 0; i3 < i; i3++) {
                            if (encode.get(i3, i2)) {
                                iArr[(i2 * i) + i3] = -16777216;
                            } else {
                                iArr[(i2 * i) + i3] = -1;
                            }
                        }
                    }
                    Bitmap createBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
                    createBitmap.setPixels(iArr, 0, i, 0, 0, i, i);
                    if (!(bitmap == null || createBitmap == null)) {
                        createBitmap = addLogo(createBitmap, bitmap);
                    }
                    int[] enclosingRectangle = encode.getEnclosingRectangle();
                    return Bitmap.createBitmap(createBitmap, enclosingRectangle[0], enclosingRectangle[1], enclosingRectangle[2], enclosingRectangle[3]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static Bitmap addLogo(Bitmap bitmap, Bitmap bitmap2) {
        if (bitmap == null) {
            return null;
        }
        if (bitmap2 == null) {
            return bitmap;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int width2 = bitmap2.getWidth();
        int height2 = bitmap2.getHeight();
        if (width == 0 || height == 0) {
            return null;
        }
        if (width2 == 0 || height2 == 0) {
            return bitmap;
        }
        float f = ((((float) width) * 1.0f) / 5.0f) / ((float) width2);
        Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(createBitmap);
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
            canvas.scale(f, f, (float) (width / 2), (float) (height / 2));
            canvas.drawBitmap(bitmap2, (float) ((width - width2) / 2), (float) ((height - height2) / 2), (Paint) null);
            canvas.save();
            canvas.restore();
            return createBitmap;
        } catch (Exception e) {
            e.getStackTrace();
            return null;
        }
    }
}
