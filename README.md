node-freemarker
=================================== 
一个grunt插件,用于编译freemarker模板，支持自定义freemarker.properties 配置文件

支持后台model自定义的get函数 例如：  ${data.isRed()}   ${data.getRed()}     

支持 后台model自定义的对象函数转换，例如:  ${pagination.html()} 

### intall

       npm install grunt-ls-freemarker

### usage
  
    var config = {
        options: {
            freemarker:path.join(__dirname,'freemarker.properties'),//freemarker的配置文件，可以不填写，
            views: '../../../../websites/src/main/webapp/WEB-INF/views/',//视图存放目录
            out: 'dist' //编译后的而输出目录
        },
        dist: 'mock/**/views/**/*.js' //对应的mockjs文件 可以指定单个也可以是一个匹配字符串
    };

    grunt.config('ls-freemarker', config);
    
    //freemarker.properties :
    locale=zh_CN
    template_update_delay=0
    datetime_format=yyyy-MM-dd HH:mm:ss
    date_format=yyyy-MM-dd
    number_format=#.##
    object_wrapper=freemarker.ext.beans.BeansWrapper
    api_builtin_enabled=true
   
    
### mock

    /*
    
      关于扩展属性  扩展属性目前支持 函数属性,时间类型属性
      
      所有扩展属性属性名如下:  扩展名称+属性名  
      
      例如：  ()+html  -->函数扩展属性  date+createTime -->时间类型扩展属性
      
      扩展属性主要用于将js的json数据支持到java后端的一些数据类型
      
      额外扩展 java bean的is函数 和get函数  
      
      例如: java bean { function isColor(){}  function getColor(){} }  --> 均可以使用 ${color}
     
     **/
    {
      "name":"demo", // 对应 ${name} 或者 ${isName()} 或者 ${getName()}
      "age":999,  //对应 ${age?c}  类型: Interge
      "hasLevel":false, //${havLevel?c}  类型: Boolean
      "()+html":"<div>.....</div>",//对应 ${html()} 或者 ${html(....参数不限)  //将html()调用的值直接去 ()+html key对应的值  
      "date+birthday":"2016-06-21",//对应 ${birthday?string("yyyy-MM-dd HH:mm:ss")}  //字符串会转换成时间  最终为Date类型
      "createTime":new Date(),// 对应 ${createTime?string('yyyy-MM-dd HH:mm:ss')} 数据类型为Date
      "strTime":"2016-02-01" // 对应${strTime} 注意这里是String类型的数据   
    }
 
