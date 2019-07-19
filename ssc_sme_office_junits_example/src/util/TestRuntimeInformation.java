package util;

import java.text.NumberFormat;
import java.util.Properties;

import com.sap.sxe.db.CustomDatabaseInfo;
import com.sap.vmc.runtime.RuntimeInformation;
import com.sap.vmc.runtime.info.AbapDateFormat;
import com.sap.vmc.runtime.info.ApplicationExecution;
import com.sap.vmc.runtime.info.ApplicationServerInstance;
import com.sap.vmc.runtime.info.ApplicationServerSystem;
import com.sap.vmc.runtime.info.DatabaseSystem;
import com.sap.vmc.runtime.info.LocaleSettings;
import com.sap.vmc.runtime.info.Logon;

/**
 * 
 * 
 * @author Thomas Wabner
 */
public class TestRuntimeInformation extends RuntimeInformation {

	private Properties properties;
	private DatabaseSystem databaseSystem;
	private Logon logon;

	public TestRuntimeInformation() {
		readProperties();
		this.databaseSystem = new DatabaseSystemUnitTestImpl();
		this.logon = new LogonUnitTestImpl();
	}

	@Override
	public DatabaseSystem getDatabaseInfo() {
		return databaseSystem;
	}

	@Override
	public ApplicationExecution getExecutionInfo() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ApplicationServerInstance getServerInfo() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ApplicationServerSystem getSystemInfo() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Logon getUserInfo() {
		return logon;
	}

	/*
	 * Private helper methods
	 */

	private void readProperties() {
		properties = new Properties();
		properties.put(CustomDatabaseInfo.DATABASE_SYSTEM_DATABASE, "test");
		properties.put(CustomDatabaseInfo.DATABASE_SYSTEM_HOSTNAME, "localhost");
		properties.put(CustomDatabaseInfo.DATABASE_SYSTEM_PORT, "1433");
		properties.put(CustomDatabaseInfo.DATABASE_SYSTEM_USER, "user");
		properties.put(CustomDatabaseInfo.DATABASE_SYSTEM_PWD, "password");
		properties.put(CustomDatabaseInfo.DATABASE_SYSTEM_JNDI_USAGE, "false");
		properties.put("db_client", "000");
	}

	/*
	 * Private classes
	 */

	private class DatabaseSystemUnitTestImpl implements DatabaseSystem {

		@Override
		public Properties getDatabaseConnectionProperties() {
			return properties;
		}

		@Override
		public String getHostname() {
			return properties.getProperty(CustomDatabaseInfo.DATABASE_SYSTEM_HOSTNAME);
		}

		@Override
		public String getRelease() {
			// throw new UnsupportedOperationException();
			return null;
		}

		@Override
		public String getSystemType() {
			// return strings as defined in class connection_factory
			return "Microsoft SQL Server 2";
		}
	}

	private class LogonUnitTestImpl implements Logon {

		private final LocaleSettings localeSettings;

		public LogonUnitTestImpl() {
			this.localeSettings = new LocaleSettingsUnitTestImpl();
		}

		@Override
		public String getClient() {
			return properties.getProperty("db_client");
		}

		@Override
		public LocaleSettings getLocaleSettings() {
			return localeSettings;
		}

		@Override
		public String getUsername() {
			// throw new UnsupportedOperationException();
			return properties.getProperty(CustomDatabaseInfo.DATABASE_SYSTEM_USER);
		}
	}

	private static class LocaleSettingsUnitTestImpl implements LocaleSettings {

		@Override
		public AbapDateFormat getDateFormat() {
			// throw new UnsupportedOperationException();
			return null;
		}

		@Override
		public String getISOLanguage() {
			return "en";
		}

		@Override
		public String getLanguage() {
			return "E";
		}

		@Override
		public NumberFormat getNumberFormat() {
			// throw new UnsupportedOperationException();
			return null;
		}

		@Override
		public NumberFormat getScientificNumberFormat() {
			// throw new UnsupportedOperationException();
			return null;
		}

		@Override
		public AbapDateFormat getTimeFormat() {
			// throw new UnsupportedOperationException();
			return null;
		}
	}

}
