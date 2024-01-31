package com.welgram.crawler.direct.fire;

import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.util.InsuranceUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/*
 * 2020.12.01
 * @author 조하연
 * AIG 상품 공시실용 클래스
 * */


//AIG손해보험 상품 중 공시실에서 크롤링해오는 상품에 대해서는 AnnounceCrawlingAIG를 상속받는다.
public abstract class AnnounceCrawlingAIG extends SeleniumCrawler {
    //크롤링 옵션 정의 메서드
    protected void setChromeOptionAIG(CrawlingProduct info) throws Exception {
        CrawlingOption option = info.getCrawlingOption();

        option.setBrowserType(CrawlingOption.BrowserType.Chrome);
        option.setImageLoad(false);
        option.setUserData(false);

        info.setCrawlingOption(option);
    }

    //버튼 클릭 메서드
    protected void btnClick(By element) {
        driver.findElement(element).click();
        waitAnnounceLoadingImg();
    }


    //inputBox에 텍스트 입력하는 메서드
    protected void setTextToInputBox(By element, String text) {
        WebElement inputBox = driver.findElement(element);
        inputBox.click();
        inputBox.clear();
        inputBox.sendKeys(text);
    }


    //공시실용 보험료 계산 버튼 클릭 메서드
    protected void announceCalcBtnClick() {
        btnClick(By.linkText("계산하기"));
    }


    //select box에서 text와 일치하는 option 클릭하는 메서드
    protected void selectOptionByText(By element, String text) {
        Select select = new Select(driver.findElement(element));
        select.selectByVisibleText(text);
    }


    //select box에서 value값이 일치하는 option 클릭하는 메서드
    protected void selectOptionByValue(WebElement selectEl, String value) {
        Select select = new Select(selectEl);
        select.selectByValue(value);
    }


    //공시실용 로딩이미지 명시적 대기
    protected void waitAnnounceLoadingImg() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("loadBox")));
    }


    //공시실 페이지에서 해당 보험을 찾는다.
    protected void findInsuFromAnnounce(String insuName) throws Exception {
        List<WebElement> thList = driver.findElements(By.cssSelector("#prodList tr th"));

        for (WebElement th : thList) {
            String targetInsuName = th.getText();

            if (targetInsuName.equals(insuName)) {
                WebElement btn = th.findElement(By.xpath("parent::tr"))
                        .findElement(By.tagName("td")).findElement(By.linkText("보험료계산"));
                btn.click();
            }
        }
    }


    //공시실용 생년월일 설정 메서드(1개 입력)
    protected void setAnnounceBirth(String fullBirth) {
        setTextToInputBox(By.id("brdt"), fullBirth);
    }


    //공시실용 성별 설정 메서드
    protected void setAnnounceGender(int gender) {
        String genderTag = (gender == MALE) ? "type01" : "type02";
        btnClick(By.cssSelector("label[for='" + genderTag + "']"));
    }


    //공시실용 플랜 설정 메서드
    protected void setAnnouncePlan(String planType) {
        selectOptionByText(By.id("prodPlanCd"), planType);
    }


    //공시실용 출산예정일 설정 메서드
    protected void setAnnounceDueDate() {
        String dueDate = InsuranceUtil.getDateOfBirth(12);

        logger.info("출산예정일 : {}", dueDate);
        setTextToInputBox(By.id("chbrSchDt"), dueDate);
    }


    //공시실용 특약 설정 메서드
    protected void setAnnounceSubTreaties(CrawlingTreaty treaty) {
        String myTreatyName = treaty.treatyName;
        String assureMoney = String.valueOf(treaty.assureMoney);

        List<WebElement> trList = driver.findElements(By.cssSelector(".tblList tbody tr"));

        for (WebElement tr : trList) {
            String targetTreatyName = tr.findElements(By.tagName("td")).get(0).getText();

            if (targetTreatyName.equals(myTreatyName)) {
                logger.info("특약명 : {}", myTreatyName);

                logger.info("특약 가입금액 : {}원", assureMoney);
                WebElement selectEl = tr.findElements(By.tagName("td")).get(1).findElement(By.tagName("select"));
                selectOptionByValue(selectEl, assureMoney);

                try {
                    WebElement checkBox = tr.findElement(By.tagName("th")).findElement(By.tagName("input"));

                    if (!checkBox.isSelected()) {
                        checkBox.click();
                        break;
                    }
                } catch (NoSuchElementException e) {
                    //주계약의 경우 체크박스가 존재하지 않아 NoSuchElementException 예외가 발생한다.
                    break;
                }
            }
        }
    }

    //공시실용 주계약 보험료 설정 메서드
    protected void setAnnouncePremium(CrawlingTreaty mainTreaty) {
        String monthlyPremium = driver.findElement(By.cssSelector(".price strong")).getText().replaceAll("[^0-9]", "");

        logger.info("월 보험료 : {}", monthlyPremium);
        mainTreaty.monthlyPremium = monthlyPremium;
    }

}
