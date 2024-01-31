package com.welgram.crawler;

import com.welgram.PropertyUtil;
import com.welgram.common.OSValidator;
import com.welgram.common.WaitUtil;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MappingCheckTest {
    private static WebDriver driver;
    private static WebDriverWait wait;
    public final static Logger logger = LoggerFactory.getLogger(MappingCheckTest.class);
        private static String[] companyList = {"CRF"};


    public static void main(String[] args) {
        String osName = OSValidator.getOsName();
        String crawlerDir = PropertyUtil.get(osName + "." + "crawler.dir");
        String driverPath = PropertyUtil.get("chrome.driver.path");
        String chromeDriverPath = crawlerDir + driverPath;
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        logger.info("chromeDriverPath :: " + chromeDriverPath);

        driver = new ChromeDriver();
//        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait = new WebDriverWait(driver, 20);


        try {
            //관리자페이지 접속
            driver.get("https://nuzal.kr/mngr/login");

            login();

            driver.findElement(By.linkText("보험상품관리")).click();
            WaitUtil.waitFor(1);

            driver.findElement(By.linkText("상품마스타특약관리")).click();
            WaitUtil.waitFor(1);



            //회사 목록을 돈다.
            for(String company : companyList) {
                logger.info("보험사({}) 클릭", company);
                driver.findElement(By.xpath("//ul[text()='보험사']/..//label[contains(., '" + company + "')]")).click();


                //상품 목록을 돈다.
                List<WebElement> productList = driver.findElements(By.cssSelector("#productByCompanyId small"));
                for(WebElement product : productList) {

                    String productId = product.getText();
                    logger.info("\n\n\n=============={} 클릭==============", productId);
                    driver.findElement(By.cssSelector("label[for='" + productId + "']")).click();
                    WaitUtil.waitFor(1);



                    //특약 개수가 50개를 초과하면 다음페이지로 이동해야 한다.
                    List<WebElement> pageBtnList = driver.findElements(By.xpath("//div[@class='nav-paging']//a[not(contains(@class, 'btn'))]"));

                    if(pageBtnList.size() > 0) {
                        //특약이 50개를 초과하는 경우
                        for(int i=0; i<pageBtnList.size(); i++) {
                            WebElement pageBtn = driver.findElements(By.xpath("//div[@class='nav-paging']//a[not(contains(@class, 'btn'))]")).get(i);
                            pageBtn.click();
                            WaitUtil.waitFor(1);

                            compareTreaty();
                        }
                    } else {
                        //특약이 50개 이하인 경우
                        compareTreaty();
                    }



                    //상품ID 체크해제.
                    driver.findElement(By.cssSelector("label[for='" + productId + "']")).click();
                    WaitUtil.waitFor(1);
                }


                //보험사명 체크해제.
                driver.findElement(By.xpath("//ul[text()='보험사']/..//label[contains(., '" + company + "')]")).click();
                WaitUtil.waitFor(1);

            }





        } catch(Exception e) {
            logger.info("예외 발생");
            e.printStackTrace();
        } finally{
            driver.quit();
        }
    }

    private static void login() throws Exception{
        setTextToInputBox(By.id("inputId"), "dev");
        setTextToInputBox(By.id("inputPassword"), "!welgram00");
        driver.findElement(By.id("login")).click();
        WaitUtil.waitFor(1);
    }

    private static void setTextToInputBox(By element, String text) {
        WebElement inputBox = driver.findElement(element);
        inputBox.click();
        inputBox.clear();
        inputBox.sendKeys(text);
    }

    //element가 보이게끔 이동
    private static void moveToElement(WebElement element) {
        Actions action = new Actions(driver);
        action.moveToElement(element);
        action.perform();
    }

    private static void mSecLoading(int mSec) throws InterruptedException {
        int count = mSec / 100;
        for (int i = 1; i <= count; i++) {
            Thread.sleep(100);
        }
    }

    private static void compareTreaty() throws Exception{
        //특약마다 수정버튼 클릭
        List<WebElement> modifyBtnList = driver.findElements(By.xpath("//button[text()='수정']"));
        for(int j=0; j<modifyBtnList.size(); j++) {
            WebElement modifyBtn = driver.findElements(By.xpath("//button[text()='수정']")).get(j);
            modifyBtn.click();
            mSecLoading(700);


            String treatyName = "";
            try {
                treatyName = ((JavascriptExecutor)driver).executeScript("return $(\"#modProductName\").val()").toString();
                String checkedTND = ((JavascriptExecutor)driver).executeScript("return $(\"input[name='modNuzalTndCodes']:checked\").attr('value')").toString();

                //매핑 상태
                checkedTND = checkedTND.substring(checkedTND.lastIndexOf("_") + 1);
//                logger.info("현재 매핑 상태 : {} => {}", treatyName, checkedTND);


                //체크된 TND 특약명과 일치하는지 검사
                WebElement mappedTND = driver.findElement(By.xpath("//td[@id='tndMappingArea']//span[@class='blu'][contains(., '" + checkedTND + "')]"));
                String mappedTreatyName = mappedTND.getText().replaceAll(" ", "").replaceAll("[·/・]", "");       //'．' 또는 '/' 문자의 경우 특약명을 비교하는데 상관없으므로 제거.
                treatyName = treatyName.replaceAll(" ", "").replaceAll("[·/・]", "");


                //잘못 매핑된 경우
                if(!mappedTreatyName.contains(treatyName)) {
                    logger.info("**********************특약 매핑 오류*******************************");
                    logger.info("특약명 : {}", treatyName);
                    logger.info("매핑된 TND : {}", mappedTreatyName);
                    logger.info("*****************************************************************");
                }

            } catch(NullPointerException e) {
                /*
                 * 특약에 매핑된 TND가 없을 때 해당 예외 발생
                 *
                 * 1. 막 버전업이 돼서 특약 매핑이 다 풀려있을 경우
                 * 2. 분석대상아닌 상품에 대해서는 매핑이 아예 없음
                 * 3. 이제는 사용하지 않아 삭제해야하는 특약인데, 가설에 묶여있어 삭제하지 못한 경우 특약 매핑이 안돼있음.
                 * */
                logger.info("******특약 매핑 오류*******");
                logger.info("특약명 : {}  이 매핑이 풀려있습니다.", treatyName);
            }


            driver.findElement(By.xpath("//div[@id='productMasterRegDialog']//button[contains(., '닫기')]")).click();
            mSecLoading(500);
        }
    }
}
