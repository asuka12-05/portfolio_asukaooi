package lib;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * コネクション取得
 * DB接続情報は定数で管理
 */
public class MySQLConnection {

    // DB接続用定数
    private static final String DATABASE_NAME = "heatbreak_db";
    private static final String PROPERTIES    = "?characterEncoding=UTF-8&serverTimezone=Asia/Tokyo";
    private static final String URL           = "jdbc:mysql://localhost:3306/" + DATABASE_NAME + PROPERTIES;

    // DB接続用ユーザ定数
    private static final String USER = "root";
    private static final String PASS = "your_password_here";

    /**
     * コネクション取得
     *
     * @return コネクション
     * @throws Exception コネクション取得時に発生した例外
     */
    public static Connection getConnection() throws Exception {
        Connection conn = null;
        try {
            // JDBCドライバをロードする
            Class.forName("com.mysql.cj.jdbc.Driver");
            // コネクション取得
            conn = DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            System.err.println("コネクション取得に失敗しました。" + e.getMessage());
            throw e;
        }
        return conn;
    }
}
