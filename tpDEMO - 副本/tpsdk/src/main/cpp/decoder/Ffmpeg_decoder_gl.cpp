//
// Created by fengzhuzhu on 2022/4/26.
//

#include "Ffmpeg_decoder_gl.h"

#define GET_STR(x) #x

static const char *vertexShader = GET_STR(
        attribute vec4 aPosition; //顶点坐标，在外部获取传递进来
        attribute vec2 aTexCoord; //材质（纹理）顶点坐标
        varying vec2 vTexCoord;   //输出的材质（纹理）坐标，给片元着色器使用
        void main() {
            //纹理坐标转换，以左上角为原点的纹理坐标转换成以左下角为原点的纹理坐标，
            // 比如以左上角为原点的（0，0）对应以左下角为原点的纹理坐标的（0，1）
            vTexCoord = vec2(aTexCoord.x, 1.0 - aTexCoord.y);
            gl_Position = aPosition;
        }
);

static const char *fragYUV420P = GET_STR(
        precision mediump float;    //精度
        varying vec2 vTexCoord;     //顶点着色器传递的坐标，相同名字opengl会自动关联
        uniform sampler2D yTexture; //输入的材质（不透明灰度，单像素）
        uniform sampler2D uTexture;
        uniform sampler2D vTexture;
        void main() {
            vec3 yuv;
            vec3 rgb;
            yuv.r = texture2D(yTexture, vTexCoord).r; // y分量
            // 因为UV的默认值是127，所以我们这里要减去0.5（OpenGLES的Shader中会把内存中0～255的整数数值换算为0.0～1.0的浮点数值）
            yuv.g = texture2D(uTexture, vTexCoord).r - 0.5; // u分量
            yuv.b = texture2D(vTexture, vTexCoord).r - 0.5; // v分量
            // yuv转换成rgb，两种方法，一种是RGB按照特定换算公式单独转换
            // 另外一种是使用矩阵转换
            rgb = mat3(1.0, 1.0, 1.0,
                       0.0, -0.39465, 2.03211,
                       1.13983, -0.58060, 0.0) * yuv;
            //输出像素颜色
            gl_FragColor = vec4(rgb, 1.0);
        }
);


Ffmpeg_decoder_gl::Ffmpeg_decoder_gl(ANativeWindow *nativeWindow_,size_callbackF sizeCallback_):Media_decoder(nativeWindow,sizeCallback){
    nativeWindow = nativeWindow_;
    sizeCallback = sizeCallback_;
}
GLint Ffmpeg_decoder_gl::InitShader(const char *code, GLint type) {
    //创建shader
    GLint sh = glCreateShader(type);
    if (sh == 0) {
        LOGE("glCreateShader %d failed!", type);
        return 0;
    }
    //加载shader
    glShaderSource(sh, 1, &code, 0);
    //编译shader
    glCompileShader(sh);

    //获取编译情况
    GLint status;
    glGetShaderiv(sh, GL_COMPILE_STATUS, &status);
    if (status == 0) {
        LOGE("glCompileShader failed!");
        return 0;
    }
    LOGE("glCompileShader success!");
    return sh;
}

