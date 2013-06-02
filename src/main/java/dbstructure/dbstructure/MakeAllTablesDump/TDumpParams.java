package dbstructure.dbstructure.MakeAllTablesDump;

import java.util.*;
import java.util.regex.Pattern;
import dbstructure.dbstructure.CommonAllTablesDump.dto.DbObjectDTO;

/**
 * TDumpParams
 * parameters for dump the database instance (database specific)
 */
public class TDumpParams {
	public ArrayList<TDumpDbParams> array_options;

	/**
	 * TDumpParams
	 */
	public TDumpParams() {
		this.array_options = new ArrayList<TDumpDbParams>();
	}

	/**
	 * getSize
	 */
	public int getSize() {
		return array_options.size();
	}

	/**
	 * isDbExists
	 */
	public boolean isDbExists (final String str_dbname) {
		boolean       boolIsFound  = false;
		TDumpDbParams DumpDbParams = null;

		for (Iterator<TDumpDbParams> it = array_options.iterator(); it.hasNext();) {
			DumpDbParams = new TDumpDbParams(it.next());

			if (DumpDbParams.patternDBName.matcher(str_dbname).matches()) {
				boolIsFound = true;
				break;
			}
		}

		return boolIsFound;
	}

	public String getDbOptionValue (final DbObjectDTO dbObjectDTO, final String strOption) {
		String        str_value    = null;
		TDumpDbParams DumpDbParams = null;

		if (dbObjectDTO.getDbName() != null) {
			for (Iterator<TDumpDbParams> it = array_options.iterator(); it.hasNext();) {
				DumpDbParams = new TDumpDbParams((TDumpDbParams)(it.next()));
	
				if (DumpDbParams.patternDBName.matcher(dbObjectDTO.getDbName()).matches()) {
					str_value = DumpDbParams.getOptionValue(
						 dbObjectDTO.getObjectType()
						,dbObjectDTO.getObjectName()
						,strOption
					);

					if (str_value.compareTo("true") == 0) {
						break;
					}
				}
			}
		}

		return str_value;
	}

	public String getDbOptionValue (final DbObjectDTO dbObjectDTO, final String strOption, final String strSubOption) {
		String        str_value    = null;
		TDumpDbParams DumpDbParams = null;

		if (dbObjectDTO.getDbName() != null) {
			for (Iterator<TDumpDbParams> it = array_options.iterator(); it.hasNext();) {
				DumpDbParams = new TDumpDbParams((TDumpDbParams)(it.next()));
	
				if (DumpDbParams.patternDBName.matcher(dbObjectDTO.getDbName()).matches()) {
					str_value = DumpDbParams.getOptionSubValue(
						 dbObjectDTO.getObjectType()
						,dbObjectDTO.getObjectName()
						,strOption
						,strSubOption
					);

					if (str_value.compareTo("true") == 0) {
						break;
					}
				}
			}
		}

		return str_value;
	}

	/**
	 * getDbOptionValue
	 */
	public String getDbOptionValue (
	 final String strDbName,
	 final String strObjectType,
	 final String strObjectName,
	 final String strObjectOwner,
	 final String strObjectSchema,
	 final String strOption)
	{
		return getDbOptionValue(new DbObjectDTO(
			 null
			,strDbName
			,strObjectType
			,strObjectName
			,strObjectOwner
			,strObjectSchema
			)
			,strOption
		);
	}

	/**
	 * getDbOptionValue
	 */
	public String getDbOptionValue (
	 final String strDbName,
	 final String strObjectType,
	 final String strObjectName,
	 final String strObjectOwner,
	 final String strObjectSchema,
	 final String strOption,
	 final String strSubOption) {
		return getDbOptionValue(new DbObjectDTO(
			 null
			,strDbName
			,strObjectType
			,strObjectName
			,strObjectOwner
			,strObjectSchema
			)
			,strOption
			,strSubOption
		);
	}

	/**
	 * setDbOptionValue
	 */
	public void setDbOptionValue (
	 final String strDbName,
	 final String strObjectType,
	 final String strObjectName,
	 final String strOption,
	 final String strValue) {
		TDumpDbParams DumpDbParams  = null;
		boolean       bool_is_found = false;

		for (Iterator<TDumpDbParams> it = array_options.iterator(); it.hasNext();) {
			DumpDbParams = it.next();

			if (DumpDbParams.str_dbname.compareTo(strDbName) == 0) {
				DumpDbParams.setOptionValue(strObjectType, strObjectName, strOption, strValue);
				bool_is_found = true;
				break;
			}
		}

		if (!bool_is_found) {
			DumpDbParams = new TDumpDbParams(strDbName);
			DumpDbParams.setOptionValue(strObjectType, strObjectName, strOption, strValue);
			array_options.add(DumpDbParams);
		}
	}

