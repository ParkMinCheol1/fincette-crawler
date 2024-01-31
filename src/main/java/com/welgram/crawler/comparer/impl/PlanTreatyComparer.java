package com.welgram.crawler.comparer.impl;

import static com.welgram.crawler.direct.fire.CrawlingSFI.getAssureMoneyStr;

import com.welgram.common.StringUtil;
import com.welgram.crawler.comparer.PlanCompareResult;
import com.welgram.crawler.comparer.TreatyDiffInfo;
import com.welgram.crawler.comparer.PlanComparer;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingProduct.Gender;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlanTreatyComparer implements PlanComparer {

    public final static Logger logger = LoggerFactory.getLogger(PlanTreatyComparer.class);

    private final CrawlingProduct info;

    public PlanTreatyComparer(CrawlingProduct info) {
        this.info = info;
    }

    @Override
    public boolean compare() {

        List<CrawlingTreaty> originalTreatyList = info.getTreatyList();
        List<CrawlingTreaty> currentTreatyList = info.getCurrentTreatyList();

        List<String> crawlingTreatNames = currentTreatyList.stream().map(t -> t.treatyName)
            .collect(Collectors.toList());

        int crawlingTreatNamesSize = crawlingTreatNames.size();
        int originalTreatyListSize = originalTreatyList.size();
        int hasCount = 0;
        for (int i = 0; i < currentTreatyList.size(); i++) {
            CrawlingTreaty crawlingTreaty = currentTreatyList.get(i);

            String treatyName = crawlingTreaty.treatyName;

            for (int j = 0; j < originalTreatyList.size(); j++) {
                CrawlingTreaty originalTreaty = originalTreatyList.get(j);

                if (treatyName.equals(originalTreaty.treatyName)) {
                    hasCount++;
                }
            }
        }

        return crawlingTreatNamesSize == originalTreatyListSize
            && crawlingTreatNamesSize == hasCount;
    }

    public boolean comparePlanComposition(){

        List<CrawlingTreaty> currentTreatyList = info.getCurrentTreatyList();

        // PlanCompareResult class에 넣어줄 값
        List<TreatyDiffInfo> treatiesToUpdateAssureMoney = new ArrayList<>();           // 가입금액을 변경해야 하는 특약
        List<TreatyDiffInfo> treatiesToAdd = new ArrayList<>();                         // 가입설계에 등록해야 하는 특약
        List<TreatyDiffInfo> treatiesToDelete = new ArrayList<>();                      // 가입설계에서 삭제해야하는 특약

        info.getTreatyList().forEach(t -> {
            TreatyDiffInfo treatyDiffInfo = new TreatyDiffInfo(
                info.planId,
                t.productMasterId,
                t.treatyName,
                t.assureMoney);
            treatiesToDelete.add(treatyDiffInfo);
        });

        // 특약명으로 sort
        treatiesToDelete.sort(Comparator.comparing(TreatyDiffInfo::getApiTreatyName));
        currentTreatyList.sort(Comparator.comparing(crawlingTreaty -> crawlingTreaty.treatyName));

        // 원수사 특약 구성 loop
        for (CrawlingTreaty newTreaty : currentTreatyList) {

            String newTreatyName = newTreaty.treatyName;
            int newAssureMoney = newTreaty.assureMoney;

            boolean hasMatchedTreaty = false;

            Iterator<TreatyDiffInfo> listToDeleteIt = treatiesToDelete.iterator();
            while (listToDeleteIt.hasNext()) {

                TreatyDiffInfo originalTreaty = listToDeleteIt.next();
                String originalTreatyName = originalTreaty.getApiTreatyName();
                int originalAssureMoney = originalTreaty.getApiTreatyAmount();
                String productMasterId = originalTreaty.getProductMasterId();

                if (treatiesToDelete.size() == 0) {
                    break;
                }

                // 특약 마스터 특약명이 원수사 보장명과 매칭되는 경우
                if (StringUtil.getTreatyNameForComparing(newTreatyName)
                    .equals(StringUtil.getTreatyNameForComparing(originalTreatyName))) {

                    // 특약 가입금액과 원수사 가입금액이 일치하지 않는 경우 listToUpdateAssureMoney에 add
                    if (originalAssureMoney != newAssureMoney) {
                        treatiesToUpdateAssureMoney.add(new TreatyDiffInfo(
                            info.planId,
                            productMasterId,
                            newTreatyName,
                            newAssureMoney,
                            originalTreatyName,
                            originalAssureMoney));
                    }

                    // 매칭되는 경우 listToDelete에서 remove
                    treatiesToDelete.remove(originalTreaty);
                    hasMatchedTreaty = true;
                    break;
                }

            }

            // 매치되는 info.TreatyList의 treaty가 없었을 경우
            if (!hasMatchedTreaty) {
                treatiesToAdd.add(
                    new TreatyDiffInfo(info.planId, newTreatyName, newAssureMoney));
            }

        } // 원수사 특약 구성 loop 끝

        // 담보구성 결과 setting
        PlanCompareResult planCompareResult = new PlanCompareResult(
            info.planId,
            info.age,
            Gender.values()[info.gender],
            treatiesToUpdateAssureMoney,
            treatiesToAdd,
            treatiesToDelete);
        info.setPlanCompareResult(planCompareResult);

        // 담보구성 비교결과 log 출력하기
        if (planCompareResult.hasDiff()) {
            logger.info(this.getInfo(planCompareResult));
        } else {
            logger.info("----------------------------------------------------------");
            logger.info(">>>> 원수사 vs API 특약 구성 비교");
            logger.info(">>>> 등록한 특약이 원수사 페이지에 모두 존재하고 금액도 일치합니다.");
            logger.info("----------------------------------------------------------");
        }

        return !planCompareResult.hasDiff();
    }


    public String getInfo(PlanCompareResult planCompareResult) {

        List<TreatyDiffInfo> treatiesToDelete = planCompareResult.getTreatiesToDelete();
        List<TreatyDiffInfo> treatiesToAdd = planCompareResult.getTreatiesToAdd();
        List<TreatyDiffInfo> treatiesToUpdateAssureMoney = planCompareResult.getTreatiesToUpdateAssureMoney();

        StringBuilder errorTxt = new StringBuilder();

        // 가입설계에서 삭제해야하는 특약
        if (treatiesToDelete.size() != 0) {
            errorTxt.append(" ::: 가설에서 삭제할 특약 ::: ")
                .append(treatiesToDelete.size()).append("개")
                .append(System.lineSeparator())
                .append(System.lineSeparator());

            for (TreatyDiffInfo treatyDiffInfo : treatiesToDelete) {
                errorTxt.append("productMasterId : ").append(treatyDiffInfo.getProductMasterId())
                    .append(" 특약명 : ").append(treatyDiffInfo.getApiTreatyName())
                    .append(System.lineSeparator());
            }
            errorTxt.append("------------------------------------------------")
                .append(System.lineSeparator());
        }

        // 가입금액 변경해야할 특약 - 원수사와 가입금액이 일치하지 않는 특약
        if (treatiesToUpdateAssureMoney.size() != 0) {
            errorTxt.append(" ::: 가입금액 변경해야할 특약 - 원수사와 가입금액이 일치하지 않는 특약 ::: ")
                .append(treatiesToUpdateAssureMoney.size()).append("개")
                .append(System.lineSeparator())
                .append(System.lineSeparator());

            for (TreatyDiffInfo a : treatiesToUpdateAssureMoney) {

                errorTxt.append("productMasterId : ").append(a.getProductMasterId())
                    .append(System.lineSeparator())
                    .append("페이지 이름 : ").append(a.getPageName()).append(System.lineSeparator())
                    .append("페이지 금액: ").append(getAssureMoneyStr(a.getPageAmount()))
                    .append(System.lineSeparator())
                    .append("상품마스터 이름 : ").append(a.getApiTreatyName())
                    .append(System.lineSeparator())
                    .append("상품마스터 금액: ").append(getAssureMoneyStr(a.getApiTreatyAmount()))
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());
            }
            errorTxt.append("------------------------------------------------")
                .append(System.lineSeparator());
        }

        // 가설에 등록해야할 특약 - 원수사페이지에만 존재하는 특약
        if (treatiesToAdd.size() != 0) {
            errorTxt.append(" ::: 가설에 등록해야할 특약 - 원수사페이지에만 특약명이 존재 ::: ")
                .append(treatiesToAdd.size()).append("개")
                .append(System.lineSeparator())
                .append(System.lineSeparator());

            for (TreatyDiffInfo d : treatiesToAdd) {
                errorTxt.append("보장명 : ").append(d.getPageName()).append(" : ")
                    .append(getAssureMoneyStr(d.getPageAmount()))
                    .append(System.lineSeparator());
            }
            errorTxt.append("------------------------------------------------")
                .append(System.lineSeparator());
        }

        return System.lineSeparator() +
            "--------- 가입설계 특약 구성 변경 발생 -------------" + System.lineSeparator() +
            "plan id :" + info.planId + System.lineSeparator() +
            "gender : " + (info.gender == 0 ? '남' : '여') + System.lineSeparator() +
            "age : " + info.getAge() + "세" + System.lineSeparator() +
            "플랜 : " + info.planSubName + System.lineSeparator() +
            "보험기간 : " + info.getInsTerm() + System.lineSeparator() +
            "납입기간 : " + info.getNapTerm() + System.lineSeparator() +
            "------------------------------------------------" + System.lineSeparator() +
            errorTxt;
    }
}