void Ffmpeg_decoder_gl::initGl() {

    glDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (glDisplay == EGL_NO_DISPLAY) {
        LOGE("eglGetDisplay failed");
        return;
    }

    if (EGL_TRUE != eglInitialize(glDisplay, 0, 0)) {
        LOGE("eglGetDisplay init failed");
        return;
    }

    EGLConfig config;
    EGLint config_num;
    EGLint config_spec[] = {
            EGL_RED_SIZE, 8,
            EGL_GREEN_SIZE, 8,
            EGL_BLUE_SIZE, 8,
            EGL_SURFACE_TYPE, EGL_WINDOW_BIT, EGL_NONE
    };
    if (EGL_TRUE != eglChooseConfig(glDisplay, config_spec, &config, 1, &config_num)) {
        LOGE("eglChooseConfig failed!");
        return;
    }

    //创建surface
    winsurface = eglCreateWindowSurface(glDisplay, config, nativeWindow, 0);
    if (winsurface == EGL_NO_SURFACE) {
        LOGE("eglCreateWindowSurface failed!");
        return;
    }

    const EGLint ctxAttr[] = {
            EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE
    };
    glContext = eglCreateContext(glDisplay, config, EGL_NO_CONTEXT, ctxAttr);
    if (glContext == EGL_NO_CONTEXT) {
        LOGE("eglCreateContext failed!");
        return;
    }
    if (EGL_TRUE != eglMakeCurrent(glDisplay, winsurface, winsurface, glContext)) {
        LOGE("eglMakeCurrent failed!");
        return;
    }

    //顶点和片元shader初始化
    //顶点shader初始化
    GLint vsh = InitShader(vertexShader, GL_VERTEX_SHADER);
    //片元yuv420 shader初始化
    GLint fsh = InitShader(fragYUV420P, GL_FRAGMENT_SHADER);

    /////////////////////////////////////////////////////////////
    //创建渲染程序
    GLint program = glCreateProgram();
    if (program == 0) {
        LOGE("glCreateProgram failed!");
        return;
    }
    //渲染程序中加入着色器代码
    glAttachShader(program, vsh);
    glAttachShader(program, fsh);

    //链接程序
    glLinkProgram(program);
    GLint status = 0;
    glGetProgramiv(program, GL_LINK_STATUS, &status);
    if (status != GL_TRUE) {
        LOGE("glLinkProgram failed!");
        return;
    }
    glUseProgram(program);
    LOGI("glLinkProgram success!");
    /////////////////////////////////////////////////////////////

    //加入三维顶点数据 两个三角形组成正方形
    static float vers[] = {
            1.0f, -1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f,
    };
    GLuint apos = (GLuint) glGetAttribLocation(program, "aPosition");
    glEnableVertexAttribArray(apos);
    //传递顶点
    glVertexAttribPointer(apos, 3, GL_FLOAT, GL_FALSE, 12, vers);

    //加入材质坐标数据
    static float txts[] = {
            1.0f, 0.0f, //右下
            0.0f, 0.0f,
            1.0f, 1.0f,
            0.0, 1.0
    };
    GLuint atex = (GLuint) glGetAttribLocation(program, "aTexCoord");
    glEnableVertexAttribArray(atex);
    glVertexAttribPointer(atex, 2, GL_FLOAT, GL_FALSE, 8, txts);

    //材质纹理初始化
    //设置纹理层
    glUniform1i(glGetUniformLocation(program, "yTexture"), 0); //对于纹理第1层
    glUniform1i(glGetUniformLocation(program, "uTexture"), 1); //对于纹理第2层
    glUniform1i(glGetUniformLocation(program, "vTexture"), 2); //对于纹理第3层

    //创建opengl纹理


}

bool Ffmpeg_decoder_gl::initDecoder() {
    if (!nativeWindow) {
        LOGE("Ffmpeg_decoder_gl ANativeWindow_fromSurface failed.");
        return false;
    }
    codec = avcodec_find_decoder(AV_CODEC_ID_H264);
    if (codec == NULL) {
        LOGE("Ffmpeg_decoder_gl Codec not found.");
        return false;
    }
    codecContext = avcodec_alloc_context3(codec);
    if (codecContext == NULL) {
        LOGE("Ffmpeg_decoder_gl CodecContext not found.");
        return false;
    }
    codecContext->codec_type = AVMEDIA_TYPE_VIDEO;
    codecContext->pix_fmt = AV_PIX_FMT_YUV420P;
    // init codex context
    if (avcodec_open2(codecContext, codec, NULL)) {
        LOGE("Init CodecContext failed.");
        return false;
    }
    // Allocate av packet
    packet = av_packet_alloc();
    if (packet == NULL) {
        LOGE("Could not allocate av packet.");
        return false;
    }
    // Allocate video frame
    LOGI("Allocate video frame");
    frame = av_frame_alloc();

    initGl();
    LOGE("Ffmpeg_decoder_gl init ok.");
    return true;
}

