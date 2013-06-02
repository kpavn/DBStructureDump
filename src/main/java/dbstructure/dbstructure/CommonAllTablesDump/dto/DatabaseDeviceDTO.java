package dbstructure.dbstructure.CommonAllTablesDump.dto;

import java.util.Date;

public final class DatabaseDeviceDTO extends BaseDTO {
	public DatabaseDeviceDTO (
	 final String strName,
	 final String strPhyname,
	 long         lintSize,
	 int          intSegmap,
	 final Date   dateCrDate,
	 boolean      isDataDevice,
	 boolean      isLogDevice
	)
	{
		super();

		this.strName      = strName;
		this.strPhyname   = strPhyname;
		this.lintSize     = lintSize;
		this.intSegmap    = intSegmap;
		this.dateCrDate   = dateCrDate;
		this.isDataDevice = isDataDevice;
		this.isLogDevice  = isLogDevice;
	}

	/*
	 * Get
	 */
	public final String getName() {
		return strName;
	}
	public final String getPhyname() {
		return strPhyname;
	}
	public long getSize() {
		return lintSize;
	}
	public long getSizeMb() {
		return lintSize / (1024L * 1024L);
	}
	public long getSizeKb() {
		return lintSize / 1024L;
	}
	public int getSegmap() {
		return intSegmap;
	}
	public final Date getCrDate() {
		return dateCrDate;
	}
	public boolean isDataDevice() {
		return isDataDevice;
	}
	public boolean isLogDevice() {
		return isLogDevice;
	}

	private String  strName;
	private String  strPhyname;
	private long    lintSize;
	private int     intSegmap;
	private Date    dateCrDate;
	private boolean isDataDevice;
	private boolean isLogDevice;
}

