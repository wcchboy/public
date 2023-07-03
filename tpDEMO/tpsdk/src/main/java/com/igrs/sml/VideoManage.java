package com.igrs.sml;


import android.util.Log;

import com.igrs.sml.tcp.TcpCallback;
import com.igrs.sml.util.BaseUtil;
import com.igrs.sml.util.L;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class VideoManage implements TcpCallback{

    private boolean isExit = false;

    private HashMap<String, LinkedBlockingQueue<byte[]>> h264Map = new HashMap<>();

    private static VideoManage instance = null;
    private String TAG = "VideoManage";

    private VideoManage() {

    }

    public void clean(String dev_id) {
        try {
            h264Map.remove(dev_id);
        } catch (Exception e) {
        }
    }

    public LinkedBlockingQueue<byte[]> getH264Queue(String dev_id) {
        LinkedBlockingQueue<byte[]> h264Queue = h264Map.get(dev_id);
        L.i("VideoManage->getH264Queue->h264Queue:" + h264Queue);
        if (h264Queue == null) {
            h264Queue = new LinkedBlockingQueue<>();
            h264Map.put(dev_id, h264Queue);
        }
        return h264Queue;
    }

    public static VideoManage getInstance() {
        if (instance == null) {
            synchronized (VideoManage.class) {
                if (instance == null) {
                    instance = new VideoManage();
                }
            }
        }
        return instance;
    }


    public void onDestroy() {
        isExit = true;
        synchronized (VideoManage.class) {
            instance = null;
        }
    }

    long temp = 0;
    int count = 0;

    private HashMap<String, Integer> needKeyMap = new HashMap<>();

    private long last_video = 0;
    @Override
    public void rev_msg(String dev_id, byte type, byte[] data) {
        Log.d(TAG, "rev_msg: dev_id:"+dev_id+", type:"+type);
//        byte[] datas= new byte[data.length-8];
//        System.arraycopy(data, 8, datas, 0, datas.length);
//        FileUtils.writeFileToSDCard(datas,"h264",dev_id+".h264",true,true);

//        if (System.currentTimeMillis() - last_video > 80) {
//            L.i("dif","rev h264 dif:" + (System.currentTimeMillis() - last_video));
//        }
        last_video = System.currentTimeMillis();

        LinkedBlockingQueue<byte[]> h264Queue = h264Map.get(dev_id);
        if (h264Queue == null) {
            h264Queue = new LinkedBlockingQueue<>();
            h264Map.put(dev_id, h264Queue);
            needKeyMap.put(dev_id, 1);
        }
        if (h264Queue.size() > 3000) {
            L.i("rev h264 >3000 ->clean ");
            needKeyMap.put(dev_id, 1);
            h264Queue.clear();
        }
        Integer needKey = needKeyMap.get(dev_id);
        if(needKey!=null && needKey==1){
            if (!BaseUtil.checkIsIFrame(data)){
                return;
            }else{
                needKeyMap.put(dev_id,0);
            }
        }
        try {
            h264Queue.put(data);
            count++;
            if (System.currentTimeMillis() - temp >= 3000) {
                temp = System.currentTimeMillis();
                L.i("fps", "rev->dev_id:"+dev_id+" ----->h264 ????(only one = ) fps:" + count / 3+" size:"+h264Queue.size());
                count = 0;
            }

        } catch (Exception e) {
        }
        if (h264Queue.size() > 30 && h264Queue.size() % 60 == 0) {
            L.i("rev h264 dev_id->" + dev_id + " map  size=" + h264Queue.size());
        }
    }

}
