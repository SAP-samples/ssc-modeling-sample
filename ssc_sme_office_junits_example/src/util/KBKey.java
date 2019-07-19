package util;


import java.util.Date;


/**
 * Key to identify a knowledge base (KB) for product configuration. This Object is immutable.
 * 
 */
public interface KBKey
{

	/**
	 * @return the product code
	 */
	public String getProductCode();

	/**
	 * @return the knowledge base name
	 */
	public String getKbName();

	/**
	 * @return the knowledge base logical system
	 */
	public String getKbLogsys();

	/**
	 * @return the knowledge base version
	 */
	public String getKbVersion();

	/**
	 * @return the date
	 */
	public Date getDate();

}