package com.welgram.crawler.direct.fire.nhf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

// (무)헤아림다이렉트암보험
public class NHF_CCR_D004 extends CrawlingNHFMobile {



    public static void main(String[] args) {
        executeCommand(new NHF_CCR_D004(), args);
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setMobile(true);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        boolean _result = false;

//        _result = announcePage(info);
        _result = frontPage(info);

        return _result;
    }

    protected boolean frontPage(CrawlingProduct info) throws Exception {

        boolean _result = false;

        String genderText = (info.getGender() == MALE) ? "남" : "여";

        logger.info("NHF_CCR_D004 :: {}", info.getProductName());
        WaitUtil.waitFor(2);

        logger.info("보험료 확인 버튼 클릭");
        btnClick(By.xpath("//span[contains(., '보험료 확인')]/parent::button"), 2);
        waitLoadingImg();

        logger.info("생년월일 :: {}", info.getFullBirth());
        setBirthday(By.id("birth"), info.getFullBirth());

        logger.info("성별  :: {}", genderText);
        setGender(By.xpath("//label[text()='" + genderText + "']"), genderText);

        logger.info("보험료 계산하기 버튼 클릭");
        btnClick(By.id("btnNext"), 2);
        waitLoadingImg();

        logger.info("플랜 설정 :: {}", info.getTextType());
        setPlanType(By.xpath("//ul[@id='boxData']//div[contains(., '" + info.getTextType() + "')]"), info.getTextType());

        logger.info("보험기간 및 납입기간 설정");
        setMobileTerm(By.id("boxData_btnPop"), info.getInsTerm(), info.getNapTerm());

        logger.info("특약별 가입금액 설정 및 비교");
        setTreaties(info.getTreatyList());

        logger.info("주계약 보험료 설정");
        crawlPremium(By.id("boxData_applPrem"), info.treatyList.get(0));

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("해약환급금 버튼 클릭");
        btnClick(By.id("btnCcltRf"), 2);
        waitMobileLoadingImg();

        logger.info("해약환급금 조회");
        crawlReturnMoneyList(By.xpath("//tbody[@id='pop0301M04P_tblData_1']/tr"), info);

        _result = true;
        return _result;
    }
}
