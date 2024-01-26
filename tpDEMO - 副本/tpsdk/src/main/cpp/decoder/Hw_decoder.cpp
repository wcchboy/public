//
// Created by fengzhuzhu on 2022/4/27.
//

#include "Hw_decoder.h"

//Hw_decoder::Hw_decoder(ANativeWindow *nativeWindow_,size_callbackF sizeCallback_):Media_decoder(nativeWindow,sizeCallback){
//    nativeWindow = nativeWindow_;
//    sizeCallback_ = sizeCallback;
//}

Hw_decoder::Hw_decoder(ANativeWindow *nativeWindow_,size_callbackF sizeCallback_):Media_decoder(nativeWindow,sizeCallback){
    nativeWindow = nativeWindow_;
    sizeCallback = sizeCallback_;
}

int64_t Hw_decoder::systemnanotime() {
    timespec now;
    clock_gettime(CLOCK_MONOTONIC, &now);
    return now.tv_sec * 1000000000LL + now.tv_nsec;
}

int64_t Hw_decoder::getTimeUsec() { //us
    struct timespec now;
    clock_gettime(CLOCK_MONOTONIC, &now);
    return now.tv_sec * 1000 * 1000 + (int64_t) now.tv_nsec / (1000);
}

void Hw_decoder::putData(jbyte* data, jint length) {
    auto bufidx = AMediaCodec_dequeueInputBuffer(codec, 0);
    if (bufidx >= 0) { //当取不到空buffer的时候，有可能是解码慢跟不上输入速度，导致buffer不够用，所以还需要在后面继续取解码后的数据。
        size_t bufsize;
        uint8_t *buf = AMediaCodec_getInputBuffer(codec, bufidx, &bufsize);
        memcpy(buf, data, length);
        uint64_t presentationTimeUs = systemnanotime();
        AMediaCodec_queueInputBuffer(codec, bufidx, 0, length, presentationTimeUs, 0);
    }
    auto status = 0;
    do {
        status = AMediaCodec_dequeueOutputBuffer(codec, &info, 0);
        if (status >= 0) {
            if (info.flags & AMEDIACODEC_BUFFER_FLAG_END_OF_STREAM) {
                LOGE("output EOS");
            }
            AMediaCodec_releaseOutputBuffer(codec, status, info.size != 0);
        } else if (status == AMEDIACODEC_INFO_OUTPUT_BUFFERS_CHANGED) {
            LOGI("[hwdec:index=%d]: output buffers changed \n", this);
        } else if (status == AMEDIACODEC_INFO_OUTPUT_FORMAT_CHANGED) {
            // 解码输出的格式发生变化
            int mWidth, mHeight;
            auto format = AMediaCodec_getOutputFormat(codec);
            AMediaFormat_getInt32(format, "width", &mWidth);
            AMediaFormat_getInt32(format, "height", &mHeight);
            int32_t localColorFMT;
            AMediaFormat_getInt32(format, AMEDIAFORMAT_KEY_COLOR_FORMAT, &localColorFMT);
            LOGE("[hwdec:index=%d]:format changed w=%d,h=%d,color=%d\n%s",this, mWidth, mHeight, localColorFMT,
                 AMediaFormat_toString(format));
            AMediaFormat_delete(format);
            if (sizeCallback != nullptr) {
                sizeCallback(mWidth, mHeight);
            }else{
                LOGI("[hwdec:index=%d]: size change callback is null \n", this);
            }

        } else if (status == AMEDIACODEC_INFO_TRY_AGAIN_LATER) {

        } else {
            LOGE("unexpected info code: %d  this:%d", status, this);
        }
    } while (status > 0);


}

// set the surface
bool Hw_decoder::initDecoder(){
    LOGI("[hwdec:index=%d]: initDecoder  \n", this);
    codec = AMediaCodec_createDecoderByType("video/avc");
    if (!codec) {
        LOGI("[hwdec:index=%d]: initDecoder AMediaCodec_createDecoderByType is null \n", this);
        return false;
    } else {
        LOGI("[hwdec:index=%d]: initDecoder AMediaCodec_createDecoderByType is ok \n", this);
    }
    AMediaFormat *format = AMediaFormat_new();
    AMediaFormat_setString(format, AMEDIAFORMAT_KEY_MIME, "video/avc");
    AMediaFormat_setInt32(format, AMEDIAFORMAT_KEY_WIDTH, 1920);
    AMediaFormat_setInt32(format, AMEDIAFORMAT_KEY_HEIGHT, 1080);
    if (!format) {
        LOGE("[hwdec:index=%d]: initDecoder AMediaFormat_new is null \n", this);
        return false;
    } else {
        LOGI("[hwdec:index=%d]: initDecoder AMediaFormat_new is ok \n", this);
    }

    media_status_t status = AMediaCodec_configure(codec, format, nativeWindow, NULL, 0);
    if (status != 0) {
        LOGE("[hwdec:index=%d]: initDecoder AMediaCodec_configure is status%d \n", this, status);
        return false;
    } else {
        LOGI("[hwdec:index=%d]: initDecoder AMediaCodec_configure is ok \n", this);
    }
    status = AMediaCodec_start(codec);
    if (status != 0) {
        LOGE("[hwdec:index=%d]: initDecoder AMediaCodec_start is status%d \n", this, status);
        return false;
    } else {
        LOGI("[hwdec:index=%d]: initDecoder AMediaCodec_start is ok \n", this);
    }
    return true;
}

void Hw_decoder::uninit() {
    LOGI("[hwdec:index=%d]: uninit 1 \n", this);
    if (nativeWindow) {
        ANativeWindow_release(nativeWindow);
        nativeWindow = NULL;
    }
    LOGI("[hwdec:index=%d]: uninit 2 \n", this);
    AMediaCodec_stop(codec);
    LOGI("[hwdec:index=%d]: uninit 3 \n", this);
    AMediaCodec_delete(codec);
    LOGI("[hwdec:index=%d]: uninit is ok \n", this);
}

