package com.welgram.crawler;

import com.welgram.crawler.general.PlanReturnMoneyApiTest;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

public class HelloSelenium {

    public final static Logger logger = LoggerFactory.getLogger(HelloSelenium.class);

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "d:\\geckodriver\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebDriverWait wait = new WebDriverWait(driver, 20);

        try {
            driver.get("https://google.com/ncr");
            driver.findElement(By.name("q")).sendKeys("cheese" + Keys.ENTER);
            WebElement firstResult = wait.until(presenceOfElementLocated(By.cssSelector("h3>div")));
            logger.debug(firstResult.getAttribute("textContent"));
        } finally {
            driver.quit();
        }
    }
}		
