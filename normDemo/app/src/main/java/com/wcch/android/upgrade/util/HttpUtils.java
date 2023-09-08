package com.wcch.android.upgrade.util;

import android.util.Log;

import com.google.gson.Gson;
import com.wcch.android.upgrade.bean.BaseResponseBean;

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Create by RyanWang
 *
 * @date
 * @Dis
 */
public class HttpUtils {

    /**
     * 结合Rxjava进行网络请求
     * @param observable
     * @param resultListener
     * @param <T>
     */
    public static<T> void requestNet(final Observable observable, final OnResultListener resultListener){

        /*if(!NetUtil.isNetworkAvailabe(BaseApplication.getAppContext())){//网络不可用
            resultListener.onError(new Exception("网络错误"),"网络未连接",-3);
            return;
        }*/
        setSubscriber(observable, new Observer<JSONObject>(){
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject jsonObject) {
                if(resultListener != null){
                    Log.e("jk","请求结果:"+jsonObject.toString());
                    BaseResponseBean baseResponse = new Gson().fromJson(jsonObject.toString(),BaseResponseBean.class);
                    if(baseResponse.isSuccess()){//数据返回成功

                        resultListener.onSuccess(jsonObject);//返回整个json数据
                    }else{//返回错误码，并进行统一的错误处理
                        switch(baseResponse.errcode){
                            case -1:
                                resultListener.onError(new Exception("服务器错误"),baseResponse.errmsg,baseResponse.errcode);
                                break;
                            case 1001:
                                resultListener.onError(new Exception("请求错误"),baseResponse.errmsg,baseResponse.errcode);
                                break;
                            case 1002:
                                resultListener.onError(new Exception("请求错误"),baseResponse.errmsg,baseResponse.errcode);
                                break;
                            case 1003:
                                resultListener.onError(new Exception("请求错误"),baseResponse.errmsg,baseResponse.errcode);
                                break;
                            case 1004:
                                resultListener.onError(new Exception("请求错误"),baseResponse.errmsg,baseResponse.errcode);
                                break;
                            case 1005:
                                resultListener.onError(new Exception("请求错误"),baseResponse.errmsg,baseResponse.errcode);
                                break;
                            default:
                                resultListener.onError(new Exception("其他错误"),baseResponse.errmsg,baseResponse.errcode);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                if(e != null && resultListener != null){
                    resultListener.onError(e,e.getMessage(),-2);
                }else if(resultListener != null){
                    resultListener.onError(new Exception("网络错误"),"网络请求失败",-2);
                    return;
                }
            }

            @Override
            public void onComplete() {

            }
        });


    }

    public static<T> void setSubscriber(Observable<T> observable, Observer<T> observer){
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    /**
     * 获取RequestBody，用于请求接口时，方便传递参数
     * @param jsonObject
     * @return
     */
    public static RequestBody getRequestBody(JSONObject jsonObject){
        //添加传递的参数，以json数据的格式
        RequestBody body = RequestBody.create(MediaType.parse("application/json"),jsonObject.toString());
        return body;
    }

    //网络请求接口的回调
    public interface OnResultListener<T>{
        void onSuccess(T t);
        void onError(Throwable error, String msg, int err_code);
    }
}
