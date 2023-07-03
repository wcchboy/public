//
// Created by fengzhuzhu on 2022/4/27.
//

#ifndef FFMPEG_HW_DECODER_H
#define FFMPEG_HW_DECODER_H

#include "Media_decoder.h"
#include "media/NdkMediaCodec.h"
#include "media/NdkMediaExtractor.h"

class Hw_decoder : public Media_decoder {
public:

    Hw_decoder(ANativeWindow *pWindow, void (*param)(int, int));

    bool initDecoder();

    void putData(jbyte *data, jint length);

    void uninit();

private:
    AMediaCodec *codec;
    AMediaCodecBufferInfo info;
    int64_t systemnanotime();
    int64_t getTimeUsec();
};


#endif //FFMPEG_HW_DECODER_H
