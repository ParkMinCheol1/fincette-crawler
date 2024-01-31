package com.welgram.crawler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class ChromeTest {

  WebDriver driver;

  @Before
  public void setupAll() {
    WebDriverManager.chromedriver().setup();
  }

  @Before
  public void setup() {
    driver = new ChromeDriver();
  }

  @After
  public void teardown() {
    driver.quit();
  }

  @Test
  public void test() {
    // Your test logic here
    System.out.println("test");
  }

}