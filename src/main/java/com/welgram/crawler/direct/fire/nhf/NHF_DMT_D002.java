package com.welgram.crawler.direct.fire.nhf;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetTravelPeriodException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;

public class NHF_DMT_D002 extends CrawlingNHFDirect {



    public static void main(String[] args) {
        executeCommand(new NHF_DMT_D002(), args);
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setIniSafe(true);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        boolean _result = false;

        _result = frontPage(info);

        return _result;
    }

    private boolean frontPage(CrawlingProduct info) throws Exception {

        boolean result = false;
        String genderOpt = (info.getGender() == 0) ? "tra01_01" : "tra01_02";
        String genderText = (info.getGender() == MALE) ? "남" : "여";

        logger.info("NHF_DMT_D002 :: {}", info.getProductName());
        // 서버에서 모니터링을 돌릴 경우 타임아웃으로 실패가 많아 대기시간을 많이 준다.
        WaitUtil.waitFor(30);
        chkSecurityProgram();

        logger.info("담보 중복가입 안내창 팝업");
        checkPopup();

        wait.until(ExpectedConditions.elementToBeClickable(By.id("fRlno")));
        logger.info("생년월일 :: {}", info.getFullBirth());
        setBirthday(By.id("fRlno"), info.getFullBirth());

        logger.info("성별 설정 :: {}", genderText);
        setGender(By.xpath("//label[@for = '" + genderOpt + "']"), genderText);

        //출발일, 도착일 선택
        logger.info("여행기간 선택");
        setTravelDate();

        logger.info("국내여행 선택");
        btnClick(By.xpath("//label[@for='tra03_01']"), 2);

        logger.info("동반자 [아니요] 선택");
        btnClick(By.xpath("//label[@for='tra04_02']"), 2);

        logger.info("보험료 계산하기 선택");
        btnClick(By.id("Anext"), 3);
        waitHomepageLoadingImg();

        logger.info("특약 확인");
        setTreaties(info);

        logger.info("보험료 가져오기");
        crawlPremium(By.id("silprice"), info);

        logger.info("스크린샷");
        takeScreenShot(info);

        result = true;
        return result;
    }

    // 담보 중복가입 안내창 팝업
    protected void checkPopup() throws Exception {

        try {
            if (driver.findElement(By.xpath("//*[@id='planpop']")).isDisplayed()) {

                driver.findElement(By.linkText("확인")).click();
                WaitUtil.waitFor(2);
            }
        } catch (Exception e) {
            logger.info("알럿표시 없음!!!");
        }
    }

    //출발일은 실제 오늘을 기준으로 7일 후이며 도착일은 출발일로 부터 7일을 더한 날짜를 반환
    protected void setTravelDate() throws Exception {

        try{
            driver.findElement(By.id("oInsOpenDate")).click();
            WaitUtil.waitFor(2);

            String departureDate = plusDateBasedOnToday(7);
            selectDay(departureDate, "stTime", "00:00");

            String arrivalDate = plusDateBasedOnToday(13);
            selectDay(arrivalDate, "edTime", "24:00");

            driver.findElement(By.xpath("//button[contains(text(), '확인')]")).click();
            WaitUtil.waitFor(2);

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
            throw new SetTravelPeriodException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    /*
    * 여행 날짜/시간 선택
    * @param1 : 오늘 기준 더할 날짜
    * @param2 : 출발/도착 시간 요소 id
    * @param3 : 출발/도착 시간
    * */
    protected void selectDay(String day, String timeId, String time) throws Exception {

        WebElement $dayDiv = null;

        int month = Integer.parseInt(day.substring(4,6));
        day = day.substring(6);
        int plusDay = Integer.parseInt(day);
        String selectDay = Integer.toString(plusDay);

        logger.info("날짜 선택");
        $dayDiv = driver.findElement(By.xpath("//div[@aria-label[contains(., '"+month+"월 "+selectDay+"')]][@class[not(contains(., 'empty'))]]//div[text()='"+selectDay+"']"));

        if($dayDiv.getText().equals("")){
            $dayDiv = driver.findElement(By.xpath("//div[@class='mbsc-calendar-slide mbsc-ios mbsc-ltr'][3]//div[@aria-label[contains(., '"+month+"월 "+selectDay+"')]][@class[not(contains(., 'empty'))]]//div[text()='"+selectDay+"']"));
        }
        btnClick($dayDiv);

        logger.info("{} Day :: {}", timeId, selectDay);
        logger.info("selected Day :: {}", $dayDiv.getText());
        if(!selectDay.equals($dayDiv.getText())){
            throw new Exception();
        }

        logger.info("시간 선택");
        helper.selectByText_check(By.id(timeId), time);
    }

    public void setTreaties(CrawlingProduct info) throws SetTreatyException{

        try{
            String homepageTreatyname = ""; // 홈페이지의 특약명
            String homepageTreatyMoney = ""; // 홈페이지의 특약금액
            String welgramTreatyName = "";

            List<WebElement> homepageTreatyList = new ArrayList<>(); // 홈페이지 특약 요소 리스트
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>(); // 홈페이지 특약 리스트
            List<CrawlingTreaty> welgramTreatyList = info.getTreatyList(); // 가설 특약 리스트

            homepageTreatyList = driver.findElements(By.xpath("//*[@id='sil1']//li"));

            for (WebElement homepageTreaty : homepageTreatyList) {
                homepageTreatyname = homepageTreaty.findElement(By.tagName("dt")).getText().trim();
                homepageTreatyMoney = homepageTreaty.findElement(By.tagName("dd")).getText().replaceAll("[^0-9]", "");

                for(CrawlingTreaty welgramTreaty : welgramTreatyList){

                    welgramTreatyName = welgramTreaty.getTreatyName().trim();
                    // 특약명 일치
                    if(homepageTreatyname.contains(welgramTreatyName)){

                        logger.info("=============================================");
                        logger.info("특약명 :: {}", homepageTreatyname);
                        logger.info("가입금액 :: {}", homepageTreatyMoney);
                        logger.info("=============================================");

                        CrawlingTreaty targetTreaty = new CrawlingTreaty();
                        targetTreaty.setTreatyName(homepageTreatyname);
                        targetTreaty.setAssureMoney(Integer.parseInt(homepageTreatyMoney));

                        targetTreatyList.add(targetTreaty);
                        break;
                    }
                }
            }

            logger.info("특약 비교 및 확인");
            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());

            if (result) {
                logger.info("특약 정보가 모두 일치합니다");
            } else {
                logger.error("특약 정보 불일치");
                throw new Exception();
            }
        }catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(exceptionEnum.getMsg());
        }

    }
}