	/**
	 * setDbOptionValue
	 */
	public void setDbOptionValue (
	 final String                     strDbName,
	 final String                     strObjectType,
	 final String                     strObjectName,
	 final String                     strOption,
	 final ArrayList<TParamNameValue> arrayList
	) {
		TDumpDbParams DumpDbParams  = null;
		boolean       bool_is_found = false;

		for (Iterator<TDumpDbParams> it = array_options.iterator(); it.hasNext();) {
			DumpDbParams = it.next();

			if (DumpDbParams.patternDBName.matcher(strDbName).matches()) {
				DumpDbParams.setOptionValue(strObjectType, strObjectName, strOption, arrayList);
				bool_is_found = true;
				break;
			}
		}

		if (!bool_is_found) {
			DumpDbParams = new TDumpDbParams(strDbName);
			DumpDbParams.setOptionValue(strObjectType, strObjectName, strOption, arrayList);
			array_options.add(DumpDbParams);
		}
	}

	/**
	 * TDumpDbParams
	 * parameters for the database
	 */
	public class TDumpDbParams {
		public String                         str_dbname;
		public ArrayList<TDumpDbParamsOption> array_options;

		/**
		 * TDumpDbParams
		 */
		public TDumpDbParams (final String str_dbname) {
			this.str_dbname    = str_dbname;

			this.array_options = new ArrayList<TDumpDbParamsOption>();
			this.patternDBName = Pattern.compile(str_dbname);
		}

		/**
		 * TDumpDbParams
		 */
		public TDumpDbParams (final TDumpDbParams DumpDbParams) {
			this(DumpDbParams.str_dbname);
			this.array_options = DumpDbParams.array_options;
		}

		/**
		 * TDumpDbParams
		 * get optional value
		 */
		public String getOptionValue (
		 final String strObjectType,
		 final String strObjectName,
		 final String strParamName)
		{
			String              strParamValue      = "";
			TDumpDbParamsOption DumpDbParamsOption = null;

			for (Iterator<TDumpDbParamsOption> it = array_options.iterator(); it.hasNext();) {
				DumpDbParamsOption = new TDumpDbParamsOption(it.next());

				if (DumpDbParamsOption.matchesObjectType(strObjectType) &&
				    DumpDbParamsOption.matchesObjectName(strObjectName) &&
				    DumpDbParamsOption.strParamName.compareTo(strParamName) == 0
				) {
					strParamValue = DumpDbParamsOption.strParamValue;

					if (strParamValue.compareTo("true") == 0) {
						break;
					}
				}
			}

			return strParamValue;
		}

		/**
		 * TDumpDbParams
		 * get optional value
		 */
		public String getOptionSubValue (
		 final String strObjectType,
		 final String strObjectName,
		 final String strParamName,
		 final String strParamSubName)
		{
			String                     strParamValue      = "";
			TDumpDbParamsOption        DumpDbParamsOption = null;
			TParamNameValue            ParamNameValue     = null;
			ArrayList<TParamNameValue> arrayList          = null;

			for (Iterator<TDumpDbParamsOption> it = array_options.iterator(); it.hasNext();) {
				DumpDbParamsOption = new TDumpDbParamsOption(it.next());

				if (DumpDbParamsOption.matchesObjectType(strObjectType) &&
				    DumpDbParamsOption.matchesObjectName(strObjectName) &&
				    DumpDbParamsOption.strParamName.compareTo(strParamName) == 0)
				{
					if ((arrayList = DumpDbParamsOption.arrayList) != null)
					{
						for (Iterator<TParamNameValue> it2 = arrayList.iterator(); it2.hasNext();) {
							ParamNameValue = new TParamNameValue(it2.next());
							if (ParamNameValue.strParamName.compareTo(strParamSubName) == 0) {
								strParamValue = ParamNameValue.strParamValue;
								if (strParamValue.compareTo("true") == 0) {
									break;
								}
							}
						}
					}
				}
			}

			return strParamValue;
		}

