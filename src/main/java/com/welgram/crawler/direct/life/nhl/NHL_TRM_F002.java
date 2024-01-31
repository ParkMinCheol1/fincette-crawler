package com.welgram.crawler.direct.life.nhl;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 *  행복한NH경영인정기보험(무배당)_2205 2형(간편가입) 4종(20%체증)
 */

public class NHL_TRM_F002 extends CrawlingNHLAnnounce {



    public static void main(String[] args) {
        executeCommand(new NHL_TRM_F002(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        String genderOpt = (info.getGender() == MALE) ? "sex_m" : "sex_fm";
        String genderText = (info.getGender() == MALE) ? "남" : "여";

        logger.info("NHL_TRM_F002 :: {}", info.getProductName());
        logger.info("홈페이지의 오류가 심해 info.textType를 기준으로 진행");
        logger.info("특히 4종의 경우 다양한 에러 발생");
        WaitUtil.waitFor(2);


        if (info.getTextType() == "" || info.getTextType() == null)
            throw new Exception("textType이 공백입니다.");

        if (info.getTextType().contains("(20%체증) 2형")) {
            logger.info("4종((20%체증) 2형(간편가입)은 특정 가입금액이 나오지않는 홈페이지 에러가 발생하여 임시 나이 입력");

            logger.info("임시 생년월일 :: {}", info.getFullBirth());
            setBirthday(By.id("brdt"), "19820405");
            WaitUtil.waitFor(1);

            logger.info("성별 :: {}", genderText);
            setGender(By.xpath("//label[@for='" + genderOpt + "']"), genderText);

            logger.info("임시 플랜선택 :: {}", info.getTextType());
            setPlanType(By.id("prodTpcd"), info.getTextType());

            if (helper.isAlertShowed()) {
                driver.switchTo().alert().accept();
                throw new Exception("남자는 가입할 수 없는 상품유형입니다.");
            }

            logger.info("보험료납입면제특약 가입 여부 확인");
            setTreatyExist(info);

            logger.info("가입금액 선택 :: {}", info.getAssureMoney());
            setAssureMoney(By.id("trtCntrEntAmt0"), info.getAssureMoney());

            logger.info("보험기간 :: {}", info.getInsTerm());
            setInsTerm(By.id("strSurPrdCod_4_0"), info.getInsTerm() + "만기");

            try {
                logger.info("납입기간 :: {}", info.getNapTerm());
                setNapTerm(By.id("strNabPrdCod_4_0"), info.getNapTerm() + "납");

                logger.info("납입주기 선택 : {}", getNapCycleName(info.getNapCycle()));
                setNapCycle(By.id("strNabMthCod_1_0"), info.getNapCycle());

                logger.info("보험료 계산하기 버튼 클릭 ");
                btnClickforPremium(By.linkText("보험료 계산하기"));

                logger.info("생년월일 :: {}", info.getFullBirth());
                setBirthday(By.id("brdt"), info.getFullBirth());
                WaitUtil.waitFor(1);

                String checkBox = driver.findElement(By.xpath("//*[@id='trtCntrEntAmt0']")).getAttribute("value");
                if ("0".equals(checkBox)) {
                    logger.info("가입금액 선택 :: {}", info.getAssureMoney());
                    setAssureMoney(By.id("trtCntrEntAmt0"), info.getAssureMoney());
                }
                WaitUtil.waitFor(7);

                logger.info("보험료 계산하기 버튼 클릭 ");
                btnClickforPremium(By.cssSelector("#pop_wrapper > p > span > button"));

                String confiremCheckBox = driver.findElement(By.xpath("//*[@id='trtCntrEntAmt0']")).getAttribute("value");
                if ("0".equals(confiremCheckBox)) {
                    logger.info("가입금액 선택 :: {}", info.getAssureMoney());
                    setAssureMoney(By.id("trtCntrEntAmt0"), info.getAssureMoney());

                    logger.info("보험료 계산하기 버튼 클릭 ");
                    btnClickforPremium(By.cssSelector("#pop_wrapper > p > span > button"));
                }

            } catch (Exception e) {
                logger.info("특정 나이에서는 납입기간이 없어 다시 설정하도록 한다.");

                logger.info("생년월일 :: {}", info.getFullBirth());
                setBirthday(By.id("brdt"), info.getFullBirth());
                WaitUtil.waitFor(1);

                logger.info("성별 :: {}", genderText);
                setGender(By.xpath("//label[@for='" + genderOpt + "']"), genderText);

                logger.info("플랜선택 :: {}", info.getTextType());
                setPlanType(By.id("prodTpcd"), info.getTextType());

                if (helper.isAlertShowed()) {
                    driver.switchTo().alert().accept();
                    throw new Exception("남자는 가입할 수 없는 상품유형입니다.");
                }

                logger.info("보험료납입면제특약 가입 여부 확인");
                setTreatyExist(info);

                logger.info("가입금액 선택 :: {}", info.getAssureMoney());
                setAssureMoney(By.id("trtCntrEntAmt0"), info.getAssureMoney());

                logger.info("보험기간 :: {}", info.getInsTerm());
                setInsTerm(By.id("strSurPrdCod_4_0"), info.getInsTerm() + "만기");

                logger.info("납입기간 :: {}", info.getNapTerm());
                setNapTerm(By.id("strNabPrdCod_4_0"), info.getNapTerm() + "납");

                logger.info("납입주기 선택 : {}", getNapCycleName(info.getNapCycle()));
                setNapCycle(By.id("strNabMthCod_1_0"), info.getNapCycle());

                logger.info("보험료 계산하기 버튼 클릭 ");
                btnClickforPremium(By.cssSelector("#pop_wrapper > p > span > button"));
            }

            logger.info("월 보험료 가져오기");
            crawlPremium(By.xpath("//*[@id='result_money_3']"), info);

            logger.info("스크린샷");
            takeScreenShot(info);

            logger.info("해약환급금 가져오기");
            // 해약환급금만 제공하는 경우 : 1 || 최저보증/공시이율에 따른 환급금을 제공하는 경우 2
            int tableType = 1;
            crawlReturnMoneyList(info, tableType);

        } else {
            logger.info("생년월일 :: {}", info.getFullBirth());
            setBirthday(By.id("brdt"), info.getFullBirth());
            WaitUtil.waitFor(1);

            logger.info("성별 :: {}", genderText);
            setGender(By.xpath("//label[@for='" + genderOpt + "']"), genderText);

            logger.info("플랜 선택 :: {}", info.getTextType());
            setPlanType(By.id("prodTpcd"), info.getTextType());
            WaitUtil.loading(3);

            logger.info("보험료납입면제특약 가입 여부 확인");
            setTreatyExist(info);

            logger.info("가입금액 선택 :: {}", info.getAssureMoney());
            setAssureMoney(By.id("trtCntrEntAmt0"), info.getAssureMoney());

            logger.info("보험기간 :: {}", info.getInsTerm());
            setInsTerm(By.id("strSurPrdCod_4_0"), info.getInsTerm() + "만기");

            logger.info("납입기간 :: {}", info.getNapTerm());
            setNapTerm(By.id("strNabPrdCod_4_0"), info.getNapTerm() + "납");

            logger.info("납입주기 선택 : {}", getNapCycleName(info.getNapCycle()));
            setNapCycle(By.id("strNabMthCod_1_0"), info.getNapCycle());

            logger.info("보험료 계산하기 버튼 클릭 ");
            btnClickforPremium(By.cssSelector("#pop_wrapper > p > span > button"));

            logger.info("월 보험료 가져오기");
            crawlPremium(By.xpath("//*[@id='result_money_3']"), info);

            logger.info("스크린샷");
            takeScreenShot(info);

            logger.info("해약환급금 가져오기");
            // 해약환급금만 제공하는 경우 : 1 || 최저보증/공시이율에 따른 환급금을 제공하는 경우 2
            int tableType = 1;
            crawlReturnMoneyList(info, tableType);

        }
        return true;
    }

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            int tableType = (int) obj[1]; // 해약환급금만 제공하는 경우 : 1 || 최저보증/공시이율에 따른 환급금을 제공하는 경우 2

            driver.findElement(By.linkText("해약환급금")).click();
            WaitUtil.waitFor(2);
            waitLoadingImg();

            List<WebElement> $trList = driver.findElements(By.xpath("//*[@id=\"PD_SUB_CONTROLLER_3\"]//table/tbody//tr"));

            if(tableType == 1){
                getPlanReturnMoney(info, $trList);
            } else{
                getPlanReturnMoneyExpanded(info, $trList);
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RETURN_PREMIUM;
            throw new ReturnMoneyListCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    // 공시이율 해약환급금 테이블
    @Override
    protected void getPlanReturnMoney(CrawlingProduct info, List<WebElement> $trList) throws ReturnMoneyListCrawlerException{

        try{
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

            // 경과기간 (만기시점)
//            String maturityYear = getMaturityYear(info);

            for (WebElement tr : $trList) {
                String term = tr.findElements(By.tagName("th")).get(0).getText();
                String premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
                String returnMoney = tr.findElements(By.tagName("td")).get(3).getText();
                String returnRate = tr.findElements(By.tagName("td")).get(4).getText();

                logger.info("경과기간 :: {}", term);
                logger.info("납입보험료 합계 :: {}", premiumSum);
                logger.info("공시이율 해약환급금 :: {}",returnMoney);
                logger.info("공시이율 환급률 :: {}", returnRate);
                logger.info("======================");

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                planReturnMoney.setPlanId(Integer.parseInt(info.getPlanId()));
                planReturnMoney.setGender((info.getGender() == MALE) ? "M" : "F");
                planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

                planReturnMoney.setTerm(term);    // 경과기간
                planReturnMoney.setPremiumSum(premiumSum);    // 납입보험료 누계
                planReturnMoney.setReturnMoney(returnMoney);    // 예상해약환급금
                planReturnMoney.setReturnRate(returnRate);    // 예상 환급률

                planReturnMoneyList.add(planReturnMoney);
                info.returnPremium = returnMoney.replace(",", "").replace("원", "");
                // 만기시점(경과기간)을 해약환급금 표에서 제공하는 경우
//                if(maturityYear.equals(term.trim()) || "만기".equals(term.trim())){
//                  info.setReturnPremium(returnMoney.replaceAll("[^0-9]",""));
//                }
            }
            // 만기환급금 크롤링이 불가한 경우
//            if(info.getProductCode().contains("TRM")) {
//              logger.info("정기보험은 만기환급금을 크롤링하지 않습니다");
//
//            } else if(info.getReturnPremium().equals("")){
//              info.setReturnPremium("-1");
//            }
            info.setPlanReturnMoneyList(planReturnMoneyList);
            // 해약환급금 관련 End
        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RETURN_MONEY_LIST;
            throw new ReturnMoneyListCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }
}
