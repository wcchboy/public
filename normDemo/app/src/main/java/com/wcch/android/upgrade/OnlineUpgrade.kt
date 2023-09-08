package com.wcch.android.upgrade

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.wcch.android.upgrade.bean.EntityTest
import com.wcch.android.upgrade.inter.ServiceApi
import com.wcch.android.upgrade.util.FileTools
import io.reactivex.Observable
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit


/**
 * Create by RyanWang
 * @date
 * @Dis
 */
class OnlineUpgrade {
    val TAG = "OnlineUpgrade"

    /*val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10,TimeUnit.SECONDS)
        .writeTimeout(10,TimeUnit.SECONDS)
        .addInterceptor(object : Interceptor{
            override fun intercept(chain: Interceptor.Chain): Response {


                //统一添加接口需要的key
                val realInterceptorChain = chain
                val request = realInterceptorChain.request()
                val builder = request.url.newBuilder()
                builder.addQueryParameter("key", "key")
                return chain.proceed(request.newBuilder().url(builder.build()).build());
            }
        }).build()*/
    /*var retrofit = Retrofit.Builder()
        .baseUrl(versionPath)
        .addConverterFactory(GsonConverterFactory.create())
        .build()*/
    /*var retrofit: Retrofit = Retrofit.Builder()
        .client(builder.build())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .baseUrl(HttpManager.BASE_URL)
        .build()*/



    //val retrofit = Retrofit.Builder().baseUrl(versionPath).build()
    //val retrofit = Retrofit.Builder().baseUrl(versionPath).client(client).build()
    /*val retrofit = Retrofit.Builder().baseUrl(versionPath)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client).build()*/
    /*val retrofit = Retrofit.Builder().baseUrl(versionPath).addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(client).build()*/
    /*var retrofit: Retrofit? = Retrofit.Builder()
        .baseUrl(versionPath)
        .addConverterFactory(GsonConverterFactory.create())
        .build()*/


    val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10,TimeUnit.SECONDS).build()
    var retrofit = Retrofit.Builder()
        .baseUrl("https://devapi.qweather.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(client)
        .build()

    public fun test(){
        //var mMovieService = RetrofitServiceManager.getInstance().create(ServiceApi::class.java)
        //var apiService = RetrofitServiceManager.getInstance().create(ServiceApi::class.java)

        val apiService = retrofit.create(ServiceApi::class.java)
        apiService.allVedioBy5.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<EntityTest>{
                override fun onSubscribe(d: Disposable) {
                    Log.i("tag", "------------------onSubscribe----->")
                }

                override fun onError(e: Throwable) {
                    Log.i("tag", "-----------------onError-----> ${e}")
                    Log.i("tag", "-----------------onError-----> ${e.printStackTrace()}")
                }

                override fun onSuccess(t: EntityTest) {
                    Log.i("tag", "onSuccess----->")
                    val entity = t.data
                    Log.i("tag", "onSuccess-----> $entity")
                    Log.i("tag", "onSuccess----->${t.code}")
                }

            })

        /*try {
            val apiService = retrofit.create(ServiceApi::class.java)
            val call: Call<EntityTest> = apiService.getAllVedio(true)
            call.enqueue(object : Callback<EntityTest>(){
                override fun onFailure(call: Call<EntityTest>, t: Throwable) {
                    Log.i("tag", "onFailure----->$t")
                }

                override fun onResponse(call: Call<EntityTest>,response: retrofit2.Response<EntityTest>) {
                    val entity: EntityTest? = response.body()
                    Log.i("tag", "onResponse----->" + entity.getMsg());
                }
            })
        }catch (e:Exception){
            println(e)
        }*/

        /*val apiService = retrofit.create(ServiceApi::class.java)
        val observable: Observable<EntityTest> = apiService.getAllVedioBy(true)
        observable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : Subscriber<EntityTest?>() {
                    fun onCompleted() {}
                    fun onError(e: Throwable?) {}
                    fun onNext(retrofitEntity: EntityTest) {
                        //tvMsg.setText("""无封装：${retrofitEntity.getData().toString()}""".trimIndent())
                    }
                }
            )*/
    }

    public fun downloadTest(mContext:Context,fileName: String,downloadUrl:String){
        //https://dldir1.qq.com/invc/tt/QQBrowser_Setup_qb11.exe
        val baseUrl ="https://dldir1.qq.com/"
        val rootPath = mContext?.filesDir?.absolutePath+ File.separator + "upgrade"
        val dataPath: String = rootPath+File.separator+fileName

        val dataFile = File(dataPath)
        if (dataFile.exists())  {
            Log.d(TAG, "downloadTest 文件已存在")
        }else{
            Log.d(TAG, "downloadTest 文件不存在 去下载")

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
    fun DownloadZipFile(filePath: String, name: String) {
        val baseUrl ="https://dldir1.qq.com/"
        //网络请求超时
        Observable.just<Any>(baseUrl)
            .map<ResponseBody?> { url: Any? ->
                val retrofitUtils = retrofit.create(ServiceApi::class.java)
                val call = retrofitUtils.DownloadTestFile()
                val response = call.execute()
                response.body()
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Consumer<ResponseBody> {
                @SuppressLint("CheckResult")
                @Throws(Exception::class)
                override fun accept(responseBody: ResponseBody) {
                    //网络请求成功，读取文件保存到sd卡
                    val tools = FileTools()
                    tools.SaveZipFile(responseBody, filePath, name)
                }
            }, object : Consumer<Throwable?> {
                @Throws(Exception::class)
                override fun accept(throwable: Throwable?) {
                    /**
                     * 网络请求失败
                     * 具体可以自己实现
                     */
                }
            })
    }

    public fun getM5080Version():String{


        return ""

    }



}


