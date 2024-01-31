package com.welgram.common.strategy;

import java.util.function.Function;

public class TreatyNameComparator_space implements TreatyNameComparator {
    @Override
    public Function<String, String> getHandler() {
        return (treatyName) -> treatyName.trim()
            .replaceAll("\\s", "")
            .replaceAll(" ", "");
    }
}

