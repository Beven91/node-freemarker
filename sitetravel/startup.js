/*************************************
 * 名称:网站旅行者
 * 日期:2016-02-24
 * 作者:Beven
 *
 *******************************************/

var urlModule = require('url');

/**
 * 旅行者构造函数
 */
function Traveler() {
    this.tripHistoryMap = {};
    this.tripQueues = [];
    this.accessedCount = 0;
    this.timeout = 600;
    this.isTraveling = false;
    this.dynamicUrls = [];
    this.isApplicationStarting = false;
    //默认不进行同一个Url不同参数请求的访问
    // 例如: http://www.lianshang.com/order?id=1
    //  与  http://www.lianshang.com/order?id=2
    //  其中只保正order访问一次即可
    this.onlyPath = true;
}

/**
 * 初始化旅行者
 */
Traveler.prototype.init = function (iframe, initUrl, loginUrl, onlyPath, dynamicUrls) {
    this.onlyPath = (onlyPath == null ? true : onlyPath);
    this.loginUrl = loginUrl;
    this.iframe = iframe;
    this.tripQueues.push(initUrl);
    this.dynamicUrls.push.apply(this.dynamicUrls, dynamicUrls);
    if (this.loginUrl) {
        this.iframe.src = this.loginUrl;
    }
    var self = this;
    this.iframe.addEventListener('load', function () {
        self.viewPage();
    });
}

/**
 * 开启旅程
 */
Traveler.prototype.start = function () {
    this.isApplicationStarting = true;
}

/**
 * 停止旅程
 */
Traveler.prototype.wait = function () {
    this.isApplicationStarting = false;
}

/**
 * 写出一条日志
 */
Traveler.prototype.log = function (message) {
    console.log(message);
}

/**
 * 添加一个url旅程
 * @param url 访问地址
 * 注意:当url已经访问过,再次添加无效
 */
Traveler.prototype.addTrip = function (url) {
    if (this.yesOfAdd(url)) {
        this.tripHistoryMap[this.urlRender(url)] = false;
        this.tripQueues.push(url);
    }
}

/**
 * 标记一个url 已经访问过
 */
Traveler.prototype.tripArrived = function (url) {
    var info = '剩余:' + this.tripQueues.length + ' 个,已访问:' + this.accessedCount + ' 个';
    this.isTraveling = false;
    this.accessedCount = this.accessedCount + 1;
    this.log(' 页面 ( ' + url + ' ) 已经到达');
    this.log(info);
    this.tripHistoryMap[this.urlRender(url)] = true;
    window.document.getElementById('title').innerHTML = info;
}

/**
 * 判断指定url是否可以添加
 */
Traveler.prototype.yesOfAdd = function (url) {
    var uri = urlModule.parse(url);
    var host = this.getTravelRegionWindow().location.host;
    //只考虑站内
    if(url.indexOf("/user/logout")>-1){
        return false;
    }
    if (uri.host != host) {
        return false;
    }
    return this.tripHistoryMap[this.urlRender(url)] ==null;
}

/**
 * 是否有为动态地址,如果为动态地址,则返回动态地址,否则返回null
 */
Traveler.prototype.getDynamicUrl = function (url) {
    var dynamicUrls = this.dynamicUrls || [];
    var dynamicUrl = null, dynamicReg;
    for (var i = 0, k = dynamicUrls.length; i < k; i++) {
        dynamicReg = new RegExp(dynamicUrls[i]);
        if (dynamicReg.test(url)) {
            dynamicUrl = dynamicUrls[i];
            break;
        }
    }
    return dynamicUrl;
}

/**
 * url地址标准化
 */
Traveler.prototype.urlRender = function (url) {
    var uri = urlModule.parse(url);
    var onlyUrl = this.getDynamicUrl(url);
    onlyUrl = onlyUrl || (uri.protocol + '//' + uri.host + uri.pathname);
    onlyUrl = (onlyUrl || "").toString().toLowerCase();
    return onlyUrl;
}

/**
 * 判断是否存在旅程
 */
Traveler.prototype.hasTrip = function () {
    return this.tripQueues.length > 0;
}

/**
 * 旅行下一站
 */
Traveler.prototype.tripNext = function () {
    if (!this.isTraveling) {
        var traveler = this;
        this.isTraveling = true;
        setTimeout(function () {
            //设置当前页面为已经到达
            traveler.tripArrived(traveler.getTravelRegionWindow().location.href);
            if (!traveler.hasTrip()) {
                traveler.alert('已经没有下一站咯!!!!!');
                return;
            }
            //开往下一站
            traveler.getTravelRegionWindow().location.href = traveler.tripQueues.shift();
            traveler.showLoading();
        }, this.timeout);
    }
}

/**
 * 提示文件对话框
 */

Traveler.prototype.alert  =function(message){
    if(window.lsmain){
        messager.alert(message);
    }else{
        alert(message);
    }
}

/**
 * 获取旅行区域上下文
 */
Traveler.prototype.getTravelRegionWindow = function () {
    return this.iframe.contentWindow;
}

/**
 * 显示加载中
 */
Traveler.prototype.showLoading = function () {
    window.document.getElementById("loading").style.display = "";
}

/**
 * 关闭加载中
 */
Traveler.prototype.closeLoading = function () {
    window.document.getElementById("loading").style.display = "none";
}

/**
 * 当前旅行的页面已经加载完毕时调用此方法进行url探测
 */
Traveler.prototype.viewPage = function () {
    this.closeLoading();
    try {
        if (this.loginUrl && this.getTravelRegionWindow().document.referrer == this.loginUrl) {
            //如果为登录界面登录成功后
            this.start();
        }
        if (this.isApplicationStarting) {
            var aList = this.getTravelRegionWindow().document.links || [];
            var a = null;
            for (var i = 0, k = aList.length; i < k; i++) {
                a = aList[i];
                this.addTrip(a.href);
            }
            //搜索完当前页面所有没有访问的请求后,开往下一站
            this.tripNext();
        }
    } catch (ex) {
        window.alert(ex.message);
    }
}

//构造一个旅行者
module.exports = new Traveler();