package it.sauronsoftware.base64;

import java.io.IOException;
import java.io.OutputStream;

public class Base64OutputStream extends OutputStream {
    private int buffer;
    private int bytecounter;
    private int linecounter;
    private int linelength;
    private OutputStream outputStream;
    public static final String ENTER = "\r\n";

    public Base64OutputStream(OutputStream outputStream2) {
        this(outputStream2, 76);
    }

    public Base64OutputStream(OutputStream outputStream2, int i) {
        this.outputStream = null;
        this.buffer = 0;
        this.bytecounter = 0;
        this.linecounter = 0;
        this.linelength = 0;
        this.outputStream = outputStream2;
        this.linelength = i;
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        commit();
        this.outputStream.close();
    }

    /* access modifiers changed from: protected */
    public void commit() throws IOException {
        if (this.bytecounter > 0) {
            int i = this.linelength;
            if (i > 0 && this.linecounter == i) {
                this.outputStream.write(ENTER.getBytes());
                this.linecounter = 0;
            }
            char charAt = Shared.chars.charAt((this.buffer << 8) >>> 26);
            char charAt2 = Shared.chars.charAt((this.buffer << 14) >>> 26);
            char charAt3 = this.bytecounter < 2 ? Shared.pad : Shared.chars.charAt((this.buffer << 20) >>> 26);
            char charAt4 = this.bytecounter < 3 ? Shared.pad : Shared.chars.charAt((this.buffer << 26) >>> 26);
            this.outputStream.write(charAt);
            this.outputStream.write(charAt2);
            this.outputStream.write(charAt3);
            this.outputStream.write(charAt4);
            this.linecounter += 4;
            this.bytecounter = 0;
            this.buffer = 0;
        }
    }

    @Override // java.io.OutputStream
    public void write(int i) throws IOException {
        int i2 = this.bytecounter;
        this.buffer = ((i & 255) << (16 - (i2 * 8))) | this.buffer;
        this.bytecounter = i2 + 1;
        if (this.bytecounter == 3) {
            commit();
        }
    }
}
