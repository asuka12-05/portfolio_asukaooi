package tool;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 課題ユーティリティクラス.
 * 
 * @author c3soft
 *
 */
public class KadaiUtil {

	/**
	 * 標準出力にメッセージ出力.
	 * 
	 * @param stMsg		メッセージ文言
	 */
	public static void logOutPut(String stMsg) {
		// 日付フォーマット
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

		// 標準出力にメッセージ出力
		System.out.println(sdf.format(Calendar.getInstance().getTime()) + " " + stMsg);
	}


}
