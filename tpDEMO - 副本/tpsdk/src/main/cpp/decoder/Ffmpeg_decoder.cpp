//
// Created by fengzhuzhu on 2022/4/26.
//


#include "Ffmpeg_decoder.h"


Ffmpeg_decoder::Ffmpeg_decoder(ANativeWindow *nativeWindow_,size_callbackF sizeCallback_):Media_decoder(nativeWindow,sizeCallback){
    nativeWindow = nativeWindow_;
    sizeCallback = sizeCallback_;
}
bool Ffmpeg_decoder::initDecoder() {
    if (!nativeWindow) {
        LOGE("Ffmpeg_decoder ANativeWindow_fromSurface failed.");
        return false;
    }
    codec = avcodec_find_decoder(AV_CODEC_ID_H264);
    if (!codec) {
        LOGE("Codec not found\n");
        return false;
    }
    parser = av_parser_init(codec->id);
    if (!parser) {
        LOGE("parser not found\n");
        return false;
    }
    codecContext = avcodec_alloc_context3(codec);
    if (!codecContext) {
        LOGE("Could not allocate video codec context\n");
        return false;
    }
    codecContext->codec_type = AVMEDIA_TYPE_VIDEO;
    codecContext->pix_fmt = AV_PIX_FMT_YUV420P;
    /* open it */
    LOGI("open Codec");
    if (avcodec_open2(codecContext, codec, NULL) < 0) {
        LOGE("Could not open codec\n.");
        return false;
    }
    frame = av_frame_alloc();
    // Allocate render frame
    LOGI("Allocate render frame");
    renderFrame = av_frame_alloc();
    if (frame == NULL || renderFrame == NULL) {
        LOGE("Could not allocate video frame.");
        return false;
    }
    packet = av_packet_alloc();
    return true;
}

void Ffmpeg_decoder::play_video(JNIEnv *env, jobject surface) {
    const char *videoPath = "/sdcard/h264/mtv.h264";
    LOGE("PlayVideo: %s", videoPath);
    if (videoPath == NULL) {
        LOGE("videoPath is null");
        return;
    }
    AVFormatContext *formatContext = avformat_alloc_context();
    // open video file
    LOGI("Open video file");
    if (avformat_open_input(&formatContext, videoPath, NULL, NULL) != 0) {
        LOGE("Cannot open video file: %s\n", videoPath);
        return;
    }
    nativeWindow = ANativeWindow_fromSurface(env, surface);
    LOGI("read frame");
    while (av_read_frame(formatContext, packet) == 0) {
        if (packet->stream_index == AVMEDIA_TYPE_VIDEO) {
            setData(codecContext, packet);
        }
        av_packet_unref(packet);
    }

    //内存释放
    LOGI("release memory");
    uninit();
    avformat_close_input(&formatContext);
    avformat_free_context(formatContext);
}

