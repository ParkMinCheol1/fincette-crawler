package com.welgram.crawler.scraper;

import com.welgram.common.except.crawler.crawl.ExpectedSavePremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAnnuityAgeException;
import com.welgram.common.except.crawler.setPlanInfo.SetAnnuityTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetProductTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRefundTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRenewTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetPrevalenceTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetDueDateException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetInjuryLevelException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.common.except.crawler.setUserInfo.SetTravelPeriodException;
import com.welgram.common.except.crawler.setUserInfo.SetUserNameException;
import com.welgram.common.except.crawler.setUserInfo.SetVehicleException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.List;
import java.util.Optional;


// todo | 가겹변수 오브젝트 사용시 다형성 문제
public interface ScrapableNew {

    /**
     * 생년월일 세팅
     *
     * @param obj
     * @throws SetBirthdayException
     */
    void setBirthday(Object... obj) throws SetBirthdayException;



    /**
     * 성별 세팅
     *
     * @param obj
     * @throws SetGenderException
     */
    void setGender(Object... obj) throws SetGenderException;



    /**
     * 상해급수 세팅
     *
     * @param obj
     * @throws SetInjuryLevelException
     */
    void setInjuryLevel(Object... obj) throws SetInjuryLevelException;



    /**
     * 직업 세팅
     *
     * @param obj
     * @throws
     */
    void setJob(Object... obj) throws SetJobException;



    /**
     * 보험기간 세팅
     *
     * @param obj
     * @throws SetInsTermException
     */
    void setInsTerm(Object... obj) throws SetInsTermException;



    /**
     * 납입기간 세팅
     *
     * @param obj
     * @throws SetNapTermException
     */
    void setNapTerm(Object... obj) throws SetNapTermException;



    /**
     * 납입주기 세팅
     *
     * @param obj
     * @throws SetNapCycleException
     */
    void setNapCycle(Object... obj) throws SetNapCycleException;



    /**
     * 갱신형 세팅
     *
     * @param obj
     * @throws SetRenewTypeException
     */
    void setRenewType(Object... obj) throws SetRenewTypeException;



    /**
     * 가입금액 세팅
     *
     * @param obj
     * @throws SetAssureMoneyException
     */
    void setAssureMoney(Object... obj) throws SetAssureMoneyException;



    /**
     * 환급형태 세팅
     *
     * @param obj
     * @throws SetRefundTypeException
     */
    void setRefundType(Object... obj) throws SetRefundTypeException;



    /**
     * 보험료 데이터 획득
     *
     * @param obj
     * @throws PremiumCrawlerException
     */
    void crawlPremium(Object... obj) throws PremiumCrawlerException;



    /**
     * 해약환급금 테이블 데이터 획득
     *
     * @param obj
     * @throws ReturnMoneyListCrawlerException
     */
    void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException;



    /**
     * 연금개시나이 설정
     *
     * @param obj
     * @throws SetAnnuityAgeException
     */
    void setAnnuityAge(Object... obj) throws SetAnnuityAgeException;



    /**
     * 연금수령형태(종신/확정) 설정
     *
     * @param obj
     * @throws SetAnnuityTypeException
     */
    void setAnnuityType(Object... obj) throws SetAnnuityTypeException;



    /**
     * 연금개시시점의 예상 적립금 크롤링
     *
     * @param obj
     * @throws ExpectedSavePremiumCrawlerException
     */
    void crawlExpectedSavePremium(Object... obj) throws ExpectedSavePremiumCrawlerException;



    /**
     * 고객명 설정
     *
     * @param obj
     * @throws SetUserNameException
     */
    void setUserName(Object... obj) throws SetUserNameException;



    /**
     * 출생예정일 설정
     *
     * @param obj
     * @throws SetDueDateException
     */
    void setDueDate(Object... obj) throws SetDueDateException;



    /**
     * 여행일 설정
     *
     * @param obj
     * @throws SetTravelPeriodException
     */
    void setTravelDate(Object... obj) throws SetTravelPeriodException;



    /**
     * 상품유형 설정
     *
     * @param obj
     * @throws SetProductTypeException
     */
    void setProductType(Object... obj) throws SetProductTypeException;



    /**
     * 심사유형 설정
     *
     * @param obj
     * @throws SetPrevalenceTypeException
     */
    void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException;



    /**
     * 운전차 용도 설정
     *
     * @param obj
     * @throws SetVehicleException
     */
    void setVehicle(Object... obj) throws SetVehicleException;



    // todo | 흡연정보 추가할 것
    // todo | 접근제한자 수정 필요 - 새로운 코드 추가되거나 .. 할 때에 패키지나 구현범위 내로 지정할 필요 있음
    /**
     * 만기환급금 데이터 획득
     *
     * @param obj
     * @throws ReturnPremiumCrawlerException
     */
//  void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException;
    default void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            Optional<PlanReturnMoney> returnMoneyOptional = getPlanReturnMoney(info);

