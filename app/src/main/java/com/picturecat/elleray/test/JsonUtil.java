package com.picturecat.elleray.test;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by velen on 2016/8/6.
 */
public class JsonUtil {
    /**
     * 对象转换成json字符串
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    /**
     * json字符串转成对象
     *
     * @param str
     * @param type
     * @return
     */
    public static <T> T fromJson(String str, Type type) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(str, type);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * json字符串转成对象
     *
     * @param str
     * @param type
     * @return
     */
    public static <T> T fromJson(String str, Class<T> type) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(str, type);
        } catch (Exception e) {
            return null;
        }
    }
}
