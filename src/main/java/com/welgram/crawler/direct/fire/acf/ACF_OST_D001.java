
package com.welgram.crawler.direct.fire.acf;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// 2022.11.02 | 최우진 | 신규
// ACF_OST_D001 :: (무)Chubb 해외여행보험
public class ACF_OST_D001 extends CrawlingACFDirect {

    public static final Logger logger = LoggerFactory.getLogger(ACF_OST_D001.class);

    public static void main(String[] args) {
        executeCommand(new ACF_OST_D001(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        crawlFromHomepage(info);

        return true;
    }

    public void crawlFromHomepage(CrawlingProduct info) throws Exception {

        /*
            유의사항
            2022.11 | 출발 날짜 선택시, 도착 날짜를 정하는 달력 엘리먼트에 대하여 이상한 케이스가 있었습니다
            말일이 31일인경우, 짝수월인 경우, 해가 바뀌는 경우 등에 한하여 달력 선택이 안되거나 임의로 선택날짜가 바뀔 수 있습니다
            2022.11 기준으로 해당 오류가 대부분 없어졌지만, 만약을 생각해 주석으로 남겨놓습니다.
        */

      String genderOpt = (info.getGender() == 0) ? "policyHolderSexCode_1" : "policyHolderSexCode_2";
      String genderText = (info.getGender() == 0) ? "남자" : "여자";
      String label = ""; // 플랜 위치
      String premiumId = ""; // 보험료 위치

      // 플랜 위치
      if (info.getTextType().contains("Basic+")) {
        label = "planSelector_1";
        premiumId = "premium_1";
      }
      else if (info.getTextType().contains("Premier")) {
        label = "planSelector_2";
        premiumId = "premium_2";
      } else if(info.getTextType().contains("자유설계")) {
        label = "planSelector_3";
        premiumId = "premium_3";
      }

      logger.info("START :: ACF_OST_D001 :: {}", info.getProductName());
      driver.manage().window().maximize();
      WaitUtil.loading(8);

      logger.info("여행일정 선택");
      setTravelDate();

      logger.info("여행목적 선택 :: 여행/관광");
      setTravelPurpose(By.cssSelector(".col2:nth-child(1) .radio_type2:nth-child(1) > span"), "여행/관광");

      logger.info("가입유형 선택 :: 본인");
      SubscriptionType(By.cssSelector(".col2:nth-child(2) .radio_type2:nth-child(1) > span"), "본인");

      logger.info("생년월일 입력 :: {}", info.getBirth());
      setBirthday(By.id("policyHolderBirth"), info.getBirth());

      logger.info("성별 설정 :: {}", genderText);
      setGender(By.xpath("//label[@for='" + genderOpt + "']"), genderText);

      logger.info("보험료 계산 버튼");
      btnClick(By.id("btnNext"), 10);

      logger.info("중복가입내용 레이어확인");
      btnClick(By.cssSelector(".btn span"), 2);

      logger.info("플랜 설정");
      setPlan(info, label);

      if(info.getTextType().contains("자유설계")){
        logger.info("특약 설정");
        setTreaties(info.getTreatyList());
      }

      logger.info("보험료 확인");
      crawlPremium(By.id(premiumId), info);

      logger.info("특약 비교");
      compareTreaties(info);

      logger.info("스크린샷");
      takeScreenShot(info);
    }

    public void setTravelPurpose(By by, String purpose) throws CommonCrawlerException {

      try{
        btnClick(by, 1);
        checkValue("여행목적", purpose, by);

      } catch (Exception e){
        throw new CommonCrawlerException("여행목적 설정 중 에러가 발생했습니다.\n" + e.getMessage());
      }

    }
    
    public void SubscriptionType(By by, String type) throws CommonCrawlerException {
  
      try{
        btnClick(by, 1);
        checkValue("여행유형", type, by);

      } catch (Exception e){
        throw new CommonCrawlerException("여행유형 설정 중 에러가 발생했습니다.\n" + e.getMessage());
      }
  
    }


    /* 서브플랜설정
     * @param1 info
     * @param2 label 플랜 위치
     */
    protected void setPlan(CrawlingProduct info, String label) throws Exception {

      String planName = ""; // 홈페이지 플랜명
      String textType = info.getTextType();
      By by = null;

      try {
        if (textType.contains("자유설계")) {
          logger.info("자유설계로 가입하기 버튼 클릭 ");
          WebElement $button = driver.findElement(By.xpath("//*[@class='pgBtnShowFree']"));
          ((JavascriptExecutor) driver).executeScript("arguments[0].click();", $button);
          WaitUtil.waitFor(2);
        }

        logger.info("플랜 선택 클릭 및 확인 ");
        by = By.cssSelector("label[for ='" + label + "']");
        btnClick(by, 2);

        planName = driver.findElement(By.cssSelector(".on > div > label > span")).getText();
        logger.debug("{} 플랜 선택완료", planName);

      } catch (Exception e) {
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
        throw new PremiumCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());
      }
    }

    // 특약일치여부 확인
    @Override
    protected void compareTreaties(CrawlingProduct info) throws CommonCrawlerException {

        try {
            WebElement $td = null;
            WebElement node = null;
            String homepageTreatyName = ""; // 원수사 홈페이지의 특약 이름
            String homepageTreatyAmt = ""; // 원수사 홈페이지의 특약 금액
            String script = "";

            List<CrawlingTreaty> welgramTreatyList = info.getTreatyList();
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
            List<WebElement> $trList = driver.findElements(By.xpath("//table[@id='resultTable']/tbody/tr"));

            for (WebElement $tr : $trList) {
                homepageTreatyName = $tr.findElements(By.tagName("td")).get(0).getText().trim();
                $td = $tr.findElement(By.xpath("./td[contains(@class, 'on')]"));
                node = $td.findElement(By.xpath(".//*[name()='input' or name()='select']"));

                if ("input".equals(node.getTagName())) {
                    //실제 홈페이지에서 설정된 보험기간 조회
                    homepageTreatyAmt = node.getText().trim();

                } else if ("select".equals(node.getTagName())) {
                    //실제 홈페이지에서 클릭된 select option 값 조회
                    script = "return $(arguments[0]).find('option:selected').text();";
                    homepageTreatyAmt = String.valueOf(helper.executeJavascript(script, node)).trim();
                }
                logger.info("=============================================");
                logger.info("특약명 :: {}", homepageTreatyName);

                if(!homepageTreatyAmt.equals("가입안함")){
                    homepageTreatyAmt = toDigitMoney(homepageTreatyAmt);
                    logger.info("가입금액 :: {}", homepageTreatyAmt);

                    CrawlingTreaty targetTreaty = new CrawlingTreaty();
                    targetTreaty.setTreatyName(homepageTreatyName);
                    targetTreaty.setAssureMoney(Integer.parseInt(homepageTreatyAmt));

                    targetTreatyList.add(targetTreaty);
                }
            }
            logger.info("=============================================");
            logger.info("특약 비교");
            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());

            if (result) {
                logger.info("특약 정보가 모두 일치합니다");
            } else {
                logger.error("특약 정보 불일치");
                throw new Exception();
            }
        } catch(Exception e){
            throw new CommonCrawlerException(ExceptionEnum.ERROR_BY_TREATIES_COMPOSIOTION + "\n" + e.getMessage());
        }
    }
}
