package com.welgram.crawler.direct.life.abl;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;



/** 해약환급금 공시이율이 나와 있는 공시실로 크롤링 */
public class ABL_ASV_D001 extends CrawlingABLAnnounce {

    public static void main(String[] args) {
        executeCommand(new ABL_ASV_D001(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // 공시실
        openAnnouncePage(info);

        // 성별
        setGender("sxdsCd1", info.getGender());

        // 생년월일
        setBirthday(By.id("insrdSno_jupiDate1"), info.getFullBirth());

        // 계약관계정보 적용
        doClickButton(By.id("applyContRltnInfo"));

        // 연금개시연령
        setAnnuityAge("anutBgnAge", info.getAnnuityAge());

        // 납입기간
        setNapTerm("mnInsrPadPrdYys", info.getNapTerm());

        // 연금지급방법 매년지급으로
        setAnnPayment("anutPymMth", "매년");

        // 월납입보험료
        setMonthlyPremium(info.getAssureMoney());

        // 보험료 계산
        calculation("calcPremium");

        // 공시실 스크롤 내리기
        logger.info("스크롤 내리기");
        discusroomscrollbottom();

        // 스크린샷 추가
        logger.info("스크린샷");
        takeScreenShot(info);

        // 보험료
        crawlPremium("prdPrm", info);

        // 해약환급금 & 연금수령액
        crawlReturnMoneyList(info, "ASV");

        return true;
    }



    /*********************************************************
     * <해약환급금 세팅 메소드>
     * @param  obj
     * obj[0] -> CrawlingProduct (필수)
     * obj[1] -> type 정리(필수 아님, 없을시에는 예외처리로 0,2,3,4번 td 크롤링)
     *           1. "TRM" -> 해약환급금 표에 사망보험금이 포함되어있어 테이블의 4(해약환급금), 5(환급률)인 경우
     *           2. "ALL,ASV" -> 해약환급금에 최저 공시 일반 모두 표기되어있어 전부 크롤링("ASV"인 경우 연금수령액까지)
     * @throws Exception - 해약환급금 세팅시 예외처리
     *********************************************************/
    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];

        try {
            element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("btnEntplRprtPbl")));
            element.click();
            WaitUtil.loading(3);

            Set<String> windowId = driver.getWindowHandles();
            Iterator<String> handles = windowId.iterator();

            String currentHandle = driver.getWindowHandle();
            String nextHandle = null;

            while (handles.hasNext()) {
                nextHandle = handles.next();
                WaitUtil.loading(2);
            }

            driver.switchTo().window(nextHandle);

