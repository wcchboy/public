#继承上⼀层的CMakeLists.txt的变量，也可以在这⾥重新赋值
#C 的编译选项是 CMAKE_C_FLAGS
# 指定编译参数，可选
#SET(CMAKE_CXX_FLAGS "-Wno-error=format-security -Wno-error=pointer-sign")
#set(libs_DIR ${PROJECT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI})
set(libs_DIR ${PROJECT_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a)
message("decoder libs_DIR"=${libs_DIR})
set(libs_include_DIR ../ffmpeg/include)

include_directories(${libs_include_DIR})
link_directories(${libs_DIR})


#⽣成动态库名称、类型、资源⽂件
add_library(ndk-decoder
        SHARED
        ndk_decoder.cpp
        Hw_decoder.cpp
        Ffmpeg_decoder.cpp
        Ffmpeg_decoder_gl.cpp
        )
#依赖库
find_library(log-lib log)
find_library(android-lib android)


add_library(avutil
        SHARED
        IMPORTED)
set_target_properties(avutil
        PROPERTIES IMPORTED_LOCATION
        ${libs_DIR}/libavutil.so)

add_library(swresample
        SHARED
        IMPORTED)
set_target_properties(swresample
        PROPERTIES IMPORTED_LOCATION
        ${libs_DIR}/libswresample.so)

add_library(swscale
        SHARED
        IMPORTED)
set_target_properties(swscale
        PROPERTIES IMPORTED_LOCATION
        ${libs_DIR}/libswscale.so)

add_library(avcodec
        SHARED
        IMPORTED)
set_target_properties(avcodec
        PROPERTIES IMPORTED_LOCATION
        ${libs_DIR}/libavcodec.so)

add_library(avformat
        SHARED
        IMPORTED)
set_target_properties(avformat
        PROPERTIES IMPORTED_LOCATION
        ${libs_DIR}/libavformat.so)

add_library(avfilter
        SHARED
        IMPORTED)
set_target_properties(avfilter
        PROPERTIES IMPORTED_LOCATION
        ${libs_DIR}/libavfilter.so)


message("avfilter"=${libs_DIR}/libavfilter.so)

target_link_libraries(ndk-decoder
        android
        mediandk
        avutil swresample swscale avcodec avformat avfilter
        #avdevice
        GLESv2 EGL
        ${log-lib} ${android-lib})
