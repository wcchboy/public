package it.sauronsoftware.base64;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class Base64 {
    private static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bArr = new byte[1024];
        while (true) {
            int read = inputStream.read(bArr);
            if (read != -1) {
                outputStream.write(bArr, 0, read);
            } else {
                return;
            }
        }
    }

    public static String decode(String str) throws RuntimeException {
        try {
            return new String(decode(str.getBytes("ASCII")));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("ASCII is not supported!", e);
        }
    }

    public static String decode(String str, String str2) throws RuntimeException {
        try {
            return new String(decode(str.getBytes("ASCII")), str2);
        } catch (UnsupportedEncodingException e) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Unsupported charset: ");
            stringBuffer.append(str2);
            throw new RuntimeException(stringBuffer.toString(), e);
        }
    }

    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0011 */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x001e A[SYNTHETIC, Splitter:B:18:0x001e] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0025 A[SYNTHETIC, Splitter:B:22:0x0025] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void decode(java.io.File r2, java.io.File r3) throws IOException {
        /*
            r0 = 0
            java.io.FileInputStream r1 = new java.io.FileInputStream     // Catch:{ all -> 0x001a }
            r1.<init>(r2)     // Catch:{ all -> 0x001a }
            java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch:{ all -> 0x0018 }
            r2.<init>(r3)     // Catch:{ all -> 0x0018 }
            decode(r1, r2)     // Catch:{ all -> 0x0015 }
            r2.close()     // Catch:{ all -> 0x0011 }
        L_0x0011:
            r1.close()     // Catch:{ all -> 0x0014 }
        L_0x0014:
            return
        L_0x0015:
            r3 = move-exception
            r0 = r2
            goto L_0x001c
        L_0x0018:
            r3 = move-exception
            goto L_0x001c
        L_0x001a:
            r3 = move-exception
            r1 = r0
        L_0x001c:
            if (r0 == 0) goto L_0x0023
            r0.close()     // Catch:{ all -> 0x0022 }
            goto L_0x0023
        L_0x0022:
        L_0x0023:
            if (r1 == 0) goto L_0x0028
            r1.close()     // Catch:{ all -> 0x0028 }
        L_0x0028:
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: it.sauronsoftware.base64.Base64.decode(java.io.File, java.io.File):void");
    }

    public static void decode(InputStream inputStream, OutputStream outputStream) throws IOException {
        copy(new Base64InputStream(inputStream), outputStream);
    }

    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x0026 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x0010 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static byte[] decode(byte[] r4) throws RuntimeException {
        /*
            java.io.ByteArrayInputStream r0 = new java.io.ByteArrayInputStream
            r0.<init>(r4)
            java.io.ByteArrayOutputStream r4 = new java.io.ByteArrayOutputStream
            r4.<init>()
            decode(r0, r4)     // Catch:{ IOException -> 0x001a }
            r0.close()     // Catch:{ all -> 0x0010 }
        L_0x0010:
            r4.close()     // Catch:{ all -> 0x0013 }
        L_0x0013:
            byte[] r4 = r4.toByteArray()
            return r4
        L_0x0018:
            r1 = move-exception
            goto L_0x0023
        L_0x001a:
            r1 = move-exception
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ all -> 0x0018 }
            java.lang.String r3 = "Unexpected I/O error"
            r2.<init>(r3, r1)     // Catch:{ all -> 0x0018 }
            throw r2     // Catch:{ all -> 0x0018 }
        L_0x0023:
            r0.close()     // Catch:{ all -> 0x0026 }
        L_0x0026:
            r4.close()     // Catch:{ all -> 0x0029 }
        L_0x0029:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: it.sauronsoftware.base64.Base64.decode(byte[]):byte[]");
    }

    public static String encode(String str) throws RuntimeException {
        try {
            return new String(encode(str.getBytes()), "ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("ASCII is not supported!", e);
        }
    }

    public static String encode(String str, String str2) throws RuntimeException {
        try {
            return new String(encode(str.getBytes(str2)), "ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("ASCII is not supported!", e);
        }
    }

    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0011 */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x001e A[SYNTHETIC, Splitter:B:18:0x001e] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0025 A[SYNTHETIC, Splitter:B:22:0x0025] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void encode(java.io.File r2, java.io.File r3) throws IOException {
        /*
            r0 = 0
            java.io.FileInputStream r1 = new java.io.FileInputStream     // Catch:{ all -> 0x001a }
            r1.<init>(r2)     // Catch:{ all -> 0x001a }
            java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch:{ all -> 0x0018 }
            r2.<init>(r3)     // Catch:{ all -> 0x0018 }
            encode(r1, r2)     // Catch:{ all -> 0x0015 }
            r2.close()     // Catch:{ all -> 0x0011 }
        L_0x0011:
            r1.close()     // Catch:{ all -> 0x0014 }
        L_0x0014:
            return
        L_0x0015:
            r3 = move-exception
            r0 = r2
            goto L_0x001c
        L_0x0018:
            r3 = move-exception
            goto L_0x001c
        L_0x001a:
            r3 = move-exception
            r1 = r0
        L_0x001c:
            if (r0 == 0) goto L_0x0023
            r0.close()     // Catch:{ all -> 0x0022 }
            goto L_0x0023
        L_0x0022:
        L_0x0023:
            if (r1 == 0) goto L_0x0028
            r1.close()     // Catch:{ all -> 0x0028 }
        L_0x0028:
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: it.sauronsoftware.base64.Base64.encode(java.io.File, java.io.File):void");
    }

    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0011 */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x001e A[SYNTHETIC, Splitter:B:18:0x001e] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0025 A[SYNTHETIC, Splitter:B:22:0x0025] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void encode(java.io.File r2, java.io.File r3, int r4) throws IOException {
        /*
            r0 = 0
            java.io.FileInputStream r1 = new java.io.FileInputStream     // Catch:{ all -> 0x001a }
            r1.<init>(r2)     // Catch:{ all -> 0x001a }
            java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch:{ all -> 0x0018 }
            r2.<init>(r3)     // Catch:{ all -> 0x0018 }
            encode(r1, r2, r4)     // Catch:{ all -> 0x0015 }
            r2.close()     // Catch:{ all -> 0x0011 }
        L_0x0011:
            r1.close()     // Catch:{ all -> 0x0014 }
        L_0x0014:
            return
        L_0x0015:
            r3 = move-exception
            r0 = r2
            goto L_0x001c
        L_0x0018:
            r3 = move-exception
            goto L_0x001c
        L_0x001a:
            r3 = move-exception
            r1 = r0
        L_0x001c:
            if (r0 == 0) goto L_0x0023
            r0.close()     // Catch:{ all -> 0x0022 }
            goto L_0x0023
        L_0x0022:
        L_0x0023:
            if (r1 == 0) goto L_0x0028
            r1.close()     // Catch:{ all -> 0x0028 }
        L_0x0028:
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: it.sauronsoftware.base64.Base64.encode(java.io.File, java.io.File, int):void");
    }

    public static void encode(InputStream inputStream, OutputStream outputStream) throws IOException {
        encode(inputStream, outputStream, 0);
    }

    public static void encode(InputStream inputStream, OutputStream outputStream, int i) throws IOException {
        Base64OutputStream base64OutputStream = new Base64OutputStream(outputStream, i);
        copy(inputStream, base64OutputStream);
        base64OutputStream.commit();
    }

    public static byte[] encode(byte[] bArr) throws RuntimeException {
        return encode(bArr, 0);
    }

    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x0026 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x0010 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static byte[] encode(byte[] r3, int r4) throws RuntimeException {
        /*
            java.io.ByteArrayInputStream r0 = new java.io.ByteArrayInputStream
            r0.<init>(r3)
            java.io.ByteArrayOutputStream r3 = new java.io.ByteArrayOutputStream
            r3.<init>()
            encode(r0, r3, r4)     // Catch:{ IOException -> 0x001a }
            r0.close()     // Catch:{ all -> 0x0010 }
        L_0x0010:
            r3.close()     // Catch:{ all -> 0x0013 }
        L_0x0013:
            byte[] r3 = r3.toByteArray()
            return r3
        L_0x0018:
            r4 = move-exception
            goto L_0x0023
        L_0x001a:
            r4 = move-exception
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ all -> 0x0018 }
            java.lang.String r2 = "Unexpected I/O error"
            r1.<init>(r2, r4)     // Catch:{ all -> 0x0018 }
            throw r1     // Catch:{ all -> 0x0018 }
        L_0x0023:
            r0.close()     // Catch:{ all -> 0x0026 }
        L_0x0026:
            r3.close()     // Catch:{ all -> 0x0029 }
        L_0x0029:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: it.sauronsoftware.base64.Base64.encode(byte[], int):byte[]");
    }
}
