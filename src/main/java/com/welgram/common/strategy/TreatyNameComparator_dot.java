package com.welgram.common.strategy;

import java.util.function.Function;

public class TreatyNameComparator_dot implements TreatyNameComparator {
    @Override
    public Function<String, String> getHandler() {
        return (treatyName) -> treatyName.replaceAll("·", "·");
    }

}
