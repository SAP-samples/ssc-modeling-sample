/**
 * (c) 2011 SAP AG. All rights reserved.
 */
package com.sap.custdev.projects.fbs.slc.junit;

import static org.junit.Assert.assertNotNull;

import java.util.Hashtable;
import java.util.UUID;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;
import com.sap.custdev.projects.fbs.slc.cfg.client.IKbProfilesData;
import com.sap.custdev.projects.fbs.slc.cfg.imp.ConfigSessionImpl;
import com.sap.custdev.projects.fbs.slc.util.IIPCConstants;
import com.sap.sxe.Environment;
import com.sap.sxe.db.conn;
import com.sap.sxe.db.connection_factory;
import com.sap.sxe.db.db;
import com.sap.sxe.db.sys_query_pair;
import com.sap.sxe.sys.exc.exc_database_error;

import util.KBKey;

/**
 * @author Thomas Wabner
 */
public class AbstractITCommandTest {

	protected static final String MATERIAL_NAME = "SME_OFFICE";
	protected static final String LOGICAL_SYSTEM = "LOCAL";
	protected static final String SME_OFFICE = "SME_OFFICE";
	protected static final String SME_WORKPLACE = "SME_WORKPLACE";
	protected static final String SME_NO_WORKPLACES_NF = "SME_NO_WORKPLACES_NF";
	protected static final String SME_WORKPLACE_TYPE_NS = "SME_WORKPLACE_TYPE_NS";
	protected static final String PRODUCT = "SME_OFFICE";
	protected static final String KB_NAME = "SME_OFFICE";
	protected static final String KB_LOGSYS = "LOCAL";
	protected static final String KB_VERSION = "0.0.1.0";
	protected connection_factory factory;
	protected static conn connection;
	protected static db classUnderTest;
	protected static String configId;
	protected static IConfigSession configSession;

	/**
	 * @param key
	 * @throws Exception
	 */
	public static void setUp(final KBKey key) throws Exception {
		// Get config Session
		final IConfigSession session = new ConfigSessionImpl();

		// Initialize config session
		final String sessionId = UUID.randomUUID().toString();
		session.createSession("true", "123456", "not empty", false, "EN");

		// Get profile
		String kbProfile = null;
		if (key.getKbName() != null) {
			IKbProfilesData[] profiles;
			profiles = session.getProfilesOfKB(key.getKbLogsys(), key.getKbName(), key.getKbVersion());
			kbProfile = profiles[0].getKbProfile();
		}

		// Instantiate product configuration
		final String rfcConfigId = null; // let the configurator create the id
		final String productId = key.getProductCode();
		final String productType = "MARA";
		final Integer kbIdInt = null; // knowledge base is already specified by
										// the triple kbLogsys, kbName,
										// kbVersion
		final String kbDateStr = null; // is only required for the search when
										// the knowledge base is not already
										// given as input
		final String kbBuild = null;
		final String useTraceStr = null;
		final Hashtable<String, String> context = null;
		final boolean setRichConfigId = true;

		configId = session.createConfig(rfcConfigId, productId, productType, key.getKbLogsys(), key.getKbName(),
				key.getKbVersion(), kbProfile, kbIdInt, kbDateStr, kbBuild, useTraceStr, context, setRichConfigId);

		assertNotNull(configId);
		configSession = session;
		initADTRelData();

	}

	/**
	 * Initialize ADT relationship data
	 */
	public static void initADTRelData() {
		try {
			final String TABLE_NAME = IIPCConstants.TABLE_NAME_ADT_REL_CRM;

			sys_query_pair[] ppSalesQuery = new sys_query_pair[4];
			ppSalesQuery[0] = new sys_query_pair(IIPCConstants.COL_ADT_REL_CLIENT, Environment.get_client());
			ppSalesQuery[1] = new sys_query_pair(IIPCConstants.COL_ADT_REL_REL_TYPE,
					IIPCConstants.ITEM_RELATIONSHIP_TYPE_SALES_PP);
			ppSalesQuery[2] = new sys_query_pair(IIPCConstants.COL_ADT_REL_FROM_ADT, "HAS_PART_PP");
			ppSalesQuery[3] = new sys_query_pair(IIPCConstants.COL_ADT_REL_TO_ADT, "IS_PART_OF_PP");

			sys_query_pair[] hardQuery = new sys_query_pair[4];
			hardQuery[0] = new sys_query_pair(IIPCConstants.COL_ADT_REL_CLIENT, Environment.get_client());
			hardQuery[1] = new sys_query_pair(IIPCConstants.COL_ADT_REL_REL_TYPE,
					IIPCConstants.ITEM_RELATIONSHIP_TYPE_SALES_HARD);
			hardQuery[2] = new sys_query_pair(IIPCConstants.COL_ADT_REL_FROM_ADT, "HAS_PART_SD_HARD");
			hardQuery[3] = new sys_query_pair(IIPCConstants.COL_ADT_REL_TO_ADT, "IS_PART_OF_SD_HARD");

			sys_query_pair[] softQuery = new sys_query_pair[4];
			softQuery[0] = new sys_query_pair(IIPCConstants.COL_ADT_REL_CLIENT, Environment.get_client());
			softQuery[1] = new sys_query_pair(IIPCConstants.COL_ADT_REL_REL_TYPE,
					IIPCConstants.ITEM_RELATIONSHIP_TYPE_SALES_SOFT);
			softQuery[2] = new sys_query_pair(IIPCConstants.COL_ADT_REL_FROM_ADT, "HAS_PART_SD_SOFT");
			softQuery[3] = new sys_query_pair(IIPCConstants.COL_ADT_REL_TO_ADT, "IS_PART_OF_SD_SOFT");

			sys_query_pair[] svQuery = new sys_query_pair[4];
			svQuery[0] = new sys_query_pair(IIPCConstants.COL_ADT_REL_CLIENT, Environment.get_client());
			svQuery[1] = new sys_query_pair(IIPCConstants.COL_ADT_REL_REL_TYPE, "SRVC");
			svQuery[2] = new sys_query_pair(IIPCConstants.COL_ADT_REL_FROM_ADT, "HAS_ITEM_SV");
			svQuery[3] = new sys_query_pair(IIPCConstants.COL_ADT_REL_TO_ADT, "IS_ITEM_OF_SV");

			sys_query_pair[] ppQuery = new sys_query_pair[4];
			ppQuery[0] = new sys_query_pair(IIPCConstants.COL_ADT_REL_CLIENT, Environment.get_client());
			ppQuery[1] = new sys_query_pair(IIPCConstants.COL_ADT_REL_REL_TYPE, "PP");
			ppQuery[2] = new sys_query_pair(IIPCConstants.COL_ADT_REL_FROM_ADT, "HAS_ITEM_PP");
			ppQuery[3] = new sys_query_pair(IIPCConstants.COL_ADT_REL_TO_ADT, "IS_ITEM_OF_PP");

			Environment.get_db().db_append_table(TABLE_NAME, ppSalesQuery);
			Environment.get_db().db_append_table(TABLE_NAME, hardQuery);
			Environment.get_db().db_append_table(TABLE_NAME, softQuery);
			Environment.get_db().db_append_table(TABLE_NAME, ppQuery);
			Environment.get_db().db_append_table(TABLE_NAME, svQuery);
		} catch (exc_database_error e) {
			// TODO: log error
		}

	}

	protected static void createConnection() {
		connection = connection_factory.create_connection();
		classUnderTest = db.getDb();
	}

}
