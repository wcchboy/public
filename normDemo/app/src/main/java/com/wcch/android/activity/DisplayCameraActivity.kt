package com.wcch.android.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.wcch.android.databinding.DisplayCameraLayoutBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 *  normDemo
 *
 *  @author Created by RyanWang on 2022/11/21
 *  Copyright © 2022年 IGRS. All rights reserved.
 *  Describe:
 */
class DisplayCameraActivity : AppCompatActivity() {
    private val TAG = "DisplayCameraActivity"
    private lateinit var viewBinding: DisplayCameraLayoutBinding

    private lateinit var cameraExecutor: ExecutorService

    companion object {
        private const val TAG = "CameraXApp-CameraActivity"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = mutableListOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = DisplayCameraLayoutBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listeners for take photo and video capture buttons
        viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }
        viewBinding.videoCaptureButton.setOnClickListener { captureVideo() }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }


    /**
     * 在相机应用中，取景器用于让用户预览他们拍摄的照片。我们将使用 CameraX Preview 类实现取景器。
     *
     */
    private fun startCamera() {
        //S 2 - 视频---//
        ///ProcessCameraProvider这用于将相机的生命周期绑定到生命周期所有者。这消除了打开和关闭相机的任务，因为 CameraX 具有生命周期感知能力
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this) //向 cameraProviderFuture 添加监听器。添加 Runnable 作为一个参数。我们会在稍后填写它。
        // 添加 ContextCompat.getMainExecutor() 作为第二个参数。这将返回一个在主线程上运行的 Executor。
        cameraProviderFuture.addListener({ // Used to bind the lifecycle of cameras to the lifecycle owner
            //在 Runnable 中，添加 ProcessCameraProvider。它用于将相机的生命周期绑定到应用进程中的 LifecycleOwner。
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview 初始化 Preview 对象，在其上调用 build，从取景器中获取 Surface 提供程序，然后在预览上进行设置
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
            }
            // Select back camera as a default 这里悬着后置摄像头
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try { //确保没有任何内容绑定到 cameraProvider，然后将 cameraSelector 和预览对象绑定到 cameraProvider
                // Unbind use cases before rebinding
                cameraProvider.unbindAll() //S --2 视频
                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview)

                //E--2

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto(){
        Log.e(TAG, "takePhoto")
    }
    private fun captureVideo(){
        Log.e(TAG, "captureVideo")
    }


    /**
     * 权限请求后返回结果
     * @param requestCode Int
     * @param permissions Array<String>
     * @param grantResults IntArray
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted by the user. 没有拿到权限", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /**
     * 检查权限
     * @return Boolean
     */
    private fun allPermissionsGranted() = DisplayCameraActivity.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

}