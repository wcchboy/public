//
// Created by fengzhuzhu on 2022/4/26.
//

#ifndef FFMPEG_DECODER_GL_H
#include <jni.h>
#include <android/log.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <unistd.h>
#include <stdio.h>
#include "../base.h"
#include <EGL/egl.h>
#include <GLES2/gl2.h>
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
#define FFMPEG_DECODER_GL_H

#include "Media_decoder.h"

class Ffmpeg_decoder_gl : public Media_decoder{

public:
    Ffmpeg_decoder_gl(ANativeWindow *nativeWindow_,size_callbackF sizeCallback_);
    void play_video(JNIEnv *env,jobject surface);
    bool initDecoder();
    void putData(jbyte* data, jint length);
    void uninit();
private:
    GLint InitShader(const char *code, GLint type);

    AVPacket *packet;
    AVFrame *frame;

    int videoWidth = 0;
    int videoHeight = 0;

    AVCodecContext *codecContext = NULL;
    const AVCodec *codec;

    GLuint texts[3] = {0};
    EGLSurface winsurface;
    EGLContext glContext;
    EGLDisplay glDisplay;

    void initGl();
    void setData(AVCodecContext *avctx, const AVPacket *avpkt);
};


#endif //FFMPEG_DECODER_GL_H
