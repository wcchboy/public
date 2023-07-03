package com.igrs.tpsdk.opengl.filter;

import android.opengl.EGL14;
import android.opengl.GLES20;
import android.util.Log;

import com.igrs.sml.util.L;
import com.igrs.tpsdk.opengl.util.MatrixUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * Description:
 */
public abstract class AFilter {
    private static final String TAG = "Filter";
    public static boolean DEBUG = true;
    /**
     * 单位矩阵
     */
    public static final float[] OM = MatrixUtils.getOriginalMatrix();
    /**
     * 程序句柄
     */
    protected int mProgram;
    /**
     * 顶点坐标句柄
     */
    protected int mHPosition;
    /**
     * 纹理坐标句柄
     */
    protected int mHCoord;
    /**
     * 总变换矩阵句柄
     */
    protected int mHMatrix;
    /**
     * 默认纹理贴图句柄
     */
    protected int mHTexture;

    /**
     * 顶点坐标Buffer
     */
    protected FloatBuffer mVerBuffer;

    /**
     * 纹理坐标Buffer
     */
    protected FloatBuffer mTexBuffer;

    /**
     * 索引坐标Buffer
     */

    protected int mFlag = 0;

    private float[] matrix = Arrays.copyOf(OM, 16);

    private int textureId = 0;

    public AFilter() {
        ByteBuffer a = ByteBuffer.allocateDirect(32);
        a.order(ByteOrder.nativeOrder());
        mVerBuffer = a.asFloatBuffer();
        //顶点坐标
        float[] pos = {
                -1.0f, 1.0f,
                -1.0f, -1.0f,
                1.0f, 1.0f,
                1.0f, -1.0f,
        };
        mVerBuffer.put(pos);
        mVerBuffer.position(0);
        ByteBuffer b = ByteBuffer.allocateDirect(32);
        b.order(ByteOrder.nativeOrder());
        mTexBuffer = b.asFloatBuffer();
        //纹理坐标
        float[] coord = {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
        };
        mTexBuffer.put(coord);
        mTexBuffer.position(0);
    }

    public final void create() {
        onCreate();
    }

    public final void setSize(int width, int height) {
        onSizeChanged(width, height);
    }

    public void draw() {
        try {
            GLES20.glUseProgram(mProgram);
            onSetExpandData();
            onBindTexture();
            onDraw();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onBindTexture() {
        //绑定默认纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, getTextureId());
        GLES20.glUniform1i(mHTexture, 0);
    }

    public void setMatrix(float[] matrix) {
        this.matrix = matrix;
    }

    public float[] getMatrix() {
        return matrix;
    }

    public final int getTextureId() {
        return textureId;
    }

    public final void setTextureId(int textureId) {
        this.textureId = textureId;
    }

    public void setFlag(int flag) {
        this.mFlag = flag;
    }

    public int getFlag() {
        return mFlag;
    }

    public int getOutputTexture() {
        return -1;
    }

    /**
     * 实现此方法，完成程序的创建，可直接调用createProgram来实现
     */
    protected abstract void onCreate();

    protected abstract void onSizeChanged(int width, int height);

    protected final void createProgram(String vertex, String fragment) {
        mProgram = uCreateGlProgram(vertex, fragment);
        mHPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mHCoord = GLES20.glGetAttribLocation(mProgram, "vCoord");
        mHMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        mHTexture = GLES20.glGetUniformLocation(mProgram, "vTexture");
    }
    private Object location_lock = new Object();
    public void setCoordMatrix(FloatBuffer mTexBuffer){
        synchronized (location_lock){
            this.mTexBuffer.clear();
            this.mTexBuffer.put(mTexBuffer);
            this.mTexBuffer.position(0);
        }

    }
    /**
     * 启用顶点坐标和纹理坐标进行绘制
     */
    protected void onDraw() {
        synchronized (location_lock) {
            GLES20.glEnableVertexAttribArray(mHPosition);
            checkEGLError();
            GLES20.glVertexAttribPointer(mHPosition, 2, GLES20.GL_FLOAT, false, 0, mVerBuffer);
            checkEGLError();
            GLES20.glEnableVertexAttribArray(mHCoord);
            checkEGLError();
            GLES20.glVertexAttribPointer(mHCoord, 2, GLES20.GL_FLOAT, false, 0, mTexBuffer);
            checkEGLError();
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            checkEGLError();
            GLES20.glDisableVertexAttribArray(mHPosition);
            GLES20.glDisableVertexAttribArray(mHCoord);
        }
    }

    /**
     * 设置其他扩展数据
     */
    protected void onSetExpandData() {
        GLES20.glUniformMatrix4fv(mHMatrix, 1, false, matrix, 0);
    }

    public static void glError(int code, Object index) {
        if (DEBUG && code != 0) {
            Log.e(TAG, "glError:" + code + "---" + index);
        }
    }

    //创建GL程序
    public static int uCreateGlProgram(String vertexSource, String fragmentSource) {
        int vertex = uLoadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertex == 0) return 0;
        int fragment = uLoadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragment == 0) return 0;
        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertex);
            GLES20.glAttachShader(program, fragment);
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                glError(1, "Could not link program:" + GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    /**
     * 加载shader
     */
    public static int uLoadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (0 != shader) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                glError(1, "Could not compile shader:" + shaderType);
                glError(1, "GLES20 Error:" + GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    private void checkEGLError() {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            String msg = "RsVideoShowView->checkEGLError->: glError 0x" + Integer.toHexString(error);
            L.e("RsVideoShowView->checkEGLError->GLES20 error:" + error);
            throw new RuntimeException(msg);
        }
        int ec = EGL14.eglGetError();
        if (ec != EGL14.EGL_SUCCESS) {
            L.e("RsVideoShowView->checkEGLError->EGL error:" + ec);
            throw new RuntimeException(": EGL14 error: 0x" + Integer.toHexString(ec));
        }
    }

    public void setRotationDegree(int degree) {
        MatrixUtils.rotate(matrix, degree);
    }
}
