package com.welgram.crawler.direct.life.hnl;

import com.welgram.common.MoneyUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class HNL_ASV_D001 extends CrawlingHNLMobile {

    public static void main(String[] args) {
        executeCommand(new HNL_ASV_D001(), args);
    }


    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $button = null;

        modalCheck();

        logger.info("내 보험료 알아보기 버튼 클릭");
        $button = driver.findElement(By.id("btnCalcShow"));
        click($button);

        logger.info("생년월일 설정");
        setBirthday(info.getFullBirth());

        logger.info("성별 설정");
        setGender(info.getGender());

        logger.info("확인 버튼 클릭");
        $button = driver.findElement(By.id("btnCalc"));
        click($button);

        /*
         *
         * 항목 세팅 순서가 중요하다.
         * 1. 연금수령형태를 가장 먼저 세팅한다(해당 값을 바꾸게되면 모든 항목 값 초기화됨)
         * 2. 연금개시나이를 두번째로 세팅한다(연금개시나이를 바꿈에 따라 납입기간이 영향을 받으므로)
         * */
        logger.info("연금을 받는 방식(=연금수령형태) 설정");
        setAnnuityType("종신연금 정액형 10년 보증형");

        logger.info("연금이 시작되는 나이(=연금개시나이) 설정");
        setAnnuityAge(info.getAnnuityAge());

        logger.info("목표 납입기간(=납입기간) 설정");
        setNapTerm(info.getNapTerm());

        logger.info("매월 납입하는 보험료(=가입금액) 설정");
        setAssureMoney(info);

        logger.info("다시 계산하기 버튼 클릭");
        $button = driver.findElement(By.xpath("//span[text()='다시 계산하기']/parent::button"));
        click($button);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("해약환급금 조회 버튼 클릭");
        $button = driver.findElement(By.id("surrBtn"));
        helper.moveToElementByJavascriptExecutor($button);
        click($button);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info);

        logger.info("연금수령액 크롤링");
        crawlAnnuityMoney(info);

        return true;
    }

    public void crawlAnnuityMoney(Object... obj) throws CommonCrawlerException {

        String title = "연금수령액 크롤링";
        CrawlingProduct info = (CrawlingProduct) obj[0];
        String welgramAnnuityType = info.getAnnuityType();
        WebElement $button = null;

        try {
            String annuityPremium = driver.findElement(By.id("chgAntyRecvYTxt")).getText().replaceAll("[^0-9]", "");

            logger.info("예상 수령액 버튼 클릭");
            $button = driver.findElement(By.id("antyBtn"));
            click($button);

            String whl20Y = driver.findElement(By.id("maxTpAmt_20")).getText();
            String whl30Y = driver.findElement(By.id("maxTpAmt_30")).getText();
            String whl100A = driver.findElement(By.id("maxTpAmt_100")).getText();
            String fxd10Y = driver.findElement(By.id("maxTpAmt_10S")).getText();
            String fxd20Y = driver.findElement(By.id("maxTpAmt_20S")).getText();
            String fxd30Y = driver.findElement(By.id("maxTpAmt_30S")).getText();
            whl20Y = String.valueOf(MoneyUtil.toDigitMoney(whl20Y));
            whl30Y = String.valueOf(MoneyUtil.toDigitMoney(whl30Y));
            whl100A = String.valueOf(MoneyUtil.toDigitMoney(whl100A));
            fxd10Y = String.valueOf(MoneyUtil.toDigitMoney(fxd10Y));
            fxd20Y = String.valueOf(MoneyUtil.toDigitMoney(fxd20Y));
            fxd30Y = String.valueOf(MoneyUtil.toDigitMoney(fxd30Y));

            PlanAnnuityMoney p = info.getPlanAnnuityMoney();
            p.setWhl10Y(annuityPremium);
            p.setWhl20Y(whl20Y);
            p.setWhl30Y(whl30Y);
            p.setWhl100A(whl100A);
            p.setFxd10Y(fxd10Y);
            p.setFxd20Y(fxd20Y);
            p.setFxd30Y(fxd30Y);

            info.setPlanAnnuityMoney(p);

            logger.info("종신10년 연금수령액 : {}원", p.getWhl10Y());
            logger.info("종신20년 연금수령액 : {}원", p.getWhl20Y());
            logger.info("종신30년 연금수령액 : {}원", p.getWhl30Y());
            logger.info("종신100세 연금수령액 : {}원", p.getWhl100A());
            logger.info("확정10년 연금수령액 : {}원", p.getFxd10Y());
            logger.info("확정20년 연금수령액 : {}원", p.getFxd20Y());
            logger.info("확정30년 연금수령액 : {}원", p.getFxd30Y());

            if(welgramAnnuityType.contains("종신")) {
                info.annuityPremium = annuityPremium;
            } else if(welgramAnnuityType.contains("확정")) {
                info.fixedAnnuityPremium = fxd10Y;
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_ANNUITY_MONEY;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {
        String title = "해약환급금 크롤링";
        CrawlingProduct info = (CrawlingProduct) obj[0];
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        String[] names = {"최저보증이율", "평균공시이율", "공시이율"};
        String[] ids = {"surrMinTbody", "surrStdTbody", "surrMaxTbody"};

        try {
            for(int i = 0; i < names.length; i++) {
                String name = names[i];
                String id = ids[i];

                List<WebElement> $trList = helper.waitVisibilityOfAllElementsLocatedBy(By.xpath("//tbody[@id='" + id + "']/tr"));

                for(WebElement $tr : $trList) {
                    List<WebElement> $tdList = $tr.findElements(By.tagName("td"));
                    String term = $tdList.get(0).getText();
                    String premiumSum = $tdList.get(1).getText().replaceAll("[^0-9]", "");
                    String returnMoney = $tdList.get(2).getText().replaceAll("[^0-9]", "");
                    String returnRate = $tdList.get(3).getText();

                    PlanReturnMoney p = null;

                    if(i == 0) {
                        p = new PlanReturnMoney();
                        p.setTerm(term);
                        p.setPremiumSum(premiumSum);
                        p.setReturnMoneyMin(returnMoney);
                        p.setReturnRateMin(returnRate);
                        planReturnMoneyList.add(p);
                    } else if(i == 1) {
                        p = planReturnMoneyList.stream().filter(prm -> prm.getTerm().equals(term)).findFirst().get();
                        p.setReturnMoneyAvg(returnMoney);
                        p.setReturnRateAvg(returnRate);
                    } else {
                        p = planReturnMoneyList.stream().filter(prm -> prm.getTerm().equals(term)).findFirst().get();
                        p.setReturnMoney(returnMoney);
                        p.setReturnRate(returnRate);

                        info.returnPremium = returnMoney;
                    }

                    logger.info("{} | 경과기간 : {} | 납입보험료 : {} | 환급금 : {} | 환급률 : {}", name, term, premiumSum, returnMoney, returnRate);
                }
            }

            logger.info("만기환급금 : {}원", info.returnPremium);

            logger.info("확인 버튼 클릭");
            WebElement $button = driver.findElement(By.xpath("//article[@id='pp-prd-001LayerArea']//button[text()='확인']"));
            click($button);
       } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {
        String title = "가입금액";

        CrawlingProduct info = (CrawlingProduct) obj[0];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
        String assureMoney = info.getAssureMoney();
        String expectedAssureMoney = assureMoney;
        String actualAssureMoney = "";

        int unit = MoneyUnit.만원.getValue();
        String unitText = "만원";

        try {
            expectedAssureMoney = String.valueOf((Integer.parseInt(expectedAssureMoney) / unit)) + unitText;

            //가입금액 설정을 위해 클릭
            WebElement $assureMoneySelect = driver.findElement(By.id("resultSelRecmPrm"));
            click($assureMoneySelect);

            //가입금액 설정
            WebElement $assureMoneyDiv = driver.findElement(By.id("selectPopup"));
            WebElement $assureMoneySpan = $assureMoneyDiv.findElement(By.xpath(".//span[text()='" + expectedAssureMoney + "']"));
            WebElement $assureMoneyBtn = $assureMoneySpan.findElement(By.xpath("./parent::button"));
            click($assureMoneyBtn);

            //실제 선택된 가입금액 읽어오기
            String script = "return $(arguments[0]).find('option:selected').val();";
            actualAssureMoney = String.valueOf(helper.executeJavascript(script, $assureMoneySelect));

            //가입금액 비교를 위해 단위값 맞춰주기
            actualAssureMoney = actualAssureMoney.replaceAll("[^0-9]", "");
            expectedAssureMoney = assureMoney;

            //비교
            super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);

            //주계약 보험료 설정
            mainTreaty.monthlyPremium = assureMoney;
            logger.info("주계약 보험료 : {}", mainTreaty.monthlyPremium);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {
        String title = "납입기간";

        String expectedNapTerm = (String) obj[0];
        String actualNapTerm = "";

        try {
            //납입기간 설정을 위해 클릭
            WebElement $napTermSelect = driver.findElement(By.id("resultSelPayPd"));
            click($napTermSelect);

            //납입기간 설정
            WebElement $napTermDiv = driver.findElement(By.id("selectPopup"));
            WebElement $napTermSpan = $napTermDiv.findElement(By.xpath(".//span[text()='" + expectedNapTerm + "']"));
            WebElement $napTermBtn = $napTermSpan.findElement(By.xpath("./parent::button"));
            click($napTermBtn);

            //실제 선택된 납입기간 읽어오기
            String script = "return $(arguments[0]).find('option:selected').text();";
            actualNapTerm = String.valueOf(helper.executeJavascript(script, $napTermSelect));

            //비교
            super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }
}