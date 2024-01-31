package com.welgram.crawler.direct.fire.acf;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.SetProductTypeException;
import com.welgram.common.except.crawler.setUserInfo.SetTravelPeriodException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// 2023.01.02       | 최우진           | 다이렉트_운전자
// ACF_DRV_D003     | Chubb One-Day레저보험 5종(운전자 플랜)
public class ACF_DRV_D003 extends CrawlingACFDirect {

    public static Logger logger = LoggerFactory.getLogger(ACF_DRV_D003.class);

    public static void main(String[] args) {
        executeCommand(new ACF_DRV_D003(), args);
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setBrowserType(BrowserType.Chrome);
        option.setImageLoad(true);
        option.setUserData(false);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        String genderOpt = (info.getGender() == 0) ? "sexCode1" : "sexCode2";
        String genderText = (info.getGender() == 0) ? "남자" : "여자";
        String productType = "운전자";
        String vehicleUseYn = "예";

        logger.info("START :: ACF_DRV_D003 :: {}", info.getProductName());
        WaitUtil.loading(3);

        logger.info("유형선택 :: {}", productType);
        setProductType(By.xpath("//label[@for='productType2ONE052']"), productType);

        logger.info("생년월일 :: {}", info.getBirth());
        setBirthday(By.name("insuredBirth"), info.getBirth());

        logger.info("성별 설정 :: {}", genderText);
        setGender(By.xpath("//label[@for='" + genderOpt + "']"), genderText);

        logger.info("운전의 용도(자가용) 선택 :: {}}", vehicleUseYn);
        setVehicle(By.xpath("//span[contains(.,'" + vehicleUseYn + "')]"), vehicleUseYn);

        logger.info("레저일정 선택");
        setTravelDate();

        logger.info("보험료 계산 버튼");
        btnClick(By.id("btnNext"), 2);

        logger.info("보험료 확인");
        crawlPremium(By.cssSelector("#resultTable > thead > tr > th.on > div > label > span > em"), info);

        logger.info("특약 비교");
        compareTreaties(info);

        logger.info("스크린샷");
        takeScreenShot(info);

        return true;
    }



    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {

        try{
            By by = (By) obj[0];
            String productType = (String) obj[1];

            driver.findElement(by).click();
            WaitUtil.waitFor(2);

            checkValue("상품유형", productType, by);

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_TYPE;
            throw new SetProductTypeException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setTravelDate(Object... obj) throws SetTravelPeriodException {

        // TODO By를 파라미터를 받는 것 고려해볼 것 : 나머지 파라미터가 영향을 받기에 실효성 의문.
        try{
            WebElement $button = null;
            String selectedDate = "";

            logger.info("시작일시 (달력) 선택");
            $button = driver.findElement(By.cssSelector("#policyStartTime"));
            $button.click();
            WaitUtil.waitFor(1);

            // 2022.03.28 | 김용준 | 오늘에 해당하는 class만 골라 찍음
            $button = driver.findElement(By.className("xdsoft_today"));
            selectedDate = $button.getText();
            $button.click();
            WaitUtil.waitFor(1);

            logger.info("선택된 날짜 :: {}", selectedDate);

            // 선택완료 버튼
            driver.findElement(By.cssSelector("#_btnCalendarSelect_")).click();
            WaitUtil.waitFor(1);

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
            throw new SetTravelPeriodException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }
}
