package com.igrs.sml.util;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.MotionEvent;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

/** 获取su进程执行 */
public class SuProcess
{
    private static long dellayMills = 0;
    private static long AddMills = 0;

    /** 添加延时 */
    public static void Delay(long mills)
    {
        AddMills = mills;
        dellayMills += mills;
    }

    /** 执行命令 */
    public static void Exec(final String cmd)
    {
        Runnable r = new Runnable()
        {
            final long AddMillsT = SuProcess.AddMills;
            @Override
            public void run()
            {
                try
                {
                    dellayMills -= AddMillsT;
                    Process process = Runtime.getRuntime().exec("su");	// 获取su进程
                    // 获取输出流
                    OutputStream outputStream = process.getOutputStream();
                    DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                    dataOutputStream.writeBytes(cmd);
                    dataOutputStream.flush();
                    dataOutputStream.close();
                    outputStream.close();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        };
        AddMills = 0;
        if(dellayMills <= 0) dellayMills = 0;
        new Handler().postDelayed(r, dellayMills);
    }

    /** 模拟按键点击 */
    public static void KeyClick(int key)
    {
        String cmd = "input keyevent " + key;
        Exec(cmd);
    }

    /** 模拟屏幕点击坐标 x,y */
    public static void ScreenClick(int x, int y)
    {
        String cmd = "input tap " + x + " " + y;
        Exec(cmd);
    }

    /** 模拟输入信息text */
    public static void InputText(String text)
    {
        String cmd = "input text '" + text + "'";
        Exec(cmd);
    }

    /** 模拟滑动屏幕 */
    public static void ScreenSwipe(final int x1, final int y1, final int x2, final int y2)
    {
        Delay(1100);	// 滑动屏幕时执行延时
        String cmd = "input swipe" + " " + x1 + " " + y1 + " " + x2 + " " + y2;
        Exec(cmd);
    }

    /** 向右滑动屏幕 */
    public static void ScreenSwipe_Right(Context context)
    {
        DisplayMetrics screen = getScreeenSize(context);

        int space = screen.widthPixels / 4;

        int x1 = space;
        int y1 = screen.heightPixels / 2;
        int x2 = screen.widthPixels - space;
        int y2 = y1;

        ScreenSwipe(x1, y1, x2, y2);
    }

    /** 向左滑动屏幕 */
    public static void ScreenSwipe_Left(Context context)
    {
        DisplayMetrics screen = getScreeenSize(context);

        int space = screen.widthPixels / 4;

        int x1 = screen.widthPixels - space;
        int y1 = screen.heightPixels / 2;
        int x2 = space;
        int y2 = y1;

        ScreenSwipe(x1, y1, x2, y2);
    }

    /** 向上滑动屏幕 */
    public static void ScreenSwipe_Up(Context context)
    {
        DisplayMetrics screen = getScreeenSize(context);

        int x1 = screen.widthPixels / 2;
        int y1 = screen.heightPixels - 10;
        int x2 = x1;
        int y2 = 10;

        ScreenSwipe(x1, y1, x2, y2);
    }

    /** 向下滑动屏幕 */
    public static void ScreenSwipe_Down(Context context)
    {
        DisplayMetrics screen = getScreeenSize(context);

        int x1 = screen.widthPixels / 2;
        int y1 = 10;
        int x2 = x1;
        int y2 = screen.heightPixels - 10;

        ScreenSwipe(x1, y1, x2, y2);
    }

    /** 获取屏幕分辨率 */
    private static DisplayMetrics getScreeenSize(Context context)
    {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        // int screenWidth = dm.widthPixels; // 屏幕宽度
        // int screenHeight = dm.heightPixels; // 屏幕高度
        return dm;
    }


    float toScreenX;
    float toScreenY;


//    long downTime, long eventTime,
//    int action, int pointerCount, PointerProperties[] pointerProperties,
//    MotionEvent.PointerCoords[] pointerCoords, int metaState, int buttonState,
//    float xPrecision, float yPrecision, int deviceId,
//    int edgeFlags, int source, int flags


    static boolean hasDown=false;
    private static int oldMovex=0;
    private static int oldMovey=0;
    public static long doMotionEvent(ByteBuffer byteBuffer,float width_scale,float height_scale) {
        byteBuffer.getInt();//time
        int action = Common.toLittleEndian(byteBuffer.getInt());
        byteBuffer.getInt();//pointerCount
        byteBuffer.getInt();//pid

        int x = Common.toLittleEndian(byteBuffer.getInt());
        int y = Common.toLittleEndian(byteBuffer.getInt());
        if(x<0 || x>1920 || y<0 || y>1080){
            L.e("AcsInput action:"+action+" width_scale:"+width_scale+" height_scale:"+height_scale+" ox:"+x+" oy:"+y+" ========================return \n\n\n\n error ");
            return 0;
        }
        if(action== MotionEvent.ACTION_DOWN){
            hasDown = true;
        }else if(action== MotionEvent.ACTION_MOVE){

            if(!hasDown){
                return 0;
            }

            if(oldMovex==x && oldMovey==y){
                return 0;
            }else{
                oldMovex = x;
                oldMovey = y;
            }
        }else if(action== MotionEvent.ACTION_UP){
            hasDown = false;
        }

        MotionEvent.PointerProperties[] pointerProperties = new MotionEvent.PointerProperties[1];
        MotionEvent.PointerCoords[] pointerCoords = new MotionEvent.PointerCoords[1];
        pointerProperties[0] = new MotionEvent.PointerProperties();
        pointerProperties[0].id = 0;
        pointerProperties[0].toolType = 1;
        pointerCoords[0] = new MotionEvent.PointerCoords();
        pointerCoords[0].x = x*width_scale;
        pointerCoords[0].y = y*height_scale;

        L.d("AcsInput  a:"+action+" ws:"+width_scale+" hs:"+height_scale+" ox:"+x+" oy:"+y+" x = " + pointerCoords[0].x+" y:"+pointerCoords[0].y);
        long eventTime = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(eventTime, eventTime, action, 1, pointerProperties, pointerCoords, 0, 0, 1.0f, 1.0f, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0);
        invokeInjectInputEvent(event);
        return 0;
    }

    public static  void invokeInjectInputEvent(MotionEvent event) {
        Class cl = InputManager.class;
        try {
            Method method = cl.getMethod("getInstance");
            Object result = method.invoke(cl);
            InputManager im = (InputManager) result;
            method = cl.getMethod("injectInputEvent", InputEvent.class, int.class);
            method.invoke(im, event, 0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            L.e("IllegalAccessException----e:"+e.toString());
        }  catch (IllegalArgumentException e) {
            L.e("IllegalArgumentException----e:"+e.toString());
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            L.e("NoSuchMethodException----e:"+e.toString());
        }catch (InvocationTargetException e) {
            e.printStackTrace();
            L.e("InvocationTargetException----e:"+e.toString());
        }
    }

    public static void injectInputEvent(Context context,InputEvent event) {
        InputManager im = (InputManager) context.getSystemService(Context. INPUT_SERVICE);

        Class[] paramTypes = new Class[2];
        paramTypes[0] = InputEvent.class;
        paramTypes[1] = Integer.TYPE;

        Object[] params = new Object[2];
        params[0] = event;
        params[1] = 0;

        try {
           // L.i("injectInputEvent----im:"+im+" params:"+params);
            Method hiddenMethod = im.getClass().getMethod("injectInputEvent", paramTypes);
            hiddenMethod.invoke(im, params);
        } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            L.e("----e:"+e.toString());
            e.printStackTrace();
        }
    }

}