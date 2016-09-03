package com.lianshang.node.freemarker;

import com.lianshang.node.freemarker.config.FreemarkerConfiguration;
import com.lianshang.node.freemarker.config.MainConfiguration;
import com.lianshang.node.freemarker.exception.ConfigPropReadException;
import com.lianshang.node.freemarker.utils.Freemarker;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created by beven on 2016/2/26.
 */
public class Main {
    //主入口
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try{
            run(args[0]);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private static void run(String configfile) throws IOException, ConfigPropReadException {
        //初始化配置对象
        MainConfiguration configuration = new MainConfiguration(configfile);
        //构建freemarker编译工具
        Freemarker freemarker = new Freemarker(new FreemarkerConfiguration(configuration));

        //创建输出流
        Writer out = new OutputStreamWriter(System.out);

        //根据不同类型进行编译输出
        freemarker.processTemplate(configuration.getTemplateName(), configuration.getFreemarkerModel(), out);
    }
}
