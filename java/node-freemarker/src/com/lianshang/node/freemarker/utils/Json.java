package com.lianshang.node.freemarker.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.lianshang.node.freemarker.gson.CustomObject;
import com.lianshang.node.freemarker.gson.CustomObjectTypeAdapter;
import com.lianshang.node.freemarker.model.DataMap;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by beven on 2016/2/26.
 */
public class Json {

    /**
     * 将指定字符换转换成Map<String, Object>
     *
     * @param json json字符串内容
     * @return Map<String, Object>
     */
    public static DataMap<String, Object> fromString(String json) {
        return createGson().fromJson(json, new TypeToken<DataMap<String, CustomObject>>() {
        }.getType());
    }

    /**
     * 读取指定json文件,并且将文件json内容解析成Map<String, Object>
     *
     * @param file 文件路径
     * @return Map<String, Object>
     */
    public static Object fromFile(String file) {
        return fromString(UFile.read(file, "utf-8"));
    }

    private static Gson createGson() {
        GsonBuilder gsonBulder = new GsonBuilder();
        gsonBulder.registerTypeAdapterFactory(CustomObjectTypeAdapter.FACTORY);
        return gsonBulder.create();
    }
}
