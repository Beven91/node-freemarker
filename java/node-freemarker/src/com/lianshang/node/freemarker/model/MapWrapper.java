package com.lianshang.node.freemarker.model;

import com.lianshang.node.freemarker.utils.Freemarker;
import freemarker.ext.beans.MapModel;
import freemarker.ext.beans.SimpleMethodModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by beven on 2016/7/9 0009.
 * map�������ݰ�װ�࣬������չjson���л���ʵ����
 * ʹmap��������֧�����ʵ���������
 * �Ӷ����mock���ݵ���Ӧ��
 */
public class MapWrapper  extends MapModel {

    private DataMap inner = null;

    public MapWrapper(DataMap map) {
        super(map,new freemarker.ext.beans.BeansWrapper());
        this.inner = map;
        this.inner.setWrapper(this);
    }

    /**
     * freemarker ����key��ȡ��Ӧ��ֵ������� ��Ϊ�ڽ���Json���л�ʱ���Ὣ���ݶ���װ��mapwrapper����
     *
     * @param name key����
     * @return key��Ӧ��ֵ
     * @throws ParseException
     */
    public Object get(Object name) throws ParseException {
        if (null != name && name instanceof String) {
            return this.extGetter((String) name);
        } else {
            return inner.get2(name);
        }
    }

    public Object exec(List arguments) throws TemplateModelException {
        Object key = this.unwrap((TemplateModel)arguments.get(0));
        return this.wrap(((Map)this.object).get(key));
    }

    /**
     * ��չȡֵ��ʽ ������:��չ֧��isxxx() getXxx()�����������Լ�֧�ַ���Key��ȡ���Ͳ�ͬ�������͵Ĵ���
     *
     * @param key ԭʼ������
     * @return
     */
    private Object extGetter(String key) throws ParseException {
        return dispatch(key);
    }

    private Object dispatch(String key) throws ParseException {
        String type = "common";
        if(inner.containsKey(key)){
            type="common";
        }else if (key.startsWith("is")) {
            type = "method";
            key = key.substring(2, 4).toLowerCase() + key.substring(4, key.length());
        } else if (key.startsWith("get")) {
            type = "method";
            key = key.substring(3, 4).toLowerCase() + key.substring(4, key.length());
        } else if (this.containsExtKey(key, "date")) {
            type = "date";
            key = "date+" + key;
        } else if (this.containsExtKey(key, "()")) {
            type = "method";
            key = "()+" + key;
        }
        switch (type) {
            case "date":
                return DataValue.dateValue(inner.get2(key));
            case "method":
                return createSimpleMethodModel(inner.get2(key));
            default:
                return inner.get2(key);
        }
    }

    /**
     * �Ƿ������չkey
     *
     * @param name   ԭʼkey����
     * @param extExp ��չ���ʽ ���磺 ������ "()" ʱ���������ԣ� "date"
     * @return
     */
    private boolean containsExtKey(String name, String extExp) {
        if (inner.containsKey(name)) {
            return false;
        } else if (inner.containsKey(extExp + "+" + name)) {
            return true;
        } else {
            return false;
        }
    }

    private SimpleMethodModel createSimpleMethodModel(Object v) {
        SimpleMethodModel methodModel = null;
        DataValue dv = new DataValue(v);
        try {
            Method method = dv.getClass().getDeclaredMethod("getValue", Array.newInstance(Object.class, 0).getClass());
            Class c = Class.forName("freemarker.ext.beans.SimpleMethodModel");
            Class[] argTypes = new Class[1];
            argTypes[0] = Array.newInstance(Object.class, 0).getClass();
            Constructor c1 = c.getDeclaredConstructor(new Class[]{Object.class, Method.class, argTypes.getClass(), Freemarker.onlyObjectWrapper.getClass()});
            c1.setAccessible(true);
            methodModel = (SimpleMethodModel) c1.newInstance(dv, method, argTypes, Freemarker.onlyObjectWrapper);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return methodModel;
    }

}
