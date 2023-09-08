package com.wcch.android.upgrade.inter;

import com.wcch.android.upgrade.bean.EntityTest;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Create by RyanWang
 *
 * @date
 * @Dis
 */
public interface ServiceApi {

    // post 方法注解用于描述该请求的 HTTP 方法，@POST 表示该请求的方法为 POST 。
    //"user/login" 是请求的相对路径，Retrofit 会将其和 baseUrl 拼接起来，构成完整的 URL
    //方法参数注解用于描述方法参数作为请求的什么内容，@Body 表示把方法参数的序列化结果作为请求体。 LoginParam请求参数对象
    //Result 是网络访问结果类，包含 code, message, data 等。
    //@POST("user/login")
    //Call<Result<String>> login(@Body LoginParam loginParam);
   /* public interface ReqTest{

        @GET("/api/")
        Call<RequestBody> reqNow(@Query("key") String key);

        @GET("/api/")
        Call<ResM5080Version> reqNow1(@Query("key") String key);

    }
    //获取豆瓣Top250 榜单
    @GET("top250")
    Call<MovieSubject> getTop250(@Query("start") int start,@Query("count")int count);

    //获取豆瓣Top250 榜单
    @GET("top250")
    Observable<MovieSubject> getTop250(@Query("start") int start, @Query("count")int count);

    @FormUrlEncoded
    @POST("/x3/weather")
    Call<String> getWeather(@Field("cityId") String cityId, @Field("key") String key);*/
    /**
     * 接口地址
     */
    @POST("AppFiftyToneGraph/videoLink")
    Call<EntityTest> getAllVedio(@Body boolean once_no);

    @POST("AppFiftyToneGraph/videoLink")
    Observable<EntityTest> getAllVedioBy(@Body boolean once_no);

    @POST("AppFiftyToneGraph/videoLink")
    Single<EntityTest> getAllVedioBy2(@Body boolean once_no);
    @POST("/AppFiftyToneGraph/videoLink")
    Single<EntityTest> getAllVedioBy4();
    @GET("/AppFiftyToneGraph/videoLink")
    Single<EntityTest> getAllVedioBy3();

    @GET("v7/weather/now")
    Single<EntityTest> getAllVedioBy5();

    @GET("/invc/tt/QQBrowser_Setup_qb11.exe")
    Call<ResponseBody> DownloadTestFile();
}
