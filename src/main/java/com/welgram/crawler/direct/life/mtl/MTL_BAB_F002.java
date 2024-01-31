package com.welgram.crawler.direct.life.mtl;

import com.welgram.common.InsuranceUtil;
import com.welgram.common.ReturnMoneyIdx;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanCalc;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MTL_BAB_F002 extends CrawlingMTLAnnounce {

    public static void main(String[] args) {
        executeCommand(new MTL_BAB_F002(), args);
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
                .filter(t -> t.productGubun == CrawlingTreaty.ProductGubun.선택특약 && t.getAssureMoney() != 0)
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


    public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {

        try {

            if(welgramTreatyList.size() > 0){
                WebElement $treatyTbody = driver.findElement(By.id("compRider"));

                logger.info("가입설계 특약을 바탕으로 원수사에 세팅하기");

                for(CrawlingTreaty welgramTreaty : welgramTreatyList) {
                    String treatyName = welgramTreaty.treatyName;
                    if(!("만기환급금".equals(treatyName))){

                        //원수사에서 해당 특약 tr 얻어오기
                        WebElement $treatyNameTd = $treatyTbody.findElement(By.xpath(".//td[normalize-space()='" + treatyName + "']"));
                        WebElement $treatyTr = $treatyNameTd.findElement(By.xpath("./parent::tr"));

                        //tr에 가입설계 특약정보 세팅하기
                        setTreatyInfoFromTr($treatyTr, welgramTreaty);
                    }
                }

                logger.info("실제 원수사에 가입 체크된 특약 정보 읽어오기");
                List<WebElement> $treatyTrList = $treatyTbody.findElements(By.tagName("tr"));
                List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
                for(WebElement $treatyTr : $treatyTrList) {

                    //tr로부터 특약정보 읽어오기
                    CrawlingTreaty targetTreaty = getTreatyInfoFromTr($treatyTr);

                    if(targetTreaty != null) {
                        targetTreatyList.add(targetTreaty);
                    }

                }

                logger.info("원수사 특약 정보 vs 가입설계 특약 정보 비교");
                boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy2());
                if(result) {
                    logger.info("특약 정보 모두 일치");
                } else {
                    logger.info("특약 정보 불일치");
                    throw new Exception();
                }
            } else {
                logger.info("가입설계에 선택특약이 없습니다.");
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
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

        // 연령,성별별로 가입금액이 달라지는 특약
        CrawlingTreaty specialTreaty = info.getTreatyList().stream()
                .filter(t -> t.getAssureMoney() == 0)
                .findFirst()
                .get();

        try {
            //보험료 크롤링 전에는 대기시간을 넉넉히 준다
            WaitUtil.waitFor(5);

            //주계약 보험료 세팅
            WebElement $p = driver.findElement(By.cssSelector("#step3 > div:nth-child(9) > div:nth-child(2) > p"));
            String premium = $p.getText();

            int idx = premium.indexOf("출생후");
            String beforePremium = premium.substring(0, idx);
            String afterPremium = premium.substring(idx);
            beforePremium = beforePremium.replaceAll("[^0-9]", "");
            afterPremium = afterPremium.replaceAll("[^0-9]", "");

            //가입금액이 매번 달라지는 특약의 가입금액 세팅하기
            WebElement $strong = driver.findElement(By.xpath("//strong[normalize-space()='총 보험료 합계']"));
            $p = $strong.findElement(By.xpath("./following-sibling::p[1]"));
            premium = $p.getText();
            int rmIdx = premium.indexOf("(");
            String specialPremium = premium.substring(0, rmIdx);
            specialPremium = specialPremium.replaceAll("[^0-9]", "");


            //TND2 만기환급금(납입한 보험료(20년납입한보험료)의 50%)
            specialPremium = String.valueOf(Integer.parseInt(specialPremium) * 12 * 20 / 2);

            PlanCalc planCalc = new PlanCalc();
            planCalc.setMapperId(Integer.parseInt(specialTreaty.mapperId));
            planCalc.setInsAge(Integer.parseInt(info.getAge()));
            planCalc.setGender(info.gender == MALE ? "M" : "F");
            planCalc.setAssureMoney(specialPremium);

            specialTreaty.setPlanCalc(planCalc);

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