var compiler = require('./compile.js');


/**
 {
        freemarker: settings.freemarker,//freemarker configuration.properties
        targetDIR: settings.views,//views directory
        encoding: (mock.encoding || settings.encoding)
 }
 */

function FreemarkerCompiler(settings) {
    compiler.settings = settings;
}

FreemarkerCompiler.prototype.compile = function (ftl,data,callback) {
    compiler.compileFtl(ftl,data,callback);
}

module.exports = FreemarkerCompiler;