void Ffmpeg_decoder_gl::play_video(JNIEnv *env, jobject surface) {
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
    initGl();

    //////////////////////////////////////////////////////
    ////纹理的修改和显示

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

void Ffmpeg_decoder_gl::setData(AVCodecContext *codecContext, const AVPacket *packet) {
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
                if (sizeCallback != nullptr) {
                    sizeCallback(videoWidth, videoHeight);
                }

                memset(texts, 0, sizeof(texts));
                //创建三个纹理
                glGenTextures(3, texts);
                for (int i = 0; i < 3; i++) {
                    glBindTexture(GL_TEXTURE_2D, texts[i]);
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                    if (i == 0) {
                        glTexImage2D(GL_TEXTURE_2D,
                                     0,           //细节基本 0默认
                                     GL_LUMINANCE,//gpu内部格式 亮度，灰度图
                                     videoWidth, videoHeight, //拉升到全屏
                                     0,             //边框
                                     GL_LUMINANCE,//数据的像素格式 亮度，灰度图 要与上面一致
                                     GL_UNSIGNED_BYTE, //像素的数据类型
                                     NULL                    //纹理的数据
                        );
                    } else {
                        glTexImage2D(GL_TEXTURE_2D,
                                     0,           //细节基本 0默认
                                     GL_LUMINANCE,//gpu内部格式 亮度，灰度图
                                     videoWidth / 2, videoHeight / 2, //拉升到全屏
                                     0,             //边框
                                     GL_LUMINANCE,//数据的像素格式 亮度，灰度图 要与上面一致
                                     GL_UNSIGNED_BYTE, //像素的数据类型
                                     NULL                    //纹理的数据
                        );
                    }
                }
            }

            LOGE("gl w=%d h=%d linesize=%d", frame->width, frame->height, frame->linesize);

            glViewport(0,0,frame->width, frame->height);

            //激活第1层纹理,绑定到创建的opengl纹理
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texts[0]);
            glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, frame->width, frame->height, GL_LUMINANCE,
                            GL_UNSIGNED_BYTE, frame->data[0]);

            //激活第2层纹理,绑定到创建的opengl纹理
            glActiveTexture(GL_TEXTURE0 + 1);
            glBindTexture(GL_TEXTURE_2D, texts[1]);
            glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, frame->width / 2, frame->height / 2,
                            GL_LUMINANCE, GL_UNSIGNED_BYTE, frame->data[1]);

            //激活第2层纹理,绑定到创建的opengl纹理
            glActiveTexture(GL_TEXTURE0 + 2);
            glBindTexture(GL_TEXTURE_2D, texts[2]);
            glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, frame->width / 2, frame->height / 2,
                            GL_LUMINANCE, GL_UNSIGNED_BYTE, frame->data[2]);

            //三维绘制
            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
            //窗口显示
            eglSwapBuffers(glDisplay, winsurface);
        }
    }
}

void Ffmpeg_decoder_gl::putData(jbyte* data, jint length) {
    //////////////////////////////////////////////////////
    if (packet == NULL) {
        LOGE("Could not allocate av packet.");
        return;
    }
    packet->data = (uint8_t *) data;
    packet->size = length;
    setData(codecContext, packet);
    av_packet_unref(packet);
}


void Ffmpeg_decoder_gl::uninit() {
    //内存释放
    LOGI("[gldec:index=%d]: uninit 1 \n", this);
    if (nativeWindow) {
        ANativeWindow_release(nativeWindow);
        nativeWindow = nullptr;
    }
    LOGI("[gldec:index=%d]: uninit 2 \n", this);
    if (winsurface != EGL_NO_SURFACE) {
        LOGE("eglCreateWindowSurface failed!");
        eglDestroySurface(glDisplay, winsurface);
    }
    LOGI("[gldec:index=%d]: uninit 3 \n", this);
    if (glContext != EGL_NO_CONTEXT) {
        eglDestroyContext(glDisplay, glContext);
    }
    LOGI("[gldec:index=%d]: uninit 4 \n", this);
    av_frame_free(&frame);
    av_packet_free(&packet);
    LOGI("[gldec:index=%d]: uninit 5 \n", this);
    avcodec_close(codecContext);
    LOGI("[gldec:index=%d]: uninit 6 \n", this);
    avcodec_free_context(&codecContext);
    LOGI("[gldec:index=%d]: uninit ok \n", this);
}










