package com.igrs.tpsdk.service;


import com.igrs.sml.callback.TransferFileCallback;
import com.igrs.sml.util.L;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class UploadService {

    private final int POOL_SIZE = 10;// 单个CPU线程池大小
    private final ExecutorService executorService;// 线程池

    //ftp服务器地址
    //private String hostname = "192.168.12.233";


    private static UploadService instance = null;

    private UploadService() {
        L.e("UploadService->new->this:  " + hashCode());
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);
    }

    public static UploadService getInstance() {
        if (instance == null) {
            synchronized (UploadService.class) {
                if (instance == null) {
                    instance = new UploadService();
                }
            }
        }
        return instance;
    }

    public void sendFile(String host, String filePath, TransferFileCallback transferFileCallback) {
        executorService.execute(new UploadRunnable(host, filePath, transferFileCallback));
    }

    class UploadRunnable implements Runnable {

        //ftp服务器端口号默认为21
        private final Integer port = 2221;

        private String hostname = "";

        private final String username = "admin";
        private final String password = "123456";
        //超时时间
        public int timeOut = 2;
        //被动模式开关 如果不开被动模式 有防火墙 可能会上传失败， 但被动模式需要ftp支持
        public boolean enterLocalPassiveMode = true;

        private long totalsize;
        private long upload_sise;


//        packagingOptions { exclude 'META-INF/LICENSE' exclude 'META-INF/NOTICE' exclude 'META-INF/DEPENDENCIES' }
//
//
//
//        private FtpServer mFtpServer;
//        String localIp =getIpAddressString();
//        startFtpServer(localIp,8877);

        String TAG = "FTP";
//        private void startFtpServer(String hostip,int port){
//            FTPSServerSocketFactory
//            Log.d(TAG, "startFtpServer: hostip "+hostip+"port "+port);
//            FtpServerFactory serverFactory = new FtpServerFactory();
//            ListenerFactory factory = new ListenerFactory();
//            serverFactory.addListener("default",factory.createListener());
//            factory.setPort(port);
//            factory.setServerAddress(hostip);
//            serverFactory.addListener("default",factory.createListener());
//            BaseUser user = new BaseUser();
//            user.setName("admin");
//            user.setPassword("123456789");
//            user.setEnabled(true);
//            user.setMaxIdleTime(3000);
//            user.setHomeDirectory("/mnt/sdcard/ftp");
//            List<Authority> authorities = new ArrayList<>();
//            authorities.add(new WritePermission());
//            user.setAuthorities(authorities);
//            try{
//                serverFactory.getUserManager().save(user);
//            }
//            catch (FtpException e){
//                e.printStackTrace();
//            }
//            if(mFtpServer!=null){
//                mFtpServer.stop();
//            }
//            mFtpServer = serverFactory.createServer();
//            try{
//                mFtpServer.start();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            Log.d(TAG, "startFtpServer: startok");
//        }

        public  String getIpAddressString() {
            try {
                for (Enumeration<NetworkInterface> enNetI = NetworkInterface
                        .getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                    NetworkInterface netI = enNetI.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = netI
                            .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
            return "0.0.0.0";
        }


        private final String filePath;
        String fileName;
        String remotePath;
        TransferFileCallback transferFileCallback;

        public UploadRunnable(String host, String filePath, TransferFileCallback transferFileCallback) {
            this.hostname = host;
            this.filePath = filePath;
            try {
                int start = filePath.lastIndexOf("/");
                if (start != -1) {
                    fileName = filePath.substring(start + 1);
                } else {
                    fileName = filePath;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.remotePath = "";
            this.transferFileCallback = transferFileCallback;
        }

        @Override
        public void run() {
            boolean uploadResult = uploadFile(filePath, fileName, remotePath);
            L.e("uploadFile->--------uploadResult:"+uploadResult);
        }

        public synchronized boolean uploadFile(String FilePath, String FileName, String RemotePath) {
            L.e("uploadFile->--------\nFilePath:" + FilePath + "\nFileName:" + FileName + "\nRemotePath:" + RemotePath);

            FileInputStream fileInputStream = null;
            try {
                //设置存储路径


                fileInputStream = new FileInputStream(FilePath);
                totalsize = fileInputStream.available();

                String remoteFileName = RemotePath + "/" + FileName;
                L.e("remoteFileName:" + remoteFileName);

            } catch (IOException e) {
                L.e("uploadFile->e:" + e.toString());
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (Exception e) {
                    }
                }

            }
            return false;
        }
    }


}
