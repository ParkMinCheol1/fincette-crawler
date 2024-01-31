package com.welgram.common.strategy;

import java.util.function.Function;

public interface TreatyNameComparator {

    // 특약명 변환함수
    Function<String, String> getHandler();

    default boolean equals(String treatyName1, String treatyName2) {
        Function<String, String> handler = getHandler();

        String appliedTreatyName1 = handler.apply(treatyName1);
        String appliedTreatyName2 = handler.apply(treatyName2);

        // 특약명 변환 후 비교
        return appliedTreatyName1.equals(appliedTreatyName2);
    }

}
