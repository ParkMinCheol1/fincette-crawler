package com.welgram.common;

import com.welgram.common.except.NotFoundMoneyPatternExcetion;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoneyUtil {

  public static final Logger logger = LoggerFactory.getLogger(MoneyUtil.class);

  public static Long toDigitMoney(String moneyStr) throws NullPointerException {

    isValid(moneyStr);

    moneyStr = moneyStr.trim();
    moneyStr = moneyStr.replaceAll(" ", "");

    if (moneyStr.contains("천원")) {
      moneyStr = moneyStr.replaceAll("천원", "000");
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

  public static Long toDigitMoney2(String moneyStr) throws NullPointerException {

    isValid(moneyStr);

    moneyStr = moneyStr.trim();
    moneyStr = moneyStr.replaceAll(" ", "");

    int unit = 1;
    if (moneyStr.contains("천원")) {
      unit = 1000;
      moneyStr = moneyStr.replaceAll("천원", "");
    }

    if (moneyStr.contains("억원")) {
      unit = 100000000;
      moneyStr = moneyStr.replaceAll("억원", "");
    }

    if (moneyStr.contains("천만원")) {
      unit = 10000000;
      moneyStr = moneyStr.replaceAll("천만원", "");
    }

    if (moneyStr.contains("백만원")) {
      unit = 1000000;
      moneyStr = moneyStr.replaceAll("백만원", "");
    }

    if (moneyStr.contains("십만원")) {
      unit = 100000;
      moneyStr = moneyStr.replaceAll("십만원", "");
    }

    if (moneyStr.contains("만원")) {
      unit = 10000;
      moneyStr = moneyStr.replaceAll("만원", "");
    }

    if(StringUtils.isEmpty(moneyStr)) {
      moneyStr = String.valueOf(unit);
    } else {
      moneyStr = moneyStr.replaceAll("[^.0-9]", "");
      moneyStr = String.valueOf((int)(Double.parseDouble(moneyStr) * unit));
    }

    return Long.valueOf(moneyStr.replaceAll("[^0-9]", ""));
  }


  private static void isValid(String moneyStr) throws NullPointerException {
    if ("".equals(moneyStr) || moneyStr == null) {
      throw new NullPointerException("empty or null..");
    }

  }

  /**
   * 단어 또는 문장으로부터 원단위 돈(text) 추출 예: 10만원(외래5,약제5) => 결과: 10만원 예: 10,000원(외래5,약제5) => 결과: 10,000원
   */
  public static String getMoneyFromWord(String str) throws NotFoundMoneyPatternExcetion {

    isValid(str);

    Pattern p = Pattern.compile("([0-9]|\\,)*(\\D)*원");
    Matcher m = p.matcher(str);
    String result = "";

    while (m.find()) {
      result = m.group(0);
    }

    if ("".equals(result)) {
      throw new NotFoundMoneyPatternExcetion();
    }

    return result;
  }

  /**
   * 단어 또는 문장으로부터 원단위 돈(Integer) 추출 예: 10만원(외래5,약제5) => 결과: 100000 예: 100,000원(외래5,약제5) => 결과:
   * 100000
   */
  public static Long getDigitMoneyFromWord(String str) throws NotFoundMoneyPatternExcetion {
    return toDigitMoney(getMoneyFromWord(str));
  }


  /**
   * 한글로 된 금액을 숫자로 변환
   * ex1) 1억 389만 5천원 => 103895000
   *
   * */
  public static Integer getDigitMoneyFromHangul(String moneyStr) {
    String[] unitTextArr = {"억", "천만", "백만", "십만", "만", "천", "백", "십", "원"};
    int[] unitArr = {100000000, 10000000, 1000000, 100000, 10000, 1000, 100, 10, 1};

    int sum = 0;
    int startIdx = 0;
    int endIdx = 0;

    for (int i = 0; i < unitTextArr.length; i++) {
      String unitText = unitTextArr[i];
      int unit = unitArr[i];
      int unitLength = unitText.length();

      endIdx = moneyStr.indexOf(unitText);
      if (endIdx > -1) {
        String unitNumStr = moneyStr.substring(startIdx, endIdx).replaceAll("[^0-9|.]", "");
        double unitNum = 0;

        if (endIdx == moneyStr.length() - 1) {
          unitNum = ("".equals(unitNumStr)) ? 0 : Double.parseDouble(unitNumStr);
        } else {
          unitNum = Double.parseDouble(unitNumStr);
        }

        sum += unitNum * unit;

        moneyStr = moneyStr.substring(endIdx + unitLength);
      }
    }

    if(sum == 0) {
      moneyStr = moneyStr.replaceAll("[^0-9]", "");
      sum = moneyStr.isEmpty() ? 0 : Integer.parseInt(moneyStr);
    }

    return sum;
  }
}
