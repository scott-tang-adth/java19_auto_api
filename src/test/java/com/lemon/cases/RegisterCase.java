package com.lemon.cases;
import com.lemon.constants.Constants;
import com.lemon.pojo.CaseInfo;
import com.lemon.utils.ExcelUtils;
import com.lemon.utils.HttpUtils;
import com.lemon.utils.SQLUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.*;
public class RegisterCase extends BaseCase {

    @Test(dataProvider = "datas")

    public void test( CaseInfo caseInfo) throws Exception {

        paramsReplace(caseInfo);
        //sql: select leave_amount from member a where id = ${member_id};
        //params: {"member_id":"${member_id}","amount":"${amount}"}
        //${member_id}=11
        //数据库前置查询结果
        Object beforesqlResult = SQLUtils.getSingleResult(caseInfo.getSql());
        HttpResponse response = HttpUtils.call(caseInfo.getMethod(), caseInfo.getContentType(), caseInfo.getUrl(), caseInfo.getParams(), Constants.HEADERS);
        String body = HttpUtils.printResponse(response);
        //HttpUtils.printResponse(response);
        boolean assertResponseFlag = assertResponse(body, caseInfo.getExpectResult());
        addWriteBackData(caseInfo.getId(), Constants.RESPONSE_WRTIE_BACK_CELLNUM, body);
        //数据库后置查询结果
        Object afterSqlResult = SQLUtils.getSingleResult(caseInfo.getSql());
        boolean assertSqlFlag=sqlAssert(caseInfo.getSql(), beforesqlResult, afterSqlResult);
        String assertResult=assertResponseFlag && assertSqlFlag ?"passed":"failed";
        addWriteBackData(caseInfo.getId(),Constants.assert_WRTIE_BACK_CELLNUM,assertResult);
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
