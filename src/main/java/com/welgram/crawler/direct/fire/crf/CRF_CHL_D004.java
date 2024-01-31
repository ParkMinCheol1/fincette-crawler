package com.welgram.crawler.direct.fire.crf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.PersonNameGenerator;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;



// 2022.12.05           | 최우진           | 다이렉트_어린이
// CRF_CHL_D004         | 마음튼튼 우리아이보험
public class CRF_CHL_D004 extends CrawlingCRFMobile {

    public static void main(String[] args) { executeCommand(new CRF_CHL_D004(), args); }

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setMobile(true);
        logger.info("크롤링(모니터링) 환경을 모바일로 전환합니다");
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        String genderOpt = info.getGender() == MALE ? "gender_1" : "gender_2";
        String genderText = (info.getGender() == 0) ? "남자" : "여자";

        logger.info("CRF_CHL_D004 :: {}", info.getProductName());

        logger.info("맞춤 플랜 시작하기 버튼 클릭");
        btnClick(By.xpath("//span[text()='맞춤 플랜 시작하기']/parent::button"), 2);

        logger.info("이름 설정");
        setUserName(By.id("customerName"), PersonNameGenerator.generate());

        logger.info("성별 :: {}", genderText);
        setGender(By.xpath("//label[@for='" + genderOpt + "']"), genderText);

        logger.info("생년월일 :: {}", info.getFullBirth());
        setBirthday(By.id("birthday"), info.getFullBirth());

        logger.info("다음 버튼 클릭");
        clickNextBtn("span");

        logger.info("특약 확인");
        setTreaties(info.getTreatyList());

        logger.info("보험료 가져오기");
        crawlPremium(info, By.xpath("//p[text()='월 보험료']/parent::div/span"));

        logger.info("스크린샷");
        takeScreenShot(info);

        logger.info("해약환급금정보 없음 :: CRF ");

        return true;
    }

    public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {

        try{
            int scrollTop = 0;
            String homepageTreatyname = ""; // 홈페이지의 특약명
            String homepageTreatyAmt = ""; // 홈페이지의 특약금액
            String welgramTreatyName = ""; // 가설특약명

            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
            List<WebElement> $homepageTreatyTrList = new ArrayList<>();
            $homepageTreatyTrList = driver.findElements(By.xpath("//tr"));

            for(WebElement homepageTreaty : $homepageTreatyTrList){
                boolean exist = false;
                homepageTreatyname = homepageTreaty.findElement(By.xpath("./th")).getText().trim();
                homepageTreatyAmt = homepageTreaty.findElement(By.xpath("./td")).getText().trim();

                // 스크롤 이동
                element = driver.findElement(By.xpath("//*[text()='" + homepageTreatyname + "']"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
                WaitUtil.waitFor(1);

                // 가설 특약과 비교
                for (int j = 0; j < welgramTreatyList.size(); j++) {
                    welgramTreatyName = welgramTreatyList.get(j).treatyName.trim();

                    if (homepageTreatyname.contains(welgramTreatyName)) { // 특약명 일치
                        // 가입금액 변환
                        homepageTreatyAmt = String.valueOf(MoneyUtil.toDigitMoney(homepageTreatyAmt));

                        logger.info("===========================================================");
                        logger.info("특약명 :: {}", homepageTreatyname);
                        logger.info("가입금액 :: {}", homepageTreatyAmt);
                        logger.info("===========================================================");

                        CrawlingTreaty targetTreaty = new CrawlingTreaty();
                        targetTreaty.setTreatyName(homepageTreatyname);
                        targetTreaty.setAssureMoney(Integer.parseInt(homepageTreatyAmt));

                        targetTreatyList.add(targetTreaty);

                        exist = true;
                        break;
                    }

                    scrollTop += 20;
                    ((JavascriptExecutor)driver).executeScript("window.scrollTo(0, " + scrollTop + ");");
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

        } catch(Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

}