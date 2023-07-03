package com.wcch.android.utils;

import android.text.TextUtils;


import com.wcch.android.environment.EnvironmentManager;
import com.wcch.android.interfaces.DownloadFileCallbackInterface;
import com.wcch.android.okhttp.IgnoreHttpsUtilsKt;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.Source;

/**
 * @title 简单封装okhttp
 */
public class OkHttpHelper {

    private static volatile OkHttpHelper instance;
    private static OkHttpClient okHttpClient;

    public static OkHttpHelper getInstance() {
        if (instance == null) {
            synchronized (OkHttpHelper.class) {
                if (instance == null) {
                    instance = new OkHttpHelper();
                }
            }
        }
        return instance;
    }

    private OkHttpHelper() {
        IgnoreHttpsUtilsKt ignoreHttpsUtilsKt = IgnoreHttpsUtilsKt.Companion.getInstance();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new ChunkedEncodingInterceptor())
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS);
        //测试包设置忽略ssl证书
        if(EnvironmentManager.VERSION_TYPE ==  EnvironmentManager.VersionType.Test &&
                ignoreHttpsUtilsKt.getSslSocketFactory() != null && ignoreHttpsUtilsKt.getTrustAllCerts() != null){
            builder.sslSocketFactory(ignoreHttpsUtilsKt.getSslSocketFactory(), ignoreHttpsUtilsKt.getTrustAllCerts()[0]);
            builder.hostnameVerifier(ignoreHttpsUtilsKt.getHostnameVerifier());
        }
        okHttpClient = builder.build();
    }

    /**
     * @title 异步get请求
     * @param url
     * @param callback
     */
    public void enqueueGet(String url, Callback callback) {
        Call call = buildGetCall(url);
        call.enqueue(callback);
    }

    /**
     * @title 异步post请求
     * @param url
     * @param callback
     */
    public void enqueuePost(String url, String requestBodyStr, HashMap<String, String> headerMap, Callback callback) {
        Call call = buildPostCall(url, requestBodyStr, headerMap);
        call.enqueue(callback);
    }

    /**
     * @title 同步get请求
     * @param url
     */
    public String get(String url) {
        Call call = buildGetCall(url);
        try {
            Response response = call.execute();
            if (response != null && response.isSuccessful() && response.body() != null) {
                return response.body().string();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @title 构建get请求
     * @param url
     */
    public Call buildGetCall(String url) {
        Request.Builder requestBuilder = buildCall(url).get();
        return okHttpClient.newCall(requestBuilder.build());
    }

    /**
     * @title 构建post请求
     * @param url
     */
    private Call buildPostCall(String url, String requestBodyStr, HashMap<String, String> headerMap) {
        Request.Builder requestBuilder = buildCall(url);
        generateHeader(requestBuilder, headerMap);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBodyStr);
        requestBuilder.post(requestBody);
        return okHttpClient.newCall(requestBuilder.build());
    }

    private Request.Builder buildCall(String url) {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalStateException("url can not be null");
        }
        return new Request.Builder().url(url);
    }

    private void generateHeader(Request.Builder requestBuilder, HashMap<String, String> headerMap) {
        if (headerMap != null && headerMap.size() > 0) {
            for(Map.Entry<String, String> entry :headerMap.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }
    /**
     * @title 下载文件
     * @param url
     * @param absoluteFilePath 完整文件路径
     * @param headerMap
     * @param callback
     */
    public void downloadFile(@NotNull String url, @NotNull final String absoluteFilePath, HashMap<String, String> headerMap, @NotNull final DownloadFileCallbackInterface callback) {
        Request.Builder requestBuilder = buildCall(url);
        headerMap.put("Accept-Encoding", "identity");
//        headerMap.put("identity", "");
        generateHeader(requestBuilder, headerMap);
        okHttpClient.newCall(requestBuilder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onResult(false);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    callback.onResult(false);
                    return;
                }
                ResponseBody body = response.body();
                if (body != null) {
                    if (body.contentLength() > 0) {
                        handleDataHasLenght(absoluteFilePath, body, callback);
                    }else {
                        callback.onResult(false);
                    }
                }else
                    callback.onResult(false);
            }
        });
    }


    /**
     * 处理流写文件然后回调
     * @param absoluteFilePath
     * @param body
     * @param callback
     */
    private void handleDataHasLenght(String absoluteFilePath, ResponseBody body, DownloadFileCallbackInterface callback) {
        InputStream inputStream = null;
        byte[] bytes = new byte[2048];
        int len;
        FileOutputStream fileOutputStream = null;
        try {
            File file = new File(absoluteFilePath);
            if (FileUtils.create(file)) {
                //用于下载进度监听
                long total = body.contentLength();
                long current = 0;
                inputStream = body.byteStream();
                fileOutputStream = new FileOutputStream(file);
                while ((len = inputStream.read(bytes)) != -1) {
                    current += len;
                    fileOutputStream.write(bytes, 0, len);
                }
                if (current == total) {//成功
                    fileOutputStream.flush();
                    callback.onResult(true);
                    return;
                }
            }
            callback.onResult(false);
        } catch (IOException e) {
            callback.onResult(false);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 当Transfer-Encoding是chunked时的拦截器，解析source重新生成responsebody
     */
    class ChunkedEncodingInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Response oringinalResponse = chain.proceed(chain.request());
            if (oringinalResponse.isSuccessful() && oringinalResponse.body() != null) {
                String transferEncoding = oringinalResponse.header("Transfer-Encoding");
                if (transferEncoding != null && transferEncoding.equals("chunked")) {
                    //okio解析buffer
                    BufferedSource source = oringinalResponse.body().source();
                    Buffer buffer = new Buffer();
                    while (!source.exhausted()) {
                        source.read(buffer, Long.MAX_VALUE);
                    }
                    if (buffer.size() > 0) {
                        //用buffer重新生成body，返回新的response
                        ResponseBody responseBody = ResponseBody.create(oringinalResponse.body().contentType(), buffer.size(), buffer);
                        return oringinalResponse.newBuilder().body(responseBody).build();
                    }
                }
            }
            return oringinalResponse;
        }
    }
}
