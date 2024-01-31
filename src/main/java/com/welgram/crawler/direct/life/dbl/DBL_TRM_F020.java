package com.welgram.crawler.direct.life.dbl;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.Optional;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class DBL_TRM_F020 extends CrawlingDBLAnnounce {

//(무) 경영인 정기보험(2309)(3형:20%체증형)

  public static void main(String[] args) {
    executeCommand(new DBL_TRM_F020(), args);
  }

  @Override
  protected boolean scrap(CrawlingProduct info) throws Exception {
    crawlFromHomepage(info);
    return true;
  }

  @Override
  protected void configCrawlingOption(CrawlingOption option) throws Exception {
    option.setBrowserType(BrowserType.Chrome);
    option.setImageLoad(true);
    option.setUserData(false);
  }

  public void crawlFromHomepage(CrawlingProduct info) throws Exception {


      WaitUtil.loading();
      calculate();
      setBirthday(info.birth);
      setGender(info);
      setProductType(info.textType);
      setAssureMoney(info.assureMoney);
      setInsTerm(info.insTerm);
      setNapTerm(info);
      calculate();

      crawlPremium(info);
      takeScreenShot(info);
      moveToElement(driver.findElement(By.cssSelector("#direct_result > div > a.btnB.greenType")));

      crawlReturnMoneyListAll(info);
      crawlReturnPremium(info);
  }

    public void crawlReturnMoneyListAll(Object... obj) throws ReturnMoneyListCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

            logger.info("해약환급금예시 클릭");
            driver.findElement(By.cssSelector("#direct_result > div > a.btnB.greenType")).click();
            WaitUtil.waitFor(3);

            elements = driver.findElements(
                By.xpath("//*[@id=\"refund_result\"]/div[2]/table/tbody/tr"));
            for (WebElement tr : elements) {
                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                String term = "";
                long premiumSum = 0;
                long returnMoneyMin = 0;
                String returnRateMin = "";
                long returnMoneyAvg = 0;
                String returnRateAvg = "";
                long returnMoney = 0;
                String returnRate = "";

                int unit = 10000;
                term = tr.findElements(By.tagName("th")).get(0).getText();
                premiumSum =
                    Long.parseLong(tr.findElements(By.tagName("td")).get(5).getText().replaceAll("\\D", "")) * unit;

                returnMoneyMin =
                    Long.parseLong(tr.findElements(By.tagName("td")).get(6).getText().replaceAll("\\D", "")) * unit;
                returnRateMin = tr.findElements(By.tagName("td")).get(7).getText();

                returnMoneyAvg =
                    Long.parseLong(tr.findElements(By.tagName("td")).get(8).getText().replaceAll("\\D", "")) * unit;
                returnRateAvg = tr.findElements(By.tagName("td")).get(9).getText();

                returnMoney =
                    Long.parseLong(tr.findElements(By.tagName("td")).get(10).getText().replaceAll("\\D", "")) * unit;
                returnRate = tr.findElements(By.tagName("td")).get(11).getText();

                logger.info("경과기간   :: {}", term);
                logger.info("납입보험료 :: {}", premiumSum);
                logger.info("해약환급금 :: {}", returnMoney);
                logger.info("환급률    :: {}", returnRate);
                logger.info("최저해약환급금 :: {}", returnMoneyMin);
                logger.info("최저해약환급률 :: {}", returnRateMin);
                logger.info("평균해약환급금 :: {}", returnMoneyAvg);
                logger.info("평균해약환급률 :: {}", returnRateAvg);
                logger.info("=================================");

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(String.valueOf(premiumSum));
                planReturnMoney.setReturnMoneyMin(String.valueOf(returnMoneyMin));
                planReturnMoney.setReturnRateMin(returnRateMin);
                planReturnMoney.setReturnMoney(String.valueOf(returnMoney));
                planReturnMoney.setReturnRate(returnRate);
                planReturnMoney.setReturnMoneyAvg(String.valueOf(returnMoneyAvg));
                planReturnMoney.setReturnRateAvg(returnRateAvg);

                planReturnMoneyList.add(planReturnMoney);

            }

            info.setPlanReturnMoneyList(planReturnMoneyList);

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }

    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            int planCalcAge = info.getCategoryName().equals("태아보험") ? 0
                : Integer.parseInt(info.age.replaceAll("\\D", ""));

            // 수집한 중도해약환급금 목록
            List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

            Optional<PlanReturnMoney> returnMoneyOptional = Optional.empty();
            for (int i = planReturnMoneyList.size() - 1; i > 0; i--) {

                PlanReturnMoney planReturnMoney = planReturnMoneyList.get(i);

                // termTxt: planReturnMoney 경과기간
                String termTxt = planReturnMoney.getTerm();

                // 경과기간이 개월단위인 경우는 일단 제외 // todo 개월단위도 포함하도록 수정
                if (termTxt.contains("개월")) {
                    continue;
                }

                // 나이로된 경과기간, 년으로 된 경과기간 추출
                String termUnit = termTxt.indexOf("년") > termTxt.indexOf("세") ? "년" : "세";
                int termUnitIndex = termTxt.indexOf(termUnit);
                int termNumberValue = Integer.parseInt(
                    termTxt.substring(0, termUnitIndex).replaceAll("\\D", ""));
                int termYear = -1;
                int termAge = -1;
                switch (termUnit) {
                    case "년":
                        termYear = termNumberValue;
                        termAge = planCalcAge + termYear;
                        break;
                    case "세":
                        termYear = termNumberValue - planCalcAge;
                        termAge = termNumberValue;
                        break;
                }

                // 해당 가설(info)의 보험기간 단위 추출 (세 or 년), 숫자 추출
                String insTermUnit = "";
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

                // 보험기간 단위에 따라 비교: 경과기간이 만기에 해당하는지 여부 반환
                if ((insTermUnit.equals("세") && termAge == insTermNumberValue)
                    || (insTermUnit.equals("년") && termYear == insTermNumberValue)) {

                    logger.info("만기환급금 크롤링 :: 카테고리 :: {}", info.categoryName);
                    logger.info("만기환급금 크롤링 :: 가설 케이스 나이 :: {}세", planCalcAge);
                    logger.info("만기환급금 크롤링 :: 가설 보험기간 :: {}", info.insTerm);
                    logger.info("만기환급금 크롤링 :: 가설 납입기간 :: {}", info.napTerm);
                    logger.info("만기환급금 크롤링 :: 해약환급금 해당 경과기간 :: {}", planReturnMoney.getTerm());

                    returnMoneyOptional = Optional.of(planReturnMoney);
                }
            }

            if (returnMoneyOptional.isPresent()) {
                info.returnPremium = returnMoneyOptional.get().getReturnMoney();
            } else {
                info.returnPremium = "-1"; // 만기에 해당하는 중도해약환급금이 없을 경우
            }

            if(productCode.contains("TRM")){
                info.returnPremium = "0";
            }

            logger.info("만기환급금 크롤링 :: 만기환급금 :: {}", info.returnPremium);

        } catch (Exception e) {
            throw new ReturnPremiumCrawlerException(e);
        }
    }

}
