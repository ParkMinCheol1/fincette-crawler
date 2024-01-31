package com.welgram.crawler.direct.fire.crf;

import com.welgram.common.PersonNameGenerator;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;


// 2022.06.24           | 최우진               | 다이렉트_질병보험
// CRF_DSS_D002         | 캐롯 직장인 생활건강보험
public class CRF_DSS_D002 extends CrawlingCRFMobile {

    public static void main(String[] args) {
        executeCommand(new CRF_DSS_D002(), args);
    }



    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setMobile(true);
        logger.info("크롤링(모니터링) 환경을 모바일로 전환합니다");
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // todo | 20220624 모든 특약 사용 >> (추후 변경시) 일부 특약만 사용할 수 있음
        // todo | 일부 특약만 사용시 플랜타입에 따라서 사용하는 특약을 (확인+조절) 하는 코드 필요

        String[] arrTextType = null;
        String genderOpt = (info.getGender() == MALE) ? "gender_1" : "gender_2";
        String genderText = (info.getGender() == 0) ? "남자" : "여자";
        boolean modalExist = helper.existElement(By.xpath("//div[@class='sgMaker-bigwidget-inner']"));

        logger.info("CRF_DSS_D002 :: {}", info.getProductName());
        WaitUtil.waitFor(2);

        logger.info("textType 확인");
        arrTextType = checkTextType(info.getTextType());
        // 0 : 캐롯 직장인 생활건강보험
        // 1 : #관리자(사무직)
        // 2 : 경영지원 사무직 관리자
        // 3 : 경영지원 사무직 관리자
        WaitUtil.waitFor(2);
        
        // 자동차보험 가입 모달창
        if(modalExist){
            logger.info("자동차보험 가입 모달창 끄기");
            closeModal();
        }

        logger.info("맞춤 플랜 시작하기 버튼 클릭");
        btnClick(By.xpath("//span[text()='맞춤 플랜 시작하기']/parent::button"), 2);

        logger.info("이름 설정");
        setUserName(By.id("customerName"), PersonNameGenerator.generate());

        logger.info("생년월일 :: {}", info.getFullBirth());
        setBirthday(By.id("birthday"), info.getFullBirth());

        logger.info("성별 :: {}", genderText);
        setGender(By.xpath("//label[@for='" + genderOpt + "']"), genderText);

        logger.info("다음 버튼 클릭");
        clickNextBtn("button");

        logger.info("직업 설정 - 분류대로 찾기");
        setJob(arrTextType);

        logger.info("다음 버튼 클릭");
        clickNextBtn("button");

        logger.info("특약 확인");
        setTreaties(info.getTreatyList(), By.xpath(("//div[@class='Calculation-styled__GuaranteeItem-dis-web-styled__sc-d5ea67d8-10 kkpBTQ']")));

        logger.info("보험료 가져오기");
        crawlPremium(info, By.xpath("//div[text()='월 보험료']/parent::div/div[2]"));

        logger.info("스크린샷");
        takeScreenShot(info);

        logger.info("해약환급금정보 없음 :: CRF ");

        return true;
    }

    public void setJob(String[] arrTextType) throws SetJobException {

        try{
            Select $selectDiv = null;
            String[] div = {"대분류선택", "중분류선택", "소분류선택"};

            driver.findElement(By.xpath("//button[text()='분류대로 찾기']")).click();
            WaitUtil.waitFor(2);

            for(int i = 0; i < div.length; i++){
                String job = arrTextType[i + 1];
                $selectDiv = new Select(driver.findElement(By.xpath("//option[text()='" + div[i] +"']/parent::select")));

                $selectDiv.selectByVisibleText(job);
                logger.info("{} :: {}",div[i], job);
                WaitUtil.waitFor(1);
            }

            // 검증
            checkValue("직업", arrTextType[arrTextType.length - 1], By.xpath("//*[@id='RG']"));


        } catch(Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_JOB;
            throw new SetJobException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }

    }
}
