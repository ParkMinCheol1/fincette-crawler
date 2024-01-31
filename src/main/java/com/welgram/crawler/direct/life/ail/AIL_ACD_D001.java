package com.welgram.crawler.direct.life.ail;

import com.welgram.common.enums.Job;
import com.welgram.crawler.direct.life.ail.CrawlingAIL.CrawlingAILAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;


// 2023.05.19 | 최우진 | (무)퍼플 휴일교통재해장해보험
// todo | 다이렉트 상품인데 공시실을 크롤링하는 특이한 케이스
public class AIL_ACD_D001 extends CrawlingAILAnnounce {



    public static void main(String[] args) {
        executeCommand(new AIL_ACD_D001(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        String tempJob = Job.MANAGER.getCodeValue();
        String drivingType = "승용차(자가용)";
        String elTitleNapCycle = "주계약 납입주기";        // element 경로 찾기용 문자열
        String elTitleAssureMoney = "주계약 납입주기";     // element 경로 찾기용 문자열
        String refundOption = "BASE";
        String[] arrTextType = info.getTextType().split("#");

        initAIL(info, arrTextType);

        // 고객정보
        setUserName("custNm");
        setBirthday("brthDt", info.getFullBirth());
        setVehicle("drvgCd", drivingType);
        setSmokeOption("grp-rdo3");
        setJob(tempJob);

        // 주계약 정보
        setInsTerm(driver.findElement(By.xpath("//*[@id='polprd_O30100_03E']")), info.getInsTerm());
        setNapTerm(driver.findElement(By.xpath("//*[@id='payprd_O30100_03E']")), info.getNapTerm(), info.getInsTerm());
        setNapCycle(driver.findElement(By.xpath("/html/body/form[1]/div/div[1]/div[2]/div/table/tbody/tr[1]/td[4]/select")), info.getNapCycleName());
        setAssureMoney(driver.findElement(By.xpath("//*[@id='premium_tot']")), info.getAssureMoney(), 1);

        // 선택특약 정보
        // 특약선택
        // 보기
        // 납기
        // 가입금액

        pushButton(driver.findElement(By.xpath("/html/body/form[1]/div/div[1]/div[3]/button")));
        pushButton(driver.findElement(By.xpath("/html/body/form[1]/div/div[2]/div/div[2]/button")));
        crawlPremium(driver.findElement(By.xpath("//*[@id='layer1']/div[3]/table/tfoot/tr/td/strong")), info);
        takeScreenShot(info);
        pushButton(driver.findElement(By.xpath("//*[@id='tabLISTBox']/div[1]/ul/li[3]/a")));
        crawlReturnMoneyList(driver.findElements(By.xpath("//*[@id='layer3']/div/div/table/tbody/tr")), info, refundOption);

        return true;

    }

}
