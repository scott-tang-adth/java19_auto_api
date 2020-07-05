package com.lemon.utils;

import com.alibaba.fastjson.JSONObject;
import com.lemon.cases.BaseCase;
import com.lemon.constants.Constants;
import io.qameta.allure.Step;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class HttpUtils {

    private static Logger logger=Logger.getLogger(HttpUtils.class);

    public static void main(String[] args) {
        //{"mobilephone":"13877788811","pwd":"12345678"}
        //mobilephone=13877788811&pwd=12345678
        String params = "{\"mobilephone\":\"13877788811\",\"pwd\":\"12345678\"}";

    }

    /**
     * 发送http请求
     * @param method            请求方式
     * @param contentType       参数类型
     * @param url               接口地址
     * @param params            接口参数
     * @return
     */
    @Step("call方法")
    public static HttpResponse call(String method,String contentType,String url,String params,Map<String, String> headers) {
        //如果请求方式是post
        try {
            if ("post".equalsIgnoreCase(method)) {
                //如果参数类型是json
                if ("json".equalsIgnoreCase(contentType)) {
                    return HttpUtils.jsonPost(url, params, headers);
                    //如果参数类型是form
                } else if ("form".equalsIgnoreCase(contentType)) {
                    //{"mobilephone":"13877788811","pwd":"12345678"}
                    //mobilephone=13877788811&pwd=12345678
                    params = json2KeyValue(params);
                    return HttpUtils.formPost(url, params,headers);
                } else {
                    System.out.println("method = " + method + ", contentType = " + contentType + ", url = " + url + ", params = " + params);

                }
                //如果请求方式是get
            } else if ("get".equalsIgnoreCase(method)) {
                //处理url   url/xxx/yyy/2/zzz
                //url?params
                return HttpUtils.get(url,headers);
                //如果请求方式是patch
            } else if ("patch".equalsIgnoreCase(method)) {
                return HttpUtils.patch(url, params,headers);
            } else {
                System.out.println("method = " + method + ", contentType = " + contentType + ", url = " + url + ", params = " + params);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * JSON参数转成form参数
     * @param jsonStr       json参数
     * @return
     */
    public static String json2KeyValue(String jsonStr) {
        //把json转成Map
        Map<String,String> map = JSONObject.parseObject(jsonStr, Map.class);
        //获取所有key
        Set<String> keySet = map.keySet();
        String result = "";
        //遍历key
        for (String key : keySet) {
            //通过key获取值
            String value = map.get(key);
            //拼接 key=value&
            result += key + "=" + value + "&";
        }
        //去掉最后一个多余的&
        result = result.substring(0,result.length()-1);
        return result;
    }

    /**
     * 发送http get请求。
     * @param url
     *      url 必须带参数，如果不带不会自动携带参数。
     *      url?KEY=VALUE&KEY2=VALUE2
     *      url/xxx/yyy/2/zzz
     * @throws Exception
     */
    public static HttpResponse get(String url,Map<String, String> headers) throws Exception {
        /**
         * 发送一个get请求
         * 1、确定url  接口地址
         * 2、确定请求类型（method）
         * 3、请求参数
         * 4、请求头
         * 5、发送请求
         * 6、接受响应
         * 7、格式化响应内容
         */
        //1、创建get请求并写入接口地址
        HttpGet get = new HttpGet(url);
        //2、在get请求上添加请求头
        addHeaders(headers,get);
        //get.addHeader("X-Lemonban-Media-Type","lemonban.v2");
        //3、创建一个客户端  XXXs  XXXUtils 工具类
        HttpClient client = HttpClients.createDefault();
        //4、客户端发送请求,并且返回响应对象（响应头、响应体、响应状态码）
        HttpResponse response = client.execute(get);
        //5、获取响应头、响应体、响应状态码
        //5.1、获取响应头
      //  printResponse(response);
        return response;
    }
    /**
     *
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws Exception
     */
    //因为之前的token字符串存在map对象中，所以现在要从map对象中取出来
   // public static HttpResponse jsonPost(String url,String params) throws Exception
    //方法抽取必须要加方法注释，否则达不到效果
    public static HttpResponse jsonPost(String url,String params,Map<String,String> headers) throws Exception
    {
        //1、创建POST请求并写入接口地址
        HttpPost post = new HttpPost(url);
        addHeaders(headers, post);
        //2、在POST请求上添加请求头
       // post.addHeader("X-Lemonban-Media-Type","lemonban.v2");
        //post.addHeader("Content-Type","application/json");
        //3、请求参数 加载请求体里面
        StringEntity stringEntity = new StringEntity(params,"utf-8");
        post.setEntity(stringEntity);
        //4、创建一个客户端  XXXs  XXXUtils 工具类
        HttpClient client = HttpClients.createDefault();
        //5、客户端发送请求,并且返回响应对象（响应头、响应体、响应状态码）
        HttpResponse response = client.execute(post);
        //6、获取响应头、响应体、响应状态码
        //printResponse(response);
        return response;
    }

    /**
     * 发送http post请求。
     * @param url               接口请求地址
     * @param params            key=value格式的参数
     * @throws Exception
     */
    public static HttpResponse formPost(String url,String params,Map<String, String> headers) throws Exception  {
        //1、创建POST请求并写入接口地址
        HttpPost post = new HttpPost(url);
        //2、在POST请求上添加请求头
        addHeaders(headers,post);
       // post.addHeader("Content-Type","application/x-www-form-urlencoded");
        //3、请求参数 加载请求体里面
        StringEntity stringEntity = new StringEntity(params,"utf-8");
        post.setEntity(stringEntity);
        //4、创建一个客户端  XXXs  XXXUtils 工具类
        HttpClient client = HttpClients.createDefault();
        //5、客户端发送请求,并且返回响应对象（响应头、响应体、响应状态码）
        HttpResponse response = client.execute(post);
        //6、获取响应头、响应体、响应状态码
      // printResponse(response);
        return response;
    }

    /**
     * 发送http post请求。
     * @param url               接口请求地址
     * @param params            json格式的参数
     * @throws Exception
     */
    public static HttpResponse patch(String url,String params,Map<String,String> headers) throws Exception  {
        //1、创建POST请求并写入接口地址
        HttpPatch patch = new HttpPatch(url);
        addHeaders(headers,patch);
        //2、在POST请求上添加请求头
        //patch.addHeader("X-Lemonban-Media-Type","lemonban.v2");
       // patch.addHeader("Content-Type","application/json");
        //3、请求参数 加载请求体里面
        StringEntity stringEntity = new StringEntity(params,"utf-8");
        patch.setEntity(stringEntity);
        //4、创建一个客户端  XXXs  XXXUtils 工具类
        HttpClient client = HttpClients.createDefault();
        //5、客户端发送请求,并且返回响应对象（响应头、响应体、响应状态码）
        HttpResponse response = client.execute(patch);
        //6、获取响应头、响应体、响应状态码
       // printResponse(response);
        return response;
    }
    /**
     * 获取响应头、响应体、状态码
     * @param response      http响应对象
     * @throws IOException
     */
    @Step("printResponse")
    public static String printResponse(HttpResponse response)  {
        //6.1、获取响应头
        try {
            Header[] allHeaders = response.getAllHeaders();
            //Header[] headers = response.getHeaders("Content-Type");
            //System.out.println(Arrays.toString(allHeaders));
            logger.info(Arrays.toString(allHeaders));
            //5.2、获取响应体
            HttpEntity entity = response.getEntity();
            String body = null;
            body=  EntityUtils.toString(entity);// java.io.IOException: Attempted read from closed stream.
            //System.out.println(body);
            logger.info(body);
            //5.3、响应状态码
            //链式编程 调用方法之后继续调用方法
            int statusCode = response.getStatusLine().getStatusCode();
            //上面的一句等于下面两句
           // System.out.println(statusCode);
           logger.info(statusCode);
            return body;
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return ""; //这种返回值还是第一次碰到
    }

    /**
     *添加请求头方法
     * @param headers 请求头
     * @param request   请求对象
     */
    //多态HttpRequest是所有请求的父类
    public static void addHeaders( Map<String, String> headers, HttpRequest request ) {
        Set<String> keySet = headers.keySet();
        for (String name:keySet) {
            String value = headers.get(name);
            request.addHeader(name,value);
        }
    }


}
