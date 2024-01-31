package com.welgram.common;

import java.util.Calendar;
import java.util.Date;

public class InsuranceUtil {
    /**
     * 보험나이를 기준으로 생년월일을 생성한다.
     * (보험나이: 생일기준으로 6개월이 지났으면 +1살이다.)
     * @param age 보험나이
     * @return 생년월일
     */
    public static String getBirthday(int age) {
        String result = "" ;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -age+1);
        calendar.add(Calendar.MONTH, -7);

        Date date = calendar.getTime();

        result = DateUtil.formatString(date, "yyyy")+DateUtil.formatString(date, "MM")+DateUtil.formatString(date, "dd");

        return result;
    }
}
