package com.welgram.crawler.direct.life.mtl;

import com.welgram.common.InsuranceUtil;
import com.welgram.common.ReturnMoneyIdx;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class MTL_BAB_F001 extends CrawlingMTLAnnounce {

    public static void main(String[] args) {
        executeCommand(new MTL_BAB_F001(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        WebElement $element = null;
        WebElement $check = null;

        driver.manage().window().maximize();

        logger.info("계약자 생년월일 설정(주민번호 앞자리)");
        String parentBirthday = InsuranceUtil.getBirthday(30).substring(2);
        setBirthday(parentBirthday, By.id("contract_jumin1"));

        logger.info("성별 설정(주민번호 뒷자리)");
        logger.info("부모의 경우 여성만 가입가능하므로 여성 선택");
        WebElement parentGenderLocation = driver.findElement(By.xpath("//input[@name='contract_jumin2']"));
        helper.sendKeys4_check(parentGenderLocation, "2");

        logger.info("태아 여부 체크");
        $check = driver.findElement(By.xpath("//input[@name='fetusYN']"));
        click($check);

        logger.info("가입자녀 생년월일 설정(주민번호 뒷자리)");
        setGender(info.getGender(), info.getFullBirth(), By.xpath("//input[@name='jumin2']"));

        logger.info("다음 버튼 클릭");
        $element = driver.findElement(By.linkText("다음"));
        click($element);

        logger.info("주계약 상품 입력");
        setMainTreatyInfo(info);

        logger.info("임신주수 기간 : 12주");
        By location = By.id("pgncWcnt");
        setDueDate(location, "12");

        logger.info("특약 상품");
        List<CrawlingTreaty> subTreatyList = info.getTreatyList().stream()
                .filter(t -> t.productGubun == CrawlingTreaty.ProductGubun.선택특약)
                .collect(Collectors.toList());
        setTreaties(subTreatyList);

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
        returnMoneyIdx.setPremiumSumIdx(1);
        returnMoneyIdx.setReturnMoneyIdx(2);
        returnMoneyIdx.setReturnRateIdx(3);
        crawlReturnMoneyList(info, returnMoneyIdx, MoneyUnit.만원);

        return true;

    }


    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        String title = "보험료 크롤링";

        CrawlingProduct info = (CrawlingProduct) obj[0];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream()
                .filter(t -> t.productGubun.equals(CrawlingTreaty.ProductGubun.주계약))
                .findFirst()
                .get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {
            //보험료 크롤링 전에는 대기시간을 넉넉히 준다
            WaitUtil.waitFor(5);

            WebElement $p = driver.findElement(By.cssSelector("#step3 > div:nth-child(9) > div:nth-child(2) > p"));
            String premium = $p.getText();

            int idx = premium.indexOf("출생후");
            String beforePremium = premium.substring(0, idx);
            String afterPremium = premium.substring(idx);
            beforePremium = beforePremium.replaceAll("[^0-9]", "");
            afterPremium = afterPremium.replaceAll("[^0-9]", "");

            mainTreaty.monthlyPremium = beforePremium;
            info.nextMoney = afterPremium;

            logger.info("출생후 보험료 : {}원", info.nextMoney);


            if("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
                throw new PremiumCrawlerException(exceptionEnum.getMsg());
            } else {
                logger.info("출생전 보험료 : {}원", mainTreaty.monthlyPremium);
            }

        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }


}