package com.welgram.crawler.common;

import static org.junit.Assert.assertTrue;

import com.welgram.common.MoneyUtil;
import com.welgram.common.except.NotFoundMoneyPatternExcetion;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoneyUtilTest {

    public final static Logger logger = LoggerFactory.getLogger(MoneyUtilTest.class);

    @Test
    public void testToNumber() {
        String str = "1,000원";
        Long result = MoneyUtil.toDigitMoney(str);

        logger.debug(str + " => " + result);
        assertTrue(result == 1000);
    }

    @Test
    public void testToNumber1() {
        String str = "1천원";
        Long result = MoneyUtil.toDigitMoney(str);

        logger.debug(str + " => " + result);
        assertTrue(result == 1000);
    }

    @Test
    public void testToNumber2() {
        String str = "1만원";
        Long result = MoneyUtil.toDigitMoney(str);

        logger.debug(str + " => " + result);
        assertTrue(result == 10000);
    }

    @Test
    public void testToNumber3() {
        String str = "1백만원";
        Long result = MoneyUtil.toDigitMoney(str);

        logger.debug(str + " => " + result);
        assertTrue(result == 1000000);
    }

    @Test
    public void testToNumber4() {
        String str = "1천만원";
        Long result = MoneyUtil.toDigitMoney(str);

        logger.debug(str + " => " + result);
        assertTrue(result == 10000000);
    }

    @Test
    public void testToNumber5() {
        String str = "1억원";
        Long result = MoneyUtil.toDigitMoney(str);

        logger.debug(str + " => " + result);
        assertTrue(result == 100000000);
    }

    @Test
    public void testToNumber6() {
        String str = "10억원";
        Long result = MoneyUtil.toDigitMoney(str);

        logger.debug(str + " => " + result);
        assertTrue(result == 1000000000);
    }

    @Test
    public void testToNumber7() {
        String str = "1000원";
        Long result = MoneyUtil.toDigitMoney(str);

        logger.debug(str + " => " + result);
        assertTrue(result == 1000);
    }

    @Test
    public void testToNumber8() {
        String str = "5,000원";
        Long result = MoneyUtil.toDigitMoney(str);

        logger.debug(str + " => " + result);
        assertTrue(result == 5000);
    }

    @Test
    public void testToNumber9() {
        String expected = "10,000원";
        Long result = MoneyUtil.toDigitMoney(expected);

        logger.debug(expected + " => " + result);
        assertTrue(result == 10000);
    }


    @Test
    public void testGetMoneyFromWord1() throws NotFoundMoneyPatternExcetion {
        String expected = "10000원";
        String str = "이십123만 " + expected + " 20000(123ㄱ)";
        String result = MoneyUtil.getMoneyFromWord(str);
        logger.debug(str + " => " + result);

        assertTrue(expected.equals(result));
    }

    @Test
    public void testGetMoneyFromWord2() throws NotFoundMoneyPatternExcetion {
        String str1 = "10만원";
        String str = "10만원(외래5,약제5)";
        String result = MoneyUtil.getMoneyFromWord(str);
        logger.debug(str + " => " + result);

        assertTrue(str1.equals(result));
    }

    @Test
    public void testGetMoneyFromWord3() throws NotFoundMoneyPatternExcetion {
        String expected = "100,000원";
        String str = expected + "(외래5,약제5)";
        String result = MoneyUtil.getMoneyFromWord(str);
        logger.debug(str + " => " + result);

        assertTrue(expected.equals(result));
    }

    @Test
    public void testGetDigitMoneyFromWord() throws NotFoundMoneyPatternExcetion {
        long expected = 100000;
        String str = "100,000원(외래5,약제5)";
        Long result = MoneyUtil.getDigitMoneyFromWord(str);
        logger.debug(str + " => " + result);

        assertTrue(expected == result);
    }

    @Test
    public void test() {
        int assureMoney = 290000000;
        float result = (float) assureMoney / 100000000;
        logger.debug("result: " + result);

        assertTrue(result == 2.9);
    }

    @Test
    public void test2() {
        int assureMoney = 300000000;
        Double result = (double) assureMoney / 100000000;
        logger.debug("result: " + result.floatValue());

        assertTrue(result == 3);
    }

    @Test
    public void testGetDigitMoneyFromHangul() {
        String[] strArr = {
            "1억 389만 5원",
            "15억 3852만 3천 5원",
            "15억 3백만 2500원",
            "1억 380만 2백3원",
            "1억5천만원",
            "1억5백만2십만원",
            "1억2십만2500원",
            "1억5만원",
            "1억2십만25원",
            "5천만원",
            "5천500만원",
            "5030만원",
            "5005원",
            "1,000만원",
            "1억원",
            "3000만원",
            "79000원",
            "7만5천원",
            "7천5백원",
            "24530000",
            "0"
        };

        for (String str : strArr) {
            System.out.println(str + " => " + MoneyUtil.getDigitMoneyFromHangul(str));
        }
    }

}



