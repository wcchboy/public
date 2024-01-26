package com.igrs.sml;


import android.media.AudioManager;
import android.util.Log;

import com.igrs.audio.OpusPlayer;
import com.igrs.sml.tcp.TcpCallback;
import com.igrs.sml.util.L;

import java.util.HashMap;

public class AudioManage implements TcpCallback{

    private boolean isExit = false;

    private HashMap<String, OpusPlayer> playerMap = new HashMap<>();

    private static AudioManage instance = null;
    private String TAG = "AudioManage";

    private AudioManage() {

    }

    public void clean(String dev_id) {
        try {
            OpusPlayer player = playerMap.remove(dev_id);
            player.stop();
        } catch (Exception e) {
        }
    }

    public static AudioManage getInstance() {
        if (instance == null) {
            synchronized (AudioManage.class) {
                if (instance == null) {
                    instance = new AudioManage();
                }
            }
        }
        return instance;
    }

    public void startPlay(AudioManager audioManager,String dev_id){
        Log.d(TAG, "startPlay dev_id:"+dev_id);
        OpusPlayer player = playerMap.get(dev_id);
        if (player == null) {
            player = new OpusPlayer(audioManager);
            playerMap.put(dev_id,player);
            player.start();
        }
    }

    public void onDestroy() {
        isExit = true;
        synchronized (AudioManage.class) {
            instance = null;
        }
    }
    @Override
    public void rev_msg(String dev_id, byte type, byte[] data) {
        //
        Log.d(TAG, "startPlay rev_msg:"+dev_id+" type:"+type);
        OpusPlayer player = playerMap.get(dev_id);
        if (player != null) {
            player.putData(data);
        }else{
            if(System.currentTimeMillis()%50_000==0){
                L.i("rev audio dev_id->" + dev_id + " player is null");
            }
        }
    }

}
