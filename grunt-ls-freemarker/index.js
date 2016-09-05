var compiler = require('./compile.js');
var path = require('path');

/**
 {
        freemarker: settings.freemarker,//freemarker configuration.properties
        views: settings.views,//views directory
        targetDIR:'' //compile 
        encoding: (mock.encoding || settings.encoding)
 }
 */

function FreemarkerCompiler(settings) {
    compiler.settings = {
        targetDIR: './',
        views: settings.views || 'views', //default  views directory
        encoding: settings.encoding || "utf-8", //default encoding
        freemarker: settings.freemarker || path.join(__dirname, '..', 'freemarker.properties'), //default freemarker.properties file
    }
}

FreemarkerCompiler.prototype.compile = function(ftl, data, callback) {
    compiler.compileFtl(ftl, data, callback);
}

module.exports = FreemarkerCompiler;