package com.welgram.crawler.direct.fire.mgf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author user MG손해보험 (무)건강명의다이렉트실손의료비보험
 */
public class MGF_MDC_D004 extends CrawlingMGFDirect {

    public static void main(String[] args) {
        executeCommand(new MGF_MDC_D004(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        crawlFromHomepage(info);

        return true;
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setBrowserType(BrowserType.Chrome);
        option.setImageLoad(true);
        option.setUserData(false);
    }


    private void crawlFromHomepage(CrawlingProduct info) throws Exception {

            String gender = (info.getGender() == MALE) ? "male" : "female";


            logger.info("보험료 계산하기 버튼");
            btnClick(driver.findElement(By.cssSelector("#cont > div.cont_set.Bg05 > button")), 1);
//            waitForCSSElement(".Loading_area");

      //안내사항 팝업이 존재할 때
//            try{
//                driver.findElement(By.cssSelector("#layer01infoPopUp2 > div.layer_cont > label"));
//                WaitUtil.loading(1);
//                driver.findElement(By.cssSelector("#infoPopUp2Close")).click();
//                logger.info("안내사항 팝업 : 하루동안 그만보기 클릭");
//            }catch (Exception e){
//                logger.info("안내사항 팝업이 없습니다.");
//            }


            logger.info("생년월일 :: {}", info.getBirth());
            setBirthday(driver.findElement(By.cssSelector("#birthDay")), info.getBirth());

            logger.info("성별 :: {}", (info.getGender() == MALE) ? "남성" : "여성");
            setGender(driver.findElement(By.xpath("//*[@id='" + gender + "']")));

            logger.info("전화번호 :: 01043211234");
            setPhoneNum(driver.findElement(By.cssSelector("#hdPhoneNum")), "01043211234");

            logger.info("보험료 계산하기 클릭");
            btnClick(driver.findElement(By.cssSelector("#contPremCalcBtn")), 2);

            logger.info("개인정보 수집 활용 동의 :: 전체 동의");
            privacyPopup();

            // [임시] 공통메서드 특약확인 위함 :: 특약명 수정시 삭제해주세요
            for(int i = 0; i < info.treatyList.size(); i++){
              CrawlingTreaty treaty = info.treatyList.get(i);
              logger.info("before :: {}", treaty.getTreatyName());
              treaty.treatyName = treaty.treatyName.substring(treaty.treatyName.indexOf("_") + 1);
              logger.info("after :: {}", treaty.getTreatyName());

            }

            logger.info("특약 확인");
            checkTreaties(driver.findElements(By.xpath("//*[@id=\"cvrInfoList\"]/tr")), info.getTreatyList(), "td_A0");

            logger.info("납입방법 설정 :: {}", info.getNapTerm());
            setNapCycle(driver.findElement(By.cssSelector("#selPayMethod")), info.getNapCycle());

            logger.info("월납 보험료 저장");
            crawlPremium(driver.findElement(By.cssSelector("#insSum1")), info);

            logger.info("스크린샷");
            takeScreenShot(info);
    }

  // 특약 일치여부 확인
  protected void checkTreaties(List<WebElement> $trList, List<CrawlingTreaty> welgramTreatyList, String $tdId) throws CommonCrawlerException {

    List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
//    int unit = 10000;

    for (int i = 1; i < $trList.size(); i++) { // tr[1]에는 th만 존재
      WebElement $tr = $trList.get(i);
      
      // 페이지 특약명
      WebElement pageTreatyNameTd = $tr.findElement(By.xpath(".//*[@class='item02']"));
      String pageTreatyName = pageTreatyNameTd.getText().trim();
      // 페이지 가입금액
      WebElement pageTreatyMoneyTd = $tr.findElement(By.xpath(".//*[@class='item02']/following-sibling::td"));
      String pageTreatyMoneyString = pageTreatyMoneyTd.getText().trim();
      int pageTreatyMoney = Math.toIntExact(MoneyUtil.toDigitMoney(pageTreatyMoneyString)) ;

      logger.info("========================================");
      logger.info("페이지 특약명 : {}", pageTreatyName);
      logger.info("페이지 가입금액 : {}", pageTreatyMoney);
      logger.info("----------------------------------------");

      for (CrawlingTreaty welgramTreaty : welgramTreatyList) {
        String wTreatyName = welgramTreaty.getTreatyName();
        int wTreatyMoney = welgramTreaty.getAssureMoney();

        if (pageTreatyName.equals(wTreatyName)) {
          CrawlingTreaty targetTreaty = new CrawlingTreaty();

          targetTreaty.setTreatyName(pageTreatyName);
          targetTreaty.setAssureMoney(pageTreatyMoney);

          logger.info(" 가설 특약명 : {}", wTreatyName);
          logger.info(" 가설 가입금액 : {}", wTreatyMoney);
          logger.info("========================================");

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
      throw new CommonCrawlerException();
    }
  }
}
