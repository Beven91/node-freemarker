package com.lianshang.node.freemarker.model;

import com.lianshang.node.freemarker.utils.Freemarker;
import freemarker.ext.beans.SimpleMethodModel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.*;

/**
 * Created by beven on 2016/2/29.
 */
public class DataMap<K, V> extends HashMap<K, V> {

    private MapWrapper dWrapper = null;

    public void setWrapper(MapWrapper wrap) {
        this.dWrapper = wrap;
    }

    @Override
    public V get(Object key) {
        try {
            if(this.dWrapper!=null){
                return (V) this.dWrapper.get(key);
            }else{
                return this.get2(key);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public  V get2(Object key){
        return super.get(key);
    }
}


