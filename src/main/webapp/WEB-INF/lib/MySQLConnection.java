package lib;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * コネクション取得
 * @author c3intl
 *
 */
public class MySQLConnection {

	// DB接続用定数
	private final static String DATABASE_NAME = "java_mysql";
	//private final static String DATABASE_NAME = "practics";
	private final static String PROPATIES = "?characterEncoding=UTF-8";
	private final static String URL = "jdbc:mySQL://localhost:3306/" + DATABASE_NAME + PROPATIES;
	// DB接続用・ユーザ定数
	private final static String USER = "root";
	private final static String PASS = "c3user";

	/**
	 * コネクション取得
	 * @return	コネクション
	 * @throws Exception	コネクション取得時に発生した例外
	 */
	public static Connection getConnection() throws Exception {

		// コネクション
		Connection conn = null;

		try {
			// JDBCドライバをロードする
			Class.forName("com.mysql.cj.jdbc.Driver");
			// コネクション取得
			conn = DriverManager.getConnection(URL, USER, PASS);

		} catch (Exception e) {
			KadaiUtil.logOutPut("コネクション取得に失敗しました。" + e.getMessage());
			throw e;
		}
		// コネクションを返却
		return conn;
	}

}
