package com.wcch.android.broadcast;

import com.wcch.android.utils.LogUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by RyanWang on 2024/3/19.
 *
 * @Description: 接受
 */
public class UDPReceiver {
    private static final String TAG = "UDPReceiver";

    public static void receiveBroadcast(int port)
    {
        LogUtils.d(TAG, "receiveBroadcast port: " + port);

        DatagramSocket socket = null;
        try
        {
            socket = new DatagramSocket(port);

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            //socket.receive(receivePacket);
            //String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());


            while (true)
            {
                socket.receive(receivePacket);
                String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Received message: " + receivedMessage);

                // 获取发送方的IP地址和端口号
                InetAddress senderAddress = receivePacket.getAddress();
                int senderPort = receivePacket.getPort();

                // 构造回复消息
                String replyMessage = "This is a reply message from B";

                // 发送回复消息给发送方
                byte[] replyData = replyMessage.getBytes();
                DatagramPacket replyPacket = new DatagramPacket(replyData, replyData.length, senderAddress, senderPort);
                socket.send(replyPacket);

                System.out.println("Reply message sent to " + senderAddress.getHostAddress() + ":" + senderPort);
            }

            //            LogUtils.d(TAG,"Received message: " + receivedMessage);
            //            String ip = NetworkCheckUtil.getIpAddress(MyApplicaion.getAppContext());
            //            LogUtils.d(TAG,"Received ip:"+ip);
            //            String name = Build.MODEL;
            //            LogUtils.d(TAG,"Received name:"+name);
            // 在这里编写回复逻辑
            // 例如，您可以调用下面的方法来发送回复消息
            //sendResponse(receivePacket.getAddress(), receivePacket.getPort(), "Response message");

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (socket != null)
            {
                socket.close();
            }
        }
    }
}