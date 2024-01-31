package com.welgram.common;

import com.google.gson.JsonObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TravelUtil {

  /**
   * 기본 여행기간(7일) 조회
   * 출발예정일: 오늘 기준으로 + 1일
   * 도착예정일: 출발예정일 기준으로 + 7일
   */
  public static JsonObject getDefaultPeriod(String datePattern) {

    return getPeriod(7, datePattern);
  }

  /**
   * 기본 여행기간(period일) 조회
   * 출발예정일(startDate): 오늘 기준으로 + 1일
   * 출발예정시간(startTime): 00:00
   * 도착예정일(endDate): 출발예정일 기준으로 + period일
   * 도착예정시간(endTime): 24:00
   */
  public static JsonObject getPeriod(int period, String datePattern) {

    // 출발일시, 도착일시
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    DateFormat df = new SimpleDateFormat(datePattern);

    cal.add(Calendar.DATE, 1);
    String departure = df.format(cal.getTime());

    cal.add(Calendar.DATE, period);
    String arrival = df.format(cal.getTime());

    JsonObject obj = new JsonObject();
    obj.addProperty("startDate", departure);
    obj.addProperty("startTime", "00:00");
    obj.addProperty("endDate", arrival);
    obj.addProperty("endTime", "24:00");

    return obj;
  }
}
