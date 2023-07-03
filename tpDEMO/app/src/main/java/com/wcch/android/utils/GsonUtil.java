package com.wcch.android.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonParseException;

import java.util.List;
import java.util.Map;

public class GsonUtil {
    private static Gson gson = null;

    static {
        if (gson == null) {
            gson = new Gson();
        }
    }

    private GsonUtil() {
    }

    /**
     * 转成json
     *
     * @param object
     * @return
     */
    public static String GsonString(Object object) {
        String gsonString = null;
        if (gson != null) {
            gsonString = gson.toJson(object);
        }
        return gsonString;
    }

    /**
     * 转成bean
     *
     * @param gsonString
     * @param cls
     * @return
     */
    public static <T> T GsonToBean(String gsonString, Class<T> cls) {
        T t = null;
        if (gson != null) {
        	try {
        		t = gson.fromJson(gsonString, cls);
        	} catch (Exception e) {
        		e.printStackTrace();
        		return t;
			}
        }
        return t;
    }

    /**
     * 转成list
     *
     * @param gsonString
     * @param cls
     * @return 
     */
    public static <T> List<T> GsonToList(String gsonString, Class<T> cls) {
        List<T> list = null;
        if (gson != null) {
        	try {
        		list = gson.fromJson(gsonString, new TypeToken<List<T>>() {
        		}.getType());
        	} catch (Exception e) {
        		e.printStackTrace();
        		return list;
			}
        }
        return list;
    }

    /**
     * 转成list中有map的
     *
     * @param gsonString
     * @return
     */
    public static <T> List<Map<String, T>> GsonToListMaps(String gsonString) {
        List<Map<String, T>> list = null;
        if (gson != null) {
        	try {
        		list = gson.fromJson(gsonString, new TypeToken<List<T>>() {
        		}.getType());
        	} catch (Exception e) {
        		e.printStackTrace();
        		return list;
        	}
        }
        return list;
    }

    /**
     * 转成map的
     * @param gsonString
     * @return
     */
    public static <T> Map<String, T> GsonToMaps(String gsonString) {
        Map<String, T> map = null;
        if (gson != null) {
        	try {
        		map = gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
                }.getType());
        	} catch (Exception e) {
        		e.printStackTrace();
        		return map;
        	}
            
        }
        return map;
    }
	
	public static <T> T fromJson(String json, Class<T> classOfT) {
		try {
			return gson.fromJson(json, classOfT);
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String toJsonDisableHtmlEscaping(Object obj) {
        return new GsonBuilder().disableHtmlEscaping().create().toJson(obj);
	}
}
