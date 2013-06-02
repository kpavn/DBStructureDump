package dbstructure.MakeAllTablesDump;

/**
 * TParamNameValue
 */
public class TParamNameValue {
	public String strParamName;
	public String strParamValue;

	public TParamNameValue (String strParamName, String strParamValue) {
		this.strParamName  = strParamName;
		this.strParamValue = strParamValue;
	}

	public TParamNameValue (final TParamNameValue ParamNameValue) {
		this (ParamNameValue.strParamName, ParamNameValue.strParamValue);
	}
}

