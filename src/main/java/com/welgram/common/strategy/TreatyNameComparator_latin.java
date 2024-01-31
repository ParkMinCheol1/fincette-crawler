package com.welgram.common.strategy;

import java.util.function.Function;

public class TreatyNameComparator_latin implements TreatyNameComparator { // 로마 숫자 변환 // 더 고민할 필요가 있음
    @Override
    public Function<String, String> getHandler() {

        // 알파벳 I를 유니코드로 변환
        return (treatyName) -> treatyName
                .replaceAll("III", "Ⅲ")
                .replaceAll("II", "Ⅱ")
                .replaceAll("I", "Ⅰ");
    }
}

