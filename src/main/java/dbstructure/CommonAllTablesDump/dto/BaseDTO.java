package dbstructure.CommonAllTablesDump.dto;

public class BaseDTO {
	public BaseDTO() {
	}

	/*
	 * Service
	 */
	public final String StrToTeX(String str) {
		String strTex = str.replaceAll("_", "\\\\_\\\\-");
		return strTex;
	}

	public final String getString4Value(final String strFormatString, int intValue) {
		return String.format(strFormatString, intValue);
	}
	public final String getString4Value(final String strFormatString, long lintValue) {
		return String.format(strFormatString, lintValue);
	}
}
