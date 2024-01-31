package com.welgram.common.strategy;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TreatyNameComparator_parentheses implements TreatyNameComparator { // 갱신정보, 보험기간 정보가 있는 괄호 삭제
    @Override
    public Function<String, String> getHandler() {
        return (treatyName) -> {
            /**
             *  특약명에서 괄호가 있고 그 안에 갱신주기에 관련된 내용이 있다면 해당 텍스트는 괄호까지 삭제하기
             *  ex: (갱신형_10년), (갱신 10년), (5년 갱신)
             */

            StringBuilder refinedName = new StringBuilder();
            String regex = "\\("
                + "("
                +   "(.*(\\d)+년.*갱신.*)|"
                +   "(.*갱신.*(\\d)+년.*)|"
                +   "(.*만기.*)|"
                +   "(.*최초.*)"
                + ")"
                + "\\)";

            int open;        // (
            int close = 0;   // )
            String substring = treatyName;

            while (substring.contains("(") && substring.contains(")")) {

                int nextOpen = substring.indexOf("(");
                if (nextOpen > 0) { // 다음 괄호전에도 텍스트가 있을 때
                    refinedName.append(substring, 0, nextOpen);
                    substring = substring.substring(nextOpen);
                }

                open = close + nextOpen;
                close = open + substring.indexOf(")") + 1;

                // parenthesis: 괄호
                String parenthesisStr = treatyName.substring(open, close);

                // regex는 갱신주기 관련 내용. 특약명에서 제외한다.
                if (!parenthesisStr.matches(regex)) {
                    refinedName.append(parenthesisStr);
                }

                // 닫는 괄호뒤로 substring 재설정
                substring = treatyName.substring(close);
            }

            return refinedName + substring;
        };
    }

}

