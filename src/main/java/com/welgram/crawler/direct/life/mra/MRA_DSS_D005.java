package com.welgram.crawler.direct.life.mra;


import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class MRA_DSS_D005 extends CrawlingMRADirect {

    public static void main(String[] args) {
        executeCommand(new MRA_DSS_D005(), args);
    }
    // 온라인 뇌심보장보험 무배당
    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $button = null;

        driver.manage().window().maximize();
        waitLoadingBar();

        logger.info("[step1] 사용자 정보 입력");
        setUserInfo(info);

        logger.info("가입조건변경 버튼 클릭");
        $button = driver.findElement(By.id("chgterm"));
        click($button);

        logger.info("주계약 선택 설정");
        int idx = info.getTextType().indexOf("#");
        String type = info.getTextType().substring(0, idx);
        setPlan(type);

        logger.info("상품유형 설정");
        idx = info.getTextType().indexOf("#") + 1;
        type = info.getTextType().substring(idx).trim();
        setProductType(type);

        logger.info("납입유형 설정");
        setNapCycle(info.getNapCycleName());

        logger.info("보험기간 설정");
        String insTerm = info.getInsTerm() + " 만기";
        setInsTerm(insTerm);

        logger.info("납입기간 설정");
        String napTerm = info.getNapTerm();
        napTerm = info.getInsTerm().equals(napTerm) ?  "전기납" : napTerm;
        napTerm = napTerm.contains("납") ? napTerm : napTerm + "납";
        setNapTerm(napTerm);

        logger.info("적용 버튼 클릭");
        $button = driver.findElement(By.id("modalCalcBtn"));
        click($button);

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info, MoneyUnit.원);

        return true;
    }

    public void setPlan(String expectedPlan) throws CommonCrawlerException {
        String title = "주계약 선택";
        String actualPlan = "";

        try {
            //주계약 선택 관련 element 찾기
            WebElement $planH2 = driver.findElement(By.xpath("//h2[normalize-space()='" + title + "']"));
            WebElement $planDiv = $planH2.findElement(By.xpath("./following-sibling::div[1]"));
            WebElement $planLabel = $planDiv.findElement(By.xpath(".//label[normalize-space()='" + expectedPlan + "']"));
            click($planLabel);

            //실제 클릭된 주계약 선택 읽어오기
            WebElement $planInput = $planDiv.findElement(By.tagName("input"));
            String name = $planInput.getAttribute("name");
            String script = "return $('input[name=" + name + "]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $planLabel = $planDiv.findElement(By.xpath("./label[@for='" + id + "']"));
            actualPlan = $planLabel.getText().trim();

            //비교
            super.printLogAndCompare(title, expectedPlan, actualPlan);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }
}