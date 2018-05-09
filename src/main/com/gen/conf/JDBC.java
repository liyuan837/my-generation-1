package com.gen.conf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @Author: LiYuan
 * @Description:数据库连接
 * @Date: 17:59 2018/5/9
 */
public class JDBC {
    // 1 mysql, 2 oracle
    public static int dbType = 1;

    private static String url = "jdbc:mysql://127.0.0.1:3306/leolderdb";
    private static String username = "root";
    private static String password = "123456";
    private static String driverManager = "com.mysql.jdbc.Driver";

    /*
    private static String url = "jdbc:oracle:thin:@172.21.4.253:1521:ofdb";
    private static String username = "ofcardoramanager";
    private static String password = "ofcard";
    private static String driverManager = "oracle.jdbc.driver.OracleDriver";
    */
    // 加载驱动
    static {
        try {
            Class.forName(driverManager);
        } catch (Exception e) {
            throw new RuntimeException("load driver fail : "+e.toString());
        }
    }

    // 获取默认连接
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Can't get the connection : "+e.toString());
        }
    }


    // 获取Statement
    public static Statement getStatement() {
        Connection connection = getConnection();
        Statement statement = null;
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return statement;
    }

    // 关闭连接的Statement
    public static void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        Connection connection = null;
        try {
            connection = statement.getConnection();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
