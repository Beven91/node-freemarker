package com.lianshang.node.freemarker.config;

import com.lianshang.node.freemarker.exception.ConfigPropReadException;
import com.lianshang.node.freemarker.model.DataMap;
import com.lianshang.node.freemarker.utils.Json;
import com.lianshang.node.freemarker.utils.UFile;

import java.io.IOException;
import java.util.Map;

/**
 * 启动jar包的参数配置对象
 */
public class MainConfiguration {

    /**
     * 主配置对象构造函数
     *
     * @param configfile 配置文件路径
     */
    public MainConfiguration(String configfile) throws IOException {
        if (!UFile.exists(configfile)) {
            throw new IOException("configfile not found!");
        }

        this.setConfig((Map<String, Object>)Json.fromFile(configfile));
    }

    private Map<String, Object> config;

    /**
     * 获取freemarker配置文件路径
     *
     * @return
     */
    public String getFreemarkerCfgFilePath() throws ConfigPropReadException {
        return getPropOf("freemarker");
    }

    /**
     * 获取freemarker上下文数据json路径
     *
     * @return
     */
    public String getFreemarkerModelJson() throws ConfigPropReadException {
        return getPropOf("dataJson");
    }

    /**
     * 获取当前要编译的freemarker模板名称
     */
    public String getTemplateName() throws ConfigPropReadException {
        return getPropOf("templateName");
    }

    /**
     * 获取当前要编译的freemarker模板视图目录
     */
    public String getTemplateViewDIR() throws ConfigPropReadException {
        return getPropOf("views");
    }

    /**
     * 获取当前编译的编码格式(该编码会设计到模板文件读取,保存等文件操作编码)
     *
     * @return
     */
    public String getEncoding() {
        return getPropOf("encoding", "utf-8");
    }

    /**
     * 获取当前编译需要的数据
     *
     * @return
     */
    public DataMap<String, Object> getFreemarkerModel() throws ConfigPropReadException {
        return Json.fromString(this.getFreemarkerModelJson());
    }

    /**
     * 获取配置map对象
     *
     * @return
     */
    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    private String getPropOf(String name) throws ConfigPropReadException {
        Object data = this.getConfig().get(name);
        if (null == data) {
            throw new ConfigPropReadException("config-property: " + name + " must be set");
        }
        return data.toString();
    }

    private String getPropOf(String name, String defaults) {
        Object data = this.getConfig().get(name);
        if (null == data || data.toString().trim() == "") {
            data = defaults;
        }
        return data.toString();
    }
}
