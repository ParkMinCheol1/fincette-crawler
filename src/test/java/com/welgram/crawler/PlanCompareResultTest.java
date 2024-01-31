//package com.welgram.crawler;
//
//import com.welgram.crawler.comparer.PlanCompareResult;
//import com.welgram.crawler.comparer.TreatyDiffInfo;
//
//import com.welgram.common.enums.Gender;
//import java.util.ArrayList;
//import java.util.List;
//
//public class PlanCompareResultTest {
//
//    public static void main(String[] args) {
//        CrawlingApi crawlingApi = new CrawlingApi();
//
//        List<TreatyDiffInfo> treatiesToUpdateAssureMoney = new ArrayList<>();           // 가입금액을 변경해야 하는 특약
//        List<TreatyDiffInfo> treatiesToAdd = new ArrayList<>();                         // 가입설계에 등록해야 하는 특약
//        List<TreatyDiffInfo> treatiesToDelete = new ArrayList<>();                      // 가입설계에서 삭제해야하는 특약
//
//        treatiesToUpdateAssureMoney.add(
//            new TreatyDiffInfo("000", "1", "페이지플랜명1", 100, "기존플랜명1", 200));
//        treatiesToUpdateAssureMoney.add(
//            new TreatyDiffInfo("000", "2", "페이지플랜명2", 100, "기존플랜명2", 150));
//
//        treatiesToDelete.add(new TreatyDiffInfo("000", "3", "플랜명3", 300));
//        treatiesToDelete.add(new TreatyDiffInfo("000", "4", "플랜명4", 400));
//
//        treatiesToAdd.add(new TreatyDiffInfo("000", "플랜명5", 500));
//        treatiesToAdd.add(new TreatyDiffInfo("000", "플랜명6", 600));
//        treatiesToAdd.add(new TreatyDiffInfo("000", "플랜명7", 700));
//
//        PlanCompareResult planCompareResult = new PlanCompareResult("111", "30", Gender.남성, treatiesToUpdateAssureMoney, treatiesToAdd, treatiesToDelete);
//
//        crawlingApi.sendPlanCompareResult(planCompareResult);
//
//        System.out.println("planCompareResult = " + planCompareResult);
//    }
//
//}
