package com.pzybrick.iote2e.tests.ksidb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestKsiDbHandlerTempToFan extends TestKsiDbHandlerBase {
	private static final Logger logger = LogManager.getLogger(TestKsiDbHandlerTempToFan.class);
	private static final String pkActuatorState = testTempToFanLoginName + "|" + testTempToFanSourceName + "|" + testTempToFanSensorName;
	
	public TestKsiDbHandlerTempToFan() throws Exception {
		super();
	}
	
	@Test
	public void testTempToFanTempFanRuleFireFanOff() throws Exception {
		logger.info("begins");

		String testTempToFanValue = "50";
		commonRun( testTempToFanLoginName, testTempToFanSourceName, testTempToFanSourceType, testTempToFanSensorName, testTempToFanValue);
		//List<Iote2eResult> iote2eResults = commonThreadSubscribeGetIote2eResults( 2000, queueIote2eResults); 
		//Assert.assertNotNull("iote2eResults is null", iote2eResults == null );
		//Assert.assertEquals("iote2eResults must have size=1", 1, iote2eResults.size() );
		//Assert.assertEquals("iote2eResults PAIRNAME_SENSOR_NAME", testTempToFanSensorName, iote2eResults.get(0).getPairs().get(Iote2eSchemaConstants.PAIRNAME_SENSOR_NAME).toString());
		//Assert.assertEquals("iote2eResults PAIRNAME_ACTUATOR_NAME", "fan1", iote2eResults.get(0).getPairs().get(Iote2eSchemaConstants.PAIRNAME_ACTUATOR_NAME).toString());
		//Assert.assertEquals("iote2eResults PAIRNAME_ACTUATOR_VALUE", "off", iote2eResults.get(0).getPairs().get(Iote2eSchemaConstants.PAIRNAME_ACTUATOR_VALUE).toString());
		//Assert.assertEquals("Cassandra actuator_state value=off", "off", ActuatorStateDao.findActuatorValue(pkActuatorState));
	}
	
	@Test
	public void testTempToFanTempFanRuleFireFanOn() throws Exception {
		logger.info("begins");
		String testTempToFanValue = "100";
		commonRun( testTempToFanLoginName, testTempToFanSourceName, testTempToFanSourceType, testTempToFanSensorName, testTempToFanValue);
		//List<Iote2eResult> iote2eResults = commonThreadSubscribeGetIote2eResults( 2000, queueIote2eResults); 
		//Assert.assertNotNull("iote2eResults is null", iote2eResults == null );
		//Assert.assertEquals("iote2eResults must have size=1", 1, iote2eResults.size() );
		//Assert.assertEquals("iote2eResults PAIRNAME_SENSOR_NAME", testTempToFanSensorName, iote2eResults.get(0).getPairs().get(Iote2eSchemaConstants.PAIRNAME_SENSOR_NAME).toString());
		//Assert.assertEquals("iote2eResults PAIRNAME_ACTUATOR_NAME", "fan1", iote2eResults.get(0).getPairs().get(Iote2eSchemaConstants.PAIRNAME_ACTUATOR_NAME).toString());
		//Assert.assertEquals("iote2eResults PAIRNAME_ACTUATOR_VALUE", "on", iote2eResults.get(0).getPairs().get(Iote2eSchemaConstants.PAIRNAME_ACTUATOR_VALUE).toString());
		//Assert.assertEquals("Cassandra actuator_state value=on", "on", ActuatorStateDao.findActuatorValue(pkActuatorState));
	}
	
	@Test
	public void testTempToFanTempFanRuleNotFire() throws Exception {
		logger.info("begins");
		String testTempToFanValue = "78";
		commonRun( testTempToFanLoginName, testTempToFanSourceName, testTempToFanSourceType, testTempToFanSensorName, testTempToFanValue);
		//List<Iote2eResult> iote2eResults = commonThreadSubscribeGetIote2eResults( 2000, queueIote2eResults); 
		//Assert.assertNotNull("iote2eResults is null", iote2eResults == null );
		//Assert.assertEquals("iote2eResults must have size=0", 0, iote2eResults.size() );
		//Assert.assertEquals("Cassandra actuator_state value=null", null, ActuatorStateDao.findActuatorValue(pkActuatorState));
	}
}