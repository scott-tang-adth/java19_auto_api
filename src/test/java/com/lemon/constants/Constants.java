package com.lemon.constants;
import java.util.HashMap;
import java.util.Map;

public class Constants {
	int a;

    //数据驱动excel路径

    public static final String EXCEL_PATH ="D:\\ideaCode\\java19\\java19_auto_api_scottv8\\src\\test\\resources\\cases_v32.xlsx";

    public static final Map<String,String>HEADERS=new HashMap<>();
    //excel 响应回写列
    public static final int RESPONSE_WRTIE_BACK_CELLNUM = 8;
    //断言回写列
    public static final int assert_WRTIE_BACK_CELLNUM = 10;

    // jdbc:数据库名称://ip:port/数据库名称
    //jdbc:oracle:thin:@//127.0.0.1:1521/orcl
    public static final String JDBC_URL = "jdbc:mysql://api.lemonban.com:3306/futureloan?useUnicode=true&characterEncoding=utf-8";

    //数据库url,用户名和密码
    public static final  String JDBC_USERNAMEL="future";

    public static final  String JDBC_PASSWORD="123456";

}
