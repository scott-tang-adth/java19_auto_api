package com.lemon.utils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang3.StringUtils;
import java.sql.Connection;
import java.util.Map;
/**
 * @author by Scott
 * @date 2020/6/24.
 */
public class SQLUtils {
    public static void main( String[] args ) {

       // mapHandler();
     // Object obj=getSingleResult("SELECT * FROM member a LIMIT 10;");
        //select count(*) from member a where a.mobile_phone = '18911000049';
      //  Object obj=getSingleResult("select count(*) from member a where a.mobile_phone = '15050378576';");
       // SELECT leave_amount from member a WHERE id=197912;
     //   Object obj=getSingleResult("select leave_amount from member a where id = ${member_id};");
       // System.out.println(obj);

       /* Object obj=getSingleResult(" select leave_amount from  member where id = 207153;");
        System.out.println(obj);*/
    }
    /**
     *
     * @param sql  sql语句
     * @return    返回结果
     */
    public static Object getSingleResult(String sql) {

        if(StringUtils.isBlank(sql))//StringUtils.isBlank(sql))
        {
            System.out.println("sql为空");
             return null;
        }
        //创建 QueryRunner对象
        Object reusult=null;
        QueryRunner runner=new QueryRunner();
        //获取数据库连接
        Connection con = JDBCUtils.getConnection();
        try {

            //创建结果集对象
            ScalarHandler handler=new ScalarHandler();
            //执行查询语句
             reusult= runner.query(con, sql, handler);//成功则返回1

            //System.out.println(reusult);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            JDBCUtils.close(con);
        }
        return reusult;
    }


    public static void mapHandler() {

        QueryRunner runner=new QueryRunner();

        Connection con = JDBCUtils.getConnection();

        try {

            String sql="SELECT * FROM member a WHERE a.mobile_phone='15670890431'";

            MapHandler handler=new MapHandler();

            Map<String, Object> map = runner.query(con, sql, handler);

            System.out.println(map);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            JDBCUtils.close(con);
        }
    }
}
