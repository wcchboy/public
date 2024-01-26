//
// Created by fengzhuzhu on 2022/4/26.
//

#ifndef FFMPEG_MEDIA_DECODER_H

#define FFMPEG_MEDIA_DECODER_H

#include <assert.h>
#include <jni.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <errno.h>
#include <limits.h>

#include "looper.h"
#include "media/NdkMediaCodec.h"
#include "media/NdkMediaExtractor.h"

#include <android/native_window_jni.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

#include "../base.h"

typedef void (*size_callbackF)(int width,int height);

class Media_decoder {

public:
    Media_decoder(ANativeWindow *nativeWindow_,size_callbackF sizeCallback_){
        nativeWindow = nativeWindow_;
        sizeCallback = sizeCallback_;
    }
    virtual bool initDecoder()=0;
    virtual void putData(jbyte* data, jint length)=0;
    virtual void uninit()=0;

protected:
    size_callbackF sizeCallback;
    ANativeWindow *nativeWindow;
private:
    Media_decoder();
};


#endif //FFMPEG_MEDIA_DECODER_H
