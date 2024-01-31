package com.welgram.crawler.direct.life.ail;

import com.welgram.common.enums.Job;
import com.welgram.crawler.direct.life.ail.CrawlingAIL.CrawlingAILAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

import java.util.HashMap;
import java.util.Map;


// 무배당 AIA 안심+ 프라임 종신보험Ⅲ(해약환급금 50%지급형) 1형
public class AIL_WLF_F009 extends CrawlingAILAnnounce {

    public static void main(String[] args) { executeCommand(new AIL_WLF_F009(), args); }



    private final Map<String, Object> vars = new HashMap<>();



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("START :: AIL_WLF_F009 :: 무배당 AIA 안심+ 프라임 종신보험Ⅲ(해약환급금 50%지급형) 1형");

        driver.manage().window().maximize();

        // todo | 공통상수 클래스 하나 만들어두자
        int unitGJ = 10000; // 1만, 10k
        String tempJob = Job.MANAGER.getCodeValue();
        String drivingType = "승용차(자가용)";
        String[] arrTextType = info.getTextType().split("#");

        initAIL(info, arrTextType);

        setUserName("custNm");
        setBirthday("brthDt", info.getFullBirth());
        setVehicle("drvgCd", drivingType);
        setSmokeOption("grp-rdo3");
        setJob(tempJob);

        setInsTerm(driver.findElement(By.xpath("//*[@id='polprd_T40101_03I']")), info.getInsTerm());
        setNapTerm(driver.findElement(By.id("payprd_T40101_03I")), info.getNapTerm(), info.getInsTerm());
        setNapCycle(driver.findElement(By.xpath("//*[@name='payCyclCd']")), info.getNapCycleName());
        setAssureMoney(driver.findElement(By.xpath("//*[@name='sfaceAmt_T40101_03I']")), info.getAssureMoney(), unitGJ);

        // 보험료 계산하기
        pushButton(By.xpath("/html/body/form[1]/div/div[1]/div[3]/button"), 2);
        // 상품제안서 보기
        pushButton(By.xpath("/html/body/form[1]/div/div[2]/div/div[2]/button"), 2);
        crawlPremium(driver.findElement(By.xpath("//*[@id='layer1']/div[3]/table/tfoot/tr/td/strong")), info);
        takeScreenShot(info);
        // 해약환급금 보기
        pushButton(By.xpath("//*[@id='tabLISTBox']/div[1]/ul/li[3]/a"), 2);
        getWebReturnPremium(info);

        // ======================================================================

        logger.info(":: INNERSCRAP DONE ::");

        return true;
    }

}
