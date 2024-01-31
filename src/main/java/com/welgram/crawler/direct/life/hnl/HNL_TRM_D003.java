package com.welgram.crawler.direct.life.hnl;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class HNL_TRM_D003 extends CrawlingHNLMobile {

    public static void main(String[] args) {
        executeCommand(new HNL_TRM_D003(), args);
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

        logger.info("보장받을 금액(=가입금액) 설정");
        setAssureMoney(info.getAssureMoney());

        logger.info("보장받을 기간(=보험기간) 설정");
        setInsTerm(info.getInsTerm());

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("계약을 해지하면 돌려받는 금액(=해약환급금) 버튼 클릭");
        $button = driver.findElement(By.id("surrBtn"));
        helper.moveToElementByJavascriptExecutor($button);
        click($button);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info);

        return true;
    }

    @Override
    public void modalCheck() throws Exception {
        logger.info("모달창이 떴는지를 확인합니다.");

        boolean isDomExist = false;
        WebElement $button = null;
        By modalPosition = By.xpath("//article[@id='eventPopup']");

        isDomExist = helper.existElement(modalPosition);
        if(isDomExist) {
            WebElement $modal = driver.findElement(modalPosition);

            if($modal.isDisplayed()) {
                logger.info("안내 모달창이 떴습니다~~");
                $button = $modal.findElement(By.xpath(".//button[text()='닫기']"));
                helper.waitElementToBeClickable($button).click();
            }
        }
    }

    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {
        String title = "보장받을 금액(=가입금액)";

        String assureMoney = (String) obj[0];
        String expectedAssureMoney = assureMoney;
        String actualAssureMoney = "";

        try {
            int unit = 10000;
            expectedAssureMoney = String.valueOf(Integer.parseInt(assureMoney) / unit);

            //금액을 선택하기 위해 버튼 클릭
            WebElement $button = driver.findElement(By.id("protection_money"));
            click($button);

            //가입금액 설정
            WebElement $assureMoneyArticle = driver.findElement(By.id("promoney"));
            WebElement $assureMoneyDiv = $assureMoneyArticle.findElement(By.xpath(".//div[@class='popup_content']"));
            WebElement $assureMoneyLabel = $assureMoneyDiv.findElement(By.xpath(".//label[@for='" + expectedAssureMoney + "']"));
            click($assureMoneyLabel);

            //실제 선택된 가입금액 읽어오기
            String script = "return $('input[name=sel_radio]:checked').val()";
            actualAssureMoney = String.valueOf(helper.executeJavascript(script));

            //가입금액 단위 맞춰주기
            expectedAssureMoney = assureMoney;
            actualAssureMoney = String.valueOf(Integer.parseInt(actualAssureMoney) * unit);

            //비교
            super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);

            logger.info("가입금액 선택 후 확인 버튼 클릭");
            $button = $assureMoneyArticle.findElement(By.xpath(".//button[text()='확인']"));
            click($button);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
        }
    }


    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        String title = "보장받을 기간(=보험기간)";

        String expectedInsTerm = (String) obj[0];
        String actualInsTerm = "";

        try {

            //보험기간을 선택하기 위해 버튼 클릭
            WebElement $button = driver.findElement(By.id("protection_period"));
            click($button);

            //보험기간 설정
            WebElement $insTermArticle = driver.findElement(By.id("protime"));
            WebElement $insTermDiv = $insTermArticle.findElement(By.xpath(".//div[@class='popup_content']"));
            WebElement $insTermLabel = $insTermDiv.findElement(By.xpath(".//label[text()='" + expectedInsTerm + "']"));
            click($insTermLabel);

            //실제 선택된 보험기간 읽어오기
            String script = "return $('input[name=sel_radio_time]:checked').attr('id')";
            String actualInsTermId = String.valueOf(helper.executeJavascript(script));
            actualInsTerm = driver.findElement(By.xpath("//label[@for='" + actualInsTermId + "']")).getText();

            //비교
            super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);

            logger.info("보험기간 선택 후 확인 버튼 클릭");
            $button = $insTermArticle.findElement(By.xpath(".//button[text()='확인']"));
            click($button);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        String title = "보험료 크롤링";

        CrawlingProduct info = (CrawlingProduct) obj[0];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(CrawlingTreaty.ProductGubun.주계약)).findFirst().get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {

            //보험료를 크롤링 하기 전에는 충분한 대기시간을 갖는다.
            WaitUtil.waitFor(3);

            //보험료 크롤링
            String premium = driver.findElement(By.id("premData")).getText();
            premium = String.valueOf(MoneyUtil.toDigitMoney(premium));

            //주계약 보험료 세팅
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
