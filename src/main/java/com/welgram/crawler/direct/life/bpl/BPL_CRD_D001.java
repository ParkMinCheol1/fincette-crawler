package com.welgram.crawler.direct.life.bpl;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.Crawler;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.cli.CrawlerCommand;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import picocli.CommandLine;



public class BPL_CRD_D001 extends SeleniumCrawler {

    public static void main(String[] args) {
        executeCommand(new BPL_CRD_D001(), args);
    }



    protected static void executeCommand(Crawler crawler, String[] args) {

        int exitCode = new CommandLine(new CrawlerCommand(crawler)).execute(args);
        System.exit(exitCode);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        if (helper.isAlertShowed()) {
            Alert alert = driver.switchTo().alert();
            String alertMessage = alert.getText();
            logger.debug(alertMessage);
            alert.accept();
        }

//        WaitUtil.waitFor(3);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#info2")));

        driver.findElement(By.cssSelector("#info2")).click();
        //span[contains(.,'유형 선택하고 내 보험료 확인하기')]
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(.,'유형 선택하고 내 보험료 확인하기')]")));
        driver.findElement(By.xpath("//a[contains(.,'유형 선택하고 내 보험료 확인하기')]")).click();

        // 상품선택(대출안심보장보험(선택부가형)

        logger.info("상품선택 : " + info.productName);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//strong[contains(.,'" + info.productName + "')]"))).click();
//        driver.findElement(By.xpath("//strong[contains(.,'" + info.productName + "')]")).click();
        // 5 | click | css=.btn_outclose |

        WaitUtil.waitFor(2);
//        driver.findElement(By.cssSelector(".btn_outclose")).click();
//
//        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("#loader-3")));

        if (driver.findElement(By.cssSelector(".btn_outclose")).isDisplayed()) {
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn_outclose"))).click();
        }

        logger.info("생년월일 : " + info.fullBirth);
        driver.findElement(By.cssSelector("input[formcontrolname='inpBirthDate']")).click();
        driver.findElement(By.cssSelector("input[formcontrolname='inpBirthDate']")).sendKeys(info.fullBirth);

        WaitUtil.waitFor(1);
        // 8 | click | linkText=남 |
        String genderName = info.gender == 0 ? "남자" : "여자";

        logger.info("성별선택 : " + genderName);

        driver.findElement(By.xpath("//label[contains(.,'" + genderName + "')]")).click();

        logger.info("보험료 계산하기");
        driver.findElement(By.xpath("//button[contains(.,'" + "보험료 계산하기" + "')]")).click();

        loading();

        // 대출금액(보장금액) 설정
//        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#wrap > form > section.result_view.ng-tns-c77-3 > div > div > span")))
//            .click();
//        driver.findElement(By.cssSelector("#wrap > form > section.result_view.ng-tns-c77-3 > div > div > span")).click();

        int _assureMoney = Integer.valueOf(info.treatyList.get(0).assureMoney);
        logger.info("대출금액(보장금액): {}", _assureMoney + "원");
        driver.findElement(By.xpath("//input[@type='tel']")).clear();
        driver.findElement(By.xpath("//input[@type='tel']")).sendKeys(_assureMoney + "");

//        driver.findElement(By.xpath("//button[contains(.,'적용하기')]")).click();

        loading();
        WaitUtil.waitFor(3);

        loading();

        WaitUtil.waitFor(3);
        {
            String _insTerm = info.insTerm.replaceAll("[^0-9]", "");
            _insTerm = _insTerm.length() == 1 ? "0" + _insTerm : _insTerm;
            logger.debug("보험기간: {}", info.insTerm);
            WebElement selectElement = driver.findElement(By.cssSelector("select[formcontrolname='insrPrdTypVal']"));
            selectElement.click();
            selectElement.findElement(
                    By.xpath("option[. = '" + _insTerm + "']")).
                click();
            //*[@id="wrap"]/form/section[2]/div/div[1]/dl[3]/dd/select/option[.='30']
//            WebElement dropdown = driver.findElement(By.cssSelector(".ng-untouched"));
        }

        {
            String _napTerm = info.napTerm.replaceAll("[^0-9]", "");
            _napTerm = _napTerm.length() == 1 ? "0" + _napTerm : _napTerm;
            logger.debug("납입기간: {}", info.napTerm);
            WebElement selectElement = driver.findElement(By.cssSelector("select[formcontrolname='payPrdTypVal']"));
            selectElement.click();
            selectElement.findElement(By.xpath("option[. = '" + _napTerm + "']")).click();

        }

        logger.info("서브플랜명 : " + info.planSubName);
        if ("실속보장형".equals(info.planSubName)) {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'" + "실속보장형" + "')]"))).click();
        } else if ("암보장형".equals(info.planSubName)) {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'" + "암보장형" + "')]"))).click();
        } else {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'" + "든든보장형" + "')]"))).click();
        }

        {
            logger.info("보험료 재계산하기");
            driver.findElement(By.xpath("//a[contains(.,'보험료 재계산')]")).click();

            loading();

            // 보험료조회
            logger.debug("보험료조회");

            String _premium = driver.findElement(
                    By.cssSelector("#boxCalculate2 > div > ul:nth-child(1) > li > dl > dd > div:nth-child(2) > p > span"))
                .getText();
            logger.debug("보험료: {}", _premium);

            _premium = MoneyUtil.toDigitMoney(_premium).toString();

            // 보험료 세팅
            info.treatyList.get(0).monthlyPremium = _premium;

            // 스크린샷
            takeScreenShot(info);

        }

        {
            WaitUtil.waitFor(3);
            logger.info("보험료/해약환급금 조회");

            {
                WebElement element = driver.findElement(By.xpath("//button[contains(.,'해약환급금 예시')]"));
                Actions builder = new Actions(driver);
                builder.moveToElement(element).perform();

                element.sendKeys(Keys.ENTER);

//                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'해약환급금 예시')]"))).click();
            }

//            driver.findElement(By.xpath("//button[contains(.,'해약환급금 예시')]")).click();

//            trElements=driver.findElement(By.xpath("//div[@id='wrap']/form/section[3]/div/div[2]/div/table/tbody/tr[1]/td[2]")).getText();
            List<WebElement> trElements = driver.findElements(By.xpath("//*[@id='info-tab3-3-2']/div/table/tbody/tr"));

            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

            for (WebElement trElement : trElements) {
                String term = trElement.findElement(By.xpath("./td[1]")).getAttribute("innerText");
                String premiumSum = trElement.findElement(By.xpath("./td[2]")).getAttribute("innerText").replaceAll("[^0-9]", "");
                String returnMoney = trElement.findElement(By.xpath("./td[3]")).getAttribute("innerText").replaceAll("[^0-9]", "");
                String returnRate = trElement.findElement(By.xpath("./td[4]")).getAttribute("innerText").replaceAll("%", "");

                logger.debug("납입기간: {},  납입보험료: {}, 해약환급금:{}, 해약환급률:{}", term, premiumSum, returnMoney, returnRate);

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                // logger.info(term + " :: " + premiumSum);
                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                planReturnMoneyList.add(planReturnMoney);

                // todo | 반드시 수정필요
                info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);

            logger.debug("trElements: {}", trElements.size());

//            driver.findElement(By.xpath("//div[@id='wrap']/form/section[3]/div/div[2]/div/table/tbody/tr/td")).click();
        }

        return true;
    }



    private void loading() {

//        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("#loader-3")));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("#loder_wrap")));
//        <div _ngcontent-wpw-c54="" id="loder_wrap" class="ng-star-inserted">...</div>
    }
}
