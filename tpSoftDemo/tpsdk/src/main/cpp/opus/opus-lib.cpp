#include <jni.h>
#include "opus/opus.h"
#include "../base.h"
#include <ctime>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jlong
JNICALL Java_com_igrs_audio_OpusUtils_createEncoder
        (JNIEnv *env, jobject thiz, jint sampleRateInHz, jint channelConfig, jint complexity) {
    int error;
    OpusEncoder *pOpusEnc = opus_encoder_create(sampleRateInHz, channelConfig,
                                                OPUS_APPLICATION_AUDIO,
                                                &error);
    if (pOpusEnc) {
        opus_encoder_ctl(pOpusEnc, OPUS_SET_VBR(1));//0:CBR, 1:VBR
        opus_encoder_ctl(pOpusEnc, OPUS_SET_BITRATE(OPUS_AUTO));
        opus_encoder_ctl(pOpusEnc, OPUS_SET_COMPLEXITY(complexity));//8    0~10
        opus_encoder_ctl(pOpusEnc, OPUS_SET_SIGNAL(OPUS_SIGNAL_MUSIC));
        opus_encoder_ctl(pOpusEnc, OPUS_SET_LSB_DEPTH(16));
    } else {
        LOGE("createEncoder->error:%d sampleRateInHz:%d channelConfig:%d complexity:%d", error,
             sampleRateInHz, channelConfig, complexity);
    }
    return (jlong) pOpusEnc;
}
JNIEXPORT jlong
JNICALL Java_com_igrs_audio_OpusUtils_createDecoder
        (JNIEnv *env, jobject thiz, jint sampleRateInHz, jint channelConfig) {
    int error;
    OpusDecoder *pOpusDec = opus_decoder_create(sampleRateInHz, channelConfig, &error);

    LOGE("decode->opus_get_version_string:%s", opus_get_version_string());
    return (jlong) pOpusDec;
}
JNIEXPORT jint
JNICALL Java_com_igrs_audio_OpusUtils_encode
        (JNIEnv *env, jobject thiz, jlong pOpusEnc, jshortArray in, jint offset,
         jbyteArray out) {
    OpusEncoder *pEnc = (OpusEncoder *) pOpusEnc;
    if (!pEnc || !in || !out) {
        LOGE("encode->sth is null  pEnc:%d samples:%d bytes:%d", pEnc, in, out);
        return 0;
    }

    jshort *pSamples = env->GetShortArrayElements(in, 0);
    jsize nSampleSize = env->GetArrayLength(in);
    jbyte *pBytes = env->GetByteArrayElements(out, 0);
    jsize nByteSize = env->GetArrayLength(out);
    //if (nSampleSize - offset < 960 || nByteSize <= 0){
    //if (nSampleSize - offset < 480 || nByteSize <= 0){
    //if (nSampleSize - offset < 240 || nByteSize <= 0){
    //if (nSampleSize - offset < 120 || nByteSize <= 0){
    if (nSampleSize - offset < 60 || nByteSize <= 0) {
        LOGE("enc-error:: nByteSize=%d; dif=%d  nSampleSize:%d offset:%d\n", nByteSize,
             (nSampleSize - offset), nSampleSize, offset);
        return 0;
    }
    nSampleSize = nSampleSize >> 1;
    int nRet = opus_encode(pEnc, pSamples + offset, nSampleSize, (unsigned char *) pBytes,
                           nByteSize);
    static int time = 0;
    if (nRet != 3 && time++ % 4000 == 0) {
        LOGI("enc-proc:: samples=%d; nRet=%d offset:%d nByteSize:%d\n", nSampleSize, nRet, offset,
             nByteSize);
    }
    env->ReleaseShortArrayElements(in, pSamples, 0);
    env->ReleaseByteArrayElements(out, pBytes, 0);
    return nRet;

}
JNIEXPORT jint
JNICALL Java_com_igrs_audio_OpusUtils_decode
        (JNIEnv *env, jobject thiz, jlong pOpusDec, jbyteArray bytes,
         jshortArray samples) {


    OpusDecoder *pDec = (OpusDecoder *) pOpusDec;
    if (!pDec || !samples || !bytes) {
        LOGE("enc-error:: pDec=%d; samples=%d bytes:%d\n", pDec, samples, bytes);
        return 0;
    }

    jshort *pSamples = env->GetShortArrayElements(samples, 0);
    jbyte *pBytes = env->GetByteArrayElements(bytes, 0);
    jsize nByteSize = env->GetArrayLength(bytes);
    jsize nShortSize = env->GetArrayLength(samples);
    if (nByteSize <= 0 || nShortSize <= 0) {
        LOGE("enc-error:: nByteSize=%d; nShortSize:%d\n", nByteSize, nShortSize);
        return -1;
    }
    int nRet = opus_decode(pDec, (unsigned char *) pBytes, nByteSize, pSamples, nShortSize, 0);
    //getCount

    static int time = 0;
    if (time++ % 3000 == 0) {
        LOGE("dec-proc:: in: samples=%d; nRet=%d; nByteSize=%d\n", nShortSize, nRet, nByteSize);
    }
    env->ReleaseShortArrayElements(samples, pSamples, 0);
    env->ReleaseByteArrayElements(bytes, pBytes, 0);
    return nRet;
}
JNIEXPORT void JNICALL Java_com_igrs_audio_OpusUtils_destroyEncoder
        (JNIEnv *env, jobject thiz, jlong pOpusEnc) {
    OpusEncoder *pEnc = (OpusEncoder *) pOpusEnc;
    if (!pEnc)
        return;
    opus_encoder_destroy(pEnc);
}
JNIEXPORT void JNICALL Java_com_igrs_audio_OpusUtils_destroyDecoder
        (JNIEnv *env, jobject thiz, jlong pOpusDec) {
    OpusDecoder *pDec = (OpusDecoder *) pOpusDec;
    if (!pDec)
        return;
    opus_decoder_destroy(pDec);
}
#ifdef __cplusplus
}
#endif