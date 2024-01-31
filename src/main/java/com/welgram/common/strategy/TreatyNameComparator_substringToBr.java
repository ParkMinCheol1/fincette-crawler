package com.welgram.common.strategy;

import java.util.function.Function;

public class TreatyNameComparator_substringToBr implements TreatyNameComparator {

    /**
     * @return <br> 태그 등 개행문자의 앞부분만 가져오기
     */
    @Override
    public Function<String, String> getHandler() {
        return treaty -> treaty.contains("\n") ?
            treaty.substring(0, treaty.indexOf("\n")) :
            treaty;
    }
}
