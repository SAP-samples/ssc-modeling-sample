package com.sap.custdev.projects.fbs.slc.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.sap.custdev.projects.fbs.slc.cfg.client.IAssumptions;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigContainer;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigLoadMessages;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigProfiling;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigurationStructure;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConflictContainer;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConflictingAssumptionsContainer;
import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticData;
import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticHeader;
import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticValueData;
import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticsGroups;
import com.sap.custdev.projects.fbs.slc.cfg.client.IDecompTypeData;
import com.sap.custdev.projects.fbs.slc.cfg.client.IDeltaBean;
import com.sap.custdev.projects.fbs.slc.cfg.client.IInstanceData;
import com.sap.custdev.projects.fbs.slc.cfg.client.IInstanceTypeData;
import com.sap.custdev.projects.fbs.slc.cfg.client.IItemRelationshipData;
import com.sap.custdev.projects.fbs.slc.cfg.client.IItemRelationshipTypeInfo;
import com.sap.custdev.projects.fbs.slc.cfg.client.IJustificationData;
import com.sap.custdev.projects.fbs.slc.cfg.client.IKbProfilesData;
import com.sap.custdev.projects.fbs.slc.cfg.client.IKnowledgeBaseData;
import com.sap.custdev.projects.fbs.slc.cfg.client.ISpecializationData;
import com.sap.custdev.projects.fbs.slc.cfg.client.ITraceSettings;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.CsticData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.CsticHeader;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.CsticValueData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.InstanceData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.TraceSettings;
import com.sap.custdev.projects.fbs.slc.cfg.exception.IpcCommandException;

import util.KBKey;
import util.KBKeyImpl;