            element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("tabRefnd")));
            element.click();
            helper.waitForCSSElement(".state-load-data");

            int num = 0;
            String text = "";

            if (info.productCode.equals("ABL00099")) {
                text = "해약환급금(투자수익률3.75%)";
            } else {
                text = "해약환급금(공시이율)";
            }

            WaitUtil.loading(3);
            element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("refndTab")));
            List<WebElement> thElements = element.findElements(By.cssSelector("table thead th"));
            for (int i = 0; i < thElements.size(); i++) {
                if (thElements.get(i).getText().replace("\n", "").equals(text)) {
                    num = i;
                    break;
                }
            }

            element = element.findElement(By.tagName("table")).findElement(By.tagName("tbody"));
            elements = element.findElements(By.tagName("tr"));

            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
            for (WebElement tr : elements) {
                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                String term = tr.findElements(By.tagName("td")).get(0).getText();
                String premiumSum = tr.findElements(By.tagName("td")).get(2).getText();
                String returnMoneyMin = tr.findElements(By.tagName("td")).get(3).getText();
                String returnRateMin = tr.findElements(By.tagName("td")).get(4).getText();
                String returnMoneyAvg = tr.findElements(By.tagName("td")).get(5).getText();
                String returnRateAvg = tr.findElements(By.tagName("td")).get(6).getText();
                String returnMoney = tr.findElements(By.tagName("td")).get(7).getText();
                String returnRate = tr.findElements(By.tagName("td")).get(8).getText();

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
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoneyMin(returnMoneyMin); // 최저 해약환급금
                planReturnMoney.setReturnRateMin(returnRateMin);   // 최저 환급률
                planReturnMoney.setReturnMoneyAvg(returnMoneyAvg); // 평균 해약환급금
                planReturnMoney.setReturnRateAvg(returnRateAvg);  // 평균 환급률
                planReturnMoney.setReturnMoney(returnMoney); // 공시이율 해약환급금
                planReturnMoney.setReturnRate(returnRate);   // 공시이율 환급률
                planReturnMoneyList.add(planReturnMoney);

                // 기본 해약환급금 세팅
                info.returnPremium = tr.findElements(By.tagName("td")).get(7).getText()
                    .replace(",", "");
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);

            // 연금수령액
            getAnnuityPremium(info);

            info.savePremium = "0"; // 적립보험료
            info.errorMsg = "";

            driver.close();
            driver.switchTo().window(currentHandle);

        } catch (Exception e){
            throw new ReturnMoneyListCrawlerException(e.getMessage());
        }
    }



    /*********************************************************
     * <연금수령액 가져오기 메소드>
     * @param  info {CrawlingProduct} - 크롤링 상품 객체
     * @throws Exception - 연금수령액 세팅시 예외처리
     *********************************************************/
    @Override
    protected void getAnnuityPremium(CrawlingProduct info) throws Exception {

        String annuityPremium = "";
        String fixedAnnuityPremium = "";
        PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();
        driver.findElement(By.id("tabAnut")).click();
        WaitUtil.loading(3);

        try {
            element = driver.findElement(By.cssSelector("#anutTab > table:nth-child(8)"));
            elements = element.findElements(By.tagName("td"));

            if (info.annuityType.contains("종신")) {            // 종신연금형일 경우
                if (info.annuityType.contains("10년")) {        // 종신 10년일 경우
                    annuityPremium
                        = driver
                            .findElement(By.cssSelector("#anutTab > table:nth-child(8) > tbody > tr:nth-child(1) > td:nth-child(4)"))
                            .getText()
                            .replaceAll("10년", "").replaceAll("[^0-9]", "");
                    info.annuityPremium = annuityPremium + "0000";      // 매년
                    logger.info("종신연금수령액: " + info.annuityPremium + "원");

                    fixedAnnuityPremium
                        = driver.findElement(By.cssSelector("#anutTab > table:nth-child(8) > tbody > tr:nth-child(3) > td:nth-child(3)"))
                            .getText()
                            .replaceAll("10년", "").replaceAll("[^0-9]", "");
                    info.fixedAnnuityPremium = fixedAnnuityPremium + "0000";

                    // 매년
                    logger.info("확정연금수령액: " + info.fixedAnnuityPremium + "원");


                } else if (info.annuityType.contains("20년")) { // 종신 20년일 경우
                    annuityPremium
                        = driver
                            .findElement(By.cssSelector("#anutTab > table:nth-child(8) > tbody > tr:nth-child(2) > td:nth-child(2)"))
                            .getText()
                            .replaceAll("20년", "").replaceAll("[^0-9]", "");
                    info.annuityPremium = annuityPremium + "0000";                      // 매년
                    logger.info("종신연금수령액: " + info.annuityPremium + "원");

                    fixedAnnuityPremium
                        = driver.findElement(By.cssSelector("#anutTab > table:nth-child(8) > tbody > tr:nth-child(5) > td:nth-child(2)"))
                            .getText()
                            .replaceAll("20년", "").replaceAll("[^0-9]", "");
                    info.fixedAnnuityPremium = fixedAnnuityPremium + "0000";            // 매년
                    logger.info("확정연금수령액: " + info.fixedAnnuityPremium + "원");

                }

            } else if (info.annuityType.contains("확정")) { // 확정연금형일 경우

                if (info.annuityType.contains("10년")) { // 확정 10년일 경우

                    annuityPremium
                        = driver.findElement(By.cssSelector("#anutTab > table:nth-child(8) > tbody > tr:nth-child(1) > td:nth-child(4)"))
                            .getText()
                            .replaceAll("10년", "").replaceAll("[^0-9]", "");
                    info.annuityPremium = annuityPremium + "0000"; // 매년
                    logger.info("종신연금수령액: " + info.annuityPremium + "원");

                    fixedAnnuityPremium
                        = driver.findElement(By.cssSelector("#anutTab > table:nth-child(8) > tbody > tr:nth-child(3) > td:nth-child(3)"))
                            .getText()
                            .replaceAll("10년", "").replaceAll("[^0-9]", "");
                    info.fixedAnnuityPremium = fixedAnnuityPremium + "0000";
                    ; // 매년
                    logger.info("확정연금수령액: " + info.fixedAnnuityPremium + "원");

                } else if (info.annuityType.contains("15년")) { // 확정 15년일 경우
                    fixedAnnuityPremium
                        = driver
                            .findElement(By.cssSelector("#anutTab > table:nth-child(8) > tbody > tr:nth-child(4) > td:nth-child(2)"))
                            .getText()
                            .replaceAll("15년", "").replaceAll("[^0-9]", "");
                    info.fixedAnnuityPremium = fixedAnnuityPremium + "0000";
                    ; // 매년
                    logger.info("확정연금수령액: " + info.fixedAnnuityPremium + "원");

                } else if (info.annuityType.contains("20년")) { // 확정 20년일 경우

                    annuityPremium
                        = driver
                            .findElement(By.cssSelector("#anutTab > table:nth-child(8) > tbody > tr:nth-child(2) > td:nth-child(2)"))
                            .getText()
                            .replaceAll("20년", "").replaceAll("[^0-9]", "");
                    info.annuityPremium = annuityPremium + "0000"; // 매년
                    logger.info("종신연금수령액: " + info.annuityPremium + "원");

                    fixedAnnuityPremium
                        = driver
                            .findElement(By.cssSelector("#anutTab > table:nth-child(8) > tbody > tr:nth-child(5) > td:nth-child(2)"))
                            .getText()
                            .replaceAll("20년", "").replaceAll("[^0-9]", "");
                    info.fixedAnnuityPremium = fixedAnnuityPremium + "0000"; // 매년
                    logger.info("확정연금수령액: " + info.fixedAnnuityPremium + "원");
                }
            }
        } catch(Exception e) {
            throw new Exception(e.getMessage());
        }

        // 종신형
        planAnnuityMoney.setWhl10Y(driver.findElement(By.cssSelector("#anutTab > table:nth-child(8) > tbody > tr:nth-child(1) > td:nth-child(4)")).getText().replaceAll("[^0-9]", "") +"0000");    //종신 10년
        planAnnuityMoney.setWhl20Y(driver.findElement(By.cssSelector("#anutTab > table:nth-child(8) > tbody > tr:nth-child(2) > td:nth-child(2)")).getText().replaceAll("[^0-9]", "") +"0000");    //종신 20년

        // 확정형
        String Fxd10 = driver.findElement(By.cssSelector("#anutTab > table:nth-child(8) > tbody > tr:nth-child(3) > td:nth-child(3)")).getText() ;  // 확정 10년
        int Fxd10last = Fxd10.indexOf("씩"); // 매년기준이기 때문에 문자열 "씩" 앞까지만

        String Fxd15 = driver.findElement(By.cssSelector("#anutTab > table:nth-child(8) > tbody > tr:nth-child(4) > td:nth-child(2)")).getText() ;  // 확정 15년
        int Fxd15last = Fxd15.indexOf("씩"); // 매년기준이기 때문에 문자열 "씩" 앞까지만

        String Fxd20y = driver.findElement(By.cssSelector("#anutTab > table:nth-child(8) > tbody > tr:nth-child(5) > td:nth-child(2)")).getText() ;  // 확정 20년
        int Fxd20last = Fxd20y.indexOf("씩"); // 매년기준이기 때문에 문자열 "씩" 앞까지만

        planAnnuityMoney.setFxd10Y(Fxd10.substring(0,Fxd10last+1).replaceAll("[^0-9]", "") +"0000");    //확정 10년
        planAnnuityMoney.setFxd15Y(Fxd15.substring(0,Fxd15last+1).replaceAll("[^0-9]", "") +"0000");    //확정 15년
        planAnnuityMoney.setFxd20Y(Fxd20y.substring(0,Fxd20last+1).replaceAll("[^0-9]", "") +"0000");    //확정 20년

        logger.info("종신10년 :: "+planAnnuityMoney.getWhl10Y());
        logger.info("종신20년 :: "+planAnnuityMoney.getWhl20Y());
        logger.info("확정10년 :: "+planAnnuityMoney.getFxd10Y());
        logger.info("확정15년 :: "+planAnnuityMoney.getFxd15Y());
        logger.info("확정20년 :: "+planAnnuityMoney.getFxd20Y());

        info.planAnnuityMoney = planAnnuityMoney;
    }

}