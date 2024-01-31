package com.welgram.crawler.direct.fire.nhf;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.Job;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;

// (무)헤아림다이렉트암보험
public class NHF_DSS_D007 extends CrawlingNHFDirect {



    public static void main(String[] args) {
        executeCommand(new NHF_DSS_D007(), args);
    }


    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setIniSafe(true);
//        option.setTouchEnPc(true);

    }


    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        crawlFromHomepage(info);

        return true;
    }


    private void crawlFromHomepage(CrawlingProduct info) throws Exception {

        boolean result = false;
        String genderOpt = (info.getGender() == 0) ? "sexDcd1" : "sexDcd2";
        String genderText = (info.getGender() == MALE) ? "남" : "여";

        logger.info("NHF_DSS_D007 :: {}", info.getProductName());
        // 서버에서 모니터링을 돌릴 경우 타임아웃으로 실패가 많아 대기시간을 많이 준다.
        WaitUtil.waitFor(30);
        chkSecurityProgram();

        logger.info("생년월일 :: {}", info.getFullBirth());
        setBirthday(By.id("iptBirth"), info.getFullBirth());

        logger.info("성별 :: {}", genderText);
        setGender(By.xpath("//label[@for = '" + genderOpt + "']"), genderText);

        logger.info("직업 설정");
        setJob();

        logger.info("이륜자동차 운전여부 :: 아니오");
        btnClick(By.xpath("//label[@for='twvDrvYn0']"), 1);

        logger.info("보험료 계산하기 버튼 클릭");
        btnClick(By.id("btnNext"), 3);
        waitHomepageLoadingImg();

        logger.info("납입기간 설정 :: {}", info.getNapTerm());
        setInsTerm(By.xpath("//ul[@id='pdtInsPrdListArea']//parent::li//span[contains(., '" + info.getNapTerm() + "')]"), info.getNapTerm()); // 요소가 보험기간설정 메서드와 같아 그대로 사용

        if(!info.getInsTerm().equals(info.getNapTerm())) {
            logger.info("가입설계 보험기간 : {}", info.getInsTerm());
            logger.info("가입설계 납입기간 : {}", info.getNapTerm());

            throw new SetInsTermException("갱신형 가설의 경우 보험기간과 납입기간이 일치해야합니다.");
        }

        logger.info("플랜유형 설정 :: {}", info.getTextType());
        setPlanType(By.xpath("//strong[text()='" + info.getTextType() + "']/parent::a"), info.getTextType());

        logger.info("특약 설정");
        setTreaties(By.xpath("//ul[@id='barGrpArea']/li[@style='display:block']//a[@class='btnAcc']"), info);

        logger.info("주계약 보험료 크롤링");
        crawlPremium(By.id("sumPremAmt"), info);

        logger.info("스크린샷 찍기");
        helper.executeJavascript("window.scrollTo(0,0);");
        takeScreenShot(info);

        logger.info("해약환급금 크롤링");
        //최대 60초 기다려보기
        wait = new WebDriverWait(driver, 60);
        waitHomepageLoadingImg();
        crawlReturnMoneyList(By.xpath("//tbody[@id='srdtRfListBody_1']/tr"), info);

        result = true;
    }
}

