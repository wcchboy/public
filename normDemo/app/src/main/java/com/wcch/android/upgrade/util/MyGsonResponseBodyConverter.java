package com.wcch.android.upgrade.util;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Create by RyanWang
 *重写GsonResponseBodyConverter方便获取网络返回的原始数据
 * @date
 * @Dis
 */
final class MyGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final Gson gson;
    private final Type type;

    MyGsonResponseBodyConverter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;

    }
    @Override
    public T convert(ResponseBody value) throws IOException {
        JSONObject jsonObject;
        String response = value.string();
        //Log.e("jk",response);
        // BaseResponseBean baseResponse = gson.fromJson(response,type);
        try {
            jsonObject = new JSONObject(response);
            return (T) jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}