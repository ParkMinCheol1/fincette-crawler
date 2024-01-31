package com.welgram.common.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * 문서화(Documentation) 주석 : 상품명에서 종/형을 구분하여 해당 타입 리턴
 */
public class ProductTypeExtractor {

    enum Option {
        Default,    // 종만 리턴
        Optional;   // 종부터 나머지 상품명
    }



    public static final Logger logger = LoggerFactory.getLogger(ProductTypeExtractor.class);



    /**
     * 문서화(Documentation) 주석 : 상품명에서 상품타입(종)을 추출하는 메서드
     *
     * @param productName 상품명
     * @param opt         Default  : 종만 리턴  (ex) 1종
     *                    Optional : 종부터 나머지 상품명 리턴  (ex) 3종 일반형
     * @return resultList
     * @throws Exception
     */
    public static String getNumType(String productName, Option opt) throws Exception {

        final String regex = "\\d종|\\d{2}종";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(productName);

        String result = "";
        String type = "";   // 상품명에서 찾은 상품 타입

        logger.info("Full name :: " + productName);

        while (matcher.find()) {
            type = matcher.group(0);
            logger.info("match :: {}", type);

            if (opt.equals(Option.Optional)) {
                result = productName.substring(productName.indexOf(type));
            } else {
                result = type;
            }

            logger.info("result :: {}", result);
        }

        logger.info("====================================================================================================");

        return result;
    }



    /**
     * 문서화(Documentation) 주석 : 상품명에서 상품타입(형)을 추출하는 메서드
     *
     * @param productName
     * @return resultTextList
     * @throws Exception
     */
    public static List<String> getStringType(String productName) throws Exception {

        List<String> resultTextList = new ArrayList<>();

        resultTextList = splitText(productName);
        logger.info("================================================================================");

        return resultTextList;
    }



    /**
     * 문서화(Documentation) 주석 : '형'을 기준으로 상품명을 나누는 메서드
     *
     * @param productName
     * @return resultTextList
     * @throws Exception
     */
    public static List<String> splitText(String productName) throws Exception {

        List<String> resultTextList = new ArrayList<>();

        int tokenlength = 0;
        int cnt = 0;
        String prevToken = "";
        String token = "";
        StringTokenizer st = new StringTokenizer(productName, "형", true);
        tokenlength = st.countTokens();

        logger.info("Full name : {}", productName);

        while (st.hasMoreTokens()) {
            token = st.nextToken();
//            logger.info("token :: {}", token);

            // StringTokenizer의 구분자인 '형'과 이전 토큰을 붙여주기 위함
            // (ex) (일반형-기본형)은 '(일반' '형' '-기본' '형' ')' 으로 분리됨
            if (cnt % 2 != 0){
                if (token.equals("형")) {
                    token = prevToken + token;

                    resultTextList.add(refineText(token));
                } else {
                    resultTextList.add(refineText(prevToken));
                }
            } else {
                if (cnt != tokenlength - 1) {
                    prevToken = token;
                } else {
                    resultTextList.add(refineText(token));
                }
            }

            cnt++;
        }

        return resultTextList;
    }



    /**
     * 문서화(Documentation) 주석 : 자른 텍스트 후처리 메서드
     * 
     * @param text
     * @return text
     * @throws Exception
     */
    public static String refineText (String text) throws Exception {

        int difference = 0; // 텍스트에 포함된 구분자의 수
        String result = "";

        // numType제거
        result = text.replaceAll("\\d종|\\d{2}종", "").replaceAll("\\d+ ", "");

        // 표준체
        if (result.contains("표준체")) { result = result.replaceAll("[^표준체]", ""); }

        // xx보험 xx형
        if (result.contains("보험")) { result = result.substring(result.lastIndexOf("보험") + 2); }

        // 보험_m형
        if (result.contains("_")) { result = result.substring(result.lastIndexOf("_") + 1); }

        // 보험_m형
        if (result.contains("-")) { result = result.substring(result.lastIndexOf("-") + 1); }

        // 보험(n형
        if (result.contains("(")) { result = result.substring(result.lastIndexOf("(") + 1); }

        // ) n형
        if (result.contains(")")) { result = result.substring(result.lastIndexOf(")") + 1); }

        // :n형
        if (result.contains(":")) { result = result.substring(result.lastIndexOf(":") + 1); }

        // ,n형
        if (result.contains(",")) { result = result.substring(result.lastIndexOf(",") + 1); }

        // /해약환급금이없는유형
        if (result.contains("/")) { result = result.substring(result.lastIndexOf("/") + 1); }

        // [저해약환급금형
        if (result.contains("[")) { result = result.substring(result.lastIndexOf("[") + 1); }

        // ] 기본형
        if (result.contains("]")) { result = result.replaceAll("]", ""); }

        result = result.trim();
        difference = compareLength(result);

        if (difference < 2){
            logger.info("extaracted :: {}", result);
        } else {
            logger.info("failed :: {}", result);
        }

        return result;
    }



    /**
     * 문서화(Documentation) 주석 : 텍스트에서 구분자의 갯수를 리턴
     *
     * @param   text
     * @return  compareLength
     * @throws  Exception
     */
    public static int compareLength(String text) throws Exception {

        int compareLength = 0;
        compareLength = text.length() - text.replaceAll("형", "").length();

        return compareLength;
    }

}