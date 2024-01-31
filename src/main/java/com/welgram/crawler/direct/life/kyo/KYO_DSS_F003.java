package com.welgram.crawler.direct.life.kyo;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingKYO;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;


// 2023.04.26               | 최우진               | 대면_질병
// KYO_DSS_F003             | (무)교보간편해요건강보험
public class KYO_DSS_F003 extends CrawlingKYO {

    public static void main(String[] args) { executeCommand(new KYO_DSS_F003(), args); }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("START :: {}", info.getProductNamePublic());

        logger.info("공시실 '종신/정기' 탭 선택");
        element = driver.findElement(By.linkText("건강/암"));
        waitElementToBeClickable(element).click();
        WaitUtil.waitFor(2);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));
        WaitUtil.loading(1);

        logger.info("플랜선택");
        elements = driver.findElements(By.cssSelector("#prodList > tr"));

        for(int i = 0; i < elements.size(); i++) {
            int y = 40 * i;
            ((JavascriptExecutor) driver).executeScript("scroll(0," + y + ");");
            String siteProductName = elements.get(i).findElement(By.className("txt-l")).getText();

            if(siteProductName.trim().equals(info.getProductName())) {        // 이름주의
                logger.info("페이지의 상품명 확인 : " + siteProductName);
                elements.get(i).findElement(By.tagName("button")).click();;
                break;
            }
        }

        WaitUtil.loading(1);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));

        // 생년월일
        logger.info("생년월일 입력");
        WaitUtil.loading(2);

        element = helper.waitElementToBeClickable(By.cssSelector("#userInfoType1 > span"));
        element.click();
        element.findElement(By.tagName("input")).sendKeys(info.fullBirth);

        WaitUtil.loading(1);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));

        // 성별
        logger.info("성별선택");
        if(info.getGender() == 0) {
            driver.findElement(By.xpath("//*[@id='userInfoType1']/label[1]")).click();

        } else {
            driver.findElement(By.xpath("//*[@id='userInfoType1']/label[2]")).click();
        }

        WaitUtil.loading(1);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));

        for (CrawlingTreaty item : info.treatyList) {
            if(item.productGubun.equals(ProductGubun.주계약)) {

                // 보험종류선택
                String planName = item.treatyName;
                Select selectPlan = new Select(driver.findElement(By.id("sel_gdcl")));
                logger.info("1");
                selectPlan.selectByVisibleText(planName);
                logger.info("2");
                WaitUtil.loading(1);
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));

                logger.info("주보험 보험기간 세팅");
                element = driver.findElement(By.cssSelector("#show_isPd"));
                Select selectInsTerm = new Select(element.findElement(By.tagName("select")));
                selectInsTerm.selectByVisibleText(item.getInsTerm() + "만기");

                WaitUtil.loading(1);
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));

                logger.info("주보험 납입기간 세팅");
                element = driver.findElement(By.cssSelector("#show_paPd"));

                // 보기 납기가 같은경우 납입기간 처리
                if(item.insTerm.equals(item.napTerm)) {
                    item.napTerm = "전기납";
                }
                Select selectNapTerm = new Select(element.findElement(By.tagName("select")));
                selectNapTerm.selectByVisibleText(item.napTerm+"납");
                WaitUtil.loading(1);
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));

                logger.info("가입금액 세팅");
                element = driver.findElement(By.cssSelector("#sbcAmtView"));
                element.click();

                element = element.findElement(By.tagName("input"));
                element.sendKeys(Keys.DELETE);
                element.sendKeys(Keys.DELETE);
                element.sendKeys(Keys.DELETE);
                element.sendKeys(Keys.DELETE);
                element.sendKeys(Keys.DELETE);
                element.sendKeys(item.assureMoney / 10000 + "");
            }
        }

        WaitUtil.loading(1);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));

        // 보장 설정 - 선택특약에 대해서만
        List<CrawlingTreaty> treatyList = info.getTreatyList();
        for(CrawlingTreaty treaty : treatyList) {
            if(treaty.productGubun.equals(CrawlingTreaty.ProductGubun.선택특약)) {
                logger.info("보장명 :: {}", treaty.getTreatyName());
                WebElement $trTreaty = driver.findElement(By.xpath("//span[text()='" + treaty.getTreatyName() + "']/parent::label/parent::td/parent::tr"));

                // 보장선택
                element = $trTreaty.findElement(By.xpath("./td[1]//input"));
                element.click();

                // 보장 - 보험기간
                Select $selInsTerm = new Select($trTreaty.findElement(By.xpath("./td[2]//select")));
                $selInsTerm.selectByVisibleText(info.getInsTerm() + "만기");

                // 보장 - 납입기간
                Select $selNapTerm = new Select($trTreaty.findElement(By.xpath("./td[3]//select")));
                $selNapTerm.selectByVisibleText(info.getNapTerm() + "납");

                // 보장 - 가입금액
                String eachAssAmt = String.valueOf(treaty.getAssureMoney() / 1_0000);
                logger.info("가입 금액 내용 :: {}", treaty.getAssureMoney());
                logger.info("가입 금액 설정 :: {}", eachAssAmt);
                element = $trTreaty.findElement(By.xpath("./td[4]//input"));
                element.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
                element.sendKeys(eachAssAmt);
            }
        }

        //보험료계산 버튼누르기
        logger.info("보험료계산버튼");
        helper.click(driver.findElement(By.cssSelector("#pop-calc > div > div.pbt > div > button")));

        WaitUtil.loading(1);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));

        //보험료 가져오기
        logger.info("보험료 가져오기");
        //this.getPremiums(info);
        String premium = driver.findElement(By.xpath("//*[@id='totPrmTx']/strong")).getText().replaceAll("[^0-9]", "");
