package com.welgram.rtcm.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class InsuranceUtil {

	/**
	 * 보험나이를 기준으로 생년월일을 생성한다. (보험나이: 생일기준으로 6개월이 지났으면 +1살이다.)
	 * @param age 보험나이
	 * @return 생년월일
	 */
	public static Birthday getBirthday(int age) {
		Birthday result = new Birthday();

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -age);
		calendar.add(Calendar.MONTH, -6 + 1);
		// 안전하게 나이기준으로 생년월일을 계산하기 위해 상령일에 +1을 한다

		Date date = calendar.getTime();
		result.setYear(DateUtil.formatString(date, "yyyy"));
		result.setMonth(DateUtil.formatString(date, "MM"));
		result.setDay(DateUtil.formatString(date, "dd"));

		return result;
	}



	/**
	 * 현재 날짜를 기준으로 생년월일의 보험나이를 계산한다.
	 * @param birthday 생년월일
	 * @return
	 */
	public static int getInsuredAge(Birthday birthday) {
		int result = 0;

		// 현재시간을 얻는다.
		Calendar currentCalendar = Calendar.getInstance();
		currentCalendar.set(Calendar.HOUR_OF_DAY, 12);
		currentCalendar.set(Calendar.MINUTE, 0);
		currentCalendar.set(Calendar.SECOND, 0);
		currentCalendar.set(Calendar.MILLISECOND, 0);

		// 생년월일 시간을 얻는다.
		Calendar birthdayCalendar = Calendar.getInstance();
		birthdayCalendar.set(Calendar.YEAR, Integer.parseInt(birthday.getYear()));
		birthdayCalendar.set(Calendar.MONTH, Integer.parseInt(birthday.getMonth()) - 1);
		birthdayCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(birthday.getDay()));
		birthdayCalendar.set(Calendar.HOUR_OF_DAY, 12);
		birthdayCalendar.set(Calendar.MINUTE, 0);
		birthdayCalendar.set(Calendar.SECOND, 0);
		birthdayCalendar.set(Calendar.MILLISECOND, 0);

		// 두 시간의 차이에 대한 개월수를 얻는다.
		int elapsedYears = currentCalendar.get(Calendar.YEAR) - birthdayCalendar.get(Calendar.YEAR);
		int elapsedMonths = currentCalendar.get(Calendar.MONTH) - birthdayCalendar.get(Calendar.MONTH);
		int elapsedDays = currentCalendar.get(Calendar.DAY_OF_MONTH) - birthdayCalendar.get(Calendar.DAY_OF_MONTH);

		if (elapsedYears == 0) {
			if (elapsedMonths >= 0) {
				result = elapsedYears * 12 + elapsedMonths + (elapsedDays < 0 ? -1 : 0);
			} else {
				result = elapsedYears * 12 + elapsedMonths + (elapsedDays > 0 ? 1 : 0);
			}
		} else if (elapsedYears > 0) {
			result = elapsedYears * 12 + elapsedMonths + (elapsedDays < 0 ? -1 : 0);
		} else {
			result = elapsedYears * 12 + elapsedMonths + (elapsedDays > 0 ? 1 : 0);
		}

		// 보험나이(년수 + 6개월이 지났으면 +1)를 계산한다.
		return result / 12 + (result % 12 >= 6 ? 1 : 0);
	}



	/**
	 * 임신 주수를 계산한다.
	 * @param value 출산예정일
	 * @return result 현재 임신 주수
	 */
	public static String getWeeksPregnant(String value) {

		int DAYSPREGNANT = 280; // 임신 평균 기간
		String result = "";
		Date now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

		try {
			long diff = differenceDate(formatter.format(now), value);
			diff = DAYSPREGNANT - diff;

			result = Integer.toString((int) (diff / 7));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return result;
	}



	/**
	 * 예상 출산예정일을 반환한다.
	 * @param pregnancyWeek 현재 임신 주수
	 * @return dateFormat.format(calendar.getTime()) 출산예정일
	 */
	public static String getDateOfBirth(int pregnancyWeek) {
		int DAYSPREGNANT = 280;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, (DAYSPREGNANT - 7 * pregnancyWeek));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

		return dateFormat.format(calendar.getTime());
	}



	/**
	 * 현재로부터 출산예정일까지의 남은 일수를 구한다.
	 * @param begin 현재날짜
	 * @param end 출산예정일
	 * @return diffDays 남은 일수
	 * @throws ParseException
	 */
	public static long differenceDate(String begin, String end) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

		Date beginDate = formatter.parse(begin);
		Date endDate = formatter.parse(end);

		long diff = endDate.getTime() - beginDate.getTime();
		long diffDays = diff / (24 * 60 * 60 * 1000);

		return diffDays;
	}
}
