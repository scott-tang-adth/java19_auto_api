package com.lemon.utils;
import com.alibaba.fastjson.JSONPath;
import com.lemon.constants.Constants;
import io.qameta.allure.Step;
import org.apache.commons.lang3.StringUtils;
import java.util.HashMap;
import java.util.Map;
public class AuthenticationUtils {
    //类似于jmeter用户变量  vars
    public static Map<String,Object> VARS = new HashMap<>();


    /**
     * 使用jsonpath获取内容存储到VARS变量，给其他接口使用。
     * @param json              json字符串
     * @param expression        jsonpath表达式
     * @param key               存储到VARS中的key
     */
    @Step("json2Vars")
    public static void json2Vars(String json,String expression,String key) {
        //如果json不是空，则继续操作
        if(StringUtils.isNotBlank(json)) {
            //使用jsonpath获取内容
            Object obj = JSONPath.read(json,expression);
            System.out.println(key + ":" + obj);
            //如果获取内容不为空，存入VARS变量，给其他接口使用。
            if(obj != null) {
                AuthenticationUtils.VARS.put(key, obj);
            }
        }
    }

    /**
     * 获取带token的请求头Map集合
     * @return
     */
    public static Map<String, String> getTokenHeader() {
        Object token = AuthenticationUtils.VARS.get("${token}");
        System.out.println("Recharge token:" + token);
        //  3.2、添加到请求头
        //  3.3、改造call支持传递请求头
        Map<String,String> headers = new HashMap<>();
        headers.put("Authorization","Bearer " + token);
        headers.putAll(Constants.HEADERS);
        return headers;
    }
}
