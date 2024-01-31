package com.welgram.crawler.comparer;


import com.welgram.crawler.general.CrawlingProduct.Gender;
import java.util.List;

public class PlanCompareResult {

    private final String planId;

    private final String age;

    private final Gender gender;

    private final List<TreatyDiffInfo> treatiesToUpdateAssureMoney;           // 가입금액을 변경해야 하는 특약
    private final List<TreatyDiffInfo> treatiesToAdd;                         // 가입설계에 등록해야 하는 특약
    private final List<TreatyDiffInfo> treatiesToDelete;                      // 가입설계에서 삭제해야하는 특약

    public PlanCompareResult(
        String planId,
        String age,
        Gender gender,
        List<TreatyDiffInfo> treatiesToUpdateAssureMoney,
        List<TreatyDiffInfo> treatiesToAdd,
        List<TreatyDiffInfo> treatiesToDelete) {

        this.planId = planId;
        this.age = age;
        this.gender = gender;
        this.treatiesToUpdateAssureMoney = treatiesToUpdateAssureMoney;
        this.treatiesToAdd = treatiesToAdd;
        this.treatiesToDelete = treatiesToDelete;
    }

    // 진행되고 있는 스크래핑 케이스에 에러(가 있는지 판단하는 method
    public boolean hasDiff() {
        return treatiesToUpdateAssureMoney.size() != 0
            || treatiesToAdd.size() != 0
            || treatiesToDelete.size() != 0;
    }

    public List<TreatyDiffInfo> getTreatiesToUpdateAssureMoney() {
        return treatiesToUpdateAssureMoney;
    }

    public List<TreatyDiffInfo> getTreatiesToAdd() {
        return treatiesToAdd;
    }

    public List<TreatyDiffInfo> getTreatiesToDelete() {
        return treatiesToDelete;
    }
}
