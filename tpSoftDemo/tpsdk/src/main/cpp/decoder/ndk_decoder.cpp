#include <jni.h>

//
// Created by fengzhuzhu on 2022/4/26.
//

#include "Media_decoder.h"
#include "Ffmpeg_decoder.h"
#include "Ffmpeg_decoder_gl.h"
#include "Hw_decoder.h"
#include "../base.h"
enum {
    dec_type_def,
    dec_type_hw,
    dec_type_sf_gl,
    dec_type_sf_rgb
};

typedef struct tick_context {
    JavaVM *g_VM;
    jobject g_obj;
    jmethodID size_callback;
} TickContext;
TickContext g_ctx;


void size_callback_F(int width,int heigth){
    LOGE("size_callbackF w= %d h=%d",width,heigth);
    JNIEnv *env = NULL;
    //获取当前native线程是否有没有被附加到jvm环境中
    if (g_ctx.g_VM->GetEnv((void **) &env, JNI_VERSION_1_6) == JNI_EDETACHED) {
        //如果没有， 主动附加到jvm环境中，获取到env
        if ( g_ctx.g_VM->AttachCurrentThread(&env, NULL) != 0) {
            return;
        }
    }
    env->CallVoidMethod(g_ctx.g_obj, g_ctx.size_callback, width,heigth);
    return;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_igrs_sml_jni_NdkDecoder_init(JNIEnv *env, jobject thiz, jobject surface,jint type) {
    LOGE("NdkDecoder_init type=%d",type);
    ANativeWindow* nativeWindow = ANativeWindow_fromSurface(env, surface);
    if (!nativeWindow) {
        LOGE("[ndkdec:type=%d]: ANativeWindow_fromSurface is null \n", type);
        return false;
    } else {
        LOGI("[ndkdec:type=%d]: ANativeWindow_fromSurface is ok \n", type);
    }

    //JavaVM是虚拟机在JNI中的表示，等下再其他线程回调java层需要用到
    env->GetJavaVM(&g_ctx.g_VM);
    g_ctx.g_obj =  env->NewGlobalRef(thiz);
    jclass cls = env->GetObjectClass(g_ctx.g_obj);
    g_ctx.size_callback = env->GetMethodID(cls,"size_change_callback","(II)V");
    env->DeleteLocalRef(cls);
    Media_decoder *ndk_decoder;
    if(type==dec_type_def || type==dec_type_hw){
        ndk_decoder = new Hw_decoder(nativeWindow,size_callback_F);
        bool ret = ndk_decoder->initDecoder();
        if(ret){
            LOGI("硬解成功");
            return (jlong) ndk_decoder;
        }else{
            LOGE("硬解失败 准备尝试软解");
            ndk_decoder->uninit();
            Ffmpeg_decoder* sf_decoder = new Ffmpeg_decoder(nativeWindow,size_callback_F);
            int sf_ret = sf_decoder->initDecoder();

            if(sf_ret==1){
                LOGE("硬解失败 尝试软解结果 成功  准备 再次尝试硬解");
                Hw_decoder* hw_decoder = new Hw_decoder(nativeWindow,size_callback_F);
                ret = hw_decoder->initDecoder();
                LOGE("硬解失败 软解成功 尝试硬解 结果 %d",ret);
                if(ret){
                    ndk_decoder = hw_decoder;
                    sf_decoder->uninit();
                    LOGE("硬解失败 尝试软解后再次尝试硬解  最终是使用硬解");
                    return (jlong) ndk_decoder;
                }else{
                    ndk_decoder = sf_decoder;
                    hw_decoder->uninit();
                    LOGE("硬解失败 尝试软解后再次尝试硬解  最终是使用软解");
                    return (jlong) ndk_decoder;
                }
            }else{
                LOGE("硬解失败 尝试软解结果 失败");
                return -1;
            }

        }
    }else if(type==dec_type_sf_gl){
        ndk_decoder = new Ffmpeg_decoder_gl(nativeWindow,size_callback_F);
        bool ret = ndk_decoder->initDecoder();
        if(ret){
            return (jlong) ndk_decoder;
        }
    }else if(type==dec_type_sf_rgb){
        ndk_decoder = new Ffmpeg_decoder(nativeWindow,size_callback_F);
        bool ret = ndk_decoder->initDecoder();
        if(ret){
            return (jlong) ndk_decoder;
        }
    }
    if(ndk_decoder!= nullptr){

    }

    return -1;
}
extern "C"
JNIEXPORT void JNICALL
Java_com_igrs_sml_jni_NdkDecoder_putData(JNIEnv *env, jobject thiz, jlong id, jbyteArray data, jint length) {
    Media_decoder *ndk_decoder = (Media_decoder *) id;
    if(ndk_decoder){
        jbyte *pyuvData = env->GetByteArrayElements(data, NULL);
        env->ReleaseByteArrayElements(data,  pyuvData, 0);
        ndk_decoder->putData(pyuvData, length);
    }

}
extern "C"
JNIEXPORT void JNICALL
Java_com_igrs_sml_jni_NdkDecoder_uninit(JNIEnv *env, jobject thiz, jlong id) {
    Media_decoder *ndk_decoder = (Media_decoder *) id;
    if(ndk_decoder){ ndk_decoder->uninit();}
}


JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    memset(&g_ctx, 0, sizeof(g_ctx));
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR; // JNI version not supported.
    }
    return JNI_VERSION_1_6;
}
