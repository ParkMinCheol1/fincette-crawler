package com.welgram.crawler.direct.life;

import com.welgram.crawler.SeleniumCrawler;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

public abstract class CrawlingMEZ extends SeleniumCrawler {

    //해당 element가 보이게 스크롤 이동
    protected void moveToElementByJavascriptExecutor(WebElement element) throws Exception {
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    //inputBox에 text 입력하는 메서드
    protected void setTextToInputBox(By id, String text) {
        WebElement element = driver.findElement(id);
        element.clear();
        element.sendKeys(text);
    }

    //스크롤 제일 밑으로 내리기
    protected void Webscrollbottom(){
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,document.body.scrollHeight);");
    }

}
