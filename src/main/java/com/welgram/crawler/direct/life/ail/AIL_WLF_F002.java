package com.welgram.crawler.direct.life.ail;

import com.welgram.common.enums.Job;
import com.welgram.crawler.direct.life.ail.CrawlingAIL.CrawlingAILAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;



// 2022.07.14 | 최우진 | (무)AIA Vitality 평생 안심+ 유니버셜 종신보험
// 구좌 미사용 >> '만원' 단위 사용 (구좌x)
public class AIL_WLF_F002 extends CrawlingAILAnnounce {

    public static void main(String[] args) {
        executeCommand(new AIL_WLF_F002(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String tempJob = Job.MANAGER.getCodeValue();
        String drivingType = "승용차(자가용)";
        String refundOption = "FULL";
        int unitGJ = 10000; // 1만, 10k
        String[] arrTextType = info.getTextType().split("#");

        // PROCESS
        logger.info("▉▉▉▉ 시작 ▉▉▉▉");
        initAIL(info, arrTextType);

        logger.info("▉▉▉▉ 고객님의 정보를 입력해주세요 ▉▉▉▉");
        setUserName("custNm");
        setBirthday("brthDt", info.getFullBirth());
        setVehicle("drvgCd", drivingType);
        setSmokeOption("grp-rdo3");
        setJob(tempJob);

        logger.info("▉▉▉▉ 주계약을 선택해 주세요 ▉▉▉▉");
        setInsTerm(driver.findElement(By.xpath("//*[@id='polprd_DK0100_09G']")), info.getInsTerm());
        setNapTerm(driver.findElement(By.xpath("//*[@id='payprd_DK0100_09G']")), info.getNapTerm(), info.getInsTerm());
        setNapCycle(driver.findElement(By.xpath("/html/body/form[1]/div/div[1]/div[2]/div/table/tbody/tr[1]/td[4]/select")), info.getNapCycleName());
        setAssureMoney(driver.findElement(By.xpath("/html/body/form[1]/div/div[1]/div[2]/div/table/tbody/tr[1]/td[5]/input[2]")), info.getAssureMoney(), unitGJ);

        logger.info("▉▉▉▉ 특약을 선택해 주세요 ▉▉▉▉");
        setSubTreaties(info, unitGJ);
        pushButton(By.xpath("//button[text()='보험료 계산하기']"), 2);                   // 보험료 계산하기
        pushButton(By.xpath("//button[text()='상품제안서 보기']"), 4);                   // 보험료 계산하기

        logger.info("▉▉▉▉ 결과확인 ▉▉▉▉");
        crawlPremium(driver.findElement(By.xpath("//*[@id='layer1']/div[3]/table/tbody/tr/td[5]")), info);
        takeScreenShot(info);
        pushButton(By.xpath("//*[@id='tabLISTBox']/div[1]/ul/li[3]/a"), 2);
        crawlReturnMoneyList(driver.findElements(By.xpath("//*[@id='layer3']/div/div/table/tbody/tr")), info, refundOption);

        return true;
    }
}

