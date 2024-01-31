package com.welgram.crawler.direct.fire.acf;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.Job;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;


// 2022.06.29       | 최우진           | 운전자보험
// ACF_DRV_D002     | Chubb 365매일안심운전자보험
public class ACF_DRV_D002 extends CrawlingACFDirect {  // 운전자보험

    public static void main(String[] args) {
        executeCommand(new ACF_DRV_D002(), args);
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
        String vehicleUseYn = "예";
        String job = Job.MANAGER.getCodeValue();

        logger.info("START :: ACF_DRV_D002 :: {}", info.getProductName());
        WaitUtil.loading(2);

        logger.info("생년월일 입력 :: {}", info.getBirth());
        setBirthday(By.id("insuredBirth"), info.getBirth());

        logger.info("성별 설정 :: {}", genderText);
        setGender(By.xpath("//label[@for='" + genderOpt + "']"), genderText);

        logger.info("운전의 용도(자가용) 선택 :: {}}", vehicleUseYn);
        setVehicle(By.xpath("//span[contains(.,'" + vehicleUseYn + "')]"), vehicleUseYn);

        logger.info("직업 설정 :: (fix){}", job);
        setJob(job);

        logger.info("보험기간 :: {}, 납입주기 :: {} ", info.getInsTerm(), getNapCycleName(info.getNapCycle()));
        setInsTerm(By.name("periodPayFrequency"), info.getInsTerm(), getNapCycleName(info.getNapCycle()));

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
}
