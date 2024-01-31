package com.welgram.crawler.direct.life.ail;

import com.welgram.common.PersonNameGenerator;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.Job;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.direct.life.ail.CrawlingAIL.CrawlingAILAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.Set;


// 2023.07.11 | 최우진 | 무배당 AIA Vitality 내가 조립하는 종합건강보험
public class AIL_DSS_F001 extends CrawlingAILAnnounce {

    // (무)AIA Vitality 내가 조립하는 종합건강보험II 1형(간편심사형)
    public static void main(String[] args) { executeCommand(new AIL_DSS_F001(), args); }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String tempJob = Job.MANAGER.getCodeValue();
        String drivingType = "승용차(자가용)";
        String refundOption = "LEVEL";
        int unitGJ = 100_000; // 10만, 100k
        String[] arrTextType = info.getTextType().split("#");

        // PROCESS
        logger.info("▉▉▉▉ 시작 ▉▉▉▉");
        initAIL(info, arrTextType);

        logger.info("▉▉▉▉ 고객님의 정보를 입력해주세요 ▉▉▉▉");
        setUserName("custNm");

        setBirthday("brthDt", info.getFullBirth());
        setGender(info.getGender(), 2);
        setVehicle("drvgCd", drivingType);
        setSmokeOption("grp-rdo3");
        setJob(tempJob);

        logger.info("▉▉▉▉ 주계약을 선택해 주세요 ▉▉▉▉");
        setInsTerm(driver.findElement(By.xpath("//*[@id='polprd_Q10101_01B']")), info.getInsTerm());
        setNapTerm(driver.findElement(By.xpath("//*[@id='payprd_Q10101_01B']")), info.getNapTerm(), info.getInsTerm());
        setNapCycle(driver.findElement(By.xpath("/html/body/form[1]/div/div[1]/div[2]/div/table/tbody/tr[1]/td[4]/select")), info.getNapCycleName());
        setAssureMoney(driver.findElement(By.xpath("/html/body/form[1]/div/div[1]/div[2]/div/table/tbody/tr[1]/td[5]/input[2]")), info.getAssureMoney(), unitGJ);

        logger.info("▉▉▉▉ 특약을 선택해 주세요 ▉▉▉▉");
        setSubTreaties(info, unitGJ);
        pushButton(By.xpath("//button[text()='보험료 계산하기']"), 2);
        pushButton(By.xpath("//button[text()='상품제안서 보기']"), 4);
        crawlPremium(driver.findElement(By.xpath("//*[@id='layer1']/div[3]/table/tfoot/tr/td/strong")), info);
        takeScreenShot(info);
        pushButton(By.xpath("//*[@id='tabLISTBox']/div[1]/ul/li[3]/a"), 2);
        crawlReturnMoneyList(driver.findElements(By.xpath("//*[@id='layer3']/div/div/table/tbody/tr")), info, refundOption);

        return true;

    }



    // subtreaties
    protected void setSubTreaties(CrawlingProduct info, int unitGJ) throws Exception {

        List<CrawlingTreaty> treatyList = info.getTreatyList();
        List<WebElement> elList = driver.findElements(By.xpath("/html/body/form[1]/div/div[1]/div[3]/div/table/tbody/tr"));
        logger.info("==    선택특약설정    ==");
        int treatyCheckCnt = 0;
        int mainCnt = 0;

        for (CrawlingTreaty treaty : treatyList) {
            for (WebElement $tr : elList) {
                String elName = $tr.findElement(By.xpath(".//td//label")).getText().replaceAll(" ", "");
                treaty.setTreatyName(treaty.getTreatyName().replaceAll(" ", ""));
                WebElement $cbEl = $tr.findElement(By.xpath(".//td//label"));
                WebElement tempInsLoc = $tr.findElement(By.xpath(".//td[2]/select"));
                WebElement tempNapLoc = $tr.findElement(By.xpath(".//td[3]/select"));
                WebElement tempAmtLoc = $tr.findElement(By.xpath(".//td[4]/input[2]"));

                if (elName.equals(treaty.getTreatyName())) {
                    $cbEl.click();
                    setInsTerm(tempInsLoc, treaty.getInsTerm());
                    setNapTerm(tempNapLoc, treaty.getNapTerm(), treaty.getInsTerm());
                    setAssureMoney(tempAmtLoc, treaty.getAssureMoney(), unitGJ);

                    logger.info("ELNAME :: {}", elName);
                    logger.info("TREATY :: {}", treaty.getTreatyName());
                    logger.info("cbEl --- SL:{}  DP:{} EN:{}", $cbEl.isSelected(), $cbEl.isDisplayed(), $cbEl.isEnabled());
                    logger.info("MATCH");
                    logger.info("=====================================");

                    treatyCheckCnt++;
                }
            }

            if (treaty.productGubun == CrawlingTreaty.ProductGubun.주계약) {
                mainCnt++;
            }
        }

        logger.info("TREATYLIST SIZE :: {}", treatyList.size());
        logger.info("MAIN TRT COUNT  :: {}", mainCnt);
        logger.info("HANDLED COUNT   :: {}", treatyCheckCnt);
        logger.info("=====================================");

        if (treatyList.size() != treatyCheckCnt + mainCnt) {
            logger.error("특약개수가 다릅니다 확인이 필요합니다");
            throw new CommonCrawlerException("전체 특약개수와 처리된 특약의 개수가 일치하지 않습니다");
        }

    }

}