		/**
		 * TDumpDbParams
		 * set optional value
		 */
		public void setOptionValue (
		 final String strObjectType,
		 final String strObjectName,
		 final String strParamName,
		 final String strParamValue)
		{

			TDumpDbParamsOption DumpDbParamsOption = null;
			boolean             bool_is_found = false;

			for (Iterator<TDumpDbParamsOption> it = array_options.iterator(); it.hasNext();) {
				DumpDbParamsOption = it.next();

				if (DumpDbParamsOption.strObjectType.compareTo(strObjectType) == 0 &&
				    DumpDbParamsOption.strObjectName.compareTo(strObjectName) == 0 &&
				    DumpDbParamsOption.strParamName.compareTo(strParamName) == 0)
				{
					bool_is_found = true;
					break;
				}
			}

			if (!bool_is_found) {
				array_options.add(new TDumpDbParamsOption(strObjectType, strObjectName, strParamName, strParamValue));
			}
		}

		/**
		 * TDumpDbParams
		 * set optional value
		 */
		public void setOptionValue (
		 final String                     strObjectType,
		 final String                     strObjectName,
		 final String                     strParamName,
		 final ArrayList<TParamNameValue> arrayList)
		{
			TDumpDbParamsOption DumpDbParamsOption = null;
			boolean             bool_is_found = false;

			for (Iterator<TDumpDbParamsOption> it = array_options.iterator(); it.hasNext();) {
				DumpDbParamsOption = it.next();

				if (DumpDbParamsOption.strObjectType.compareTo(strObjectType) == 0 &&
				    DumpDbParamsOption.strObjectName.compareTo(strObjectName) == 0 &&
				    DumpDbParamsOption.strParamName.compareTo(strParamName) == 0)
				{
					bool_is_found = true;
					break;
				}
			}

			if (!bool_is_found) {
				array_options.add(new TDumpDbParamsOption(strObjectType, strObjectName, strParamName, arrayList));
			}
		}

		/**
		 * TDumpDbParamsOption
		 */
		public class TDumpDbParamsOption {
			public String                     strObjectType;
			public String                     strObjectName;
			public String                     strParamName;
			public String                     strParamValue;
			public ArrayList<TParamNameValue> arrayList;

			private Pattern patternObjectType;
			private Pattern patternObjectName;

			/**
			 * TDumpDbParamsOption
			 * base constructor
			 */
			private TDumpDbParamsOption (
			 final String                     stObjectType,
			 final String                     stObjectName,
			 final String                     strParamName,
			 final String                     strParamValue,
			 final ArrayList<TParamNameValue> arrayList) {
				this.strObjectType = stObjectType;
				this.strObjectName = stObjectName;
				this.strParamName  = strParamName;
				this.strParamValue = strParamValue;

				if (arrayList != null) {
					this.arrayList = new ArrayList<TParamNameValue>(arrayList);
				}
				else {
					this.arrayList = null;
				}

				this.patternObjectType = Pattern.compile(strObjectType);
				this.patternObjectName = Pattern.compile(strObjectName);
			}

			/**
			 * TDumpDbParamsOption
			 * base constructor
			 */
			public TDumpDbParamsOption (
			 final String stObjectType,
			 final String stObjectName,
			 final String strParamName,
			 final String strParamValue) {
				this (
				 stObjectType,
				 stObjectName,
				 strParamName,
				 strParamValue,
				 null
				);
			}

			/**
			 * TDumpDbParamsOption
			 * base constructor
			 */
			public TDumpDbParamsOption (
			 final String                     stObjectType,
			 final String                     stObjectName,
			 final String                     strParamName,
			 final ArrayList<TParamNameValue> arrayList) {
				this (
				 stObjectType,
				 stObjectName,
				 strParamName,
				 null,
				 arrayList);
			}

			/**
			 * TDumpDbParamsOption
			 * additional constructor
			 */
			public TDumpDbParamsOption (final TDumpDbParamsOption DumpDbParamsOption) {
			 this(DumpDbParamsOption.strObjectType,
			      DumpDbParamsOption.strObjectName,
			      DumpDbParamsOption.strParamName,
			      DumpDbParamsOption.strParamValue,
			      DumpDbParamsOption.arrayList);
			}

			/**
			 * matchesObjectType
			 */
			public boolean matchesObjectType (final String strType) {
				return patternObjectType.matcher(strType).matches();
			}

			/**
			 * matchesObjectType
			 */
			public boolean matchesObjectName (final String strName) {
				return patternObjectName.matcher(strName).matches();
			}
		}

		private Pattern patternDBName;
	}
}
