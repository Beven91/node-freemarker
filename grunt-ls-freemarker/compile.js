/**
 * 名称：编译freemarker文件工具
 * 作者: Beven
 * 日期：2016-09-02
 * 描述：
 */

var spawn = require('child_process').spawn;
var iconv = require("iconv-lite");
var path = require("path");
var fs = require('fs');

var jarFile = path.join(__dirname, "/jar/node-freemarker.jar");

/**
 * constructor of  nodejs compile freemarker.ftl utils
 */
function Freemarker() {}

/**
 * compile  .ftl views
 */
Freemarker.prototype.compileMock = function(files, settings, asyncDone) {
    try {
        //set done function
        this.asyncDone = asyncDone;
        this.settings = settings;
        //get all ftl views
        this.findMockjsList(files);
        //compile all ftl views
        this.compileAllByMockjs();
    } catch (ex) {
        asyncDone(false);
        this.warn(ex.stack);
    }
}

/**
 * find ftl views
 */
Freemarker.prototype.findMockjsList = function(files) {
    var mockjsList = this.findedMockjsList = [];
    files.forEach(function(f) {
        var afterMockJsList = f.src.filter(function(filepath) {
            // Warn on and remove invalid source files (if nonull was set).
            if (!fs.existsSync(filepath)) {
                this.warn('Mock file "' + filepath + '" not found.');
                return false;
            } else {
                return true;
            }
        });
        mockjsList.push.apply(mockjsList, afterMockJsList);
    });
}

/**
 * compile views use finded mockjs list
 */
Freemarker.prototype.compileAllByMockjs = function() {
    var self = this;
    this.processCount = this.findedMockjsList.length;
    if (this.processCount > 0) {
        this.findedMockjsList.map(function(view) {
            self.compileByMockjs(view);
        });
    } else {
        this.done(true);
    }
}

/**
 * compile view use mockjs
 */
Freemarker.prototype.compileByMockjs = function(filepath) {
    var self = this;
    var settings = this.settings;
    // Load the mock
    var file = path.resolve(filepath);
    // kill require cache, reload file newest content
    delete require.cache[require.resolve(file)];
    var mock = require(file);
    var destFile = path.join(settings.targetDIR, mock.out || mock.view.replace(path.extname(mock.view), ".html"));
    var cfgfile = this.configCompile(mock, file);
    // Get results
    this.processTemplate(cfgfile, function(err, result) {
        try {
            fs.unlinkSync(cfgfile);
        } catch (ex) {

        }
        self.onViewCompiled(err, result, destFile, filepath);
    });
}

/**
 * compile view
 */
Freemarker.prototype.compileFtl = function(ftl, data, callback) {
    var self = this;
    var settings = this.settings;
    var mock = {
        view: ftl,
        data: data
    };
    var destFile = path.join(settings.targetDIR, mock.view.replace(path.extname(mock.view), ".html"));
    var cfgfile = this.configCompile(mock, ftl);
    // Get results
    this.processTemplate(cfgfile, function(err, result) {
        try {
            fs.unlinkSync(cfgfile);
        } catch (ex) {

        }
        callback && callback(err, result);
    });
}

/**
 * config compile
 */
Freemarker.prototype.configCompile = function(mock, filePath) {
    var settings = this.settings;
    var dir = path.join(__dirname, 'compile');
    var file = path.join(dir, path.basename(filePath) + ".json");
    var compileOptions = {
        freemarker: settings.freemarker, //freemarker configuration.properties
        views: settings.views, //views directory
        templateName: mock.view, //template Name
        encoding: (mock.encoding || settings.encoding),
        dataJson: this.getDataJson(mock.data)
    }
    if (!fs.existsSync(dir)) {
        fs.mkdirSync(dir);
    }
    fs.writeFileSync(file, JSON.stringify(compileOptions));
    return file;
}

/**
 * 获取dataJson
 */
Freemarker.prototype.getDataJson = function(data) {
    this.extensionKeyObject(data);
    return JSON.stringify(data) //data jsonstring
}

/**
 * 数据类型处理
 */
Freemarker.prototype.extensionKeyObject = function(data) {
    var keys = Object.keys(data);
    var value = null,
        key = null;
    for (var i = 0, k = keys.length; i < k; i++) {
        key = keys[i];
        value = data[key];
        if (value && this.isObject(value) || value instanceof Array) {
            this.extensionKeyObject(value);
        } else {
            this.extensionKeyOfValue(value, data, key);
        }
    }
}

/**
 * 判断是否为Object类型
 */
Freemarker.prototype.isObject = function(v) {
    return Object.prototype.toString.apply(v) === "[object Object]";
}

/**
 * 数据key处理
 */
Freemarker.prototype.extensionKeyOfValue = function(value, data, key) {
    if (value instanceof Date) {
        delete data[key];
        data['date+' + key] = dateFormat(value, 'yyyy-MM-dd hh:mm:ss.0');
    }
}


/**
 * on view compiled
 */
Freemarker.prototype.onViewCompiled = function(err, result, dest, filepath) {
    var content = result;
    this.processCount--;
    if (err) {
        content = err;
        this.warn('compile file' + filepath + '" error!');
        this.done(false);
    }
    //write content or err
    fs.writeFileSync(dest, content);
    this.log('File "' + dest + '" created.');
    if (this.processCount < 1) {
        this.done(true);
    }
}

/**
 * compile freemarker.ftl
 * @param cfgFile config file path
 * @param callback on compiled called function
 */
Freemarker.prototype.processTemplate = function(cfgFile, callback) {
    var dataBuffers = [];
    var cmd = spawn('java', ["-jar", jarFile, cfgFile]);
    var error = null,
        hasError = false,
        self = this;
    if (callback) {
        //on received cmd data
        cmd.stdout.on("data", function(data) {
            dataBuffers.push(iconv.decode(data, 'gbk'));
        });
        //on received cmd error info
        cmd.stderr.on("data", function(data) {
            hasError = true;
            error = iconv.decode(data, 'gbk');
            dataBuffers.push(error);
            console.log(error);
        });
        //on compiled
        cmd.stdout.on("end", function() {
            callback(null, self.resultRender(dataBuffers, hasError));
        });
    }
}

/**
 * render template result
 */
Freemarker.prototype.resultRender = function(dataBuffers, hasError) {
    var content = dataBuffers.join('');
    if (hasError) {
        return this.errorWrap(content);
    } else {
        return content;
    }
}

/**
 * error template html
 */

Freemarker.prototype.errorWrap = function(content) {
    content = (content || "").replace(".compile.ftl", ".ftl");
    var htmls = [
        '<html>',
        '   <head>',
        '       <meta charset="UTF-8">',
        '       <title>compile error</title>',
        '   </head>',
        '   <body>',
        '       <code style="white-space:pre;">',
        content,
        '       </code>',
        '   </body>',
        '</html>'
    ];
    return htmls.join('\n');
}

/**
 * done compile
 */
Freemarker.prototype.done = function(result) {
    this.asyncDone(result);
}

/**
 * out log
 */
Freemarker.prototype.log = function(message) {
    console.log(message);
}

/**
 * out warn log
 */
Freemarker.prototype.warn = function(message) {
    console.error(message);
}

function dateFormat(date, fmt) {
    var o = {
        "M+": date.getMonth() + 1, //月份 
        "d+": date.getDate(), //日 
        "h+": date.getHours(), //小时 
        "m+": date.getMinutes(), //分 
        "s+": date.getSeconds(), //秒 
        "q+": Math.floor((date.getMonth() + 3) / 3), //季度 
        "S": date.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        }
    }
    return fmt;
}

module.exports = new Freemarker();