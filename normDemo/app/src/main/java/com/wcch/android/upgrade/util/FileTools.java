package com.wcch.android.upgrade.util;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import com.wcch.android.upgrade.inter.ServiceApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Create by RyanWang
 *
 * @date
 * @Dis
 */
public class FileTools {
    private static final String TAG = "FileTools";

    /**
     *
     * @param body
     * @param filename  例如 xx.png
     * @return
     */
    private boolean writeResponseBodyToDisk(ResponseBody body,String filename) {
        try {
            File futureStudioIconFile = new File(Environment.getExternalStorageDirectory() + File.separator + filename);
            Log.d(TAG, "writeResponseBodyToDisk: " + Environment.getExternalStorageDirectory().getAbsolutePath());
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }


    /**
     * 下载zip文件
     * retrofit
     * rxjava
     * lamda
     * 结合使用
     * @param filePath
     * @param name
     */
    @SuppressLint("CheckResult")
    public void downloadZipFile(String filePath, String name) {
        String baseUrl ="https://dldir1.qq.com/";
        //网络请求超时
        OkHttpClient client = new OkHttpClient.Builder().
                connectTimeout(10, TimeUnit.SECONDS).
                readTimeout(5, TimeUnit.SECONDS).
                writeTimeout(5, TimeUnit.SECONDS).build();
        Observable.just(baseUrl)
                .map(url ->{
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(url).client(client).build();

                    ServiceApi retrofitUtils = retrofit.create(ServiceApi.class);
                    Call<ResponseBody> call = retrofitUtils.DownloadTestFile();

                    retrofit2.Response<ResponseBody> response = call.execute();
                    return response.body();
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        Log.d(TAG, "网络请求成功，读取文件保存到sd卡");
                        //网络请求成功，读取文件保存到sd卡
                        SaveZipFile(responseBody,filePath,name);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        /**
                         * 网络请求失败
                         * 具体可以自己实现
                         */
                        Log.d(TAG, "网络请求失败");
                    }
                });
    }
    /**
     * 保存文件到sd卡
     * @param zipData
     * @param filePath
     */
    public void SaveZipFile(ResponseBody zipData,String filePath,String name){
        InputStream is = null;
        byte[] buf = new byte[4096];
        int len = 0;
        FileOutputStream fos = null;

        try {
            is = zipData.byteStream();
            long total = zipData.contentLength();
            File file = new File(filePath, getNameFromUrl(name));
            fos = new FileOutputStream(file);
            long sum = 0;

            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
                sum += len;
                int progress = (int) (sum * 1.0f / total * 100);
                // 下载中，可以自行加入进度条

            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
            }
        }

        /**
         * 下载完成。可以解压
         * 如果是经过加密的zip包，先解密，再解压
         */

    }

    /**
     * 判断文件是否存在
     * @param filePath zip下载到的文件夹路径名称
     * @param zipfileName zip名称
     */
    @SuppressLint("LongLogTag")
    public void fileIsExists(String filePath, String zipfileName) {
        try {
            File downloadFile = new File(filePath);
            if (!downloadFile.exists()) {
                /**
                 * 文件夹不存在
                 * 创建文件夹
                 * 下载文件
                 */
                downloadFile.mkdirs();
                downloadZipFile(filePath,zipfileName);//下载文件
            }else if (downloadFile.exists()){
                /**
                 * 文件夹存在
                 * 检测文件是否存在
                 * 若文件存在--->检测版本号--->是否需要更新资源（需要把旧的资源先删掉）
                 * 若文件不存在，则下载资源文件解压，并把压缩包删除
                 */
                File file = new File(filePath,getNameFromUrl(zipfileName));//zip解压后文件夹路径
                if (!file.exists()){
                    //文件不存在，下载文件
                    downloadZipFile(filePath,zipfileName);//下载文件
                }else if (file.exists()){
                    /**
                     * 文件存在，可以做显示操作等等
                     * 也可以做资源版本的检测更新
                     */
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("HttpDownUtil:fileIsExists",e.getMessage());
        }
    }

    /**
     * 判断文件是否存在
     * @param filePath 下载到的文件夹路径名称
     * @param fileName  名称
     */
    @SuppressLint("LongLogTag")
    public void fileIsExists_2(String filePath, String fileName) {
        try {
            File downloadFile = new File(filePath);
            if (!downloadFile.exists()) {
                Log.d(TAG, "文件夹不存在");
                /**
                 * 文件夹不存在
                 * 创建文件夹
                 * 下载文件
                 */
                downloadFile.mkdirs();
                downloadZipFile(filePath,fileName);//下载文件
            }else if (downloadFile.exists()){
                Log.d(TAG, "文件夹存在");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("HttpDownUtil:fileIsExists",e.getMessage());
        }
    }

    public String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
