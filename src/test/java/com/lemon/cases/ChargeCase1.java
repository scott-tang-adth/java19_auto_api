package com.lemon.cases;
import com.alibaba.fastjson.JSONPath;
import com.lemon.constants.Constants;
import com.lemon.pojo.CaseInfo;
import com.lemon.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.*;
import java.math.BigDecimal;
import java.util.Map;
import static com.lemon.utils.HttpUtils.call;
/**
 * @author by Scott
 * @date 2020/6/17.
 */
//1.去登录的case里的响应体里获取token
//2.token存起来
//3.充值的case 请求之前把token取出来放到请求头里
public class ChargeCase1 extends BaseCase{
    @Test(dataProvider = "datas")
    public void testCase( CaseInfo caseInfo)
    {
        paramsReplace(caseInfo);
        //没请求前的数据
        Object beforesqlResult = SQLUtils.getSingleResult(caseInfo.getSql());
      /*  //从vars中获取token
        Object token = Authentication.Vars.get("${token}");
        System.out.println("Recharge token:" + token);
        //2、添加到请求头
        //3、改造call支持传递请求头
        Map<String,String> headers = new HashMap<>();
        headers.put("Authorization","Bearer "+token);
        headers.putAll(Constants.HEADERS);*/
        Map<String, String> headers = AuthenticationUtils.getTokenHeader();
        //改造call方法支持传递请求头
        //数据库前置查询结果
        //注意这里的参数必须是headers，用constants.Constants.HEADERS会报错
        HttpResponse response = call(caseInfo.getMethod(), caseInfo.getContentType(), caseInfo.getUrl(), caseInfo.getParams(), headers);
        String body = HttpUtils.printResponse(response);
        addWriteBackData(caseInfo.getId(),Constants.RESPONSE_WRTIE_BACK_CELLNUM,body);
        boolean assertResponseFlag = assertResponse(body, caseInfo.getExpectResult());
        //充值后数据
        Object afterSqlResult = SQLUtils.getSingleResult(caseInfo.getSql());
        boolean assertResultFlag=sqlAssert(caseInfo , beforesqlResult, afterSqlResult);
        //添加断言回写内容
        String assertResult=assertResponseFlag && assertResultFlag ?"passed":"failed";
        addWriteBackData(caseInfo.getId(),Constants.assert_WRTIE_BACK_CELLNUM,assertResult);
        //报表断言
        Assert.assertEquals(assertResult,"passed");

    }
    public boolean sqlAssert( CaseInfo caseInfo, Object beforesqlResult, Object afterSqlResult ) {
        boolean flag=false;
        if (StringUtils.isNotBlank(caseInfo.getSql())){
            if (beforesqlResult == null || afterSqlResult == null) {
                System.out.println("数据库断言失败");
            } else {
                //object 类型不可以比较
                BigDecimal b1 = (BigDecimal) beforesqlResult;//数据库的数据类型
                BigDecimal b2 = (BigDecimal) afterSqlResult;
                //接口执行之前查询结果0，接口执行后查询结果为1
                BigDecimal result1= b2.subtract(b1);//实际比较值
                Object obj = JSONPath.read(caseInfo.getParams(), "$.amount");
                BigDecimal result2=new BigDecimal(obj.toString());//期望值
                System.out.println("b1="+b1);
                System.out.println("b2="+b2);
                System.out.println("result1="+result1);//
                System.out.println(result2);
                if(result1.compareTo(result2)==0){
                    System.out.println("数据库断言成功");
                    flag=true;
                }else {
                    System.out.println("数据库断言失败");
                }
            }
        }else {
            System.out.println("sql为空，不需要断言");
        }
        return flag;
    }

    @DataProvider
    public Object[] datas() {
        Object[] datas = ExcelUtils.getDatas(sheetIndex, 1, CaseInfo.class);
        return datas;
    }
}
