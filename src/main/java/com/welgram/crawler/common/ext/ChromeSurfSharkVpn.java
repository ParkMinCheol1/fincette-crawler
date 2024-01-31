package com.welgram.crawler.common.ext;

import com.welgram.PropertyUtil;
import com.welgram.common.HostUtil;
import com.welgram.common.MyIpUtil;
import com.welgram.common.OSValidator;
import com.welgram.common.WaitUtil;
import javax.xml.bind.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * 미꾸라지VPN
 *
 * @author gusfo
 */
public class ChromeSurfSharkVpn implements CrawlingVpn {

    private static final Logger logger = LoggerFactory.getLogger(ChromeSurfSharkVpn.class);

    private static final String ID = "master@welgram.com";
    private static final String PASSWORD = "!@Dnpfrmfoa12";


    public static String subHandle;
    private String id = "master@welgram.com";

    protected WebDriver driver;
    protected WebDriverWait wait;

    private String[] countries = new String[]{"KR"};

    public ChromeSurfSharkVpn(String[] countries) {
        this.countries = countries;
    }

    public ChromeSurfSharkVpn() {

    }

    @Override
    public void init(ChromeOptions options) throws Exception {

        String osName = OSValidator.getOsName();
        String crawlerDir = PropertyUtil.get(osName + "." + "crawler.dir");
        options.addExtensions(new File(crawlerDir + "/extensions/ailoabdmgclmfmhdagmlohpjlbpffblp/4.6.0_0.crx"));

    }

    @Override
    public boolean connect(WebDriver driver, FluentWait<WebDriver> wait) throws Exception {

        // 정상페이지 확인
        try {

            this.driver = driver;

            String currentHandle = driver.getWindowHandle();
            WaitUtil.loading(2);

            currentHandle = driver.getWindowHandles().iterator().next();

            WaitUtil.loading(2);

            switchToWindow(currentHandle, driver.getWindowHandles(), false);

            logger.info("Sufshark VPN 설정창 열기");
            driver.get("chrome-extension://ailoabdmgclmfmhdagmlohpjlbpffblp/index.html");
            WaitUtil.waitFor(3);

            //로그인 버튼 선택
            logger.info("로그인 버튼 클릭");
            WebElement element = driver.findElement(By.xpath("//button[text()='로그인']"));
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
            WaitUtil.waitFor(3);

            currentHandle = driver.getWindowHandles().iterator().next();
            switchToWindow(currentHandle, driver.getWindowHandles(), false);

            // 아이디/패스워드 입력
            logger.info("아이디 입력");
            element = driver.findElement(By.xpath("//input[@name='emailField']"));
            element.sendKeys(this.id);

            logger.info("비밀번호 입력");
            element = driver.findElement(By.xpath("//input[@name='passwordField']"));
            element.sendKeys(PASSWORD);

            logger.info("로그인 버튼 클릭");
            element = driver.findElement(By.id("loginSubmit"));
            element.click();
            WaitUtil.waitFor(3);

            logger.info("Sufshark VPN 설정창 열기");
            driver.get("chrome-extension://ailoabdmgclmfmhdagmlohpjlbpffblp/index.html");
            WaitUtil.waitFor(5);
            element = driver.findElement(By.xpath("//button[text()='빠른 연결']"));
            wait.until(ExpectedConditions.elementToBeClickable(element));

            logger.info("빠른 연결 버튼 선택");
            element.click();
            WaitUtil.waitFor(3);

            return true;
        } catch (Exception e) {
            logger.info("VPN 페이지 로딩 에러 :: " + e.getMessage());
            return false;
        }
    }

    protected void switchToWindow(String currentHandle, Set<String> windowId, boolean value) {
        Iterator<String> handles = windowId.iterator();
        // 메인 윈도우 창 확인
        subHandle = null;

        while (handles.hasNext()) {
            subHandle = handles.next();
            if (subHandle.equals(currentHandle)) {
                continue;
            } else {
                // true : 이전 창을 닫지 않음, false : 이전 창을 닫음
                if (!value) {
                    driver.close();
                }
                driver.switchTo().window(subHandle);
                wait = new WebDriverWait(driver, 30);
                break;
            }
        }
    }

}
