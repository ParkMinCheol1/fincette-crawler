package com.welgram.crawler.common.ext;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.welgram.PropertyUtil;
import com.welgram.common.HostUtil;
import com.welgram.common.MyIpUtil;
import com.welgram.common.OSValidator;
import com.welgram.common.WaitUtil;

import java.io.File;
import java.util.*;

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

/**
 * 미꾸라지VPN
 *
 * @author gusfo
 */
public class ChromeMudfishVpn implements CrawlingVpn {

    private static final Logger logger = LoggerFactory.getLogger(ChromeMudfishVpn.class);

    //	private static final String ID = "welgram1";
    private static final String PASSWORD = "!welgram00";

    private String id = "welgram";

    public static String subHandle;
    protected WebDriver driver;
    protected WebDriverWait wait;

    private String[] countries = new String[]{"kr"};
//    private String[] countries = new String[]{"kr", "jp", "us"};

    public ChromeMudfishVpn() {
        this.id = HostUtil.getUsername();
    }

    public ChromeMudfishVpn(String id) {
        this.id = id;
        countries = new String[]{"kr"};
    }

    public ChromeMudfishVpn(String[] countries) {
        this.id = HostUtil.getUsername();
        this.countries = countries;
    }

    public ChromeMudfishVpn(String id, String[] countries) {
        this.id = id;
        this.countries = countries;
    }

    @Override
    public void init(ChromeOptions options) throws Exception {

        String osName = OSValidator.getOsName();
        String crawlerDir = PropertyUtil.get(osName + "." + "crawler.dir");
        options.addExtensions(new File(crawlerDir + "/extensions/ipkbbcamfcnlflkedfdaokofdmfgocfp/4.4.8_0.crx"));

    }

    @Override
    public boolean connect(WebDriver driver, FluentWait<WebDriver> wait) throws Exception {

        // 정상페이지 확인
        try {

            this.driver = driver;

            String currentHandle = driver.getWindowHandle();

            WaitUtil.loading(2);

            logger.info("미꾸라지 VPN 설정창 열기");
            driver.get("chrome-extension://ipkbbcamfcnlflkedfdaokofdmfgocfp/html/popup_connect.html");

            // 옵션 버튼
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#mudfish_conf_options")));
            element.click();

            // 계정 기억하기 버튼
            element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("remember_me")));
            if (!element.isSelected()) {
                element.click();
                // 아이디/패스워드 입력
                element = driver.findElement(By.id("username"));
                element.sendKeys(this.id);
                element = driver.findElement(By.id("password"));
                element.sendKeys(PASSWORD);
            }

            element = driver.findElement(By.id("popup_connect_save")); // 저장버튼
            element.click();

            element = driver.findElement(By.cssSelector("#select2-staticnodes_select-container"));
            element.click();

            // 한국VPN 목록조회
            element = driver.findElement(By.cssSelector(".select2-search__field"));
            String _country = countries[new Random().nextInt(countries.length)];
            element.sendKeys(_country);

            List<WebElement> firstSearch = driver.findElements(By.cssSelector(".select2-results__option"));
            List<WebElement> elements = new ArrayList<>();
            for (int i = 0; i < firstSearch.size(); i++) {
                if (_country.equals(firstSearch.get(i).getText().substring(0, 2))) {
                    elements.add(firstSearch.get(i));
                }
            }

            int elementsSize = elements.size();
            logger.info("[" + _country + "]" + " vpn list size: " + elementsSize);

            WebElement randomElement = elements.get(new Random().nextInt(elementsSize));

            logger.info("[" + _country + "]" + " VPN 목록 중, 랜덤한 VPN서버 선택");
            logger.info(randomElement.getAttribute("innerText"));
            randomElement.click();

            WaitUtil.loading(2);
            // 현재창
            currentHandle = driver.getWindowHandles().iterator().next();

            WaitUtil.loading(2);

            switchToWindow(currentHandle, driver.getWindowHandles(), false);

            String popupHandle = driver.getWindowHandle();
            logger.debug("popup handler: {}", popupHandle);

            // 윈도우 창을 1개만 남김
            switchToWindow(popupHandle, driver.getWindowHandles(), false);
            logger.debug("switchToWindow");

            WaitUtil.loading(2);

            boolean isConnected = false;
            int retryCount = 0;
            while (!isConnected && retryCount <= 2) {
                try {

                    logger.info((retryCount + 1) + "번째 접속시도!!");
                    driver.findElement(By.id("looks_good"));
                    logger.info("================");
                    logger.info("VPN 정상페이지 확인");
                    logger.info("================");

                    isConnected = true;

//                    String _myip = MyIpUtil.getMyIp(driver);
//                    logger.info("myip: {}", _myip);

                } catch (org.openqa.selenium.NoSuchElementException e) {
                    e.printStackTrace();
                    ((JavascriptExecutor) driver).executeScript("location.reload();");
                    retryCount++;
                    WaitUtil.loading(3);
                }
            }
            return true;
        } catch (Exception e) {
            logger.info("VPN 페이지 로딩 에러 :: " + e.getMessage());
            return false;
        }


//		try {
//			WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("looks_good")));
//			logger.debug("el :: " + el);
//			return true;
//		} catch (Exception e) {
//			logger.info("VPN 페이지 로딩 에러 :: " + e.getMessage());
//			return false;
//		}

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
