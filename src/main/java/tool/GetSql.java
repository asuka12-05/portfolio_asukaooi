package tool;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * SQL文を取得するクラス
 * @author c3intl
 *
 */
public class GetSql {

	/** プロパティファイル */
	private final static String SQL_PROPERTY = "sqlProperty.properties";

	/**
	 * SQL取得するメソッド
	 * @param key	SQLプロパティからSQLを取得するキー
	 * @return	SQL文
	 * @exception SQL文が取得できなかった場合
	 */
	public static String getSql(String key) throws Exception {
		Properties properties = new Properties();
		String stReturn = null;
        try {
        	// プロパティファイルの読み込み
            properties.load(Files.newBufferedReader(Paths.get(SQL_PROPERTY), StandardCharsets.UTF_8));
            // SQL文の取得
            stReturn = (String) properties.get(key);
        } catch (IOException e) {
            // ファイル読み込みに失敗
			KadaiUtil.logOutPut(String.format("ファイルの読み込みに失敗しました。ファイル名:%s", SQL_PROPERTY));
			throw e;
        }
        // SQL文を戻す
        return stReturn;
	}
}
