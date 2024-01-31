package com.welgram.rtcm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoneyUtil {

    public static final Logger logger = LoggerFactory.getLogger(MoneyUtil.class);


    /**
     * TODO 소수점 단위도 변환되도록
     * TODO "만원" 같이 숫자없는 단위가 들어와도 변환되도록
     */
    public static Long toDigitMoney(String moneyStr) throws NullPointerException {

        isValid(moneyStr);

        moneyStr = moneyStr.trim();
        moneyStr = moneyStr.replaceAll(" ", "");

        if (moneyStr.contains("천원")) {
//      moneyStr = moneyStr.replaceAll("천원", "000");
            moneyStr = moneyStr.replaceAll("천원", "");
        }

        if (moneyStr.contains("억원")) {
            moneyStr = moneyStr.replaceAll("억원", "00000000");
        }

        if (moneyStr.contains("천만원")) {
            moneyStr = moneyStr.replaceAll("천만원", "0000000");
        }

        if (moneyStr.contains("백만원")) {
            moneyStr = moneyStr.replaceAll("백만원", "000000");
        }

        if (moneyStr.contains("십만원")) {
            moneyStr = moneyStr.replaceAll("십만원", "00000");
        }

        if (moneyStr.contains("만원")) {
            moneyStr = moneyStr.replaceAll("만원", "0000");
        }

        return Long.valueOf(moneyStr.replaceAll("[^0-9]", ""));
    }


    private static void isValid(String moneyStr) throws NullPointerException {

        if ("".equals(moneyStr) || moneyStr == null) {
            throw new NullPointerException("empty or null..");
        }
    }

}
