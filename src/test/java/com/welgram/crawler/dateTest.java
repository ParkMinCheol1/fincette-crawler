package com.welgram.crawler;

import com.welgram.util.Birthday;
import com.welgram.util.InsuranceUtil;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.welgram.crawler.general.CrawlerSlackClientTest;
import com.welgram.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class dateTest {

    public final static Logger logger = LoggerFactory.getLogger(dateTest.class);

    public static void main(String[] args) {
        // TODO Auto-generated method stub


        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        // 출발일시
        cal.add(Calendar.DATE, 1);
        String departure = df.format(cal.getTime());
        // 도착일시
        cal.add(Calendar.DATE, 7);
        String arrival = df.format(cal.getTime());

        logger.debug("departure :: " + departure);
        logger.debug("arrival :: " + arrival);

        logger.debug("dd :: " + DateUtil.formatString(DateUtil.addDay(new Date(), 1), "yyyyMMdd"));
        logger.debug("dd :: " + DateUtil.formatString(DateUtil.addDay(new Date(), 7), "yyyyMMdd"));

        Birthday _birthDay = InsuranceUtil.getBirthday(30);

        logger.info(""+_birthDay);
    }

}
