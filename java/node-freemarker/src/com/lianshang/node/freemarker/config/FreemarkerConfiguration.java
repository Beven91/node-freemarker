package com.lianshang.node.freemarker.config;

import com.lianshang.node.freemarker.exception.ConfigPropReadException;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.*;
import java.util.Locale;
import java.util.Properties;

/**
 * 自定义freemarker配置对象
 */
public class FreemarkerConfiguration extends Configuration {

    private MainConfiguration mainCfg = null;

    /**
     * 自定义配置freemarker工具构造函数
     *
     * @param _mainCfg
     */
    public FreemarkerConfiguration(MainConfiguration _mainCfg) throws ConfigPropReadException {
        super(Configuration.VERSION_2_3_23);
        this.mainCfg = _mainCfg;
        this.setFreemarkerProperties();
    }

    /**
     * 加载自定义的freemarker属性配置
     */
    private void setFreemarkerProperties() throws ConfigPropReadException {
        File file = new File(mainCfg.getFreemarkerCfgFilePath());
        boolean defaultUse = true;
        if (file.exists()) {
            Properties p = new Properties();
            try {
                p.load(new FileInputStream(file));
                this.setSettings(p);
                defaultUse = false;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TemplateException e) {
                e.printStackTrace();
            }
        }
        if (defaultUse) {
            setDefaultFreemarker();
        }
        this.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
    }

    /**
     * 获取mainCfg
     * @return
     */
    public MainConfiguration getMainConfig() {
        return mainCfg;
    }

    /**
     * 设置默认freemarker配置
     */
    private void setDefaultFreemarker() {
        this.setLocale(Locale.ENGLISH);
        this.setDefaultEncoding("utf-8");
        this.setEncoding(Locale.ENGLISH, "utf-8");
        this.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        this.setObjectWrapper(new freemarker.ext.beans.BeansWrapper());
        this.setAPIBuiltinEnabled(true);
        this.setNumberFormat("#");
        this.setDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
