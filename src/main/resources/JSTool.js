function isNull(str) {
    return str == null || str === "" || typeof (str) == "undefined" || str.length == 0 || str === "undefined";
}

function isNotNull(str) {
    return isNull(str);
}

function defaultIfNull(str, defaultStr) {
    if (isNull(str)) {
        return defaultStr;
    }
    return str;
}

function formatStr(str) {
    for (var i = 0; i < arguments.length - 1; i++) {
        str = str.replace("{" + i + "}", arguments[i + 1]);
    }
    return str;
}

function formatDate(str, format) {
    if (typeof (str) == "undefined" || str == null)
        return "";
    var date = new Date(str);
    var o = {
        "M+" : date.getMonth() + 1,
        "d+" : date.getDate(),
        "h+" : date.getHours(),
        "m+" : date.getMinutes(),
        "s+" : date.getSeconds(),
        "q+" : Math.floor((date.getMonth() + 3) / 3),
        "S" : date.getMilliseconds()
    }
    if (/(y+)/.test(format)) {
        format = format.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for ( var k in o) {
        if (new RegExp("(" + k + ")").test(format)) {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
        }
    }
    return format;
}

function strToDate(strDate) {
    var date = eval('new Date(' + strDate.replace(/\d+(?=-[^-]+$)/, function(a) {
        return parseInt(a, 10) - 1;
    }).match(/\d+/g) + ')');
    return date;
}

function offsetDay(date, offset, format) {
    if (offset != null)
        date.setDate(date.getDate() + offset);
    return formatDate(date, format);
}

/**
 * 格式化double
 *
 * 例如：formatDoubleZero(2.34, 4) =》 2.3400
 *
 * @param val
 * @param num 保留小数点后面的位数
 * @returns {*}
 */
function round2(val, num) {
    if (val == null || val === "")
        return "";
    var s = formatDouble(val, num).toString();
    var rs = s.indexOf('.');
    if (rs < 0) {
        rs = s.length;
        s += '.';
    }
    while (s.length <= rs + num) {
        s += '0';
    }
    return s;
}

/**
 *
 * 格式化double
 *
 * 例如：formatDoubleZero(2.34, 1) =》 2.3
 *
 * @param arg
 * @param index保留小数点后面的位数
 * @returns {number}
 */
function round(arg, index) {
    return (arg ? arg * 1 : 0).toFixed(index) * 1;
}

function toJsonStr(obj){
    return JSON.stringify(obj);
}

function parseJSON(str){
    return JSON.parse(str);
}

function  startWith(str,str2) {
    return str.toString().indexOf(str2) == 0;
}

function betweenDay(day,day1){
    day = new Date(day);
    day1 = new Date(day1);
    var days = day.getTime() - day1.getTime();
    var time = parseInt(days / (1000 * 60 * 60 * 24));
    return time;
}

function uuid(){
    return 'xxxxxxxxxxxx4xxxyxxxxxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
        return v.toString(16);
    });
}

function removePrefix(str,str2){
    if(isNull(str) || isNull(str2)){
        return str;
    }
    str = str.toString();
    if( str.toString().indexOf(str2) == 0){
        return str.substr(str2.length);
    }
    return str;
}

function removeSuffix(str,str2){
    if(isNull(str) || isNull(str2)){
        return str;
    }
    str = str.toString();
    if(str.lastIndexOf(str2) == (str.length - str2.length)){
        return str.substring(0, str.lastIndexOf(str2));
    }
    return str;
}

function  replaceAll(str,str2,str3) {
    if(isNull(str) || isNull(str2)){
        return str;
    }
    return str.toString().replace(new RegExp(str2, "g"),str3);
}

function  replace(str,str2,str3) {
    if(isNull(str) || isNull(str2)){
        return str;
    }
    return str.toString().replace(str2,str3);
}

function isDate(mystring) {
    var reg = /^(\d{4})[\/,-](\d{1,2})[\/,-](\d{1,2})$/;
    var str = mystring;
    var arr = reg.exec(str);
    if (str=="") return true;
    if (!reg.test(str)&&RegExp.$2<=12&&RegExp.$3<=31){
        return false;
    }
    return true;
}

function isNum(str){
    return !isNaN(str);
}
