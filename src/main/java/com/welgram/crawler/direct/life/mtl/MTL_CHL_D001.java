package com.welgram.crawler.direct.life.mtl;

import com.welgram.common.MoneyUtil;
import com.welgram.common.ReturnMoneyIdx;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanCalc;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class MTL_CHL_D001 extends CrawlingMTLAnnounce {

    public static void main(String[] args) {
        executeCommand(new MTL_CHL_D001(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $element = null;

        driver.manage().window().maximize();

        logger.info("계약자 생년월일 설정(주민등록번호 앞자리)");
        setBirthday(info.getParent_Birth(), By.id("contract_jumin1"));

        logger.info("계약자 성별 설정(주민등록번호 뒷자리)");
        setGender(info.getGender(), info.getParent_FullBirth(), By.xpath("//input[@name='contract_jumin2']"));

        logger.info("주피보험자 생년월일 설정(주민등록번호 앞자리)");
        setBirthday(info.getBirth(), By.id("jumin1"));

        logger.info("주피보험자 성별 설정(주민등록번호 뒷자리)");
        setGender(info.getGender(), info.getFullBirth(), By.xpath("//input[@name='jumin2']"));

        logger.info("다음 버튼 클릭");
        $element = driver.findElement(By.linkText("다음"));
        click($element);

        logger.info("[STEP 2] 주계약 정보 입력");
        setMainTreatyInfo(info);

        logger.info("다음 버튼 클릭");
        $element = driver.findElement(By.id("goNext"));
        click($element);

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("스크린샷 찍기");
        $element = driver.findElement(By.id("productName"));
        helper.moveToElementByJavascriptExecutor($element);
        takeScreenShot(info);

        logger.info("다음 버튼 클릭");
        $element = driver.findElement(By.id("goCoverage"));
        click($element);

        logger.info("해약환급금 크롤링");
        ReturnMoneyIdx returnMoneyIdx = new ReturnMoneyIdx();
        returnMoneyIdx.setPremiumSumIdx(5);
        returnMoneyIdx.setReturnMoneyIdx(6);
        returnMoneyIdx.setReturnRateIdx(7);
        crawlReturnMoneyList(info, returnMoneyIdx, MoneyUnit.원);

        return true;
    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        String title = "보험료 크롤링";
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        CrawlingProduct info = (CrawlingProduct) obj[0];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream()
                .filter(t -> t.productGubun.equals(CrawlingTreaty.ProductGubun.주계약))
                .findFirst()
                .get();

        //가입금액이 나이, 성별마다 달라지는 특약
       CrawlingTreaty specialTreaty = info.getTreatyList().stream().filter(t -> t.getAssureMoney() == 0)
                .findFirst()
                .get();

        try {
            //나이, 성별마다 가입금액이 달라지는 특약의 가입금액 읽어오기
            WebElement $treatyNameTh = driver.findElement(By.xpath("//th[normalize-space()='" + specialTreaty.getTreatyName() + "']"));
            WebElement $treatyAssureMoneyTd = $treatyNameTh.findElement(By.xpath("./following-sibling::td[1]"));
            String treatyAssureMoney = $treatyAssureMoneyTd.getText();
            treatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(treatyAssureMoney));


            //특약 계산테이블에 금액 세팅
            PlanCalc planCalc = new PlanCalc();
            planCalc.setMapperId(Integer.parseInt(specialTreaty.mapperId));
            planCalc.setInsAge(Integer.parseInt(info.getAge()));
            planCalc.setGender(info.gender == MALE ? "M" : "F");
            planCalc.setAssureMoney(treatyAssureMoney);
            specialTreaty.setPlanCalc(planCalc);


            //보험료 크롤링 전에는 대기시간을 넉넉히 준다
            WaitUtil.waitFor(5);

            WebElement $premiumP = driver.findElement(By.xpath("//div[@id='step3']//strong[normalize-space()='초회 보험료']/following-sibling::p[@class='txtBox']"));
            String premium = $premiumP.getText();
            premium = String.valueOf(MoneyUtil.toDigitMoney(premium));

            mainTreaty.monthlyPremium = premium;

            if("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
                throw new PremiumCrawlerException(exceptionEnum.getMsg());
            } else {
                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
            }

        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }
}