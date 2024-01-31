package com.welgram.rtcm.util;

import java.io.Serializable;

/**
 * 생년월일 유틸리티 클래스
 */

public class Birthday implements Serializable {

	private static final long serialVersionUID = -2063074920679454596L;

	private String year;		//년

	private String month;		//월

	private String day;			//일

	@Override
	public String toString() {
		return year + month + day;
	}



	//yyyyMMdd 형식의 생년월일을 반환
	public String getFullBirthday() {
		return year + month + day;
	}



	public void setFullBirthday(String fullBirth) {
		this.year = fullBirth.substring(0, 4);
		this.month = fullBirth.substring(4, 6);
		this.day = fullBirth.substring(6);
	}



	//yyMMdd 형식의 생년월일을 반환
	public String getShortBirthday() {
		String fullBirth = year + month + day;

		return fullBirth.substring(2);
	}



	public Integer getAge() {
		return InsuranceUtil.getInsuredAge(this);
	}



	public String getYear() {
		return year;
	}



	public void setYear(String year) {
		this.year = year;
	}



	public String getMonth() {
		return month;
	}



	public void setMonth(String month) {
		this.month = month;
	}



	public String getDay() {
		return day;
	}



	public void setDay(String day) {
		this.day = day;
	}
}