/**
 * The purpose of this JUnit test class is to act as an example of how to use the "ready-to-integrate" EJB API of SAP
 * Solution Sales Configuration (SSC). The test methods (marked with annotation @Test) need to be executed in the order
 * in which they are declared in the class. This is achieved with annotation <code>@FixMethodOrder</code> that is
 * available as of JUnit 4.11. The assertNotNull calls serve not only as a minimal result check, but also to avoid
 * compiler warnings about unused variables. The member variables need to be static when running this class in JUnit
 * environment as every execution of a test method is performed in a separate instance of this class.
 * 
 * @author SAP Custom Development
 * @version Draft 0.9
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConfigSessionBeanExampleTest extends AbstractITCommandTest {

	private static IKnowledgeBaseData[] knowledgebaseData;
	private static IInstanceData rootInstance;

	private static String configXML;
	private static IDecompTypeData[] decompDatas;
	private static IDeltaBean deltaBean;
	private static IInstanceTypeData[] instanceTypeData;
	private static Map<String, String> context;
	private static ISpecializationData[] specializationData;
	private static byte[] attachmentData;
	private static IInstanceData[] smeWorkplaceInstances;
	private static String richXMLAsString;
	private static String[] configResultXMLAsStringArrays;
	private static IInstanceData[] instances;
	IConflictingAssumptionsContainer conflictingAssumption;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.getProperties().setProperty("vmc.runtimeInformationImpl", "util.TestRuntimeInformation");
		createConnection();
		setUp(new KBKeyImpl(PRODUCT, KB_NAME, KB_LOGSYS, KB_VERSION));
	}

	@AfterClass
	public static void tearDownAfterClass() throws IpcCommandException {
		configSession.deleteConfig(configId);
	}

	@Before
	public void setUpBeforeTestMethod() throws Exception {
	}

	@After
	public void tearDownAfterTestMethod() throws IpcCommandException {
	}

	private void createInitialConfiguration(final KBKey key) throws IpcCommandException {
		assertNotNull(configId);
	}

	/**
	 * Step 1: Create a handle to the EJB and create the EJB instance.
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#ejbCreate(java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String)}.
	 * @throws Exception
	 * @throws CreateException
	 * */
	@Test
	public final void test01EjbCreateStringStringStringBooleanString() {
		// fail("Not Required");
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#findKnowledgeBases(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)}.
	 * Step 2 Optional and only recommended if a different knowledge base than the one retrieved by the default find mechanism is to be used.
	 * The calling program can iterate over the returned list of knowledge bases and select one using its own strategy (e.g. naming convention).
	 * Please note that this is an expensive operation that searches through the database of the connected back-end.
	 * @throws IpcCommandException
	 * */
	@Test
	public final void test02FindKnowledgeBases() throws IpcCommandException {
		knowledgebaseData = configSession.findKnowledgeBases("MARA", MATERIAL_NAME, LOGICAL_SYSTEM, null, null, null, null, null, true);
		assertNotNull(knowledgebaseData);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getProfilesOfKB(java.lang.String, java.lang.String, java.lang.String)}.
	 * Optional and recommended if a knowledge base has multiple knowledge base profiles and a different one than the one returned from
	 * {@code findKnowledgeBases} is required for configuration creation.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test03GetProfilesOfKB() throws IpcCommandException {
		String kbLogSys = knowledgebaseData[0].getKbLogsys();
		String kbName = knowledgebaseData[0].getKbName();
		String kbVersion = knowledgebaseData[0].getKbVersion();
		IKbProfilesData[] profiles = configSession.getProfilesOfKB(kbLogSys, kbName, kbVersion);
		assertNotNull(profiles);
	}

	/**
	 *
	 * Step 3 Create the actual configuration in the config session bean and retrieve the configuration identifier.
	 * The configuration identifier will be used in all further calls that reference that configuration.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test04CreateConfigStringStringStringStringStringStringStringIntegerStringStringStringHashtableBoolean() throws IpcCommandException {
		createInitialConfiguration(new KBKeyImpl(PRODUCT, KB_NAME, KB_LOGSYS, KB_VERSION));
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#activateEngineTrace(java.lang.String, com.sap.custdev.projects.fbs.slc.cfg.client.ITraceSettings[])}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test05ActivateEngineTrace() throws IpcCommandException {

		// manually define all tracing settings
		// could also be retrieved with Trace.getSystemModules();
		String[] systemTraceModules = { "DDB", "PMS", "CSTR", "RULE", "CFG",
				"SCND", "PCND", "PROC", "FUNC", "TABL" };
		ITraceSettings[] traceSettings = new ITraceSettings[systemTraceModules.length];

		for (int i = 0; i < traceSettings.length; i++) {
			ITraceSettings currentTraceSetting = new TraceSettings();
			currentTraceSetting.setModule(systemTraceModules[i]);
			currentTraceSetting.setLevel(9);
			traceSettings[i] = currentTraceSetting;
		}
		configSession.activateEngineTrace(configId, traceSettings);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#setSessionContext(javax.ejb.SessionContext)}.
	 * Optional It is much better to set the context at the same time the configuration is created as otherwise the dependencies have to be evaluated
	 * again. The context values may have impact on the state of the configuration. 
	 * @throws IpcCommandException
	 */
	@Test
	public final void test06SetSessionContext() throws IpcCommandException {
		context = new Hashtable<String, String>();
		context.put("DOCUMENT_ID", "123456");
		context.put("DOCUMENT_SALES_ORG_R3", "1000");
		context.put("DOCUMENT_DISTRIBUTION_CHANNEL", "10");
		configSession.setContext(context, configId);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getContext()}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test07GetContext() throws IpcCommandException {
		Map<String, String> actualContext = configSession.getContext();
		assertEquals(context, actualContext);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getRootInstance(java.lang.String, boolean)}.
	 * Optional Retrieve the root of the hierarchy of a classical configuration. Please note that a solution configuration may also include
	 * non-part instances which are located at root level.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test08GetRootInstance() throws IpcCommandException {
		boolean mimeObj = true; // return binaries attached to the instance
		rootInstance = null;
		rootInstance = configSession.getRootInstance(configId, mimeObj);
		assertNotNull(rootInstance);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#setCsticsValues(java.lang.String, java.lang.String, boolean, com.sap.custdev.projects.fbs.slc.cfg.client.ICsticData[])}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test09SetCsticsValues() throws IpcCommandException {
		ICsticHeader header = new CsticHeader();
		header.setCsticName(SME_NO_WORKPLACES_NF);
		ICsticData[] valuesToSet = new ICsticData[1];
		valuesToSet[0] = new CsticData();
		valuesToSet[0].setCsticHeader(header);
		ICsticValueData[] datas = new ICsticValueData[1];
		datas[0] = new CsticValueData();
		String valueName = "4";
		datas[0].setValueName(valueName);
		valuesToSet[0].setCsticValues(datas);
		deltaBean = configSession.setCsticsValues(rootInstance.getInstId(), configId, false, valuesToSet);

		assertNotNull(deltaBean);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getValueJustifications(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test10GetValueJustifications() throws IpcCommandException {
		configSession.getValueJustifications(configId, rootInstance.getInstId(), SME_NO_WORKPLACES_NF, "4");
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getInstances(java.lang.String, boolean)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test11GetInstances() throws IpcCommandException {
		boolean mimeObj = true; // return binaries attached to the instance
		instances = configSession.getInstances(configId, mimeObj);
		assertNotNull(instances);
		assertEquals(5, instances.length); // 1 SME_OFFICE + 4 SME_WORKPLACE = 5 instances
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getDomainJustifications(java.lang.String, java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test12GetDomainJustifications() throws IpcCommandException {
		IJustificationData[] justificationData = configSession.getDomainJustifications(configId, instances[1].getInstId(), SME_WORKPLACE_TYPE_NS);
		assertNotNull(justificationData);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getCstic(java.lang.String, java.lang.String, boolean, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test13GetCstic() throws IpcCommandException {
		ICsticData cstic = configSession.getCstic(rootInstance.getInstId(), SME_NO_WORKPLACES_NF, false, configId);
		assertNotNull(cstic);
		assertEquals(SME_NO_WORKPLACES_NF, cstic.getCsticHeader().getCsticName());
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getDecompPositions(java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test14GetDecompPositions() throws IpcCommandException {
		decompDatas = configSession.getDecompPositions(configId, instances[1].getInstId());
		assertNotNull(decompDatas);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#createInstance(java.lang.String, java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test15CreateInstance() throws IpcCommandException {
		deltaBean = configSession.createInstance(instances[1].getInstId(), decompDatas[0].getPosition(), configId);
		assertNotNull(deltaBean);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getInstancesOfType(java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test17GetInstancesOfType() throws IpcCommandException {
		smeWorkplaceInstances = configSession.getInstancesOfType(configId, SME_WORKPLACE);
		assertNotNull("getInstancesOfType returns null!", smeWorkplaceInstances);
		assertEquals(4, smeWorkplaceInstances.length);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getInstanceTypes(java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test18GetInstanceTypesString() throws IpcCommandException {
		IInstanceTypeData[] instanceTypeData = configSession.getInstanceTypes(configId);
		assertEquals(33, instanceTypeData.length);
	}

	/**
	 * Test method for {@link com.sap
	 * .custdev.projects.fbs.slc.ejb.configSessionBean#getInstanceTypes(java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test19GetInstanceTypesStringString() throws IpcCommandException {
		instanceTypeData = configSession.getInstanceTypes(configId, SME_OFFICE);
		assertNotNull(instanceTypeData);
		assertEquals(2, instanceTypeData.length);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getInstancesOfClass(java.lang.String, java.lang.String, java.lang.Integer)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test20GetInstancesOfClass() throws IpcCommandException {
		IInstanceData[] instanceData = configSession.getInstancesOfClass(configId, instanceTypeData[1].getName(), instanceTypeData[1].getUid());
		assertNotNull("GetInstancesOfClass returns null!", instanceData);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getInstance(java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test21GetInstance() throws IpcCommandException {
		IInstanceData workplaceInstance = configSession.getInstance(configId, rootInstance.getInstId());
		assertNotNull(workplaceInstance);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getSpecializations(java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test22GetSpecializations() throws IpcCommandException {
		String fbsComputerInstance = deltaBean.getNewInstances().get(0).getInstId();
		specializationData = configSession.getSpecializations(configId, fbsComputerInstance);
		assertNotNull(specializationData);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#specialize(java.lang.String, java.lang.Integer, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test23Specialize() throws IpcCommandException {
		String fbsComputerInstance = deltaBean.getNewInstances().get(0).getInstId();
		Integer specId = specializationData[0].getSpecId();
		deltaBean = configSession.specialize(fbsComputerInstance, specId, configId);
		assertNotNull(deltaBean);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#unspecialize(java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test24Unspecialize() throws IpcCommandException {
		// String fbsDesktopInstance = deltaBean.getChangedInstances().get(0).getInstId();
		Iterator itr = deltaBean.getSplInstances().keySet().iterator();
		String smeComputerInstance = null;
		while (itr.hasNext()) {
			smeComputerInstance = (String) itr.next();
			deltaBean = configSession.unspecialize(smeComputerInstance, configId);
			assertNotNull(deltaBean);
		}
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#deleteInstance(java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test25DeleteInstance() throws IpcCommandException {
		IInstanceTypeData[] typeData = configSession.getInstanceTypes(configId, "SME_NP_INST_1");
		deltaBean = configSession.createNonPartInstance(configId, typeData[0].getInstTypeId());
		deltaBean = configSession.deleteInstance(deltaBean.getNewInstances().get(0).getInstId(), configId);
		assertNotNull(deltaBean);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getInstanceJustifications(java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test16GetInstanceJustifications() throws IpcCommandException {
		IJustificationData[] justificationData = configSession.getInstanceJustifications(configId, deltaBean.getNewInstances().get(0).getInstId());
		assertNotNull(justificationData);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getCsticValues(java.lang.String, java.lang.String, boolean, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test26GetCsticValues() throws IpcCommandException {
		configSession.getCsticValues(rootInstance.getInstId(), SME_NO_WORKPLACES_NF, false, configId);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#deleteCsticValues(java.lang.String, java.lang.String, com.sap.custdev.projects.fbs.slc.cfg.client.ICsticData[], java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test27DeleteCsticValues() throws IpcCommandException {
		ICsticData[] valuesToDelete = { new CsticData() };
		ICsticHeader headers = new CsticHeader();
		valuesToDelete[0].setCsticHeader(headers);
		String formatValues = "F"; // pass values in language independent format
		IDeltaBean deltaBean = configSession.deleteCsticValues(rootInstance.getInstId(), formatValues , valuesToDelete , configId);
		assertNotNull(deltaBean);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getCstics(java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test28GetCstics() throws IpcCommandException {
		ICsticData[] cstics = configSession.getCstics(configId, rootInstance.getInstId());
		assertNotNull(cstics);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getCsticsAndAssignedValues(java.lang.String, java.lang.String, boolean, boolean, boolean, java.util.Hashtable)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test29GetCsticsAndAssignedValues() throws IpcCommandException {
		ICsticData[] cstics = configSession.getCsticsAndAssignedValues(configId, rootInstance.getInstId(), false, false, true, null);
		assertNotNull(cstics);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getConfigItemInfo(java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test30GetConfigItemInfo() throws IpcCommandException {
		IConfigContainer configContainer = configSession.getConfigItemInfo(configId);
		assertNotNull(configContainer);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getConfigItemInfoXML(boolean)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test31GetConfigItemInfoXMLBoolean() throws IpcCommandException {
		configXML = configSession.getConfigItemInfoXML(false);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#recreateConfigFromXml(java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test32ecreateConfigFromXmlString() throws IpcCommandException {
		configSession.recreateConfigFromXml(configXML);
		String actualConfigXML = configSession.getConfigItemInfoXML(false);
		assertNotNull(actualConfigXML);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getConflicts(java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test33GetConflicts() throws IpcCommandException {
		String instanceId = null; // get for the whole configuration
		IConflictContainer[] conflicts = configSession.getConflicts(configId, instanceId);
		assertNull(conflicts); // configuration is not in conflicting state
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getConflictingAssumptions(java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test34GetConflictingAssumptions() throws IpcCommandException {
		IConflictingAssumptionsContainer[] conflictingAssumptions = configSession.getConflictingAssumptions(configId);
		if (conflictingAssumptions != null && conflictingAssumptions.length > 0) {
			conflictingAssumption = conflictingAssumptions[0];
			assertNotNull(conflictingAssumptions); // configuration is not in conflicting state
		}
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#deleteConflictingAssumption(java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test35DeleteConflictingAssumption() throws IpcCommandException {
		if (conflictingAssumption != null) {
			IAssumptions[] assumptions = conflictingAssumption.getAssumptions();
			if (assumptions != null && assumptions.length > 0) {
				String assumptionId = assumptions[0].getAsumptionId();
				// the model FBS_OFFICE is conflict free and as calling
				// deleteConflictingAssumptions
				// without a valid assumptionId causes exceptions, the call is
				// not
				// performed when this model is used.
				if (assumptionId != null) {
					deltaBean = configSession.deleteConflictingAssumption(configId, assumptionId);
					assertNotNull(deltaBean);
				}
			}
		}
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getNonPartInstanceTypes(java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test36GetNonPartInstanceTypes() throws IpcCommandException {
		IInstanceTypeData[] nonPartInstanceTypes = configSession.getNonPartInstanceTypes(configId);
		assertNotNull(nonPartInstanceTypes);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getCsticGroups(java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test37GetCsticGroups() throws IpcCommandException {
		ICsticsGroups[] csticGroups = configSession.getCsticGroups(configId, rootInstance.getInstId());
		assertNotNull(csticGroups);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#enableDeltaFilter(java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test38EnableDeltaFilter() throws IpcCommandException {
		configSession.enableDeltaFilter(configId);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getConfigHasChanged(java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test39GetConfigHasChanged() throws IpcCommandException {
		String key = null; // DEFAULT used
		configSession.getConfigHasChanged(key);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getRichConfigId()}.
	 */
	@Test
	public final void test41GetRichConfigId() {
		String richConfigId = configSession.getRichConfigId();
		assertNotNull(richConfigId);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getInstanceSubTypes(java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test43GetInstanceSubTypes() throws IpcCommandException {
		IInstanceTypeData[] instanceTypeData = configSession.getInstanceSubTypes(configId, rootInstance.getInstId());
		assertNotNull(instanceTypeData);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getInstanceTypeSubTypes(java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test44GetInstanceTypeSubTypes() throws IpcCommandException {
		IInstanceTypeData[] instanceTypeData = configSession.getInstanceTypeSubTypes(configId, rootInstance.getInstanceTypeId());
		assertNotNull(instanceTypeData);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getInstanceTypeSuperTypes(java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test45GetInstanceTypeSuperTypes() throws IpcCommandException {
		IInstanceTypeData[] instanceTypeData = configSession.getInstanceTypeSuperTypes(configId, rootInstance.getInstanceTypeId());
		assertNotNull(instanceTypeData);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getInstanceTypesAtRelation(java.lang.String, java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test46GetInstanceTypesAtRelation() throws IpcCommandException {
		String adtCsticName = "HAS_PART_SD_SOFT";
		instanceTypeData = configSession.getInstanceTypesAtRelation(configId, rootInstance.getInstId(), adtCsticName);
		assertNotNull(instanceTypeData);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#createInstanceAtRelation(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test47CreateInstanceAtRelation() throws IpcCommandException {
		String adtCsticName = "HAS_PART_SD_SOFT";
		IDeltaBean deltaBean = configSession.createInstanceAtRelation(configId, rootInstance.getInstId(), adtCsticName, instanceTypeData[0].getInstTypeId());
		assertNotNull(deltaBean);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getInstancesAtRelation(java.lang.String, java.lang.String, java.lang.String, boolean)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test48GetInstancesAtRelation() throws IpcCommandException {
		String adtCsticName = "HAS_PART_SD_SOFT";
		boolean assignableInsts = true;
		IInstanceData[] instanceData = configSession.getInstancesAtRelation(configId, rootInstance.getInstId(), adtCsticName, assignableInsts );
		assertNotNull(instanceData);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getNonPartInstances(java.lang.String, boolean)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test49GetNonPartInstances() throws IpcCommandException {
		boolean getMimeObjects = true;
		IInstanceData[] nonPartInstances = configSession.getNonPartInstances(configId, getMimeObjects);
		assertNotNull(nonPartInstances);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getPartInstances(java.lang.String, java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test50GetPartInstancesStringStringString() throws IpcCommandException {
		String instId = rootInstance.getInstId();
		String posNr = "0010";
		IInstanceData[] instanceData = configSession.getPartInstances(configId, instId, posNr);
		assertNotNull(instanceData);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getPartInstances(java.lang.String, boolean)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test51GetPartInstancesStringBoolean() throws IpcCommandException {
		boolean getMimeObjects = true;
		IInstanceData[] instanceData = configSession.getPartInstances(configId, getMimeObjects);
		assertNotNull(instanceData);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getParentInstance(java.lang.String, java.lang.String, boolean)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test52GetParentInstance() throws IpcCommandException {
		IInstanceData instance = configSession.getParentInstance(configId, instances[0].getInstId(), false);
		assertNotNull(instance);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getSpecializationJustifications(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void test53GetSpecializationJustifications() throws IpcCommandException {
		IJustificationData[] justData=configSession.getSpecializationJustifications(configId, instances[0].getInstId());
		assertNotNull(justData);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getTraceSystemModules(java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test54GetTraceSystemModules() throws IpcCommandException {
		String[] traceSystemModules = configSession.getTraceSystemModules(configId);
		assertNotNull(traceSystemModules);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getTraceOutputModules(java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test55GetTraceOutputModules() throws IpcCommandException {
		String[] traceOutputModules = configSession.getTraceOutputModules(configId);
		assertNotNull(traceOutputModules);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getTraceOutputLevel(java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test56GetTraceOutputLevel() throws IpcCommandException {
		Integer traceOutputLevel = configSession.getTraceOutputLevel(configId);
		assertNotNull(traceOutputLevel);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getTraceInputModules(java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test57GetTraceInputModules() throws IpcCommandException {
		String[] traceInputModules = configSession.getTraceInputModules(configId);
		assertNotNull(traceInputModules);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getTraceInputLevel(java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test58GetTraceInputLevel() throws IpcCommandException {
		Integer traceInputLevel = configSession.getTraceInputLevel(configId);
		assertNotNull(traceInputLevel);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#deleteTraceOutputModule(java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test59DeleteTraceOutputModule() throws IpcCommandException {
		String cKbrtTrcModuleName = "KBRT";
		configSession.deleteTraceOutputModule(configId, cKbrtTrcModuleName);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getEngineTraceLines(java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test60GetEngineTraceLines() throws IpcCommandException {
		String[] engineTraceLines = configSession.getEngineTraceLines(configId);
		assertNotNull(engineTraceLines);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#deactivateEngineTrace()}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test61DeactivateEngineTrace() throws IpcCommandException {
		configSession.deactivateEngineTrace();
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#deleteTrace(java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test62DeleteTrace() throws IpcCommandException {
		configSession.deleteTrace(configId);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#setTraceOutputLevel(java.lang.String, java.lang.Integer)}.
	 */
	@Test
	public final void test63SetTraceOutputLevel() {
		// fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#addTraceOutputModule(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void test64AddTraceOutputModule() {
		// fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#setTraceInputLevel(java.lang.String, java.lang.Integer)}.
	 */
	@Test
	public final void test65SetTraceInputLevel() {
		// fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#cancelConfiguration()}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test66CancelConfiguration() throws IpcCommandException {
		configSession.cancelConfiguration();
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#acceptConfiguration()}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test67AcceptConfiguration() throws IpcCommandException {
		configSession.acceptConfiguration();
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#pmsStartProfiling(java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test68PmsStartProfiling() throws IpcCommandException {
		configSession.pmsStartProfiling(configId, configId , "cstr");
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#pmsStopProfiling(java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test69PmsStopProfiling() throws IpcCommandException {
		configSession.pmsStopProfiling(configId, configId,"cstr");
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#pmsDiscardProfiling(java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test70PmsDiscardProfiling() throws IpcCommandException {
		configSession.pmsDiscardProfiling(configId, configId,"cstr");
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getConfigProfiling(java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test71GetConfigProfiling() throws IpcCommandException {
		List<IConfigProfiling> configProfiling = configSession.getConfigProfiling(configId, configId);
		assertNotNull(configProfiling);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getRichXMLAsString()}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test72GetRichXMLAsString() throws IpcCommandException {
		richXMLAsString = configSession.getRichXMLAsString();
		assertNotNull(richXMLAsString);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getRichConfigurationResultXML(boolean)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test73GetRichConfigurationResultXML() throws IpcCommandException {
		// fail(""); // TODO
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#uploadAttachment(java.lang.String, byte[])}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test75UploadAttachment() throws IpcCommandException {
		String docName = "ATTACHMENT";
		attachmentData = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		boolean success = configSession.uploadAttachment(docName, attachmentData);
		assertTrue(success);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#downloadAttachment(java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test76DownloadAttachment() throws IpcCommandException {
		String docName = "ATTACHMENT";
		byte[] attachmentDataAct = configSession.downloadAttachment(docName);
		assertNotNull(attachmentDataAct);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#removeAttachment(java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test77RemoveAttachment() throws IpcCommandException {
		String docName = "ATTACHMENT";
		boolean success = configSession.removeAttachment(docName);
		assertTrue(success);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getSalesStructure()}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test78GetSalesStructure() throws IpcCommandException {
		IConfigurationStructure<IInstanceData> salesStructure = configSession.getSalesStructure();
		assertNotNull(salesStructure);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getItemRelationshipByType(java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test79GetItemRelationshipByTypeString() throws IpcCommandException {
		ICsticHeader header = new CsticHeader();
		header.setCsticName(SME_NO_WORKPLACES_NF);
		ICsticData[] valuesToSet = new ICsticData[1];
		valuesToSet[0] = new CsticData();
		valuesToSet[0].setCsticHeader(header);
		ICsticValueData[] datas = new ICsticValueData[1];
		datas[0] = new CsticValueData();
		String valueName = "1";
		datas[0].setValueName(valueName);
		valuesToSet[0].setCsticValues(datas);
		configSession.setCsticsValues(rootInstance.getInstId(), configId, false, valuesToSet);

		String relationType = "SALES_SOFT";
		IItemRelationshipData<InstanceData> itemRelationShipData = configSession.getItemRelationshipByType(relationType);
		assertNotNull(itemRelationShipData);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getAllRelationshipTypes()}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test83GetAllRelationshipTypes() throws IpcCommandException {
		Collection<IItemRelationshipTypeInfo> relationShipTypes = configSession.getAllRelationshipTypes();
		assertNotNull(relationShipTypes);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#getConfigLoadMessages(java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test84GetConfigLoadMessages() throws IpcCommandException {
		IConfigLoadMessages[] loadMessages = configSession.getConfigLoadMessages(configId);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#resetConfig(java.lang.String, java.lang.String)}.
	 * @throws IpcCommandException
	 */

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#loadRichConfigurationResultXML(java.lang.String[])}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test85LoadRichConfigurationResultXMLStringArray() throws IpcCommandException {
		String loadRichConfigurationResultXML = configSession.loadRichConfigurationResultXML(configResultXMLAsStringArrays);
		assertNotNull(loadRichConfigurationResultXML);
	}

	/**
	 * Test method for {@link com.sap.custdev.projects.fbs.slc.cfg.ConfigSession#loadRichConfigurationResultXMLAsString(java.lang.String)}.
	 * @throws IpcCommandException
	 */
	@Test
	public final void test87LoadRichConfigurationResultXMLAsStringString() throws IpcCommandException {
		String loadRichConfigurationResultXML = configSession.loadRichConfigurationResultXMLAsString(richXMLAsString);
		assertNotNull(loadRichConfigurationResultXML);
	}

	@Test
	public final void test88ResetConfig() throws IpcCommandException {
		IDeltaBean deltaBean = configSession.resetConfig(configId, null);
		assertNotNull(deltaBean);
	}
}
