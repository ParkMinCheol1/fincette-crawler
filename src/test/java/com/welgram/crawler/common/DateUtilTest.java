package com.welgram.crawler.common;

import com.welgram.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class DateUtilTest {

	public final static Logger logger = LoggerFactory.getLogger(DateUtilTest.class);

    public static void main(String[] args) throws InterruptedException {
    	

    	String nowDate = DateUtil.formatString(new Date(), "yyyyMMdd");
    	String add1Day = DateUtil.formatString(DateUtil.addDay(new Date(), 1), "yyyyMMdd");
    	String add7Day = DateUtil.formatString(DateUtil.addDay(new Date(), 7), "yyyyMMdd");
    	
    	logger.debug("현재일 :: " + nowDate);
    	logger.debug("내일 :: " + add1Day);
    	logger.debug("일주일 :: " + add7Day);
    }
    
}