package com.welgram.crawler;

public class WordTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String s1 = "룰루루루 : 6세 보장개시";
		String s2 = "룰루루루 : 2세 보장개시";
		String s3 = "룰루루루 : 5세 보장개시";
		String s4 = "룰루루루(예약가입형) : 5세 보장개시";
		String s5 = "룰루루루 : 80세 보장개시";
		String s6 = "룰루루루 : 800세 보장개시";

		System.out.println(s1.replaceAll(": [0-9]*세 보장개시", ""));
		System.out.println(s2.replaceAll(": [0-9]*세 보장개시", ""));
		System.out.println(s3.replaceAll(": [0-9]*세 보장개시", ""));
		System.out.println(s4.replaceAll("\\(예약가입형\\) : [0-9]*세 보장개시", ""));
		System.out.println(s5.replaceAll(": [0-9]*세 보장개시", ""));
		System.out.println(s6.replaceAll(": [0-9]*세 보장개시", ""));
	}

}
