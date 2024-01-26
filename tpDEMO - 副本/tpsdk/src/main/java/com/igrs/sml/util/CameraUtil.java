package com.igrs.sml.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;

import com.igrs.tpsdk.opengl.VideoEncoder;
import com.igrs.tpsdk.service.EncoderH264CallBack;

import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 *
 */

public class CameraUtil {

    private String cameraID = "1";
    private final Context context;
    private VideoEncoder videoEncoder;

    private String ip;
    public CameraUtil(Context context,String ip) {
        this.context = context;
        this.ip = ip;
        int cameraCnt = Camera.getNumberOfCameras();
        L.i("-----------cameraCnt=" + cameraCnt);
        startCameraThread();
    }

    private Handler mCameraHandler;
    private HandlerThread mCameraThread;

    private void startCameraThread() {
        mCameraThread = new HandlerThread("CameraThread");
        mCameraThread.start();
        mCameraHandler = new Handler(mCameraThread.getLooper());
    }

    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreViewBuilder;
    private CameraCaptureSession mCameraSession;



    @SuppressLint("MissingPermission")
    public synchronized boolean openCamera(int cameraIndex) throws CameraAccessException {

        L.e("Camera2->openCamera->");

        final CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        closeCamera();
        Size selectSize = null;
        // end
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                L.i("PrivateMsg-inJava:: to set camera resolution.......Time out waiting to lock camera opening.");
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            if(cameraIndex == 0)
                cameraID = "0";
            else
                cameraID = "1";
            cameraManager.openCamera(cameraID, mStateCallback, mCameraHandler);
        } catch (InterruptedException e) {
            e.printStackTrace();
            L.e("Camera2->openCamera-> e:" + e.toString());
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
        return true;
    }

    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice cameraDevice) {
            {
                mCameraDevice = cameraDevice;
                try {
                    //打印支持帧率
                    //Range<Integer>[] fpsRanges = mCameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
                    // Log.d("qf", "SYNC_MAX_LATENCY_PER_FRAME_CONTROL: " + Arrays.toString(fpsRanges));

                    mPreViewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//                        mPreViewBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
//                        mPreViewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_USE_SCENE_MODE);
//                        mPreViewBuilder.set(CaptureRequest.CONTROL_SCENE_MODE, CaptureRequest.CONTROL_SCENE_MODE_FACE_PRIORITY);
//                        mPreViewBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE, CaptureRequest.STATISTICS_FACE_DETECT_MODE_SIMPLE);
                    mPreViewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
                    mPreViewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
 //                   mPreViewBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, new Range<>(0, 25));

                    if (videoEncoder == null) {
                        try {
                            videoEncoder = new VideoEncoder(640, 480);
                            //videoEncoder.setCameraIndex(Integer.valueOf(cameraID));
                            videoEncoder.startRecord();
                        } catch (Exception e) {
                        }
                    }

                    //初始化编码器
                    final Surface mediaUpSurface = videoEncoder.getInputSurface();
                    mPreViewBuilder.addTarget(mediaUpSurface);
                    cameraDevice.createCaptureSession(Arrays.asList(mediaUpSurface), new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                            mCameraSession = cameraCaptureSession;
                            try {
                                L.e("CameraInterface->onOpened->surface:" + " mediaUpSurface:" + (mediaUpSurface == null));
                                cameraCaptureSession.setRepeatingRequest(mPreViewBuilder.build(), new CameraCaptureSession.CaptureCallback() {
                                    @Override
                                    public void onCaptureCompleted( CameraCaptureSession session, CaptureRequest request,  TotalCaptureResult result) {
                                        super.onCaptureCompleted(session, request, result);
                                        videoEncoder.drainEncoder(System.nanoTime());
                                    }
                                }, null);

                            } catch (Exception e) {
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                            L.e("Camera2->openCamera->onConfigureFailed-----------");
                        }
                    }, mCameraHandler);

                } catch (CameraAccessException e) {
                    e.printStackTrace();
                    L.e("Camera2->openCamera->onConfigured-----------CameraAccessException:" + e.toString());
                }
                mCameraOpenCloseLock.release();
            }
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

    };
    private final Semaphore mCameraOpenCloseLock = new Semaphore(1);

    /**
     * Closes the current {@link CameraDevice}.
     */
    public void closeCamera() {
        try {
            //L.e("Camera2->closeCamera 1");
            mCameraOpenCloseLock.acquire();
            //L.e("Camera2->closeCamera 2");
            if (null != mCameraSession) {
                mCameraSession.close();
                mCameraSession = null;
            }
            //L.e("Camera2->closeCamera 3");
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            //L.e("Camera2->closeCamera 4");
            if (null != videoEncoder) {
                videoEncoder.release();
                videoEncoder = null;
            }
            //L.e("Camera2->closeCamera 5");
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            //L.e("Camera2->closeCamera end");
            mCameraOpenCloseLock.release();
        }
    }
    public void destroy(){
        closeCamera();
    }

    public void adjustCodecParameter(int vbv)
    {
        if (videoEncoder != null)
            videoEncoder.adjustCodecParameter(vbv);
    }


    public String getCameraId() {
        return cameraID;
    }

    public void changeCamera() {
        if (cameraID.equals("1")) {
            this.cameraID = "0";
        } else if (cameraID.equals("0")) {
            this.cameraID = "1";
        }
        //openCamera(Integer.valueOf(this.cameraID));

    }

}
