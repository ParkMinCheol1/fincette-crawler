package com.welgram.util;

import java.io.Serializable;

/**
 * 생년월일 유틸리티 클래스
 */
public class Birthday implements Serializable {

	private static final long serialVersionUID = -2063074920679454596L;

	/** 생년 */
	private String year;

	/** 생월 */
	private String month;

	/** 생일 */
	private String day;

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

	@Override
	public String toString() {
		return year + "." + month + "." + day;
	}

}
