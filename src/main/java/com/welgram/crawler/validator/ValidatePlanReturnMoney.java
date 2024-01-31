package com.welgram.crawler.validator;

import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.List;
import java.util.Optional;

public class ValidatePlanReturnMoney implements PostValidator {

    @Override
    public String getValidatorName() {
        return "중도해약환급금 수집 검증";
    }

    @Override
    public Optional<String> validateAndAddErrorMsg(CrawlingProduct info) {
        return this.validatePlanReturnMoneyList(info.getPlanReturnMoneyList());
    }

    public Optional<String> validatePlanReturnMoneyList(List<PlanReturnMoney> planReturnMoneyList) {

        StringBuilder errorMsgBuilder = new StringBuilder();

        for (PlanReturnMoney planReturnMoney : planReturnMoneyList) {

            String term = planReturnMoney.getTerm();
            String premiumSum = planReturnMoney.getPremiumSum();
            String returnMoney = planReturnMoney.getReturnMoney();
            String returnRate = planReturnMoney.getReturnRate();

            if (term == null || term.isEmpty()) {
                errorMsgBuilder.append("\n term (경과기간) 수집이 누락됐습니다.");
            }

            if (premiumSum == null || premiumSum.isEmpty()) {
                errorMsgBuilder.append("\n premiumSum (납입보험료) 수집이 누락됐습니다.");
            }

            if (returnMoney == null || returnMoney.isEmpty()) {
                errorMsgBuilder.append("\n returnMoney (해약환급금) 수집이 누락됐습니다.");
            }

            if (returnRate == null || returnRate.isEmpty()) {
                errorMsgBuilder.append("\n returnRate (해약환급률) 수집이 누락됐습니다.");
            }

            String errorMsg = errorMsgBuilder.toString();
            if (!errorMsg.isEmpty()) {
                return Optional.of(errorMsg);
            }
        }

        return Optional.empty();
    }
}
