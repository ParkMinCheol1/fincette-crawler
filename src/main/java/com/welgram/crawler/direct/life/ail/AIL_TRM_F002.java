package com.welgram.crawler.direct.life.ail;


import com.welgram.common.enums.Job;
import com.welgram.crawler.direct.life.ail.CrawlingAIL.CrawlingAILAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;



// 2022.11.07           | 최우진               | 대면_정기
// AIL_TRM_F002         | (무)AIA 경영인을 위한 안심+ 정기보험 (간편심사) 15%
public class AIL_TRM_F002 extends CrawlingAILAnnounce {

    public static void main(String[] args) { executeCommand(new AIL_TRM_F002(), args); }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String tempJob = Job.MANAGER.getCodeValue();
        String drivingType = "승용차(자가용)";
        String refundOption = "FULL";
        int unitGJ = 10_000; // 1만, 10k (이상품은 구좌 미사용)
        String[] arrTextType = info.getTextType().split("#");

        // PROCESS
        logger.info("▉▉▉▉ START ▉▉▉▉");
        initAIL(info, arrTextType);

        logger.info("▉▉▉▉ 고객님의 정보 ▉▉▉▉");
        setUserName("custNm");
        setBirthday("brthDt", info.getFullBirth());
        setGender(info.getGender(), 2);
        setVehicle("drvgCd", drivingType);
        setSmokeOption("grp-rdo3");
        setJob(tempJob);

        logger.info("▉▉▉▉ 주계약 ▉▉▉▉");
        setInsTerm(driver.findElement(By.xpath("//*[@id='polprd_DN0121_01B']")), info.getInsTerm());
        setNapTerm(driver.findElement(By.xpath("//*[@id='payprd_DN0121_01B']")), info.getNapTerm(), info.getInsTerm());
        setNapCycle(driver.findElement(By.xpath("/html/body/form[1]/div/div[1]/div[2]/div/table/tbody/tr[1]/td[4]/select")), info.getNapCycleName());
        setAssureMoney(driver.findElement(By.xpath("/html/body/form[1]/div/div[1]/div[2]/div/table/tbody/tr[1]/td[5]/input[2]")), info.getAssureMoney(), unitGJ);

        logger.info("▉▉▉▉ 선택특약 설정 ▉▉▉▉");
        setSubTreaties(info, unitGJ);
        pushButton(By.xpath("//button[text()='보험료 계산하기']"), 2); // 보험료 계산하기
        pushButton(By.xpath("//button[text()='상품제안서 보기']"), 2); // 보험료 계산하기

        logger.info("▉▉▉▉ 결과 확인 ▉▉▉▉");
        crawlPremium(driver.findElement(By.xpath("//*[@id='layer1']/div[3]/table/tfoot/tr/td/strong")), info);
        takeScreenShot(info);
        pushButton(By.xpath("//*[@id='tabLISTBox']/div[1]/ul/li[3]/a"), 2);
        crawlReturnMoneyList(driver.findElements(By.xpath("//*[@id='layer3']/div/div/table/tbody/tr")), info, refundOption);

        return true;
    }
}
