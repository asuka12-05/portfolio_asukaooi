package bending.tool;

/**
 * メッセージ作成クラス
 */
public class MessageFormatter {
	/**
	 * メッセージ作成
	 * @param template	置換可能文字がある定型文
	 * @param param		置換する単語
	 * @return			置換後の文字列
	 */
	public static String format(String template, String param) {
		// 入力したい値を置き換え
		String msg = template.replace("?", param);
		// 置き換え後のメッセージを返却
		return msg;
	}
	
	/**
	 * メッセージ作成
	 * @param template	置換可能文字がある定型文
	 * @param param		置換する単語(複数可)
	 * @return			置換後の文字列
	 */
	public static String formatMultiple(String template, String... params) {
		String msg = template;
	    for (int i = 0; i < params.length; i++) {
	        // ?1, ?2, ?3... を順番に置き換え
	        msg = msg.replace("?" + (i + 1), params[i]);
	    }
		// 置き換え後のメッセージを返却
		return msg;
	}
}
