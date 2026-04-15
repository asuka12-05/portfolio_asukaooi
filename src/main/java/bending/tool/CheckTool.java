package bending.tool;

/**
 * チェックツール
 */
public class CheckTool {
	/**
	 * 必須チェック
	 * @param param		チェック対象の文字列
	 * @return			真：入力済、偽：未入力
	 */
	public static boolean compulsoryCheck(String param) {
		return !(param == null || param.isEmpty());
	}
	
	/**
	 * 入力値のブランクを取り除く
	 * @param input     入力値
	 * @return          ブランクを除去した文字列。inputがnullの場合はnull
	 */
	public static String blankTrim(String input) {
	    if (input == null) {
	        return null;
	    }
	    return input.replaceFirst(Const.REGEX_BLANK_FIRST, "")
	                .replaceFirst(Const.REGEX_BLANK_END, "");
	}

}
