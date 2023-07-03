//
// Created by fengzhuzhu on 2022/4/26.
//

#ifndef FFMPEG_FFMPEG_DECODER_H
#include <jni.h>
#include <android/log.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <unistd.h>
#include <stdio.h>
#include "../base.h"
extern "C"{
#include<libavutil/log.h>
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavfilter/avfilter.h>
#include <libswscale/swscale.h>
#include <libswresample/swresample.h>
#include <libavutil/opt.h>
#include <libavutil/imgutils.h>
};


#define FFMPEG_FFMPEG_DECODER_H

#include "Media_decoder.h"

class Ffmpeg_decoder : public Media_decoder{
public:
    Ffmpeg_decoder(ANativeWindow *nativeWindow_,size_callbackF sizeCallback_);

    void play_video(JNIEnv *env, jobject surface);
    bool initDecoder();
    void putData(jbyte* data, jint length);
    void uninit();
private:

    ANativeWindow_Buffer windowBuffer;
    AVPacket *packet;
    AVFrame *frame;
    AVFrame *renderFrame;

    int videoWidth =0;
    int videoHeight =0;
    struct SwsContext *swsContext;

    const AVCodec *codec;
    AVCodecParserContext *parser;
    AVCodecContext *codecContext = NULL;

    void setData(AVCodecContext *codecContext, const AVPacket *packet);

    Ffmpeg_decoder();

};



#endif //FFMPEG_FFMPEG_DECODER_H
