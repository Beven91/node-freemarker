/*
 * grunt-ls-freemarker
 *
 */
'use strict';

var compiler = require('../compile.js');

module.exports = function (grunt) {
    //regist grunt task
    grunt.registerMultiTask('ls-freemarker', 'Freemarker renderer plugin for grunt.', function () {
        // Defaults options.
        var options = this.options({
            views: 'views',//default  views directory
            out: "public", //default out directory
            encoding: "utf-8", //default encoding
            freemarker: path.join(__dirname, '..', 'freemarker.properties'),//default freemarker.properties file
        });
        options.targetDIR = path.resolve(options.out);
        compiler.compile(this.files, options,this.async());
    });
};
