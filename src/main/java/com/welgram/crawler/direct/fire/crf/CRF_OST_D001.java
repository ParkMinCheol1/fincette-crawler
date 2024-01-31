package com.welgram.crawler.direct.fire.crf;

import com.welgram.common.PersonNameGenerator;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setUserInfo.SetTravelPeriodException;
import com.welgram.crawler.general.CrawlingProduct;
import java.time.LocalDate;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;


// 2022.12.01 		| 최우진 			| 다이렉트_해외여행
// CRF_OST_D001 	| 캐롯해외여행자보험
public class CRF_OST_D001 extends CrawlingCRFDirect {

  public static void main(String[] args) {
    executeCommand(new CRF_OST_D001(), args);
  }


  @Override
  protected boolean scrap(CrawlingProduct info) throws Exception {

    String genderOpt = (info.getGender() == 0) ? "gender1" : "gender2";
    String genderText = (info.getGender() == 0) ? "남자" : "여자";

    logger.info("START :: CRF_OST_D001 :: {}", info.getProductName());
    WaitUtil.loading(6);

    logger.info("자동차보험 가입 모달창 끄기");
    closeModal();

    logger.info("이름 입력");
    setUserName(By.xpath("//*[@id='customerName']"), PersonNameGenerator.generate());

    logger.info("생년월일 입력 :: {}", info.getFullBirth());
    setBirthday(By.id("birthday"), info.getFullBirth());

    logger.info("성별 설정 :: {}", genderText);
    setGender(By.xpath("//label[@for='" + genderOpt + "']"), genderText);

    logger.info("next 버튼 클릭");
    btnClick(By.xpath("//button[@id='btn-basic-start']"), 2);

    logger.info("가입시 유의사항 레이어");
    checkPopup(By.xpath("//strong[contains(.,'유의사항')]/parent::div//following-sibling::div//button"));

    logger.info("여행 날짜 설정");
    setTravelDate();

    logger.info("다음 클릭");
    btnClick(By.cssSelector("#btn-basic-end"), 2);

    logger.info("담보 중복 가입 안내 레이어 닫기");
    checkPopup(By.cssSelector("#OverlapGuidePop > div:nth-child(1) > div > div > div.box-footer > button"));

    logger.info("보험 플랜 선택 :: {}", info.getTextType());
    btnClick(By.xpath("//strong[contains(text(), '" + info.getTextType() + "')]"), 2);

    logger.info("특약 세팅");
    setTreaties(info.getTreatyList(), By.xpath("//tbody//tr"));

    logger.info("보험료 가져오기");
    crawlPremium(info, By.xpath("//li[@class='plan-select-item selected']//em[@class='item-stit']"));

    logger.info("스크린샷");
    ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, -500)");
    takeScreenShot(info);

    logger.info("해약환급금정보 없음");

    return true;
  }

  @Override
  public void setTravelDate(Object... obj) throws SetTravelPeriodException {

    try {
      logger.info("여행시작일 선택");
      driver.findElement(By.id("startDate_dd1")).click();
      WaitUtil.waitFor(2);

      // 오늘 정보
      LocalDate today = LocalDate.now();
      LocalDate departureDateInfo = today.plusDays(7);    // 7일 뒤 출발
      LocalDate arrivalDateInfo = today.plusDays(13);      // 13일 뒤 도착

      // 출발, 도착 날짜 정보 String, Date | 년, 월, 일
      String todayMonth = String.valueOf(today.getMonthValue());
      String departMonth = String.valueOf(departureDateInfo.getMonthValue());
      String departDate = String.valueOf(departureDateInfo.getDayOfMonth());
      String arrivalMonth = String.valueOf(arrivalDateInfo.getMonthValue());
      String arrivalDate = String.valueOf(arrivalDateInfo.getDayOfMonth());

      if (departDate.length() == 1) {
        departDate = "0" + departDate;
      }
      if (arrivalDate.length() == 1) {
        arrivalDate = "0" + arrivalDate;
      }

      logger.info("======================================");
      logger.info("departMonth  :: {}", departMonth);
      logger.info("departDate   :: {}", departDate);
      logger.info("arrivalMonth :: {}", arrivalMonth);
      logger.info("arrivalDate  :: {}", arrivalDate);
      logger.info("======================================");

      logger.info("출발일 선택");
      setTravelDepatureDate(today, departureDateInfo, departDate);

      logger.info("출발시간 선택");
      setTravelTime("startTime", "startTime_1");

      logger.info("도착일 선택");
      setTravelArrivalDate(departMonth, arrivalMonth, arrivalDate);

      logger.info("도착시간 선택");
      setTravelTime("endTime", "endTime_24");

    } catch (Exception e) {
      ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
      throw new SetTravelPeriodException(exceptionEnum.getMsg() + "\n" + e.getMessage());
    }
  }

  // 여행출발날짜 선택
  @Override
  protected void setTravelDepatureDate(LocalDate today, LocalDate departureDateInfo, String departDate) throws SetTravelPeriodException{

    try{
      if(today.getMonthValue() != departureDateInfo.getMonthValue()) {

        logger.info("다음달 선택하기");
        driver.findElement(By.cssSelector("#calWrap_startDate_dd1 > div.datepicker-head > div.datepicker-head-btn > button.btn-arrow.ui-datepicker-next")).click();
      }

       selectDay(departDate, driver.findElements(By.cssSelector("#calWrap_startDate_dd1 > div.datepicker-core > table > tbody > tr")));

    } catch (Exception e){
      ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
      throw new SetTravelPeriodException(exceptionEnum.getMsg() + "\n" + e.getMessage());
    }
  }

  // 여행도착날짜 선택
  @Override
  protected void setTravelArrivalDate(String departMonth, String arrivalMonth, String arrivalDate) throws SetTravelPeriodException{

    WebElement $button = null;
    List<WebElement> $trList = null;

    try{
      $button = driver.findElement(By.id("endDate_dd1"));
      btnClick($button, 2);

      // 도착월이 자동으로 다음달로 세팅되는 경우 존재
      if(!arrivalMonth.equals(departMonth)) {
        // 현재 선택된 월을 가져와서 비교
        String selectedMonth = driver.findElement(By.cssSelector("#calWrap_endDate_dd1 > div.datepicker-head > div.datepicker-head-date > span.month"))
            .getText().replaceAll("[^0-9]","");

        if(arrivalMonth.length() == 1 ) {
          arrivalMonth = "0" + arrivalMonth;
        }

        if(!selectedMonth.equals(arrivalMonth)){
          $button = driver.findElement(By.cssSelector("#calWrap_endDate_dd1 > div.datepicker-head > div.datepicker-head-btn > button.btn-arrow.ui-datepicker-next"));
          btnClick($button, 2);
        }
      }

      $trList = driver.findElements(By.cssSelector("#calWrap_endDate_dd1  > div.datepicker-core > table > tbody > tr"));
      selectDay(arrivalDate, $trList);

    } catch (Exception e){
      ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
      throw new SetTravelPeriodException(exceptionEnum.getMsg() + "\n" + e.getMessage());
    }
  }

}