void Ffmpeg_decoder::setData(AVCodecContext *codecContext, const AVPacket *packet) {
    int ret = avcodec_send_packet(codecContext, packet);
    while (ret >= 0) {
        ret = avcodec_receive_frame(codecContext, frame);
        if (ret == AVERROR(EAGAIN) || ret == AVERROR_EOF) {
            break;
        } else if (ret < 0) {
            LOGE("从解码器-接收-数据失败：AVERROR(EAGAIN) ret=%d", ret);
            break;
        } else if (ret == 0) {
            if (videoWidth != frame->width) {
                videoWidth = frame->width;
                videoHeight = frame->height;
                swsContext = sws_getContext(videoWidth,
                                            videoHeight,
                                            AV_PIX_FMT_YUV420P,
                                            videoWidth,
                                            videoHeight,
                                            AV_PIX_FMT_RGBA,
                                            SWS_BILINEAR,
                                            NULL,
                                            NULL,
                                            NULL);
                if (swsContext == NULL) {
                    LOGE("Init SwsContext failed.");
                    return;
                }
                int size = av_image_get_buffer_size(AV_PIX_FMT_RGBA, frame->width, frame->height, 1);
                uint8_t *buffer = (uint8_t *) av_malloc(size * sizeof(uint8_t));
                av_image_fill_arrays(renderFrame->data, renderFrame->linesize, buffer,AV_PIX_FMT_RGBA, frame->width, frame->height, 1);
                if (videoWidth > videoHeight) {
                    LOGE("rgb 横屏 w=%d h=%d linesize=%d", frame->width, frame->height, frame->linesize);
                } else {
                    LOGE("rgb 竖屏 w=%d h=%d linesize=%d", frame->width, frame->height,frame->linesize);
                }
                int32_t ret = ANativeWindow_setBuffersGeometry(nativeWindow, videoWidth,
                                                               videoHeight,
                                                               WINDOW_FORMAT_RGBA_8888);
                if (ret != 0) {
                    LOGE("Ffmpeg_decoder ANativeWindow_setBuffersGeometry failed.");
                    return;
                }
                if(sizeCallback!= nullptr){
                    sizeCallback(videoWidth,videoHeight);
                }

                LOGE("Ffmpeg_decoder size change end ");
            }

            int32_t ret = ANativeWindow_lock(nativeWindow, &windowBuffer, NULL);
            if(ret!=0){
                LOGE("Ffmpeg_decoder ANativeWindow_lock failed.");
                return;
            }
            sws_scale(swsContext, (uint8_t const *const *) frame->data,
                      frame->linesize, 0, frame->height,
                      renderFrame->data, renderFrame->linesize);

//            LOGE("rgb w=%d h=%d linesize=%d , rw=%d rh=%d linesize=%d", frame->width, frame->height,
//                 frame->linesize[0], renderFrame->width, renderFrame->height,
//                 renderFrame->linesize[0]);
            // 获取stride
            uint8_t *dst = (uint8_t *) windowBuffer.bits;
            uint8_t *src = (renderFrame->data[0]);
            int dstStride = windowBuffer.stride * 4;
            int srcStride = renderFrame->linesize[0];
            // 由于window的stride和帧的stride不同,因此需要逐行复制
            for (int i = 0; i < frame->height; i++) {
                memcpy(dst + i * dstStride, src + i * srcStride, srcStride);
            }
            ANativeWindow_unlockAndPost(nativeWindow);
        }
    }
}

void Ffmpeg_decoder::putData(jbyte* data, jint length) {

    if (packet == NULL) {
        LOGE("Could not allocate av packet.");
        return;
    }
    if (length < 0) {
        LOGE("error h264 data length=%d", length);
        return;
    }
    packet->data = (uint8_t *) data;
    packet->size = length;
    setData(codecContext, packet);
    av_packet_unref(packet);
}

void Ffmpeg_decoder::uninit() {
    //内存释放
    LOGI("[rgbdec:index=%d]: uninit 1 \n", this);
    if (nativeWindow) {
        ANativeWindow_release(nativeWindow);
        nativeWindow = nullptr;
    }
    LOGI("[rgbdec:index=%d]: uninit 2 \n", this);
    if (frame) {
        av_frame_free(&frame);
        frame = nullptr;
    }
    LOGI("[rgbdec:index=%d]: uninit 3 \n", this);
    if (renderFrame) {
        av_frame_free(&renderFrame);
        renderFrame = nullptr;
    }
    LOGI("[rgbdec:index=%d]: uninit 4 \n", this);
    if (packet) {
        av_packet_free(&packet);
        packet = nullptr;
    }
    LOGI("[rgbdec:index=%d]: uninit 5 \n", this);
    if (codecContext) {
        avcodec_close(codecContext);
        avcodec_free_context(&codecContext);
        codecContext = nullptr;
    }
    LOGI("[rgbdec:index=%d]: uninit ok \n", this);

}