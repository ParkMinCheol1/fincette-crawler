package com.welgram.crawler;

import com.welgram.crawler.helper.SeleniumCrawlingHelper;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

public class ExtensionsTest {
    private static WebDriver driver;
    private static  WebDriverWait wait;
    private static ChromeOptions options;
    private static SeleniumCrawlingHelper helper;




    public static void main(String[] args) {

        //chrome dirver 경로 세팅
        String chromeDriverPath = "C:\\crawler\\driver\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);


        //gofullpage 확장프로그램 추가
        options =  new ChromeOptions();
        options.addExtensions(new File("C:\\crawler\\extensions\\fdpohaocaechififmbbbbbknoalclacl\\extension_7_7_0_0.crx"));


        //option 정보를 바탕으로 chrome driver 생성
        driver = new ChromeDriver(options);
//        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait = new WebDriverWait(driver, 30);
        helper = new SeleniumCrawlingHelper(driver, wait);


        //AIG 사이트로 테스트 진행
        driver.get("https://www.aig.co.kr/wm/dpwmm001.html");
        String currentHandle = driver.getWindowHandle();
        driver.switchTo().window(currentHandle);



        WebElement element = driver.findElement(By.tagName("body"));
        File screenshot = ((TakesScreenshot)element).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(screenshot, new File("C:\\screenshot\\screenshot.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }





    }

}
