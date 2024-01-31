package com.welgram.crawler.direct.fire.kbf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.util.StringUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.tess4j.Tesseract;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;

public abstract class CrawlingKBFAnnounce extends CrawlingKBFNew {


    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        String title = "생년월일";

        WebElement $birthInput = (WebElement) obj[0];
        String expectedAge = (String) obj[1];
        String actualAge = "";

        try {

            //생년월일 설정
            actualAge = helper.sendKeys4_check($birthInput, expectedAge);

            //생년월일 비교
            super.printLogAndCompare(title, expectedAge, actualAge);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setGender(Object... obj) throws SetGenderException {
        String title = "성별";

        int gender = (int) obj[0];
        String expectedGenderText = (gender == MALE) ? "남" : "여";
        String actualGenderText = "";
        String script = "";

        try {

            //성별 element 찾기
            WebElement $genderTr = driver.findElement(By.xpath("//td[normalize-space()='성별']/parent::tr"));
            WebElement $genderLabel = $genderTr.findElement(By.xpath(".//label[normalize-space()='" + expectedGenderText + "']"));

            //성별 클릭
            click($genderLabel);

            //실제 선택된 성별 값 읽어오기
            script = "return $('input[name=sex_cd]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $genderLabel = $genderTr.findElement(By.xpath(".//label[@for='" + id + "']"));
            actualGenderText = $genderLabel.getText().trim();

            //비교
            super.printLogAndCompare(title, expectedGenderText, actualGenderText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setTreaties(CrawlingProduct info) throws SetTreatyException {

        List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

        try{
            WebElement $tbody = driver.findElement(By.cssSelector(".tb_default03 tbody"));
            List<WebElement> $trSize = $tbody.findElements(By.cssSelector("tr"));
            List<WebElement> $treatyName = $tbody.findElements(By.cssSelector("tr th"));
            List<WebElement> $treatyAssureMoney = $tbody.findElements(By.cssSelector("tr td:nth-child(2)"));


            for(int i = 0; i < $trSize.size(); i++){
                CrawlingTreaty targetTreaty = new CrawlingTreaty();

                String targetTretryName = $treatyName.get(i).getText();
                int targetTretryAssureMoney = Integer.parseInt(String.valueOf(MoneyUtil.toDigitMoney($treatyAssureMoney.get(i).getText())));

                targetTreaty.setTreatyName(targetTretryName);
                targetTreaty.setAssureMoney(targetTretryAssureMoney);
                targetTreatyList.add(targetTreaty);
            }

            //비교
            boolean result = advancedCompareTreaties(targetTreatyList, info.treatyList, new CrawlingTreatyEqualStrategy1());

            if(result) {
                logger.info("특약 정보가 모두 일치합니다~~~");
            } else {
                logger.error("특약 정보 불일치~~~");
                throw new Exception();
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    protected void tesseract(CrawlingProduct info) throws Exception {
        logger.info("viewer 창으로 전환");
        driver.findElement(By.id("preview")).click();
        ArrayList<String> map = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(map.get(1));
        driver.manage().window().setSize(new Dimension(2775, 1540));
//            driver.manage().window().maximize();
        WaitUtil.waitFor(5);

        File img = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        WaitUtil.waitFor(2);

        Tesseract instance = new Tesseract();
        instance.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
        instance.setLanguage("kor");
        instance.setTessVariable("user_defined_dpi", "70");
        WaitUtil.waitFor(1);
        String result = instance.doOCR(img);

        logger.info("추출된 문자열 : {}", result);
        WaitUtil.waitFor(1);

        int start = 0;
        String firstPremium = "";
        String[] text = {"보험료", "보엄료", "보멈료", "보범료"};
        boolean isFound = false;
        for(int i = 0; i < text.length; i++){
            start = result.indexOf("초회"+text[i]);
            isFound = (start != -1) ? true : false;

            if(isFound) {
                break;
            }
        }
        if(isFound){
            int end = result.indexOf("원", start);
            firstPremium = result.substring(start, end + 1);
        }

        if(!isFound){
            start = 0 ;
            int end = 0;
            boolean find = false;

            for(int i = 0; i<result.length(); i++){
                start = result.indexOf( "보험료");
                end = result.indexOf("원", start);
                find = true;
                if(find) break;
            }

            int targetNum = result.lastIndexOf(" ", end);
            firstPremium = result.substring(targetNum+1, end);
        }

        String premium = firstPremium.replaceAll("[^0-9]", "");
        if(StringUtil.isEmpty(premium) || (Integer.parseInt(premium) > 100000) || (Integer.parseInt(premium) < 1000)){
            throw new Exception("잘못된 값이 들어가 있습니다. 다시 확인해주세요.");
        }

        info.getTreatyList().get(0).monthlyPremium = premium;
        logger.info("월 보험료 :: {}원", premium);

        WaitUtil.waitFor(2);
    }

    protected void alert() throws Exception{
        boolean isShowed = helper.isAlertShowed();
        while (isShowed) {
            driver.switchTo().alert().accept();
            isShowed = helper.isAlertShowed();
        }
        WaitUtil.waitFor(2);
    }

    //로딩바 명시적 대기
    public void waitLoadingBar() {
        try {
            helper.waitForCSSElement(".ui-loading");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //TODO 추후에 helper로 뺄것
    public void click(WebElement $element) throws Exception {
        helper.waitElementToBeClickable($element).click();
        waitLoadingBar();
        WaitUtil.waitFor(1);
    }


    //TODO 추후에 helper로 뺄것
    public void click(By position) throws Exception {
        WebElement $element = driver.findElement(position);
        click($element);
    }
}