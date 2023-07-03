package com.igrs.sml.jni;

import android.view.Surface;

import com.igrs.sml.callback.DecoderCallback;
import com.igrs.sml.util.BaseUtil;
import com.igrs.sml.util.L;

import java.util.concurrent.LinkedBlockingQueue;

public class NdkDecoder extends Thread {

    static {
        System.loadLibrary("ndk-decoder");
    }

    private long id;

    private native long init(Surface surface, int type);

    private native void putData(long id, byte[] data, int length);

    private native void uninit(long id);

    /*
     * 桌面尺寸改变 通知回调函数
     * new_w，new_h: 最新的桌面宽高
     * */
    private void size_change_callback(int new_w, int new_h) {
        if(decoderCallback!=null){
            decoderCallback.decoderSizeChage(new_w,new_h,null);
        }
        L.i("desk_size_change_callback: ---------  new_w:" + new_w + "  new_h:" + new_h );
    }

    private boolean isExit;
    private Surface surface;
    private LinkedBlockingQueue<byte[]> mH264DataQueue;

    private int type = 0;
    private String dev_id;
    private DecoderCallback decoderCallback;


    private boolean findFirstKey = false;

    public NdkDecoder(LinkedBlockingQueue<byte[]> mH264DataQueue, Surface surface,String dev_id, DecoderCallback decoderCallback) {
        this.dev_id = dev_id;
        this.decoderCallback = decoderCallback;
        this.surface = surface;
        this.mH264DataQueue = mH264DataQueue;
        this.type = 3;
    }

    @Override
    public void run() {
        super.run();
        id = init(surface, type);
        L.i("NdkDecoder->id:" + id);
        if(id==-1){
            L.i("NdkDecoder->解码器启动失败:" + id);
            if(decoderCallback!=null){
                decoderCallback.decoderCallback(3);
            }
            return;
        }else{
            if(decoderCallback!=null){
                decoderCallback.decoderCallback(0);
            }
        }
        int count=0;
        long temp=0;
        while (!isExit) {
            try {
                byte[] data = mH264DataQueue.take();
                if (!findFirstKey) {
                    boolean isKey = false;
                    long time = System.currentTimeMillis();
                    L.e("NdkDecoder dev_id:"+dev_id+" need  first key ");
                    while (!isExit && !isKey) {
                        isKey = BaseUtil.checkIsIFrame(data);
                        if (!isKey) {
                            if(data.length>100){
                                StringBuffer sb = new StringBuffer();
                                for (int i = 0; i < 100; i++) {
                                    sb.append(data[i]+" ");
                                }
                                L.i("NdkDecoder dev_id:"+dev_id+" need  first key data["+sb+"]");
                            }
                            data = mH264DataQueue.take();
                        } else {
                            findFirstKey = true;
                            L.e("NdkDecoder  dev_id:"+dev_id+"  find  first key dif:" + (System.currentTimeMillis() - time));
                        }
                    }
                }
                if (data == null || data.length == 0) {
                    continue;
                }
                count++;
                if (System.currentTimeMillis() - temp >= 3000) {
                    temp = System.currentTimeMillis();
                    //RuntimeInfo.info_fps = count / 3;
                    //L.i("fps", "NdkDecoder--- dev_id:"+dev_id+" ------fps-->count:" + RuntimeInfo.info_fps+" queue.size="+mH264DataQueue.size());
                    count = 0;
                }
                putData(id, data, data.length);
            } catch (Exception e) {
                break;
            }
        }
    }

    public void reset(){

    }
    public void onDestroy() {
        isExit = true;
        uninit(id);
        try {
            interrupt();
        } catch (Exception e) {
        }
    }
}
