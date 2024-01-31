package com.welgram.crawler.general;

import static org.junit.Assert.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.welgram.common.HttpClientUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlanReturnMoneyApiTest {

	public final static Logger logger = LoggerFactory.getLogger(PlanReturnMoneyApiTest.class);

	/**
	 * ProductMasterApiTest
	 */
	@Test
	public void testExecute() {
		
		boolean result = false ;
		
		try {
			String jsonData = "{\"productId\":\"SLI00029\",\"planId\":\"391\",\"insAge\":\"20\",\"gender\":\"F\",\"planReturnMoney\":[{\"planId\":391,\"insAge\":20,\"gender\":\"F\",\"sort\":0,\"term\":\"3개월\",\"premiumSum\":\"7,560 원\",\"returnMoney\":\"0 원\",\"returnRate\":\"0.0 %\",\"returnMoneyMin\":\"\",\"returnRateMin\":\"\",\"returnMoneyAvg\":\"\",\"returnRateAvg\":\"\",\"regTime\":\"\"},{\"planId\":391,\"insAge\":20,\"gender\":\"F\",\"sort\":0,\"term\":\"6개월\",\"premiumSum\":\"15,120 원\",\"returnMoney\":\"0 원\",\"returnRate\":\"0.0 %\",\"returnMoneyMin\":\"\",\"returnRateMin\":\"\",\"returnMoneyAvg\":\"\",\"returnRateAvg\":\"\",\"regTime\":\"\"},{\"planId\":391,\"insAge\":20,\"gender\":\"F\",\"sort\":0,\"term\":\"9개월\",\"premiumSum\":\"22,680 원\",\"returnMoney\":\"0 원\",\"returnRate\":\"0.0 %\",\"returnMoneyMin\":\"\",\"returnRateMin\":\"\",\"returnMoneyAvg\":\"\",\"returnRateAvg\":\"\",\"regTime\":\"\"},{\"planId\":391,\"insAge\":20,\"gender\":\"F\",\"sort\":0,\"term\":\"1년\",\"premiumSum\":\"30,240 원\",\"returnMoney\":\"0 원\",\"returnRate\":\"0.0 %\",\"returnMoneyMin\":\"\",\"returnRateMin\":\"\",\"returnMoneyAvg\":\"\",\"returnRateAvg\":\"\",\"regTime\":\"\"},{\"planId\":391,\"insAge\":20,\"gender\":\"F\",\"sort\":0,\"term\":\"2년\",\"premiumSum\":\"60,480 원\",\"returnMoney\":\"0 원\",\"returnRate\":\"0.0 %\",\"returnMoneyMin\":\"\",\"returnRateMin\":\"\",\"returnMoneyAvg\":\"\",\"returnRateAvg\":\"\",\"regTime\":\"\"},{\"planId\":391,\"insAge\":20,\"gender\":\"F\",\"sort\":0,\"term\":\"3년\",\"premiumSum\":\"90,720 원\",\"returnMoney\":\"0 원\",\"returnRate\":\"0.0 %\",\"returnMoneyMin\":\"\",\"returnRateMin\":\"\",\"returnMoneyAvg\":\"\",\"returnRateAvg\":\"\",\"regTime\":\"\"},{\"planId\":391,\"insAge\":20,\"gender\":\"F\",\"sort\":0,\"term\":\"4년\",\"premiumSum\":\"120,960 원\",\"returnMoney\":\"190 원\",\"returnRate\":\"0.1 %\",\"returnMoneyMin\":\"\",\"returnRateMin\":\"\",\"returnMoneyAvg\":\"\",\"returnRateAvg\":\"\",\"regTime\":\"\"},{\"planId\":391,\"insAge\":20,\"gender\":\"F\",\"sort\":0,\"term\":\"5년\",\"premiumSum\":\"151,200 원\",\"returnMoney\":\"370 원\",\"returnRate\":\"0.2 %\",\"returnMoneyMin\":\"\",\"returnRateMin\":\"\",\"returnMoneyAvg\":\"\",\"returnRateAvg\":\"\",\"regTime\":\"\"},{\"planId\":391,\"insAge\":20,\"gender\":\"F\",\"sort\":0,\"term\":\"6년\",\"premiumSum\":\"181,440 원\",\"returnMoney\":\"4,680 원\",\"returnRate\":\"2.5 %\",\"returnMoneyMin\":\"\",\"returnRateMin\":\"\",\"returnMoneyAvg\":\"\",\"returnRateAvg\":\"\",\"regTime\":\"\"},{\"planId\":391,\"insAge\":20,\"gender\":\"F\",\"sort\":0,\"term\":\"7년\",\"premiumSum\":\"211,680 원\",\"returnMoney\":\"12,960 원\",\"returnRate\":\"6.1 %\",\"returnMoneyMin\":\"\",\"returnRateMin\":\"\",\"returnMoneyAvg\":\"\",\"returnRateAvg\":\"\",\"regTime\":\"\"},{\"planId\":391,\"insAge\":20,\"gender\":\"F\",\"sort\":0,\"term\":\"8년\",\"premiumSum\":\"241,920 원\",\"returnMoney\":\"10,580 원\",\"returnRate\":\"4.3 %\",\"returnMoneyMin\":\"\",\"returnRateMin\":\"\",\"returnMoneyAvg\":\"\",\"returnRateAvg\":\"\",\"regTime\":\"\"},{\"planId\":391,\"insAge\":20,\"gender\":\"F\",\"sort\":0,\"term\":\"9년\",\"premiumSum\":\"272,160 원\",\"returnMoney\":\"6,160 원\",\"returnRate\":\"2.2 %\",\"returnMoneyMin\":\"\",\"returnRateMin\":\"\",\"returnMoneyAvg\":\"\",\"returnRateAvg\":\"\",\"regTime\":\"\"},{\"planId\":391,\"insAge\":20,\"gender\":\"F\",\"sort\":0,\"term\":\"10년\",\"premiumSum\":\"302,400 원\",\"returnMoney\":\"0 원\",\"returnRate\":\"0.0 %\",\"returnMoneyMin\":\"\",\"returnRateMin\":\"\",\"returnMoneyAvg\":\"\",\"returnRateAvg\":\"\",\"regTime\":\"\"}]}";
			
			logger.debug(jsonData);

			JsonObject data = JsonParser.parseString(jsonData).getAsJsonObject();
			
			String sendData = data.toString();
			logger.debug(sendData);
			
			String planId = data.get("planId").getAsString();

			JsonObject resultJson = HttpClientUtil.sendPUT("http://localhost:8080"+"/api/updatePlanReturnMoney/planId/"+planId, sendData);
			
			logger.debug("resultJson :: " + resultJson.toString());
			result = true;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		assertTrue(result);
	}
}