            if (returnMoneyOptional.isPresent()) {
                info.returnPremium = returnMoneyOptional.get().getReturnMoney();

            } else {
                info.returnPremium = "-1"; // 만기에 해당하는 중도해약환급금이 없을 경우
            }

//      logger.info("만기환급금 크롤링 :: 만기환급금 :: {}", info.returnPremium);

        } catch (Exception e) {
            throw new ReturnPremiumCrawlerException(e);
        }
    }



    /**
     * 기수집한 중도해약환급금 목록에서 경과기간이 만기에 해당하는 환급금 데이터를 찾아 반환
     *
     * @param info
     * @return Optional<PlanReturnMoney>
     */
    static Optional<PlanReturnMoney> getPlanReturnMoney(CrawlingProduct info) {

        Optional<PlanReturnMoney> returnMoneyOptional = Optional.empty();
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        int planCalcAge = info.getCategoryName().equals("태아보험")
            ? 0
            : Integer.parseInt(info.age.replaceAll("\\D", ""));

        // 만기에 해당하는 환급금이 있는지 확인
        for (int i = planReturnMoneyList.size() - 1; i >= 0; i--) {

            PlanReturnMoney planReturnMoney = planReturnMoneyList.get(i);
            String term = planReturnMoney.getTerm();

            // 경과기간이 개월단위인 경우는 일단 제외
          if (term.contains("개월")) {
            continue;
          }

            if (term.equals("만기")) {
                returnMoneyOptional = Optional.of(planReturnMoney);
                break;
            }

            // 해약환급금 행에서 경과기간 추출 (년단위로 변환)
            int annualTerm = getAnnualTerm(term, planCalcAge);

            // 해당 가설(info)의 보험기간 추출 (년단위로 변환)
            int annualInsTerm = getAnnaulInsTerm(info, planCalcAge);

            // 경과기간이 만기에 해당하는지 여부 반환
            if (annualTerm == annualInsTerm) {

//        logger.info("만기환급금 크롤링 :: 카테고리 :: {}", info.categoryName);
//        logger.info("만기환급금 크롤링 :: 가설 케이스 나이 :: {}세", planCalcAge);
//        logger.info("만기환급금 크롤링 :: 가설 보험기간 :: {}", info.insTerm);
//        logger.info("만기환급금 크롤링 :: 가설 납입기간 :: {}", info.napTerm);
//        logger.info("만기환급금 크롤링 :: 해약환급금 해당 경과기간 :: {}", term);

                returnMoneyOptional = Optional.of(planReturnMoney);
            }
        }

        return returnMoneyOptional;
    }



    /**
     * 중도해약환급금 데이터에서 경과기간을 (planReturnMoney.term) 년단위로 변환해서 반환
     */
    static int getAnnualTerm(String term, int planCalcAge) {

        int annualTerm = -1;

        // 경과기관이 예를 들어 "70년 (100세)"와 같이 표기되는 경우 대응 가능 -> termUnit = "년"
        String termUnit;
        if (term.contains("년") && term.contains("세")) {
            termUnit = term.indexOf("년") < term.indexOf("세") ? "년" : "세";
        } else if (term.contains("년")) {
            termUnit = "년";
        } else if (term.contains("세")) {
            termUnit = "세";
        } else {
            throw new RuntimeException("처리할 수 없는 경과기간 단위: " + term);
        }

        int termUnitIndex = term.indexOf(termUnit);
        int termNumberValue = Integer.parseInt(
            term.substring(0, termUnitIndex).replaceAll("\\D", ""));

        switch (termUnit) {
            case "년":
                annualTerm = termNumberValue;
                break;
            case "세":
                annualTerm = termNumberValue - planCalcAge;
                break;
        }

        return annualTerm;
    }



    /**
     * 가설의 보험기간을 (info.insTerm) 년단위로 변환해서 반환
     */
    static int getAnnaulInsTerm(CrawlingProduct info, int planCalcAge) {

        int annaulInsTerm;
        String insTermUnit;
        int insTermNumberValue = -1;

        if (info.categoryName.contains("종신")) {
            String napTermUnit = info.napTerm.replaceAll("[0-9]", "");
            int napTerm = Integer.parseInt(info.napTerm.replaceAll("[^0-9]", ""));
            switch (napTermUnit) {
                case "년":
                    insTermNumberValue = napTerm + 10;
                    break;

                case "세":
                    insTermNumberValue = planCalcAge + napTerm;
            }
            insTermUnit = "년";

        } else if (info.categoryName.contains("연금")) { // 연금보험, 연금저축보험
            insTermUnit = "세"; // 환급금 크롤링 시점은 개시나이
            insTermNumberValue = Integer.parseInt(info.annuityAge.replaceAll("[^0-9]", ""));

        } else {
            insTermUnit = info.insTerm.replaceAll("[0-9]", "");
            insTermNumberValue = Integer.parseInt(info.insTerm.replaceAll("[^0-9]", ""));

        }

        switch (insTermUnit) {
            case "년":
                annaulInsTerm = insTermNumberValue;
                break;

            case "세":
                annaulInsTerm = insTermNumberValue - planCalcAge;
                break;

            default:
                annaulInsTerm = -1;
        }

        return annaulInsTerm;
    }



    // todo 정상 동작 확인할 것
    static void setVariableAssureMoneyTreaty(/*CrawlingProduct info, */CrawlingTreaty treaty) throws SetTreatyException {

        try {

/*          int targetTreatyMoney = treaty.assureMoney;
          int mapperId = Integer.parseInt(treaty.mapperId);
          String gender = (info.getGender() == Crawler.MALE) ? "남" : "여";
          int age = Integer.parseInt(info.getAge());

          PlanCalc planCalc = new PlanCalc(
              mapperId,
              age,
              gender,
              String.valueOf(targetTreatyMoney)
          );

          treaty.setPlanCalc(planCalc);*/

            treaty.getPlanCalc().setAssureMoney(
                String.valueOf(treaty.getAssureMoney())
            );

        } catch (Exception e) {
            throw new SetTreatyException(e);
        }
    }
}
