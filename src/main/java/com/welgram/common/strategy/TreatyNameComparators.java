package com.welgram.common.strategy;

import java.util.function.Function;

public enum TreatyNameComparators implements TreatyNameComparator {

    removeSpace(new TreatyNameComparator_space().getHandler()),

    removeParenthesesInfo(new TreatyNameComparator_parentheses().getHandler()),

    replaceLatin(new TreatyNameComparator_latin().getHandler()),

    replaceDot(new TreatyNameComparator_dot().getHandler()),

    allApplied(removeSpace.handler
            .andThen(removeParenthesesInfo.handler)
            .andThen(replaceLatin.handler)
            .andThen(replaceDot.handler));

    private final Function<String, String> handler;



    TreatyNameComparators(Function<String, String> handler) {
        this.handler = handler;
    }

    @Override
    public Function<String, String> getHandler() {
        return handler;
    }
}
