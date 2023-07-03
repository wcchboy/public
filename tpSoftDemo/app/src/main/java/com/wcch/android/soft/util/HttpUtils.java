package com.wcch.android.soft.util;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Config;

import com.igrs.sml.util.L;
import com.wcch.android.App;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * okhttp请求
 * 
 * @author Flyjun
 * 
 */
public class HttpUtils {


	//测试URL：https://szdp.igrsservice.com:8443/igrsapp/android/hasUpdate
	//生产URL：https://dp.igrsservice.com:8443/igrsapp/android/hasUpdate


	public static final String BASEHOST = "https://"+ (Config.DEBUG?"szdp":"dp") +".igrsservice.com";//测试

	public static final String APIHOST = BASEHOST+":8443/igrsapp/android/";


	public static String version = "1.03";
	
	private OkHttpClient client;
	// 超时时间
	public static final int TIMEOUT = 1000 * 60;

	private static class SingletonHolder {
		/**
		 * 单例对象实例
		 */
		static final HttpUtils INSTANCE = new HttpUtils();
	}

	public static HttpUtils getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * private的构造函数用于避免外界直接使用new来实例化对象
	 */
	protected HttpUtils() {

		this.init();
	}
	
	
	// json请求
	public static final MediaType JSON = MediaType
			.parse("application/json; charset=utf-8");

	private Handler handler = new Handler(Looper.getMainLooper());


	private void init() {

		client = new OkHttpClient();


		OkHttpClient.Builder builder =client.newBuilder();

		builder.hostnameVerifier(new AllowAllHostnameVerifier());

		// 设置超时时间
		builder.connectTimeout(TIMEOUT, TimeUnit.SECONDS)
				.writeTimeout(TIMEOUT, TimeUnit.SECONDS)
				.readTimeout(TIMEOUT, TimeUnit.SECONDS).build();

	}

	/**
	 *
	 * @return 不带 ntspheader
	 */
	public static JSONObject ntspheader() {
		JSONObject json = new JSONObject();
		try {
			json.put("version", version);
//			json.put("salt", ConfigUtil.getInstance().getToken());
//			json.put("uid", ConfigUtil.getInstance().getUid());
		} catch (Exception e) {
		}
		return json;
	}
	public static String ntspheaderString() {
		JSONObject json = new JSONObject();
		try {
			json.put("ntspheader", ntspheader());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	public static JSONObject ntspheaderJSONObject() {
		JSONObject json = new JSONObject();
		try {
			json.put("ntspheader", ntspheader());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
//	public static JSONObject pageJson(Page page) {
//		JSONObject json = new JSONObject();
//		try {
//			json.put("ntspheader",ntspheader());
//			JSONObject json_page = new JSONObject();
//			json_page.put("page", page.page);
//			json_page.put("count", page.count);
//			json.put("page", json_page);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		return json;
//	}
//	public static String pageString(Page page) {
//		return pageJson(page).toString();
//	}

	/**
	 * post请求，json数据为body
	 * 
	 * @param url
	 * @param json
	 * @param callback
	 */
	public void postJson(final String url, String json, final HttpCallback callback) {
		if(TextUtils.isEmpty(json))json = ntspheaderString();
		RequestBody body = RequestBody.create(JSON, json);
		L.http(APIHOST+url);
		L.http(json);
		Request request = new Request.Builder().url(APIHOST+url).post(body).build();

		onStart(callback);

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				L.http("http访问失败 onFailure："+APIHOST+url+"\n\n");
				onError(callback, arg1.getMessage());
				arg1.printStackTrace();
			}

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				if (arg1.isSuccessful()) {
					String data = arg1.body().string();
					L.http("http访问成功："+data+"  <"+1+">"+url+"\n\n");
					try {
						//{"ntspheader":{"code":304, "msg":"非法访问"}}
						JSONObject ntspheader = new JSONObject(data).getJSONObject("ntspheader");
						if(ntspheader.getInt("code")==304) {

							return;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					onSuccess(callback, data);
				} else {
					String msg = arg1.message();
					L.http("http访问失败："+msg+""+"\n\n");
					onError(callback, msg);
				}
			}
		});

	}



	/**
	 * post请求 map为body
	 * 
	 * @param url
	 * @param map
	 * @param callback
	 */
	public void post(String url, Map<String, Object> map,
			final HttpCallback callback) {

		String authorization="";
		try{
			authorization= RSAUtils.getAuthorization(App.getInstance().getApplicationContext());
			//L.e("authorization:"+authorization);
		}catch (Exception e){
			e.printStackTrace();
			L.e("post->e:"+e.toString());
		}



		/**
		 * 创建请求的参数body
		 */
		FormBody.Builder builder = new FormBody.Builder();
		/**
		 * 遍历key
		 */
		if (null != map) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				L.i("Key = " + entry.getKey() + ", Value = "+ entry.getValue());
				builder.add(entry.getKey(), entry.getValue().toString());
			}
		}

		L.http(APIHOST+url);
		L.http(map.toString());

		RequestBody body = builder.build();
		Request request = new Request.Builder().url(APIHOST+url)
				.addHeader("Authorization",authorization)
				.post(body).build();

		onStart(callback);
		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				L.e("onFailure:"+arg0.toString());
				arg1.printStackTrace();
				onError(callback, arg1.getMessage());
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if (response.isSuccessful()) {
					String data  = response.body().string();
					L.http("onResponse:"+ data);
					onSuccess(callback, data);
				} else {
					onError(callback, response.message());
				}
			}
		});
