package com.welgram.crawler.direct.life.ail;

import com.welgram.common.enums.Job;
import com.welgram.crawler.direct.life.ail.CrawlingAIL.CrawlingAILAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;



// AIL_CCR_F005     | 무배당 AIA 건강+ 암보험(갱신형) 2형(일반심사형) 최신항암치료비형
//public class AIL_CCR_F005 extends CrawlingAIL implements Scrapable {
public class AIL_CCR_F005 extends CrawlingAILAnnounce {

    public static void main(String[] args) {
        executeCommand(new AIL_CCR_F005(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // todo | 공통상수 클래스 하나 만들어두자
        // todo | 구좌단위는 여기에 기록이 맞는듯
        // INFO
        String tempJob = Job.MANAGER.getCodeValue();
        String drivingType = "승용차(자가용)";
        String refundOption = "BASE";
        int unitGJ = 10_0000;
        String[] arrTextType = info.getTextType().split("#");

        // PROCESS
        initAIL(info, arrTextType);

        // 고객님의 정보
        setUserName("custNm");
        setBirthday("brthDt", info.getFullBirth());
        setVehicle("drvgCd", drivingType);
        setSmokeOption("grp-rdo3");
        setJob(tempJob);

        // 주계약
        setInsTerm(driver.findElement(By.xpath("//*[@id='polprd_Q90102_02D']")), info.getInsTerm());
        setNapTerm(driver.findElement(By.xpath("//*[@id='payprd_Q90102_02D']")), info.getNapTerm(), info.getInsTerm());
        setNapCycle(driver.findElement(By.xpath("/html/body/form[1]/div/div[1]/div[2]/div/table/tbody/tr[1]/td[4]/select")), info.getNapCycleName());
        setAssureMoney(driver.findElement(By.xpath("/html/body/form[1]/div/div[1]/div[2]/div/table/tbody/tr[1]/td[5]/input[2]")), info.getAssureMoney(), unitGJ);

        // | 선택특약 설정
        setSubTreaties(info, unitGJ);
        pushButton(By.xpath("//button[text()='보험료 계산하기']"), 2);                   // 보험료 계산하기
        pushButton(By.xpath("//button[text()='상품제안서 보기']"), 3);                   // 보험료 계산하기
        crawlPremium(driver.findElement(By.xpath("//*[@id='layer1']/div[3]/table/tfoot/tr/td/strong")), info);
        takeScreenShot(info);
        pushButton(By.xpath("//*[@id='tabLISTBox']/div[1]/ul/li[3]/a"), 2);
        crawlReturnMoneyList(driver.findElements(By.xpath("//*[@id='layer3']/div/div/table/tbody/tr")), info, refundOption);

        return true;
    }
}