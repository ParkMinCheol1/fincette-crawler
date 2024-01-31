package com.welgram.crawler.direct.fire.nhf;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.Job;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

// (무) NH다이렉트운전자보험2304
public class NHF_DRV_D007 extends CrawlingNHFMobile {

    public static void main(String[] args) {
        executeCommand(new NHF_DRV_D007(), args);
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

        String genderOpt = (info.getGender() == MALE) ? "남" : "여";
        String job = Job.TEACHER.getCodeValue();
        String vehicle = "자가용";

        logger.info("NHF_DRV_D007 :: {}", info.getProductName());
        WaitUtil.waitFor(2);

        logger.info("보험료 확인 버튼 클릭");
        btnClick(By.xpath("//span[contains(., '보험료 확인')]/parent::button"), 2);
        waitLoadingImg();

        logger.info("생년월일 :: {}", info.getFullBirth());
        setBirthday(By.id("birth"), info.getFullBirth());

        logger.info("성별 :: {}", genderOpt);
        setGender(By.xpath("//label[text()='" + genderOpt + "']"), genderOpt);

        logger.info("직업 선택 :: 초등학교 교사");
        setJob(job);

        logger.info("보험료 계산하기 버튼 클릭");
        btnClick(By.id("btnNext"), 2);
        waitLoadingImg();

        logger.info("차량 운전여부 :: {}", vehicle);
        setVehicle(By.xpath("//li[@id='pop0201M_drvYn']//div[@class='inpChkBox']//label[text()='" + vehicle + "']"), vehicle);

        logger.info("이륜자동차 운전여부 :: [아니오] 선택");
        btnClick(By.xpath("//label[@for='pop0201M_twvDrvYn0']"), 1);

        logger.info("확인 버튼 클릭");
        btnClick(By.xpath("//span[normalize-space()='확인']"), 3);
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