package com.lemon.cases;
import com.lemon.constants.Constants;
import com.lemon.pojo.CaseInfo;
import com.lemon.utils.AuthenticationUtils;
import com.lemon.utils.ExcelUtils;
import com.lemon.utils.HttpUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
public class LoginCase extends BaseCase{
    private static Logger logger=Logger.getLogger(LoginCase.class);//哪个类用就用哪个类的class
    @Test(dataProvider = "datas",description ="登录测试description属性")
    @Description("description 注解")
    @Step
    public void test( CaseInfo caseInfo) {

        paramsReplace(caseInfo);//从内存中的静态map中获取
        //获得响应对象
        HttpResponse response = HttpUtils.call(caseInfo.getMethod(),caseInfo.getContentType(),caseInfo.getUrl(),caseInfo.getParams(),Constants.HEADERS);
        //获得响应信息字符串
        String body = HttpUtils.printResponse(response);
        //获取token
        AuthenticationUtils.json2Vars(body,"$.data.token_info.token","${token}");
        //获取member_id
        AuthenticationUtils.json2Vars(body,"$.data.id","${member_id}");
        boolean assertResponseFlag = assertResponse(body, caseInfo.getExpectResult());
        addWriteBackData(caseInfo.getId(),Constants.RESPONSE_WRTIE_BACK_CELLNUM,body);
        String assertResult=assertResponseFlag ?"passed":"failed";
        addWriteBackData(caseInfo.getId(),Constants.assert_WRTIE_BACK_CELLNUM,assertResult);
        //报表断言
        Assert.assertEquals(assertResult,"passed");

    }
    @DataProvider
    public Object[] datas() {
        //testng传过来sheetIndex的值
        Object[] datas = ExcelUtils.getDatas(sheetIndex, 1, CaseInfo.class);
        return datas;
    }

}
