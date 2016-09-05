var compiler = require('./compile.js');


/**
 {
        freemarker: settings.freemarker,//freemarker configuration.properties
        targetDIR: settings.views,//views directory
        encoding: (mock.encoding || settings.encoding)
 }
 */

function FreemarkerCompiler(settings) {
    compiler.settings = {
        targetDIR: settings.targetDIR || 'views', //default  views directory
        encoding: settings.encoding || "utf-8", //default encoding
        freemarker: settings.freemarker || path.join(__dirname, '..', 'freemarker.properties'), //default freemarker.properties file
    }
}

FreemarkerCompiler.prototype.compile = function(ftl, data, callback) {
    compiler.compileFtl(ftl, data, callback);
}

module.exports = FreemarkerCompiler;