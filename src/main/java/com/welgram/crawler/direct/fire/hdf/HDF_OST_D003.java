package com.welgram.crawler.direct.fire.hdf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.fire.CrawlingHDF;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;



public class HDF_OST_D003 extends CrawlingHDF { // 해외여행보험
    public static void main(String[] args) {
        executeCommand(new HDF_OST_D003(), args);
    }



    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        logger.info("크롤링(모니터링) 환경을 모바일로 전환합니다");
        option.setMobile(true);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        mobileSetCountry();

        // 보험개시일 여행 일정 설정 시작
        mobileInsTerm(info);

        // 생년월일
        mobileSetBirth(info);

        // 성별
        mobileSetGender(info.getGender());

        // 보험료 가져오기
        mobileGetPremium(info);
//        mobileGetPremium2(info);

        logger.info("스크린샷");
        takeScreenShot(info);

        return true;
    }



    @Override
    protected void mobileInsTerm(CrawlingProduct info) throws InterruptedException {
        // 출발일(보험 개시일) 세팅
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='insStDt']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='ui-datepicker-div']")));

        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        // 해외여행자보험은 보험 개시일이 오늘 기준 7일 후이기 때문에 날짜를 비교하여 달력을 넘기거나 혹은 바로 날짜를 선택하여 보험기간 세팅
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7);
        logger.info("출발 날짜(보험 개시일) : {}", format.format(cal.getTime()));

        int mMonth     = Integer.valueOf(
            helper.waitVisibilityOfElementLocated(
                By.xpath("//div[@class='ui-datepicker-title']/span[@class='ui-datepicker-month']")
                ).getText().replaceAll("[^0-9]", "")
        );

        String[] sDay  = format.format(cal.getTime()).split("[.]");
        int startYear  = Integer.valueOf(sDay[0]);
        int startMonth = Integer.valueOf(sDay[1]);
        int startDay   = Integer.valueOf(sDay[2]);

        // 오늘로부터 7일 뒤(보험 개시일)가 다음 달이면 달력 넘기기 버튼 클릭
        if (startMonth != mMonth) {
            WaitUtil.waitFor(1);
            helper.click(By.xpath("//div[@id='ui-datepicker-div']/div/a[2]"), "다음달로 넘기기");
        }
        helper.click(By.xpath("//td[@data-event='click']/a[@class='ui-state-default'][text()='" + startDay + "']"), "출발일 선택");

        logger.info("출발 시간(보험 개시 시간) 선택");
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//select[@id='insStTm']")));
        Select sel = new Select(driver.findElement(By.xpath("//select[@id='insStTm']")));
        sel.selectByValue("00");
        WaitUtil.waitFor(2);

        // 도착일(보험 종료일) 세팅
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='insEdDt']"))).click();
        // Date객체는 월을 0~11로 따지기 때문에 Month에 -1, 보험 종료일은 보험 개시일로부터 6일 후 23시까지 이므로 보험 개시일 + 6
        cal.set(startYear, startMonth-1, startDay);
        cal.add(Calendar.DATE, 6);
        logger.info("도착 날짜(보험 종료일) : {}", format.format(cal.getTime()));

        String[] eDay = format.format(cal.getTime()).split("[.]");
        int endMonth  = Integer.valueOf(eDay[1]);
        int endDay    = Integer.valueOf(eDay[2]);

        WaitUtil.waitFor(1);
        // 보험 개시일로부터 6일 뒤(보험 종료일)가 다음 달이면 달력 넘기기 버튼 클릭
        if (startMonth != endMonth && endMonth != mMonth) {
            driver.findElement(By.xpath("//div[@id='ui-datepicker-div']/div/a[2]")).click();
        }
        driver.findElement(By.xpath("//a[@class='ui-state-default'][text()='" + endDay + "']")).click();

        logger.info("도착 시간(보험 종료 시간) 선택");
        sel = new Select(driver.findElement(By.xpath("//select[@id='insEdTm']")));
        sel.selectByValue("23");
    }



    protected void mobileSetCountry() {
        logger.info("여행 국가 선택");
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//strong[@id='trvAraNm2']/following-sibling::span[text()='도착']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//label[@for='country01']"))).click();
        driver.findElement(By.xpath("//div[@id='GITR8013G']/div/div/div[2]/div/button[text()='확인']")).click();
    }



    @Override
    protected void mobileSetBirth(CrawlingProduct product) {
        logger.info("mobileSetBirth 생일 : {}", product.getBirth());
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='abBirth']"))).sendKeys(product.getBirth());
    }



    @Override
    protected void mobileSetGender(int gender) throws InterruptedException {
        logger.info("mobileSetGender 성별 : {}", gender == 0 ? "남자" : "여자");
        driver.findElement(By.xpath("//button[@id='genderNm']")).click();
        if (gender == 0) { // 남자
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//label[@for='male']"))).click();
        } else { // 여자
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//label[@for='female']"))).click();
        }
        driver.findElement(By.xpath("//div[@id='GITR8043G']/div/div/div[2]/div/button[text()='확인']")).click();
        WaitUtil.waitFor(1);
    }



    @Override
    protected void mobileGetPremium(CrawlingProduct info) {
        // 중복담보 제외 후 진행 버튼 클릭하면 원수사 특약이 가설과 일치하게 선택됨.
        driver.findElement(By.xpath("//*[@id='content']/div[2]/div/button[text()='다음']")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='GITR8009G']/div/div/div[2]/div[1]/button[text()='중복담보 제외 후 진행']"))).click();
        try {
            WaitUtil.waitFor(5);
            String price = driver.findElement(By.xpath("//strong[@id='premiumAmt']")).getText().replaceAll("[^0-9]","");
            // 가입유형 선택!
            info.treatyList.get(0).monthlyPremium = price;
            logger.info("mobileGetPremium : 월보험료 {} ", info.treatyList.get(0).monthlyPremium);
        } catch (InterruptedException e) {
            logger.error("mobileGetPremium InterruptedException : {}", e.getMessage());
        }
    }



    // 특약 체크 해제하는 코드(mobileGetPremium에서 진행한 특약 설정과 내 가설 특약이 달라지면 mobileGetPremium2 적용시킬것)
    protected void mobileGetPremium2(CrawlingProduct info) throws InterruptedException {
        driver.findElement(By.xpath("//*[@id='content']/div[2]/div/button[text()='다음']")).click();
        WaitUtil.waitFor(5);

        // X(닫기)버튼 클릭
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='GITR8009G']/div/div/button"))).click();

        // 닫기버튼을 클릭하면 특약이 모두 체크되어있는 상황이기 때문에 가설에 존재하지 않은 특약만 체크 해제
        List<WebElement> planBodyList        = driver.findElements(By.xpath("//ul[@id='planBody']/li"));
        List<CrawlingTreaty> userTreatyList  = info.getTreatyList();
        List<String> duplicatedTreatyNmList  = new ArrayList(); // 모바일 특약 List + 가설 특약 List
        List<String> leftTreatyNmList;                          // 중복된 특약을 제외하고 남은 특약 List(가설에 포함되지 않은 특약이름 List)

        for (WebElement planBody : planBodyList) {
            String uTreatyNm = planBody.findElement(By.xpath("./div/label")).getText();
            if (uTreatyNm.contains("(필수)")) { uTreatyNm = uTreatyNm.replaceAll("\\(필수\\)", ""); }
            duplicatedTreatyNmList.add(uTreatyNm);
        }

        for (CrawlingTreaty cTreaty : userTreatyList) {
            String uTreatyNm = cTreaty.getTreatyName();
            duplicatedTreatyNmList.add(uTreatyNm);
        }

        /* duplicatedTreatyNmList에 모바일 특약, 가설 특약을 담고 중복 데이터를 모두 제거해줌으로 가설에 포함되지 않은 특약만 leftTreatyNmList에 존재하게 된다.
            따라서 크롤링 시, LeftTreatyNmList에 존재하는 가설만 체크 해제한다.
            ex) 모바일특약 해외여행 상해, 해외여행 질병
                가설특약  해외여행 상해
           duplicatedTreatyNmList에는 ["해외여행 상해", "해외여행 상해", "해외여행 질병"]가 담기며 중복된 데이터를 제거한
           leftTreatyNmList에는       ["해외여행 질병"]이 남기 때문에 크롤링 시 가설에 포함되지 않은 특약 항목인 "해외여행 질병"만 체크해제 한다.
        * */
        leftTreatyNmList = duplicatedTreatyNmList
                .stream()
                .filter(value -> duplicatedTreatyNmList
                        .stream()
                        .filter(v -> v.equals(value))
                        .count() <= 1)
                .collect(Collectors.toList());

        int planBodyListSize = planBodyList.size();
        for (int i = 0; i < planBodyListSize; i++) {
            String uTreatyNm = driver.findElement(By.xpath("//ul[@id='planBody']/li[" + (i+1) + "]/div/label")).getText();
            for (String leftTreatyNm : leftTreatyNmList) {
                if (leftTreatyNm.equals(uTreatyNm)) {
                    logger.info("특약 체크 해제 : {}", leftTreatyNm);
                    boolean isSelected = driver.findElement(By.xpath("//ul[@id='planBody']/li[" + (i+1) + "]/div/input")).isSelected();
                    if (isSelected) {
                        logger.info("가설에 없는 특약이 선택 되어 있어 체크 해제");
                        // 스크롤 이동
                        element = driver.findElement(By.xpath("//ul[@id='planBody']/li[" + (i+1) + "]/div/label"));
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
                        WaitUtil.waitFor(1);

                        // 체크박스 해제
                        driver.findElement(By.xpath("//ul[@id='planBody']/li[" + (i+1) + "]/div/label")).click();
                        WaitUtil.waitFor(1);
                        boolean isModalDisplayed = driver.findElement(By.xpath("//div[@id='UIAlert']")).isDisplayed();
                        if (isModalDisplayed) {
                            logger.info("모달창 닫기");
                            driver.findElement(By.xpath("//div[@id='UIAlert']/div/div/div[3]/div/button")).click();
                            WaitUtil.waitFor(5);
                        }
                    }
                    break;
                }
            }
        }

        String price = driver.findElement(By.xpath("//strong[@id='premiumAmt']")).getText().replaceAll("[^0-9]","");
        info.treatyList.get(0).monthlyPremium = price;
        logger.info("mobileGetPremium : 월보험료 {} ", info.treatyList.get(0).monthlyPremium);
    }

}