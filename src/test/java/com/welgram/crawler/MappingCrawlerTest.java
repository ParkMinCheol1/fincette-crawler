package com.welgram.crawler;

import com.welgram.PropertyUtil;
import com.welgram.common.OSValidator;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.helper.SeleniumCrawlingHelper;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class MappingCrawler {
    public final static Logger logger = LoggerFactory.getLogger(MappingCrawler.class);
    private WebDriver driver;
    private WebDriverWait wait;
    private SeleniumCrawlingHelper helper;
    private HashMap<String, String> tndMap = new HashMap<>();                //분석쪽 TND정보(key : tnd코드, value : 특약명)
    private List<String> toMapTreatyList = new ArrayList<>();               //매핑되지 않은 내 특약리스트

    public MappingCrawler() {
        setDriverInfo();

        driver = new ChromeDriver();
//        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait = new WebDriverWait(driver, 20);
        helper = new SeleniumCrawlingHelper(driver, wait);
    }

    public boolean execute(String[] productList) {
        boolean result = false;

        //보험사 이름을 키값으로 상품들을 담는다.
        HashMap<String, HashSet<String>> productMap = new HashMap<>();
        for(String product : productList) {
            String company = product.substring(0, 3);

            if(productMap.containsKey(company)) {
                productMap.get(company).add(product);
            } else {
                HashSet<String> productSet = new HashSet<>();
                productSet.add(product);
                productMap.put(company, productSet);
            }
        }


        try {
            //관리자페이지 접속
            driver.get("https://nuzal.kr/mngr/login");
            login();

            driver.findElement(By.linkText("P-보험상품관리")).click();
            WaitUtil.waitFor(1);

            driver.findElement(By.linkText("상품마스타특약관리")).click();
            WaitUtil.waitFor(1);


            //상품마스터 페이지에서 회사명을 클릭하고 상품명을 클릭한다.
            Set<String> companyList = productMap.keySet();
            for(String company : companyList) {
                WebElement companyEl = driver.findElement(By.xpath("//ul[text()='보험사']/..//label[contains(., '" + company + "')]"));
                companyEl.click();
                logger.info("보험사({}) 클릭", company);
                WaitUtil.waitFor(1);

                for(String product : productMap.get(company)) {
                    WebElement productEl = driver.findElement(By.xpath("//ul[@id='productByCompanyId']//label[@for='" + product + "']"));
                    productEl.click();
                    logger.info("======================상품({}) 클릭======================", product);
                    WaitUtil.waitFor(1);

                    //특약 개수가 50개를 초과하면 다음페이지로 이동해야 한다.
                    List<WebElement> pageBtnList = driver.findElements(By.xpath("//div[@class='nav-paging']//a[not(contains(@class, 'btn'))]"));

                    if(pageBtnList.size() > 0) {
                        //특약이 50개를 초과하는 경우

                        for(int i=0; i<pageBtnList.size(); i++) {
                            //페이지 버튼 클릭
                            WebElement pageBtn = driver.findElements(By.xpath("//div[@class='nav-paging']//a[not(contains(@class, 'btn'))]")).get(i);
                            pageBtn.click();
                            WaitUtil.waitFor(1);

                            startMapping();
                        }

                    } else {
                        startMapping();
                    }

                    if(toMapTreatyList.size() == 0) {
                        logger.info("모든 특약이 매핑된 상태입니다.");
                    } else {
                        logger.info("\n\n●●●●● 매핑해야 할 특약리스트 ●●●●●");

                        for(String toMapTreaty : toMapTreatyList) {
                            logger.info("특약명 : {}", toMapTreaty);
                        }
                    }

                    //상품ID 체크해제.
                    productEl.click();
                    WaitUtil.waitFor(1);

                    toMapTreatyList.clear();
                    tndMap.clear();
                }

                //보험사명 체크해제.
                companyEl.click();
                WaitUtil.waitFor(1);
            }

            result = true;
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            driver.quit();
        }

        return result;
    }

    private void setDriverInfo() {
        String osName = OSValidator.getOsName();
        String crawlerDir = PropertyUtil.get(osName + "." + "crawler.dir");
        String driverPath = PropertyUtil.get("chrome.driver.path");
        String chromeDriverPath = crawlerDir + driverPath;
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
    }

    private void login() throws Exception{
        setTextToInputBox(By.id("inputId"), "dev");
        setTextToInputBox(By.id("inputPassword"), "!welgram00");
        driver.findElement(By.id("login")).click();
        WaitUtil.waitFor(1);
    }

    private void setTextToInputBox(By element, String text) {
        WebElement inputBox = driver.findElement(element);
        inputBox.click();
        inputBox.clear();
        inputBox.sendKeys(text);
    }

    private void mSecLoading(int mSec) throws InterruptedException {
        int count = mSec / 100;
        for (int i = 1; i <= count; i++) {
            Thread.sleep(100);
        }
    }

    //map에서 value에 해당하는 key를 리턴한다.
    private static String getKey(HashMap map, String value) {
        for(Object key : map.keySet()) {
            String _key = key.toString();

            if(value.equals(map.get(_key))) {
                return _key;
            }
        }

        return null;
    }



    private void startMapping() throws Exception{
        List<WebElement> modifyBtnList = driver.findElements(By.xpath("//button[text()='수정']"));

        for(int j = 0; j < modifyBtnList.size(); j++) {
            //수정 버튼 클릭
            WebElement modifyBtn = driver.findElements(By.xpath("//button[text()='수정']")).get(j);
            modifyBtn.click();
            mSecLoading(700);


            //처음 수정 버튼을 클릭했을 때 분석팀의 TND 정보를 map에 담는다.
            if(tndMap.size() == 0) {
                List<WebElement> tndElList = driver.findElements(By.xpath("//td[@id='tndMappingArea']//span[@class='blu']"));

                for(WebElement tndEl : tndElList) {
                    int start = tndEl.getText().indexOf(" ");
                    int end = tndEl.getText().lastIndexOf(" ");

                    String tndCode = tndEl.getText().substring(0, start);
                    String tndTreatyName = tndEl.getText().substring(start + 1, end);

                    tndMap.put(tndCode, tndTreatyName);
                }
            }


            String myTreatyName = ((JavascriptExecutor)driver).executeScript("return $(\"#modProductName\").val()").toString();

            if(tndMap.containsValue(myTreatyName)) {
                //분석 TND 특약명과 내 특약명이 일치할 때

                String tndCode = getKey(tndMap, myTreatyName);

                //매핑이 안되어 있을 때만 매핑시킨다.
                WebElement element = driver.findElement(By.xpath("//td[@id='tndMappingArea']//input[contains(@value, '" + tndCode + "')]"));
                if(!element.isSelected()) {
                    element.click();

                    logger.info("[매핑완료] {} => {}", myTreatyName, tndCode);
                } else {
                    logger.info("[이미 매핑된 특약입니다] {} => {}", myTreatyName, tndCode);
                }

//                tndMap.remove(tndCode);

                driver.findElement(By.xpath("//div[@id='productMasterRegDialog']//button[contains(., '수정')]")).click();
                if(helper.isAlertShowed()) {
                    driver.switchTo().alert().accept();
                    mSecLoading(500);
                    driver.switchTo().alert().accept();
                    mSecLoading(500);
                }

            } else {
                //case 1. 분석 TND 특약명과 내 특약명이 다를 때
                //case 2. 이제는 사용안하는 특약이지만 가설에 물려있어 삭제가 안된 특약일 경우
                logger.info("[매핑 불가] {}", myTreatyName);
                toMapTreatyList.add(myTreatyName);
                driver.findElement(By.xpath("//div[@id='productMasterRegDialog']//button[contains(., '닫기')]")).click();
                mSecLoading(500);
            }
        }
    }


}

public class MappingCrawlerTest {
    public static void main(String[] args) {
        String[] productList = {"KBF_CHL_D006"};

        new MappingCrawler().execute(productList);
    }
}
