package com.lemon.cases;
import com.lemon.constants.Constants;
import com.lemon.pojo.CaseInfo;
import com.lemon.utils.AuthenticationUtils;
import com.lemon.utils.ExcelUtils;
import com.lemon.utils.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

public class AddCase extends BaseCase {

    @Test(dataProvider = "datas")

    public void test( CaseInfo caseInfo) throws Exception {

        //excel表格参数化
        paramsReplace(caseInfo);
       //接口鉴权
        Map<String, String> header = AuthenticationUtils.getTokenHeader();
        HttpResponse response = HttpUtils.call(caseInfo.getMethod(), caseInfo.getContentType(), caseInfo.getUrl(), caseInfo.getParams(), header);
        String body = HttpUtils.printResponse(response);
        //获取 loan_id
        AuthenticationUtils.json2Vars(body,"$.data.id","${loan_id}");
        boolean assertResponseFlag = assertResponse(body, caseInfo.getExpectResult());
        //接口响应回写内容
        addWriteBackData(caseInfo.getId(), Constants.RESPONSE_WRTIE_BACK_CELLNUM, body);
        //数据库后置查询结果
        String assertResult=assertResponseFlag ?"passed":"failed";
        addWriteBackData(caseInfo.getId(),Constants.assert_WRTIE_BACK_CELLNUM,assertResult);
        //报表断言
        Assert.assertEquals(assertResult,"passed");
    }
    public boolean sqlAssert( String sql, Object beforesqlResult, Object afterSqlResult ) {

        boolean flag=false;
        if (StringUtils.isNotBlank(sql)) {
            if (beforesqlResult == null || afterSqlResult == null) {
                System.out.println("数据库断言失败");
            } else {
                //object 类型不可以比较
                Long l1 = (long) beforesqlResult;
                Long l2 = (long) afterSqlResult;
                //接口执行之前查询结果0，接口执行后查询结果为1
                if (l1 == 0 && l2 == 1) {
                    flag=true;
                    System.out.println("数据库断言成功");
                } else {
                    System.out.println("数据库断言失败");
                }
            }
        }else {
            System.out.println("sql为空，不需要断言");
        }
        return flag;
    }
    @DataProvider
    public Object[] datas() throws Exception {
        //这个方法从公用成员变量中或者传过来的值
        Object[] datas = ExcelUtils.getDatas(sheetIndex,1,CaseInfo.class);//List转化成七个参数的一维数组
        return datas;
    }

}
