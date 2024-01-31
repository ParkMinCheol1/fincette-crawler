package com.welgram.crawler.common.except;

import com.google.gson.Gson;
import com.welgram.crawler.comparer.PlanCompareResult;

// 원수사 가입설계의 특약구성 변동시 발생하는 오류
public class WrongPlanCompositionException extends RuntimeException{


    public WrongPlanCompositionException() {
        super();
    }
}
