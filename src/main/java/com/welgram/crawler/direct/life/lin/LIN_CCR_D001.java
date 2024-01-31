package com.welgram.crawler.direct.life.lin;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanCalc;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

public class LIN_CCR_D001 extends CrawlingLINDirect { // 무배당 9900 ONE암보험

    public static void main(String[] args) {
        executeCommand(new LIN_CCR_D001(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        webCrawling(info);
        return true;
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setImageLoad(true);
    }


    // 사이트웹
    // https://direct.lina.co.kr/product/dtc006
    private void webCrawling(CrawlingProduct info) throws Exception {

        String genderOpt = (info.getGender() == MALE) ? "남자" : "여자";
        By by = null;

        logger.info("LIN_CCR_D001 :: {}", info.getProductName());
        WaitUtil.waitFor(1);

        logger.info("생년월일 :: {}", info.getBirth());
        setBirthday(By.cssSelector(".el-input__inner"), info.getBirth());

        logger.info("성별 :: {}", genderOpt);
        setGender(By.xpath("//div[@class='inner-sm']//span[text()='" + genderOpt + "']"), genderOpt);

        logger.info("내 보장금액 확인하기 버튼 클릭 ");
        btnClick(By.xpath("//span[contains(.,'보험료 확인하고 가입하기')]"), 2);
        waitLoadingImg();

        logger.info("월 보험료");
        crawlPremium(By.xpath("//div[@class='content-footer']//span[contains(.,'바로 가입하기')]"), info);
        WaitUtil.waitFor(2);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("가입금액변동 특약 데이터 쌓기");
        // 보장내역 버튼 클릭
        btnClick(By.xpath("//div[@class='price-info-box']//*[normalize-space()='보장내용']"), 2);
        // 특약별 가입금액 메소드
        setVariableTreatyAssureMoney(info);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0, -200);");

        logger.info("해약환급금 버튼 클릭");
        by = By.xpath("//div[@class='price-info-box']//span[normalize-space()='해약환급금']");
        helper.waitElementToBeClickable(by);
        btnClick(by, 2);

        logger.info("해약환급금 조회");
        crawlReturnMoneyList(info, By.cssSelector("div.el-dialog__body > div.l-pop > div > div:nth-child(2) > div.l-table.mt12 > div > div.el-table__body-wrapper.is-scrolling-none > table > tbody > tr"));
    }


    protected void setVariableTreatyAssureMoney(CrawlingProduct info) throws CommonCrawlerException {

        List<CrawlingTreaty> specialTreatyList
            = info.getTreatyList().stream()
            .filter(t -> t.getAssureMoney() == 0)
            .collect(Collectors.toList());

        try{

            for (CrawlingTreaty specialTreaty : specialTreatyList) {

                String treatyName = specialTreaty.getTreatyName();
                WebElement $th = driver.findElement(By.xpath("//*[@class=\"l-table mt12\"]//table//p[contains(.,'"+ treatyName + "')]"));
                WebElement $tr = $th.findElement(By.xpath("./ancestor::tr"));
                WebElement $assureMoneyTd = $tr.findElement((By.xpath("./td[3]")));


                String targetTreatyName = $th.getText().trim().replaceAll("(\r\n|\r|\n|\n\r)", " ");
                String targetTreatyMoney = String.valueOf(MoneyUtil.toDigitMoney2($assureMoneyTd.getText().trim()));

                logger.info("===================================");
                logger.info("특약명 :: {}", targetTreatyName);
                logger.info("가입금액 :: {}", targetTreatyMoney);
                logger.info("===================================");


                //특약 계산테이블에 달라지는 가입금액 세팅
                PlanCalc planCalc = new PlanCalc();
                planCalc.setMapperId(Integer.parseInt(specialTreaty.mapperId));
                planCalc.setInsAge(Integer.parseInt(info.getAge()));
                planCalc.setGender(info.gender == MALE ? "M" : "F");
                planCalc.setAssureMoney(targetTreatyMoney);
                specialTreaty.setPlanCalc(planCalc);
            }

            // 확인 버튼
            driver.findElement(By.xpath("//div[@class='footer']//span[contains(.,'확인')]")).click();
            WaitUtil.waitFor(2);

            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollBy(0, 300);");

        } catch (Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.ERROR_BY_TREATIES_GETTING + "\n" + e.getMessage());
        }
    }



}
