package com.sumslack.excel;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.sumslack.js.Db;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.script.ScriptUtil;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class JSUtil extends ScriptUtil {
    private static String JAVASCRIPT_TEMPLATE = "JSTool.js";
    private static ClassPathResource resource = new ClassPathResource(JAVASCRIPT_TEMPLATE);
    static FileReader fileReader;
    static ScriptEngine engine = getScript("javascript");
    static {
    	engine.put("Db", new Db());
    }

    static {
        try {
            fileReader = new FileReader(resource.getFile());
            engine.eval(fileReader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行js脚本，内置了js工具类
     *
     * @param script 脚本
     * @param args   参数
     * @return
     * @throws ScriptException
     * @throws FileNotFoundException
     */
    public static Object execute(String script, Map<String, Object> args) {
        try {
            if (script == null || script.equals("")) {
                return null;
            }
            if (args != null || !args.isEmpty()) {
                for (Map.Entry<String, Object> entry : args.entrySet()) {
                    engine.put(entry.getKey(), entry.getValue());
                }
            }
            Object res = engine.eval(script);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static List execRetList(String script, Map<String, Object> args) {
        try {
            if (script == null || script.equals("")) {
                return null;
            }
            if (args != null || !args.isEmpty()) {
                for (Map.Entry<String, Object> entry : args.entrySet()) {
                    engine.put(entry.getKey(), entry.getValue());
                }
            }
            Object obj = engine.eval(script);
            if(obj instanceof ScriptObjectMirror) {
	            ScriptObjectMirror res = (ScriptObjectMirror)obj;
	            if(res.isArray()) {
	            	Collection<Object> vvs = res.values();
	            	if(vvs!=null)
	            		return new ArrayList(vvs);
	            }
            }else if(obj instanceof List) {
            	return (List)obj;
            }else {
            	List retList = new ArrayList();
            	retList.add(obj);
            	return retList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isDate(String strDate) {
        if(StrUtil.isBlank(strDate)){
            return false;
        }
        Pattern pattern = Pattern.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        Matcher m = pattern.matcher(strDate);
        boolean ok = m.matches();
        return ok;
    }

    public static void main(String[] args) {
        Map<String, Object> bindings = new HashMap<>();
        bindings.put("c36", "(dddd)");
        bindings.put("c12", "strToDate('2019-09-09')");
        bindings.put("bbb", 2);
//       Console.log(execute("formatDouble(aaa*2 , bbb)", bindings));
//        Console.log(execute("formatDouble(aaa*3 , bbb)", bindings));
//        Console.log(execute("formatDouble(aaa*4 , bbb)", bindings));
//        Console.log(execute("removePrefix('w123e','w1')", bindings));
//        Console.log(execute("removeSuffix(removePrefix(c36,'('),')')", bindings));
//        Console.log(execute("replace(c12,'暂','')", bindings));
//        Console.log(execute("isDate('2018-1/1')", bindings));
        //Object data = execute("formatDate(new Date(),'yyyy-MM-dd hh:mm:ss')", bindings);
        //Console.log(isDate(null));
        
        Console.log(execRetList("(function(){return [{a:'11',b:'22'},{a:'11a',b:'22a'}];})()",bindings));

    }


}
