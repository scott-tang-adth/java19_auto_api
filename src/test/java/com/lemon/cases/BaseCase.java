package com.lemon.cases;
import cn.binarywang.tools.generator.ChineseIDCardNumberGenerator;
import cn.binarywang.tools.generator.ChineseMobileNumberGenerator;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.lemon.constants.Constants;
import com.lemon.pojo.CaseInfo;
import com.lemon.pojo.WriteBackData;
import com.lemon.utils.AuthenticationUtils;
import com.lemon.utils.ExcelUtils;
import io.qameta.allure.Step;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


import static com.lemon.utils.ExcelUtils.batchWrite;

/**
 * @author by Scott
 * @date 2020/6/23.
 */
public class BaseCase {
    public static void main( String[] args ) {

        String MobileNumber = ChineseMobileNumberGenerator.getInstance().generate();
        String IDCardNumber = ChineseIDCardNumberGenerator.getInstance().generate();
        System.out.println("MobileNumber="+MobileNumber);
        System.out.println(IDCardNumber);
    }

    public int sheetIndex;

    private static Logger logger=Logger.getLogger(BaseCase.class);

    @BeforeSuite//只执行一次
    public void init() throws Exception{
        logger.info("===============init================================");

        Constants.HEADERS.put("X-Lemonban-Media-Type", "lemonban.v2");
        Constants.HEADERS.put("Content-Type", "application/json");
        //把配置文件读到集合中
        Properties prop=new Properties();
        String path=BaseCase.class.getClassLoader().getResource("./params.properties").getPath();
        FileInputStream fis=new FileInputStream(path);
        prop.load(fis);
        fis.close();
        AuthenticationUtils.VARS.putAll((Map)prop);//调用map的putAll方法把配置文件存入全局的map VARS中
        System.out.println("AuthenticationUtils.VARS=================="+AuthenticationUtils.VARS);
        logger.info("AuthenticationUtils.VARS=================="+AuthenticationUtils.VARS);

    }

    @AfterSuite
    public void finish() {
        batchWrite();
    }

    @BeforeClass
    @Parameters({"sheetIndex"})
    public void beforeClass( int sheetIndex ) {
        //System.out.println("RegisterCase ===== sheetIndex:=============" + sheetIndex);
        this.sheetIndex = sheetIndex;//从testng参数中传过来的赋值给成员变量

    }
    //这里不能写static
    public void addWriteBackData(int rownum,int cellnum, String content) {
        WriteBackData wbd =
                new WriteBackData(sheetIndex,rownum, cellnum,content);
        //添加到回写集合
        ExcelUtils.wbdList.add(wbd);
    }
    //断言模块
    @Step("assertResponse")
    public boolean assertResponse(String body, String expectResult) {
        //json转成map
        Map<String,Object> map = JSONObject.parseObject(expectResult,Map.class);
        Set<String> keySet = map.keySet();
        boolean assertResponseFlag = true;
        for (String expression : keySet) {
            //1、获取期望值
            Object expectValue = map.get(expression);
            //2、通过jsonpath找到实际值
            Object actualValue = JSONPath.read(body,expression);
            //3、比较期望值和实际值
            if(expectValue == null && actualValue != null) {
                assertResponseFlag = false;
                break;
            }
            if(expectValue == null && actualValue == null) {
                continue;
            }
            if(!expectValue.equals(actualValue)) {
                assertResponseFlag = false;
                break;
            }
        }
        System.out.println("响应断言结果：" + assertResponseFlag);
        return assertResponseFlag;
    }
    //参数替换函数
    public void paramsReplace( CaseInfo caseInfo ) {
        Set<String> keySet= AuthenticationUtils.VARS.keySet();
        for (String key : keySet) {
            String value=AuthenticationUtils.VARS.get(key).toString();
            //sql、params、expectResult
            //select leave_amount from member where id = ${member_id};用字符串函数replace替换得到真实数据
            if(StringUtils.isNotBlank(caseInfo.getSql())) {
                String sql = caseInfo.getSql().replace(key, value);
                //设置回去要不然会为空
                caseInfo.setSql(sql);
            }//替换params
            if(StringUtils.isNotBlank(caseInfo.getParams())) {
                String params = caseInfo.getParams().replace(key, value);
                caseInfo.setParams(params);
            }
            //替换expectResult
            if(StringUtils.isNotBlank(caseInfo.getExpectResult())) {
                String expectResult = caseInfo.getExpectResult().replace(key, value);
                caseInfo.setExpectResult(expectResult);
            }
            //替换url 为特殊接口需要url替换
            if(StringUtils.isNotBlank(caseInfo.getUrl())){
                String expectResult=caseInfo.getExpectResult().replace(key,value);
                caseInfo.setExpectResult(expectResult);
            }
        }
    }
}