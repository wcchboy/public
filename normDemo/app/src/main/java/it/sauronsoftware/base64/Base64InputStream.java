package it.sauronsoftware.base64;

import java.io.IOException;
import java.io.InputStream;

public class Base64InputStream extends InputStream {
    private int[] buffer;
    private int bufferCounter = 0;
    private boolean eof = false;
    private InputStream inputStream;

    public Base64InputStream(InputStream inputStream2) {
        this.inputStream = inputStream2;
    }

    private void acquire() throws IOException {
        int i;
        char[] cArr = new char[4];
        int i2 = 0;
        do {
            int read = this.inputStream.read();
            i = 1;
            if (read != -1) {
                char c = (char) read;
                if (Shared.chars.indexOf(c) != -1 || c == Shared.pad) {
                    cArr[i2] = c;
                    i2++;
                    continue;
                } else if (!(c == '\r' || c == '\n')) {
                    throw new IOException("Bad base64 stream");
                }
            } else if (i2 == 0) {
                this.buffer = new int[0];
                this.eof = true;
                return;
            } else {
                throw new IOException("Bad base64 stream");
            }
        } while (i2 < 4);
        boolean z = false;
        for (int i3 = 0; i3 < 4; i3++) {
            if (cArr[i3] != Shared.pad) {
                if (z) {
                    throw new IOException("Bad base64 stream");
                }
            } else if (!z) {
                z = true;
            }
        }
        if (cArr[3] != Shared.pad) {
            i = 3;
        } else if (this.inputStream.read() == -1) {
            this.eof = true;
            if (cArr[2] != Shared.pad) {
                i = 2;
            }
        } else {
            throw new IOException("Bad base64 stream");
        }
        int i4 = 0;
        for (int i5 = 0; i5 < 4; i5++) {
            if (cArr[i5] != Shared.pad) {
                i4 |= Shared.chars.indexOf(cArr[i5]) << ((3 - i5) * 6);
            }
        }
        this.buffer = new int[i];
        for (int i6 = 0; i6 < i; i6++) {
            this.buffer[i6] = (i4 >>> ((2 - i6) * 8)) & 255;
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable, java.io.InputStream
    public void close() throws IOException {
        this.inputStream.close();
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        int[] iArr = this.buffer;
        if (iArr == null || this.bufferCounter == iArr.length) {
            if (this.eof) {
                return -1;
            }
            acquire();
            if (this.buffer.length == 0) {
                this.buffer = null;
                return -1;
            }
            this.bufferCounter = 0;
        }
        int[] iArr2 = this.buffer;
        int i = this.bufferCounter;
        this.bufferCounter = i + 1;
        return iArr2[i];
    }
}
