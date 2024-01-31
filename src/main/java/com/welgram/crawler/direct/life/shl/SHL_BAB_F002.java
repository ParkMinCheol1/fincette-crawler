package com.welgram.crawler.direct.life.shl;

import com.welgram.crawler.direct.life.shl.CrawlingSHL.CrawlingSHLAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.util.InsuranceUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;


// 2023.08.23 | 최우진 | 신한주니어큐브종합건강상해보험(무배당, 태아형) (출생후보장)일반형
public class SHL_BAB_F002 extends CrawlingSHLAnnounce {

    public static void main(String[] args) { executeCommand(new SHL_BAB_F002(), args); }

    @Override
    protected boolean preValidation(CrawlingProduct info) {
        logger.info("남자는 가입할 수 없습니다.");
        return info.getGender() == FEMALE;
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        int unitAmt = 10_000;
        String prodKind = "태아형";
        String planForm = "(출생후보장)일반형";
        String[] arrTextType = info.getTextType().split("#");
        String refundOption = "BASE";
        String fetusBirth = InsuranceUtil.getDateOfBirth(12);
        logger.info("FETUS :: {}", fetusBirth);

        // PROCESS
        logger.info("▉▉▉▉ STEP00 [ 상품검색 ] ▉▉▉▉");
        initSHL(info, "06");

        logger.info("▉▉▉▉ STEP01 [ '고객정보(피보험자)' 입력 ] ▉▉▉▉");
        setBirthday(driver.findElement(By.xpath("//*[@id='csinDivision']/div[1]/ul/li[1]/div[2]/div/input")), info.getFullBirth());
        setGender2(info.getGender());
            // setGender(info.getGender());
        setBirthday(driver.findElement(By.xpath("//*[@id='datepicker0']")), fetusBirth);
            // 진행중 ui변경
        pushButton(driver.findElement(By.xpath("//*[@id='csinDivision']/div[3]/span/button")), 3);
        pushButton(driver.findElement(By.id("popupBtn2")), 2);
            // 종피보험자 내용입력란 중간에 뜸
        setBirthday(driver.findElement(By.xpath("//*[@id='csinDivision']/div[3]/ul/li[1]/div[2]/div/input")), info.getFullBirth());
        pushButton(driver.findElement(By.xpath("//*[@id='csinDivision']/div[4]/span/button")), 3);

        logger.info("▉▉▉▉ STEP02 [ '주계약계산'입력 ] ▉▉▉▉");
        logger.info("▉▉▉▉ 보험형태, 가입설계형태, 납입주기, 보험기간, 납입기간, 가입금액 ▉▉▉▉");
        setProductKind(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[1]/li[1]/div[2]/select")), prodKind);
        setPlanForm(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[1]/li[5]/div[2]/select")), planForm);
        setNapCycle(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[1]/li[4]/div[2]/select")), info.getNapCycleName());
        setInsTerm(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[2]/li[1]/div[2]/select")), "1년");
        setNapTerm(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[2]/li[2]/div[2]/select")), "1년");
        setAssureMoney(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[2]/li[3]/div[2]/div/input")), info.getAssureMoney(), unitAmt);
        pushButton(driver.findElement(By.xpath("//*[@id='mnprDivision']/div[1]/span/button")), 3);

        logger.info("▉▉▉▉ STEP03 [ '특약계산'입력 ] ▉▉▉▉");
        setBABTreatyList(driver.findElements(By.xpath("//*[@id='trtyDivision']/table/tbody/tr")), info.getTreatyList(), arrTextType);
        pushButton(driver.findElement(By.xpath("//*[@id='acco_3']/div[2]/span/button")), 2);                                        // 진행 버튼
        pushButton(driver.findElement(By.xpath("//*[@id='cdhi0050p']/div[2]/div/div/div[2]/section[2]/div[2]/span/button")), 4);    // 보험료 계산 버튼

        logger.info("▉▉▉▉ STEP04 [ 결과확인 ] ▉▉▉▉");
        crawlBABPremium(
            driver.findElement(By.xpath("//*[@id='tblxCal1']/table/tfoot/tr[3]/td")),
            driver.findElement(By.xpath("//*[@id='cdhi0050p']/div[2]/div/div/div[2]/section[2]/div[3]/div[2]/section/div[6]/span/span[1]")),
            info
        );
        snapScreenShot(info);
        pushButton(driver.findElement(By.xpath("//*[@id='tab_0_2']")), 6);

        helper.waitForLoading(By.cssSelector("div.loading"));
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//*[@id='tblInmRtFxty01']//tbody/tr"), 1));
        crawlReturnMoneyList(driver.findElements(By.xpath("//*[@id='tblInmRtFxty01']//tbody/tr")), info, refundOption);

        return true;
    }
}