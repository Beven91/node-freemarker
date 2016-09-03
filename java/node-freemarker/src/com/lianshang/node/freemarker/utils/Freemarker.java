package com.lianshang.node.freemarker.utils;

import com.lianshang.node.freemarker.config.FreemarkerConfiguration;
import com.lianshang.node.freemarker.exception.ConfigPropReadException;
import com.lianshang.node.freemarker.model.DataMap;
import com.lianshang.node.freemarker.model.MapWrapper;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

/**
 * Created by beven on 2016/2/26.
 */
public class Freemarker {

    private FreemarkerConfiguration freemarkerConfiguration;

    public static ObjectWrapper onlyObjectWrapper;

    /**
     * Freemarker工具构造函数
     *
     * @param cfg freemarker辅助配置对象
     */
    public Freemarker(FreemarkerConfiguration cfg) {
        freemarkerConfiguration = cfg;
    }

    /**
     * @param templateName 模板名字
     * @param root         模板上下文数据
     * @param out          输出流
     */
    @SuppressWarnings("unchecked")
    public void processTemplate(String templateName, DataMap<String, Object> root, Writer out) {
        try {
            String encoding = freemarkerConfiguration.getMainConfig().getEncoding();
            String dir = freemarkerConfiguration.getMainConfig().getTemplateViewDIR();
            onlyObjectWrapper = freemarkerConfiguration.getObjectWrapper();
            // 获得模板
            //templateName = NormalForTemplateLoader.start(templateName,dir,encoding);
            freemarkerConfiguration.setDirectoryForTemplateLoading(new File(freemarkerConfiguration.getMainConfig().getTemplateViewDIR()));
            freemarkerConfiguration.addAutoImport("", templateName);
            freemarkerConfiguration.clearTemplateCache();
            Template template = freemarkerConfiguration.getTemplate(templateName, encoding);
            template.process(new MapWrapper(root), out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (ConfigPropReadException e) {
            e.printStackTrace();
        } finally {
            try {
                //NormalForTemplateLoader.end();
                out.close();
                out = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