//        element = driver.findElement(By.cssSelector("#show_pdtPrm > span > input:nth-child(3)"));

        logger.info("월 보험료 스크랩 :: " + premium);
        info.treatyList.get(0).monthlyPremium = premium;

        //스크린샷
        takeScreenShot(info);

        logger.info("보장내용클릭");

        driver.findElement(By.cssSelector("#areaPrm > div.btn-set.mt20 > button.btn.b.md")).click();
        WaitUtil.loading(1);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));

        logger.info("해약환급금 탭 클릭 ");
        driver.findElement(By.cssSelector("#oPopHisMenu > li:nth-child(2) > a")).click();
        WaitUtil.loading(1);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));

//        List<WebElement> $trList = driver.findElements(By.xpath("//div[@class='ut-tbl a mt16']//table/tbody/tr"));
        List<WebElement> $trList = driver.findElements(By.xpath("//*[@id='trmRview']/div[2]/table/tbody/tr"));
        List<PlanReturnMoney> pl = new ArrayList<>();
        for(WebElement $tr : $trList) {
            String term = $tr.findElement(By.xpath("./td[1]"))
                .getText();
            String premiumSum = $tr.findElement(By.xpath("./td[2]"))
                .getText()
                .replaceAll("[^0-9]", "");
            String returnMoney = $tr.findElement(By.xpath("./td[3]"))
                .getText()
                .replaceAll("[^0-9]", "");
            String returnRate = $tr.findElement(By.xpath("./td[4]"))
                .getText();

            logger.info("경과기간 : {}", term);
            logger.info("납입보험료 : {}", premiumSum);
            logger.info("공시환급금 : {}", returnMoney);
            logger.info("공시환급률 : {}", returnRate);
            logger.info("==========================================");

            PlanReturnMoney p = new PlanReturnMoney();
            p.setTerm(term);
            p.setPremiumSum(premiumSum);
            p.setReturnMoney(returnMoney);
            p.setReturnRate(returnRate);

            // 환급정보
            pl.add(p);

            // 만기환급금 세팅
            info.setReturnPremium(returnMoney);
        }

        info.setPlanReturnMoneyList(pl);

        logger.info("만기환급금 : {}", info.returnPremium);
        logger.info("==========================================");

        return true;
    }
}
