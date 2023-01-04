package com.wcch.android.activity

import android.graphics.ImageFormat
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import com.wcch.android.databinding.CameraSurfaceviewBinding

/**
 *  normDemo
 *
 *  @author Created by RyanWang on 2022/11/21
 *  Copyright © 2022年 IGRS. All rights reserved.
 *  Describe:
 */
class CameraSufaceViewActivity: AppCompatActivity() {
    private val TAG = "CameraActivity"
    private var holder: SurfaceHolder? = null
    private lateinit var viewBinding: CameraSurfaceviewBinding

    private var camera: Camera? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = CameraSurfaceviewBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }
    private fun initView() {
        holder = viewBinding.sfv.holder
        holder?.let {
            it.addCallback(SufaceViewCallback())
        }

    }

    /**
     * 打开相机
     */
    /*private void openCamera() {
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        //获取相机参数
        Camera.Parameters parameters = camera.getParameters();
        //获取相机支持的预览的大小
        Camera.Size previewSize = getCameraPreviewSize(parameters);
        int width = previewSize.width;
        int height = previewSize.height;
        //设置预览格式（也就是每一帧的视频格式）YUV420下的NV21
        parameters.setPreviewFormat(ImageFormat.NV21);
        //设置预览图像分辨率
        parameters.setPreviewSize(width, height);
        //相机旋转90度
        camera.setDisplayOrientation(90);
        //配置camera参数
        camera.setParameters(parameters);
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //设置监听获取视频流的每一帧
        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
            }
        });
        //调用startPreview()用以更新preview的surface
        camera.startPreview();
    }

    *//**
     * 获取设备支持的最大分辨率
     *//*
    private Camera.Size getCameraPreviewSize(Camera.Parameters parameters) {
        List<Camera.Size> list = parameters.getSupportedPreviewSizes();
        Camera.Size needSize = null;
        for (Camera.Size size : list) {
            if (needSize == null) {
                needSize = size;
                continue;
            }
            if (size.width >= needSize.width) {
                if (size.height > needSize.height) {
                    needSize = size;
                }
            }
        }
        return needSize;
    }

    *//**
     * 关闭相机
     *//*
    public void releaseCamera(Camera camera) {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
        }
    }*/
    inner class SufaceViewCallback: SurfaceHolder.Callback{
        override fun surfaceCreated(holder: SurfaceHolder?) {
            Log.d(TAG,"SufaceViewCallback")
        }

        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            Log.d(TAG,"surfaceChanged format:$format width: $width height: $height ")
        }

        override fun surfaceDestroyed(holder: SurfaceHolder?) {
            Log.d(TAG,"surfaceDestroyed")
        }

    }





}