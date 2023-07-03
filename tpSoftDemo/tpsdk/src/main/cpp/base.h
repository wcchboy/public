//
// Created by yuanxuzhen on 3/26/21.
//


#ifndef YUANNATIVE_BASE_H
#define YUANNATIVE_BASE_H

#include <android/log.h>
#include <jni.h>
#include <stdio.h>
#define  LOG_TAG    "qf"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define YUAN_TRUE  1
#define YUAN_FALSE  0



#endif //YUANNATIVE_BASE_H
