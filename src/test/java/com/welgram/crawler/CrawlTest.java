package com.welgram.crawler;

import com.welgram.PropertyUtil;
import com.welgram.common.OSValidator;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.helper.SeleniumCrawlingHelper;
import com.welgram.util.Birthday;
import com.welgram.util.InsuranceUtil;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//AIG_DRV_D004 코드 개발
public class CrawlTest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static ChromeOptions options;
    private static SeleniumCrawlingHelper helper;
    public final static Logger logger = LoggerFactory.getLogger(CrawlTest.class);


    public static void main(String[] args) {
        String osName = OSValidator.getOsName();
        String crawlerDir = PropertyUtil.get(osName + "." + "crawler.dir");
        String driverPath = PropertyUtil.get("chrome.driver.path");
        String chromeDriverPath = crawlerDir + driverPath;
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        logger.info("chromeDriverPath :: " + chromeDriverPath);

        Map<String, String> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceName", "iPhone X");
        options = new ChromeOptions();
        options.setExperimentalOption("mobileEmulation", mobileEmulation);

        TreeSet<String> insTermSet = new TreeSet<>();
        TreeSet<String> napTermSet = new TreeSet<>();
        TreeSet<String> planTypeSet = new TreeSet<>();
        TreeMap<String, HashSet<String>> treatyMap = new TreeMap<>();

        long startTime = 0;
        long endTime = 0;

        for (int i = 20; i <= 60; i += 20) {
            Birthday birthday = InsuranceUtil.getBirthday(i);
            String userBirthday = birthday.getYear() + birthday.getMonth() + birthday.getDay();

            try {
                startTime = System.currentTimeMillis();

                driver = new ChromeDriver(options);
                wait = new WebDriverWait(driver, 20);
//                wait = new WebDriverWait(driver, Duration.ofSeconds(20));
                helper = new SeleniumCrawlingHelper(driver, wait);

                //보험사 사이트 접속
                driver.get("https://m.aig.co.kr/wp/dpwpm087c.jsp?prodAlias=digitaldriver&menuId=MS1074&prodCd=L0254");
                waitHomepageLoadingImg();

                //생년월일 세팅
                setTextToInputBox(By.id("birth"), userBirthday);
                System.out.println("생년월일 : " + userBirthday + " 입력");

                ((JavascriptExecutor) driver).executeScript("$('#pdrb01').trigger('click')");
                System.out.println("성별 : 남 클릭");
                waitHomepageLoadingImg();

                helper.waitElementToBeClickable(By.linkText("보험료 계산")).click();
                waitHomepageLoadingImg();

                int size = driver.findElements(By.xpath("//ul[@id='premium__termList']//button")).size();
                for (int j = 1; j <= size; j++) {
                    WebElement element = driver.findElement(By.xpath("(//ul[@id='premium__termList']//button)[" + j + "]"));
                    String targetTerms = element.findElement(By.xpath("./span")).getText();
                    int idx = targetTerms.indexOf("·");
                    String targetNapTerm = targetTerms.substring(0, idx);
                    String targetInsTerm = targetTerms.substring(idx + 1);

                    insTermSet.add(targetInsTerm);
                    napTermSet.add(targetNapTerm);

                    helper.waitElementToBeClickable(element).click();
                    System.out.println(targetTerms + " 버튼 클릭");
                    waitHomepageLoadingImg();

                    List<WebElement> planTypeElements = driver.findElements(By.xpath("//fieldset[@id='premium__guaranteeList']//label"));
                    for (WebElement planTypeElement : planTypeElements) {
                        String targetPlanTypeId = planTypeElement.getAttribute("for");
                        String targetPlanType = ((JavascriptExecutor) driver).executeScript("return $('#" + targetPlanTypeId + "').data('plannm');")
                            .toString();

                        System.out.println(targetPlanType + " 버튼 클릭");
                        helper.waitElementToBeClickable(planTypeElement).click();
                        waitHomepageLoadingImg();

                        planTypeSet.add(targetPlanType);

                        List<WebElement> trList = driver.findElements(By.xpath("//table[@id='premium__result']//tr"));
                        for (WebElement tr : trList) {
                            WebElement targetTreatyButton = tr.findElement(By.xpath(".//button"));
                            String targetTreatyName = tr.findElement(By.xpath("./th//span")).getText();
                            String targetTreatyMoney = tr.findElement(By.xpath("./td//span")).getText();

                            if(targetTreatyButton.isEnabled() && targetTreatyButton.isDisplayed()) {
                                helper.waitElementToBeClickable(targetTreatyButton).click();
                                WaitUtil.waitFor(1);

                                WebElement modalElement = driver.findElement(By.id("details"));
                                WebElement joinAreaElement = modalElement.findElement(By.xpath(".//div[@id='covJoinYnArea']"));

                                List<WebElement> joinBtnList = joinAreaElement.findElements(By.xpath("//div[@id='covJoinYnArea']//span[@class='radioSquareBox__text']/parent::label"));
                                for(WebElement joinBtn : joinBtnList) {
                                    
                                }
                            }











                            //가입하는 특약에 대해서만
                            if (!"-".equals(targetTreatyMoney)) {
                                HashSet<String> targetTreatyMoneySet = new HashSet<>();
                                if (treatyMap.containsKey(targetTreatyName)) {
                                    targetTreatyMoneySet = treatyMap.get(targetTreatyName);
                                    targetTreatyMoneySet.add(targetTreatyMoney);
                                } else {
                                    targetTreatyMoneySet.add(targetTreatyMoney);
                                    treatyMap.put(targetTreatyName, targetTreatyMoneySet);

                                    System.out.println("특약(" + targetTreatyName + ") | 가입금액 : " + targetTreatyMoney + " 추가");
                                }

                            }
                        }
                    }
                }

            } catch (Exception e) {
                logger.info("예외 발생");
                e.printStackTrace();
            } finally {
                driver.close();

                endTime = System.currentTimeMillis();

                System.out.println("========================납입기간 중복제거========================");
                for (String napTerm : napTermSet) {
                    System.out.println(napTerm);
                }
                System.out.println();

                System.out.println("========================보험기간 중복제거========================");
                for (String insTerm : insTermSet) {
                    System.out.println(insTerm);
                }
                System.out.println();

                System.out.println("========================플랜유형 중복제거========================");
                for (String planType : planTypeSet) {
                    System.out.println(planType);
                }
                System.out.println();

                System.out.println("========================특약 중복제거========================");
                for (String treatyName : treatyMap.keySet()) {
                    System.out.println("특약명 : " + treatyName + " | 가입금액 : " + treatyMap.get(treatyName).toString());
                }
                System.out.println();

                long crawlTime = endTime - startTime;
                System.out.println("크롤링 시간 : " + (crawlTime / 1000) + "초");
            }

        }

        driver.quit();
    }


    private static void setTextToInputBox(By id, String text) {
        WebElement element = driver.findElement(id);
        element.clear();
        element.sendKeys(text);
    }

    private static void waitHomepageLoadingImg() throws Exception {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("loadBox")));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading")));
    }
}
