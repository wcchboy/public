package com.wcch.android.upgrade.util;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Create by RyanWang
 *网络请求Retrofit的帮助类
 * @date
 * @Dis
 */
public class RetrofitHelper {
    private static final int DEFAULT_TIME_OUT = 30;//超时时间 10s
    private static final int DEFAULT_READ_TIME_OUT = 30;
    private Retrofit mRetrofit;

    private RetrofitHelper(){
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);//连接超时时间
        builder.readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);//读操作超时时间

        // 添加公共参数拦截器
       /* HttpCommonInterceptor commonInterceptor = new HttpCommonInterceptor.Builder()
                .addHeaderParams("cookie", OrtherUtil.getCookie(BasicUtil.getUserId()))//添加的公共请求头部（即cookie信息）
                .build();
        builder.addInterceptor(commonInterceptor);
        mRetrofit = new Retrofit.Builder()
                .client(builder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MyGsonConverterFactory.create())
                .baseUrl(Interface_Url.HTTPURL)//添加请求的URL地址（前面公共部分）
                .build();*/
    }

    //使用静态内部类的方式获取单例模式
//    private static class SingletonHolder{
//        private static final RetrofitHelper INSTANCE = new RetrofitHelper();
//    }
    //获取RetrofitHelper对象，为保证每次的cookie信息为最新的，所以每次调用都重新给mRetrofit赋值
    public static RetrofitHelper getInstance(){
//        return SingletonHolder.INSTANCE;
        return new RetrofitHelper();
    }
    /**
     * 获取服务对象 以接口的形式
     * @param classz 接口类
     * @param <T>
     * @return
     */
    public <T> T getService(Class<T> classz){
        return mRetrofit.create(classz);
    }
}