//		try{
//			OkHttpClient client = new OkHttpClient().newBuilder()
//					.build();
//			MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
//			RequestBody body = RequestBody.create(mediaType, "packageName=com.lenovo.projection&version=1");
//			Request request = new Request.Builder()
//					.url("https://szdp.igrsservice.com:8443/igrsapp/android/hasUpdate")
//					.method("POST", body)
//					.addHeader("Authorization", "EoqjKIC7Y6/P9KetGKqh2iq0OFMnUNANI4fHYY5Du6tds5FLfGWfQmcxKPGwdzmLMO9ojqYHIlvm4MHn0Oa+dI7sURk6Y6Uej2kL7UteQvIvtXQMvIKiznEK5ZU6ro2pxqxkdVNtaeu0L4R/FM5N66Pnr1LdmBMnvkNZgXjbbgM=")
//					.addHeader("Content-Type", "application/x-www-form-urlencoded")
//					.build();
//			Response response = client.newCall(request).execute();
//		}catch (Exception e){
//		}
	}

	/**
	 * get请求
	 * 
	 * @param url
	 * @param callback
	 */
	public void get(String url, final HttpCallback callback) {

		Request request = new Request.Builder().url(APIHOST+url).build();

		onStart(callback);

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onFailure(Call arg0, IOException arg1) {

				onError(callback, arg1.getMessage());
				arg1.printStackTrace();
			}

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				if (arg1.isSuccessful()) {
					onSuccess(callback, arg1.body().string());
				} else {
					onError(callback, arg1.message());
				}
			}
		});

	}

	 

	private void onStart(HttpCallback callback) {
		if (null != callback) {
			callback.onStart();
		}
	}

	private void onSuccess(final HttpCallback callback, final String data) {
		if (null != callback) {
			handler.post(new Runnable() {
				public void run() {
					// 需要在主线程的操作。
					callback.onSuccess(data);
				}
			});
		}
	}

	private void onError(final HttpCallback callback, final String msg) {
		if (null != callback) {
			handler.post(new Runnable() {
				public void run() {
					// 需要在主线程的操作。
					callback.onError(msg);
				}
			});
		}
	}

	/**
	 * http请求回调
	 * 
	 */
	public static abstract class HttpCallback {
		// 开始
		public void onStart() {
		};

		// 成功回调
		public abstract void onSuccess(String data);

		// 失败回调
		public void onError(String msg) {
		};
	}

}