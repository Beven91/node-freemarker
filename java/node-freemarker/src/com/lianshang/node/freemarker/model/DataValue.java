package com.lianshang.node.freemarker.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by beven on 2016/3/1.
 */
public class DataValue {

    DataValue(Object value) {
        this.value = value;
    }

    public  Object getValue(Object ...paras){
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public static Object dateValue(Object v) throws ParseException {
        if(v instanceof  String){
            if(((String) v).split(" ").length<2){
                  v = v +" 00:00:00.0";
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(v.toString());
            return date;
        }else{
            return v;
        }
    }

    private boolean isDate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(this.value.toString());
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private Object value;

}
