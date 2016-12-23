package com.pzybrick.test.iote2e.ruleproc.ksi;

import java.util.List;

import org.apache.avro.util.Utf8;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.pzybrick.iote2e.ruleproc.svc.RuleEvalResult;
import com.pzybrick.iote2e.schema.avro.Iote2eResult;
import com.pzybrick.iote2e.schema.util.Iote2eSchemaConstants;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestKsiHandlerHumidityToMister extends TestKsiHandlerBase {
	private static final Logger logger = LogManager.getLogger(TestKsiHandlerHumidityToMister.class);
	
	public TestKsiHandlerHumidityToMister() {
		super();
	}
	
	@Test
	public void testHumidityHumidityToMisterRuleFireFanOff() throws Exception {
		logger.info("begins");

		String testHumidityValue = "50";
		commonRun( testHumidityLoginName, testHumiditySourceName, testHumiditySourceType, testHumiditySensorName, testHumidityValue, testHumidityFilterKey);
		List<Iote2eResult> iote2eResults = commonThreadSubscribeGetIote2eResults( 2000, subscribeResults, iote2eResultReuseItem  );
		Assert.assertNotNull("iote2eResults is null", iote2eResults == null );
		Assert.assertEquals("iote2eResults must have size=1", 1, iote2eResults.size() );
		Assert.assertEquals("iote2eResults PAIRNAME_SENSOR_NAME", testHumiditySensorName, iote2eResults.get(0).getPairs().get(Iote2eSchemaConstants.PAIRNAME_SENSOR_NAME).toString());
		Assert.assertEquals("iote2eResults PAIRNAME_ACTUATOR_NAME", "mister1", iote2eResults.get(0).getPairs().get(Iote2eSchemaConstants.PAIRNAME_ACTUATOR_NAME).toString());
		Assert.assertEquals("iote2eResults PAIRNAME_ACTUATOR_VALUE", "on", iote2eResults.get(0).getPairs().get(Iote2eSchemaConstants.PAIRNAME_ACTUATOR_VALUE).toString());
	}
	
	@Test
	public void testHumidityHumidityToMisterRuleFireFanOn() throws Exception {
		logger.info("begins");
		String testHumidityValue = "100";
		commonRun( testHumidityLoginName, testHumiditySourceName, testHumiditySourceType, testHumiditySensorName, testHumidityValue, testHumidityFilterKey);
		List<Iote2eResult> iote2eResults = commonThreadSubscribeGetIote2eResults( 2000, subscribeResults, iote2eResultReuseItem  );
		Assert.assertNotNull("iote2eResults is null", iote2eResults == null );
		Assert.assertEquals("iote2eResults must have size=1", 1, iote2eResults.size() );
		Assert.assertEquals("iote2eResults PAIRNAME_SENSOR_NAME", testHumiditySensorName, iote2eResults.get(0).getPairs().get(Iote2eSchemaConstants.PAIRNAME_SENSOR_NAME).toString());
		Assert.assertEquals("iote2eResults PAIRNAME_ACTUATOR_NAME", "mister1", iote2eResults.get(0).getPairs().get(Iote2eSchemaConstants.PAIRNAME_ACTUATOR_NAME).toString());
		Assert.assertEquals("iote2eResults PAIRNAME_ACTUATOR_VALUE", "off", iote2eResults.get(0).getPairs().get(Iote2eSchemaConstants.PAIRNAME_ACTUATOR_VALUE).toString());
	}
	
	@Test
	public void testHumidityHumidityToMisterRuleNotFire() throws Exception {
		logger.info("begins");
		String testHumidityValue = "87";
		commonRun( testHumidityLoginName, testHumiditySourceName, testHumiditySourceType, testHumiditySensorName, testHumidityValue, testHumidityFilterKey);
		List<Iote2eResult> iote2eResults = commonThreadSubscribeGetIote2eResults( 2000, subscribeResults, iote2eResultReuseItem  );
		Assert.assertNotNull("iote2eResults is null", iote2eResults == null );
		Assert.assertEquals("iote2eResults must have size=0", 0, iote2eResults.size() );
	}
}
