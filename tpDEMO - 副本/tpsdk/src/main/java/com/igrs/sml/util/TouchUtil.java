package com.igrs.sml.util;


import android.content.Context;
import android.hardware.input.InputManager;
import android.os.SystemClock;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.igrs.sml.tcp.TcpCallback;
import com.igrs.tpsdk.ProjectionSDK;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

public class TouchUtil {

    public static final int key_pause = -100; //-100 切换至后台
    public static final int key_resume = -101;//-101 切换至前台
    public static final int key_fps = -102;//设置帧率
    public static final int key_bit = -103;//设置码率


    private static TouchUtil instance = null;

    private TouchUtil() {
    }

    public static TouchUtil getInstance() {
        if (instance == null) {
            synchronized (TouchUtil.class) {
                if (instance == null) {
                    instance = new TouchUtil();
                }
            }
        }
        return instance;
    }


    Context context;
    private float width_scale = 1.0f;
    private float height_scale = 1.0f;
    private int width = 1920;
    private int height = 1080;

    public TouchUtil init(Context context, int width, int height, EncoderSetCallBack reqestKeyCallBack) {
        this.context = context;
        this.width = width;
        this.height = height;

        ProjectionSDK.getInstance().setTcpTouchCallback(new TcpCallback() {
            @Override
            public void rev_msg(String dev_id, byte type, byte[] data) {
                //L.e("SDKService onTuuchEventCallback->ip=" + ip + " event:" + event.length);
                if (data.length > 3) {
                    ByteBuffer buffer = ByteBuffer.allocate(data.length);
                    buffer.put(data);
                    buffer.flip();
                    try {
                        int result = doMotionEvent(dev_id, buffer);
                        if (result == 1 && reqestKeyCallBack != null) {
                            reqestKeyCallBack.requestKeyCallback(dev_id);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        L.e("TouchUtil onTuuchEventCallback->handleMessage=e:" + e + " event:" + data.length);
                    }
                }
            }
        });
        return this;
    }

    public TouchUtil reSize(float width_scale, float height_scale) {
        this.width_scale = width_scale;
        this.height_scale = height_scale;
        return this;
    }


    /**
     * 后台和前台切换  压后台后服务器会停止发送数据;到前台后服务器恢复发送数据
     *
     * @param isPause true:切换至后台; false:切换至前台
     * @return true调用成功；false调用失败（可能是未初始化成功）
     */
    public boolean setPause(String dev_id, boolean isPause) {
        //-100 切换至后台 ；-101 切换至前台
        int pasu = isPause ? -100 : -101;
        sendMotionEvent(dev_id, pasu, 0, 0);
        L.e("TouchUtil setPause-->isPause:" + isPause + " time:" + System.currentTimeMillis());
        return true;
    }


    public void sendKeEvent(String dev_id, int action, int key) {
        ByteBuffer buffer = ByteBuffer.allocate(100);
        buffer.clear();
        buffer.putInt(action);
        buffer.putInt(key);
        L.i("TouchUtil sendKeEvent-----action:" + action + " key:" + key);
        buffer.flip();
        int len = buffer.limit() - buffer.position();
        byte[] bytes = new byte[len];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = buffer.get();
        }
        ProjectionSDK.getInstance().sendTouchMsg(dev_id, bytes);
    }

