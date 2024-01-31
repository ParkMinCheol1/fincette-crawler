package com.welgram.crawler.validator;

import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class PostValidationExecutor {

    CrawlingProduct info;

    public PostValidationExecutor(CrawlingProduct info) {
        this.info = info;
    }

    // 유효성 검사 class를 추가하면 됩니다.
    static final List<PostValidator> postValidators = new ArrayList<>();
    static {
        postValidators.add(new ValidatePlanReturnMoney()); // 중도해약환급금 수집 검사
    }

    // 유효성 검사 결과를 담을 Map
    // key: 유효성 검사 이름
    // value: 유효성 검사 결과
    private final Map<String, String> errorMap = new HashMap<>();

    public void execute() throws Exception {

        // 추가한 유효성 검사 class를 순회하면서 유효성 검사를 수행합니다.
        postValidators.forEach(
            postValidator ->

                // 유효성 검사 결과 오류가 존재하면 errorMap에 추가합니다.
                postValidator.validateAndAddErrorMsg(info).ifPresent(
                    errorMsg -> errorMap.put(postValidator.getValidatorName(), errorMsg))
        );

        // errorMap에 오류가 존재하면 CommonCrawlerException을 발생시킵니다.
        if (!errorMap.isEmpty()) {
            throw new Exception(errorMap.toString());
        }
    }

}
