package com.welgram.crawler.direct.life.kyo;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.crawler.direct.life.CrawlingKYO;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


// 2023.05.15           | 최우진               | 대면_암
// KYO_CCR_F001         | (무)교보괜찮아요암보험(갱신형)
public class KYO_CCR_F001 extends CrawlingKYO {

    public static void main(String[] args) { executeCommand(new KYO_CCR_F001(), args); }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("공시실 진입 후 건강/암 버튼 클릭");
        element = driver.findElement(By.linkText("건강/암"));
        waitElementToBeClickable(element).click();
        WaitUtil.waitFor(2);

        logger.info("상품명 : {} 클릭", info.getProductName());
        element = driver.findElement(By.xpath("//td[text()='" + info.getProductName() + "']/parent::tr//button"));
        waitElementToBeClickable(element).click();
        waitAnnouncePageLoadingBar();
        waitAnnouncePageLoadingBar();
        WaitUtil.waitFor(3);

        logger.info("생년월일 설정");
        setBirthdayNew(info.getFullBirth());

        logger.info("성별 설정");
        setGenderNew(info.getGender());

        logger.info("보험종류 설정");
//        setPlanType(info.textType);
        setPlanType(info.getProductNamePublic());

        logger.info("납입주기 설정");
        setNapCycleNew(info.getNapCycleName());

        logger.info("보험기간 설정");
        setInsTermNew(info.getInsTerm());

        logger.info("납입기간 설정");
        this.setNapTermNew(info.getInsTerm(), info.getNapTerm());

        logger.info("가입금액 설정");
        setAssureMoneyNew(info.getAssureMoney());

        logger.info("특약 설정 및 비교");
        setTreaties(info);

        logger.info("보험료 크롤링");
        WebElement element = driver.findElement(By.xpath("//div[@id='totPrmTx']/strong"));
        String premium = element.getText().replaceAll("[^0-9]", "");
        info.treatyList.get(0).monthlyPremium = premium;

        logger.info("스크린샷찍기");
        moveToElementByJavascriptExecutor(element);
        takeScreenShot(info);

        logger.info("보장내용 버튼 클릭");
        element = driver.findElement(By.xpath("//button[text()='보장내용']"));
        waitElementToBeClickable(element).click();
        WaitUtil.waitFor(1);
        waitAnnouncePageLoadingBar();

        logger.info("해약환급금 탭 버튼 클릭");
        element = driver.findElement(By.linkText("해약환급금"));
        waitElementToBeClickable(element).click();

        logger.info("해약환급금 크롤링");
        this.crawlReturnMoneyListNew(info);

        return true;
    }


    private void setPlanType(Object obj) throws Exception {
        String title = "보험종류";
        String welgramPlanType = (String) obj;

        //보험종류 클릭
        WebElement select = driver.findElement(By.id("sel_gdcl"));
        selectOptionByText(select, welgramPlanType);

        //실제로 클릭된 보험종류 읽어오기
        String script = "return $(arguments[0]).find('option:selected').text();";
        String targetPlanType = String.valueOf(executeJavascript(script, select));

        //비교
        printAndCompare(title, welgramPlanType, targetPlanType);
    }


    public void setNapTermNew(Object obj, Object obj2) throws SetNapTermException {
        String title = "납입기간";
        String welgramInsTerm = (String) obj;
        String welgramNapTerm = (String) obj2;
        welgramNapTerm = (welgramInsTerm.equals(welgramNapTerm)) ? "전기납" : welgramNapTerm + "납";

        try {

            //납입기간 클릭
            WebElement select = driver.findElement(By.xpath("//span[@id='show_paPd']//select[@name='pdtScnCd_paPd']"));
            selectOptionByText(select, welgramNapTerm);

            //실제로 클릭된 납입기간 읽어오기
            String script = "return $(arguments[0]).find('option:selected').text();";
            String targetNapTerm = String.valueOf(executeJavascript(script, select));

            //비교
            printAndCompare(title, welgramNapTerm, targetNapTerm);

        } catch (Exception e) {
            throw new SetNapTermException(e.getMessage());
        }
    }


    public void crawlReturnMoneyListNew(Object obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj;
        List<WebElement> $trList = driver.findElements(By.xpath("//div[@id='trmRview']//table/tbody/tr"));

        for(WebElement $tr : $trList) {
            String term = $tr.findElement(By.xpath("./td[1]")).getText();
            String premiumSum = $tr.findElement(By.xpath("./td[2]")).getText();
            String returnMoney = $tr.findElement(By.xpath("./td[3]")).getText();
            String returnRate = $tr.findElement(By.xpath("./td[4]")).getText();
            returnMoney = String.valueOf(MoneyUtil.toDigitMoney(returnMoney));

            logger.info("경과기간 : {}", term);
            logger.info("납입보험료 : {}", premiumSum);
            logger.info("공시환급금 : {}", returnMoney);
            logger.info("공시환급률 : {}", returnRate);
            logger.info("==========================================");

            PlanReturnMoney p = new PlanReturnMoney();
            p.setTerm(term);
            p.setPremiumSum(premiumSum);
            p.setReturnMoney(returnMoney);
            p.setReturnRate(returnRate);

            //만기환급금 세팅
            info.returnPremium = returnMoney;
            info.planReturnMoneyList.add(p);
        }

        // 순수보장형 만기환급금 0
        info.returnPremium = "0";
        logger.info("만기환급금 : {}", info.returnPremium);

    }

}