    public void sendMotionEvent(String dev_id, int action, float x, float y) {
        ByteBuffer buffer = ByteBuffer.allocate(1000);
        buffer.putInt(0);
        buffer.putInt(action);
        buffer.putInt(1);//point
        buffer.putInt(0);//id
        buffer.putInt((int) (x * width_scale));//x
        buffer.putInt((int) (y * height_scale));//y
        L.i("TransNodeActivity sendMotionEvent----- action:" + action + " width_scale:" + width_scale + " height_scale:" + height_scale);
        buffer.flip();
        int len = buffer.limit() - buffer.position();
        byte[] bytes = new byte[len];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = buffer.get();
        }
        ProjectionSDK.getInstance().sendTouchMsg(dev_id, bytes);
    }


    static boolean hasDown = false;


    private int oldMovex = 0;
    private int oldMovey = 0;


    long lastDown = 0;

    public synchronized int doMotionEvent(String peerIp, ByteBuffer byteBuffer) {
        //0 0 0 0 64 54 -42 4 1 0 0 0 0 0 0 0 84 8 0 0 62 1 0 0
        //0 0 0 0 -64 76 -115 107 1 0 0 0 1 0 0 0 2 10 0 0 -103 1 0 0

        int time = byteBuffer.getInt();//time
        int action = byteBuffer.getInt();
        //L.i("SDKService doMotionEvent->------" + peerIp + "------action=" + action);
        //-100 切换至后台 ；-101 切换至前台
        if (action == -100) {
            //TaskManage.getInstance().setSendDeskSwitch(peerIp, false);
            return 0;
        } else if (action == -101) {
            //TaskManage.getInstance().setSendDeskSwitch(peerIp, true);
            return 1;
        }

        byteBuffer.getInt();//pointerCount
        byteBuffer.getInt();//pid

        int x = byteBuffer.getInt();
        int y = byteBuffer.getInt();


        if (action == MotionEvent.ACTION_UP) {
            hasDown = false;
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (oldMovex == x && oldMovey == y) {
                return 0;
            } else {
                oldMovex = x;
                oldMovey = y;
            }
        }


        L.i("fps", peerIp + " AcsInput action:" + action + " width_scale:" + width_scale + " height_scale:" + height_scale + " ox:" + x + " oy:" + y);
        if (x < 0 || x > width || y < 0 || y > height) {

            L.e(peerIp + " AcsInput action:" + action + " width_scale:" + width_scale + " height_scale:" + height_scale + " ox:" + x + " oy:" + y + " ========================return \n\n\n\n error ");
            return 0;
        }
        if (action == MotionEvent.ACTION_UP) {
            hasDown = false;
            L.e("SDKService doMotionEvent->------action:" + action + " time:" + time + " dif:" + (SystemClock.uptimeMillis() - lastDown));
        } else if (action == KeyEvent.KEYCODE_CALL) {
            return 0;
        } else if (action == KeyEvent.KEYCODE_BACK) {
            try {
                // L.i("SDKService onTuuchEventCallback->keyevent:"+new String(event));
                String keyCommand = "input keyevent " + KeyEvent.KEYCODE_BACK;
                Runtime.getRuntime().exec(keyCommand);
            } catch (IOException e) {
            }
            return 0;
        } else if (action == MotionEvent.ACTION_DOWN) {
            hasDown = true;
            lastDown = SystemClock.uptimeMillis();
            L.i("SDKService doMotionEvent->------action:" + action + " time:" + time);
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (!hasDown) {
                return 0;
            }
        } else {
            L.e(peerIp + " AcsInput action:" + action + " width_scale:" + width_scale + " height_scale:" + height_scale + " ox:" + x + " oy:" + y + " =======unknow action=================return \n\n\n\n error ");
            return 0;
        }
        MotionEvent.PointerProperties[] pointerProperties = new MotionEvent.PointerProperties[1];
        MotionEvent.PointerCoords[] pointerCoords = new MotionEvent.PointerCoords[1];
        pointerProperties[0] = new MotionEvent.PointerProperties();
        pointerProperties[0].id = 0;
        pointerProperties[0].toolType = 1;
        pointerCoords[0] = new MotionEvent.PointerCoords();
        pointerCoords[0].x = x * width_scale;
        pointerCoords[0].y = y * height_scale;

       long eventTime = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(eventTime, eventTime, action, 1, pointerProperties, pointerCoords, 0, 0, 1.0f, 1.0f, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
        invokeInjectInputEvent(event);
        L.d("AcsInput  a:" + action + " ws:" + width_scale + " hs:" + height_scale + " ox:" + x + " oy:" + y + " x = " + pointerCoords[0].x + " y:" + pointerCoords[0].y);
        return 0;
    }

    public void invokeInjectInputEvent(InputEvent event) {
        try {
            InputManager im = (InputManager) context.getSystemService(Context.INPUT_SERVICE);
            Class[] paramTypes = new Class[2];
            paramTypes[0] = InputEvent.class;
            paramTypes[1] = Integer.TYPE;

            Object[] params = new Object[2];
            params[0] = event;
            params[1] = 0;

            Method hiddenMethod = im.getClass().getMethod("injectInputEvent", paramTypes);
            hiddenMethod.invoke(im, params);
        } catch (Exception e) {
            L.e("----e:" + e.toString());
            e.printStackTrace();
        }
    }


    public interface EncoderSetCallBack {
        void requestKeyCallback(String peerIp);

        void setFps(int fps);

        void setBit(int bit);
    }
}
