package com.welgram.common;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 날짜 관련 유틸리티 클래스
 * @author www.inpion.com
 */
public class DateUtil {



	/**
	 * 날짜를 format 형식의 문자열로 변환한다.
	 * @param date 날짜
	 * @param format 형식
	 * @return 변환된 문자열
	 */
	public static String formatString(Date date, String format) {
		String result = "";
		
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		result = formatter.format(date);
		
		return result;
	}



	/**
	 * 문자열 형식의 날짜를 Date로 변환한다.
	 * @param date 문자열날짜
	 * @param format 형식
	 * @return 날짜
	 */
	public static Date parseDate(String date, String format) {
		Date result = null;
		
		SimpleDateFormat formatter = null;
		
		try {
			formatter = new SimpleDateFormat(format);
			result = formatter.parse(date);
		} catch (ParseException e) {
		}
		
		return result;
	}



	/**
	 * 날짜에 일수를 더한다.
	 * @param date 날짜
	 * @param amount 더할 일수
	 * @return 날짜
	 */
	public static Date addDay(Date date, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		calendar.add(Calendar.DAY_OF_YEAR, amount);
		
		return calendar.getTime();
	}



	/**
	 * 날짜에 개월을 더한다.
	 * @param date 날짜
	 * @param amount 더할 개월수
	 * @return 날짜
	 */
	public static Date addMonth(Date date, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		calendar.add(Calendar.MONTH, amount);
		
		return calendar.getTime();
	}



	/**
	 * 날짜에 년수를 더한다.
	 * @param date 날짜
	 * @param amount 더할 년수
	 * @return 날짜
	 */
	public static Date addYear(Date date, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		calendar.add(Calendar.YEAR, amount);
		
		return calendar.getTime();
	}



	/**
	 * 두 날짜간 차이를 구함
	 * @param begin 시작일자
	 * @param end 종료일자
	 * @return 날짜
	 * @throws Exception 
	 */
	public static long diffOfDate(String begin, String end) throws Exception {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
	 
	    Date beginDate = formatter.parse(begin);
	    Date endDate = formatter.parse(end);
	 
	    long diff = endDate.getTime() - beginDate.getTime();
	    long diffDays = diff / (24 * 60 * 60 * 1000);
	 
	    return diffDays;
	}



	public static String getBabyBirth(String format){
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(new Date());
	    cal.add(Calendar.MONTH, 3);

	    SimpleDateFormat df = new SimpleDateFormat(format);
	    String strDate = df.format(cal.getTime());
	    
	    return strDate;
	}



	public static String getBabyBirth(String format, int month){
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(new Date());
	    cal.add(Calendar.MONTH, month);

	    SimpleDateFormat df = new SimpleDateFormat(format);
	    String strDate = df.format(cal.getTime());
	    
	    return strDate;
	}



	// 2022.06.30 | 최우진 | *_OST_* (해외여행보험)에서 사용하는 출발일과 도착일입니다

	// 오늘기준 출발일
	// return : String
	public static String dateAfter7DaysFromToday() {
		Date today = new Date();
		Date departureDay = DateUtil.addDay(today, 7);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		return sdf.format(departureDay);
	}



	// 오늘기준 도착일
	// return : String
	public static String dateAfter13DaysFromToday() {
		Date today = new Date();
		Date arrivalDay = DateUtil.addDay(today,  13);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		return sdf.format(arrivalDay);
	}



	// 파라미터로 부터 7일뒤
	// param : Date
	// return : String
	public static String dateAfter7Days(Date date) {
		date = DateUtil.addDay(date, 7);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		return sdf.format(date);
	}



	// 파라미터로부터 13일뒤
	// param : Date
	// return : String
	public static String dateAfter13Days(Date date) {
		date = DateUtil.addDay(date, 13);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		return sdf.format(date);
	}

}
