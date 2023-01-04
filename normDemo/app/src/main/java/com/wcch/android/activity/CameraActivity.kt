package com.wcch.android.activity

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.wcch.android.databinding.CameraLayoutBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.wcch.android.R


/**
 *  normDemo
 *
 *  @author Created by RyanWang on 2022/11/21
 *  Copyright © 2022年 IGRS. All rights reserved.
 *  Describe:
 */
typealias LumaListener = (luma: Double) -> Unit

class CameraActivity : AppCompatActivity() {
    private val TAG = "CameraActivity"
    private lateinit var viewBinding: CameraLayoutBinding

    private var imageCapture: ImageCapture? = null

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = CameraLayoutBinding.inflate(layoutInflater)
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

    /**
     * 其他用例与 Preview 非常相似。首先，我们定义一个配置对象，该对象用于实例化实际用例对象。若要拍摄照片，您需要实现 takePhoto() 方法，该方法会在用户按下 photo 按钮时调用。
     *
     */
    private fun takePhoto() { // Get a stable reference of the modifiable image capture use case
        //首先，获取对 ImageCapture 用例的引用。如果用例为 null，请退出函数。如果在设置图片拍摄之前点按“photo”按钮，它将为 null。如果没有 return 语句，应用会在该用例为 null 时崩溃。
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.创建用于保存图片的 MediaStore 内容值。请使用时间戳，确保 MediaStore 中的显示名是唯一的
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        //创建一个 OutputFileOptions 对象。在该对象中，您可以指定所需的输出内容。我们希望将输出保存在 MediaStore 中，以便其他应用可以显示它，因此，请添加我们的 MediaStore 条目。
        val outputOptions =
            ImageCapture.OutputFileOptions.Builder(contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        //对 imageCapture 对象调用 takePicture()。传入 outputOptions、执行器和保存图片时使用的回调。接下来，您需要填写回调。
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
            }

            /**
             * 如果拍摄未失败，即表示照片拍摄成功！将照片保存到我们之前创建的文件中，显示消息框，让用户知道照片已拍摄成功，并输出日志语句
             * @param output OutputFileResults
             */
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val msg = "Photo capture succeeded: ${output.savedUri}"
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                Log.d(TAG, msg)
            }
        })
    }

    /**
     * TODOCameraX 在 1.1.0-alpha10 版中添加了 VideoCapture 用例，并且从那以后一直在改进。
     * 请注意，VideoCapture API 支持很多视频捕获功能，因此，为了使此 Codelab 易于管理，此 Codelab 仅演示如何在 MediaStore 中捕获视频和音频
     * 该方法可以控制 VideoCapture 用例的启动和停止
     */
    private fun captureVideo() {
        //检查是否已创建 VideoCapture 用例：如果尚未创建，则不执行任何操作
        val videoCapture = this.videoCapture ?: return
        //在 CameraX 完成请求操作之前，停用界面；在后续步骤中，它会在我们的已注册的 VideoRecordListener 内重新启用。
        viewBinding.videoCaptureButton.isEnabled = false
        //如果有正在进行的录制操作，请将其停止并释放当前的 recording。当所捕获的视频文件可供我们的应用使用时，我们会收到通知。
        val curRecording = recording
        if (curRecording != null) {
            // Stop the current recording session.
            curRecording.stop()
            recording = null
            return
        }
        //为了开始录制，我们会创建一个新的录制会话。首先，我们创建预定的 MediaStore 视频内容对象，将系统时间戳作为显示名（以便我们可以捕获多个视频）
        // create and start a new recording session
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }
        //使用外部内容选项创建 MediaStoreOutputOptions.Builder。
        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            //将创建的视频 contentValues 设置为 MediaStoreOutputOptions.Builder，并构建我们的 MediaStoreOutputOptions 实例。
            .setContentValues(contentValues)
            .build()
        //将输出选项配置为 VideoCapture<Recorder> 的 Recorder 并启用录音：
        recording = videoCapture.output
            .prepareRecording(this, mediaStoreOutputOptions)
            //在此录音中启用音频。
            .apply {
                if (PermissionChecker.checkSelfPermission(this@CameraActivity,Manifest.permission.RECORD_AUDIO)
                    ==PermissionChecker.PERMISSION_GRANTED){
                    withAudioEnabled()
                }
            }
            //启动这项新录制内容，并注册一个 lambda VideoRecordEvent 监听器。
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                when(recordEvent) {
                    //当相机设备开始请求录制时，将“Start Capture”按钮文本切换为“Stop Capture”。
                    is VideoRecordEvent.Start -> {
                        viewBinding.videoCaptureButton.apply {
                            text = getString(R.string.stop_capture)
                            isEnabled = true
                        }
                    }
                    //完成录制后，用消息框通知用户，并将“Stop Capture”按钮切换回“Start Capture”，然后重新启用它：
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val msg = "Video capture succeeded: " +"${recordEvent.outputResults.outputUri}"
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                            Log.d(TAG, msg)
                        } else {
                            recording?.close()
                            recording = null
                            Log.e(TAG, "Video capture ends with error: " + "${recordEvent.error}")
                        }
                        viewBinding.videoCaptureButton.apply {
                            text = getString(R.string.start_capture)
                            isEnabled = true
                        }
                    }
                }
            }
    }

    /**
     * 在相机应用中，取景器用于让用户预览他们拍摄的照片。我们将使用 CameraX Preview 类实现取景器。
     *
     */
    private fun startCamera() {
        //S 1 ---拍照图片方法-//
        //imageCapture = ImageCapture.Builder().build()

        //E 1-----//


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

        //E---2///

        //s--1 拍照//
        /* cameraProviderFuture.addListener({
             // Used to bind the lifecycle of cameras to the lifecycle owner
             val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

             // Preview
             val preview = Preview.Builder()
                 .build()
                 .also {
                     it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                 }
             imageCapture = ImageCapture.Builder()
                 .build()

             // Select back camera as a default
             val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

             try {
                 // Unbind use cases before rebinding
                 cameraProvider.unbindAll()

                 // Bind use cases to camera
                 cameraProvider.bindToLifecycle(
                     this, cameraSelector, preview, imageCapture)

             } catch(exc: Exception) {
                 Log.e(TAG, "Use case binding failed", exc)
             }

         }, ContextCompat.getMainExecutor(this))*/ //E --1//

        //S 3 -- 视频捕获

        /*cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)

//            imageCapture = ImageCapture.Builder().build()
//
//            val imageAnalyzer = ImageAnalysis.Builder()
//                .build()
//                .also {
//                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
//                        Log.d(TAG, "Average luminosity: $luma")
//                    })
//                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider
                    .bindToLifecycle(this, cameraSelector, preview, videoCapture)
            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))*/
        //E--3 ----

    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

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


    //所支持的硬件级别可以从 Camera2CameraInfo 中检索。例如，以下代码可检查默认的后置摄像头是否是 LEVEL_3 设备：
    @androidx.annotation.OptIn(ExperimentalCamera2Interop::class)
    fun isBackCameraLevel3Device(cameraProvider: ProcessCameraProvider) : Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return CameraSelector.DEFAULT_BACK_CAMERA
                .filter(cameraProvider.availableCameraInfos)
                .firstOrNull()
                ?.let { Camera2CameraInfo.from(it) }
                ?.getCameraCharacteristic(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL) ==
                    CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3
        }
        return false
    }
}