package com.welgram.crawler.general;

import java.io.File;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class CrawlerGeneralTest {

  private static final long WAIT_TIME = 60;

  ChromeDriver driver;
  //	WebDriverWait wait = new WebDriverWait(driver, WAIT_TIME);
  ChromeOptions options;

  @Before
  public void test() {
    System.setProperty("webdriver.chrome.driver", "c:/crawler/driver/chromedriver.exe");

    options = new ChromeOptions();

  }

  @Test
  public void testSimpleCrawling() {

    options = new ChromeOptions();
    driver = new ChromeDriver(options);

    String crawlUrl = "http://nuzal.kr";
    driver.get(crawlUrl);
    driver.manage().window().setSize(new Dimension(1280, 1024));

  }

  /**
   * 크롬확장프로그램 설치
   */
  @Test
  public void testInstallChromeExtensions() {

    options = new ChromeOptions();
    options.addExtensions(
        new File("c:/crawler/extensions/icadabneccecohhaonmhgbjelhgodfaa/1.0.1.13_0.crx"));

    driver = new ChromeDriver(options);

    String crawlUrl = "https://www.idbins.com/AuthenticationCenter.do?mode=login&url=%2FCyCerPri.do&gbn=2";
    driver.get(crawlUrl);

    driver.manage().window().setSize(new Dimension(1280, 1024));

  }

  /**
   * 크롬확장프로그램 VPN 설치
   */
  @Test
  public void testInstallChromeExtensionVpn() {

    options = new ChromeOptions();
//		options.addExtensions(new File("c:/crawler/extensions/icadabneccecohhaonmhgbjelhgodfaa/1.0.1.13_0.crx"));
    options.addExtensions(
        new File("c:/crawler/extensions/ipkbbcamfcnlflkedfdaokofdmfgocfp/4.4.8_0.crx"));
    driver = new ChromeDriver(options);

    String crawlUrl = "chrome-extension://ipkbbcamfcnlflkedfdaokofdmfgocfp/html/popup_connect.html";
    driver.get(crawlUrl);

    driver.manage().window().setSize(new Dimension(1280, 1024));

  }

}
