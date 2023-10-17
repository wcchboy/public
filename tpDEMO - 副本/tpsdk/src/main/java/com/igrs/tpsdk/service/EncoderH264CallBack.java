package com.igrs.tpsdk.service;

public interface EncoderH264CallBack {
    void encoderSuccess(byte[] data, boolean isKey);
}
