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

public class NHL_SAV_F001 extends CrawlingNHLAnnounce {



    public static void main(String[] args) {
        executeCommand(new NHL_SAV_F001(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {


        String genderOpt = (info.getGender() == MALE) ? "sex_m" : "sex_fm";
        String genderText = (info.getGender() == MALE) ? "남" : "여";

        logger.info("NHL_SAV_F001 :: {}", info.getProductName());
        WaitUtil.waitFor(2);

        logger.info("생년월일 :: {}", info.getFullBirth());
        setBirthday(By.id("brdt"), info.getFullBirth());
        WaitUtil.waitFor(1);

        logger.info("성별 :: {}", genderText);
        setGender(By.xpath("//label[@for='" + genderOpt + "']"), genderText);

        logger.info("상품유형 :: {}", info.getTextType());
        setPlanType(By.id("prodTpcd"), info.getTextType());

        logger.info("보험기간 :: {}", info.getInsTerm());
        setInsTerm(By.id("strSurPrdCod_4_0"), info.getInsTerm() + "만기");

        logger.info("납입기간 :: {}납", info.getNapTerm());
        setNapTerm(By.id("strNabPrdCod_4_0"), info.getNapTerm() + "납");

        logger.info("납입주기 선택 : {}", getNapCycleName(info.getNapCycle()));
        setNapCycle(By.id("strNabMthCod_1_0"), info.getNapCycle());

        logger.info("월 보험료 입력 :: {} 원", info.getAssureMoney());
        setAssureMoneyIntoInputBox(By.id("div_txtResultMny0"), info.getAssureMoney());

        logger.info("보험료 계산하기 버튼 클릭 ");
        btnClickforPremium(By.cssSelector("#pop_wrapper > p > span > button"));

        logger.info("월 보험료 가져오기");
        crawlPremium(By.id("result_money_3"), info);

        logger.info("스크린샷");
        takeScreenShot(info);

        logger.info("해약환급금 가져오기");
        // 해약환급금만 제공하는 경우 : 1 || 최저보증/공시이율에 따른 환급금을 제공하는 경우 2
        int tableType = 2;
        crawlReturnMoneyList(info, tableType);

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


    // 최저보증/공시이율 해약환급금 테이블
    @Override
    protected void getPlanReturnMoneyExpanded(CrawlingProduct info, List<WebElement> $trList) throws ReturnMoneyListCrawlerException{

        try{
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

            // 경과기간 (만기시점)
//            String maturityYear = getMaturityYear(info);

            for (WebElement $tr : $trList) {
                String term = $tr.findElements(By.tagName("th")).get(0).getText();
                String premiumSum = $tr.findElements(By.tagName("td")).get(0).getText();
                String returnMoneyMin = $tr.findElements(By.tagName("td")).get(1).getText();
                String returnRateMin = $tr.findElements(By.tagName("td")).get(2).getText();
                String returnMoneyAvg = $tr.findElements(By.tagName("td")).get(3).getText();
                String returnRateAvg = $tr.findElements(By.tagName("td")).get(4).getText();
                String returnMoney = $tr.findElements(By.tagName("td")).get(5).getText();
                String returnRate = $tr.findElements(By.tagName("td")).get(6).getText();

                logger.info("__________해약환급급__________");
                logger.info("경과기간 :: {}", term);
                logger.info("납입보험료 합계 :: {}", premiumSum);
                logger.info("최저보증이율 :: {}", returnMoneyMin);
                logger.info("최저보증 환급율 :: {}", returnRateMin);
                logger.info("평균공시이율 :: {}", returnMoneyAvg);
                logger.info("평균공시 환급율 :: {}", returnRateAvg);
                logger.info("공시이율 해약환급금 :: {}",returnMoney);
                logger.info("공시이율 환급률 :: {}", returnRate);
                logger.info("|___________________________");

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                planReturnMoney.setPlanId(Integer.parseInt(info.getPlanId()));
                planReturnMoney.setGender((info.getGender() == MALE) ? "M" : "F");
                planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

                planReturnMoney.setTerm(term);    // 경과기간
                planReturnMoney.setPremiumSum(premiumSum);    // 납입보험료 누계
                planReturnMoney.setReturnMoneyMin(returnMoneyMin);  //최저 보증
                planReturnMoney.setReturnRateMin(returnRateMin);    //최저 보증 환급율
                planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);  //평균 공시
                planReturnMoney.setReturnRateAvg(returnRateAvg);    //평균 공시 이율 환급율
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

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RETURN_MONEY_LIST;
            throw new ReturnMoneyListCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

}
