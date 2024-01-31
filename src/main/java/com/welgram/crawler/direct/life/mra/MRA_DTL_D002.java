package com.welgram.crawler.direct.life.mra;


import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetProductTypeException;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class MRA_DTL_D002 extends CrawlingMRADirect {

    public static void main(String[] args) {
        executeCommand(new MRA_DTL_D002(), args);
    }

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

        logger.info("비흡연 할인여부 설정");
        int idx = info.getTextType().indexOf(",");
        String type = info.getTextType().substring(0, idx);
        setSmokeDiscount(type);

        logger.info("주보험 유형 설정");
        idx = info.getTextType().indexOf(",") + 1;
        type = info.getTextType().substring(idx).trim();
        setPlan(type);

        logger.info("보험기간 설정");
        String insTerm = info.getInsTerm() + " 만기";
        setInsTerm(insTerm);

        logger.info("납입기간 설정");
        String napTerm = info.getNapTerm();
        napTerm = info.getInsTerm().equals(napTerm) ?  "전기납" : napTerm;
        napTerm = napTerm.contains("납") ? napTerm : napTerm + "납";
        napTerm = napTerm + "(" + info.getNapCycleName() + ")";
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
        String title = "주보험 유형";
        String actualPlan = "";

        try {
            //주보험 유형 관련 element 찾기
            WebElement $planH2 = driver.findElement(By.xpath("//h2[normalize-space()='" + title + "']"));
            WebElement $planDiv = $planH2.findElement(By.xpath("./following-sibling::div[1]"));
            WebElement $planLabel = $planDiv.findElement(By.xpath(".//label[normalize-space()='" + expectedPlan + "']"));
            click($planLabel);

            //실제 클릭된 주보험 유형 읽어오기
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
            throw new SetProductTypeException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setSmokeDiscount(String expectedDiscount) throws CommonCrawlerException {
        String title = "비흡연 할인여부";
        String actualDiscount = "";

        try {
            //비흡연 할인여부 관련 element 찾기
            WebElement $discountH2 = driver.findElement(By.xpath("//h2[normalize-space()='" + title + "']"));
            WebElement $discountDiv = $discountH2.findElement(By.xpath("./following-sibling::div[1]"));
            WebElement $discountLabel = $discountDiv.findElement(By.xpath(".//label[normalize-space()='" + expectedDiscount + "']"));
            click($discountLabel);

            //실제 클릭된 비흡연 할인여부 읽어오기
            WebElement $discountInput = $discountDiv.findElement(By.tagName("input"));
            String name = $discountInput.getAttribute("name");
            String script = "return $('input[name=" + name + "]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $discountLabel = $discountDiv.findElement(By.xpath("./label[@for='" + id + "']"));
            actualDiscount = $discountLabel.getText().trim();

            //비교
            super.printLogAndCompare(title, expectedDiscount, actualDiscount);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SMOKE;
            throw new SetProductTypeException(e.getCause(), exceptionEnum.getMsg());
        }
    }
}
