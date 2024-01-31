package com.welgram.crawler.direct.life.tyl;

import com.welgram.common.PersonNameGenerator;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingTYL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductKind;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebListener;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// 2023.03.20           | 최우진               | 대면_연금보험
// TYL_ANT_F002         | 무배당수호천사누구나행복연금보험
public class TYL_ANT_F002 extends CrawlingTYL {

    public static void main(String[] args) { executeCommand(new TYL_ANT_F002(), args); }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String birth = info.getFullBirth();
        String gender = (info.getGender() == MALE)? "남자" : "여자";
        String title = "무배당수호천사누구나행복연금보험";
        String mainTreatyName = "(무)수호천사누구나행복연금(적립형)_3년납초과";
        List<CrawlingTreaty> trtList = info.getTreatyList();
        String tempName = PersonNameGenerator.generate();

        // PROCESS
        logger.info("▉▉▉▉ #000 | 공시실에서 '{}'를 검색합니다", title);
        searchProdByTitle( title );

        logger.info("▉▉▉▉ #001 | 'step1.고객정보를입력해주세요'를 입력합니다");
        inputCustomerInfo( tempName, birth, gender );

        logger.info("▉▉▉▉ #002 | 'step2 주상품을 선택해주세요'를 입력합니다");
        inputMainTretyInfo( mainTreatyName );

        logger.info("▉▉▉▉ #003 | 'step3 특약을 선택해주세요'를 입력합니다");
        logger.info("trtAnnAge :; {}", info.getAnnuityAge());
        logger.info("NAPTERM :: {}", info.getNapTerm());
        logger.info("NAPCYCLE :: {}", info.getNapCycleName());
        logger.info("ASSAMT :: {}", info.getAssureMoney());
        WaitUtil.waitFor(1);

        // 보험기간
        Select selInsTerm = new Select(driver.findElement(By.id("trmins_E95421DG1")));

        selInsTerm.selectByVisibleText(info.getAnnuityAge() + "세");
        logger.info("(INPUT) INSTERM :: {}", info.getAnnuityAge());         // todo | 동양생명은 보험기간에 연금개시나이 들어감 주의

        // 납입기간
        Select selNapTerm = new Select(driver.findElement(By.id("pypd_E95421DG1")));
        selNapTerm.selectByVisibleText(info.getNapTerm());
        logger.info("(INPUT) NAPTERM :: {}", info.getNapTerm());

        // 납입주기
        Select selNapCycle = new Select(driver.findElement(By.id("pycyc_E95421DG1")));
        selNapCycle.selectByVisibleText(info.getNapCycleName());
        logger.info("(INPUT) NAPCYCLE :: {}", info.getNapCycleName());

        // 보험료
        element = driver.findElement(By.id("money_psby2_E95421DG1"));
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        element.sendKeys(info.getAssureMoney());
        logger.info("(INPUT) ASSAMT :: {}", info.getAssureMoney());


        logger.info("보험료계산 버튼 클릭");
        pushButton("보험료 계산");
        WaitUtil.waitFor(8);

        logger.info("보험료 확인");
        noteMonthlyPremium(info.getTreatyList().get(0));
        WaitUtil.waitFor(2);

        logger.info("스크린샷");
        takeScreenShot(info);

        logger.info("보장내용상세보기 버튼 클릭");
        pushButton("보장내용상세보기");
        WaitUtil.waitFor(8);

        logger.info("연금 확인");
        List<WebElement> tdList = driver.findElements(By.xpath("//caption[text()='연금 예시표 - 정액형']//parent::table//tbody//tr[position() > 3]//th[contains(., '10년')]//parent::tr//td[10]"));
        PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();
        String tempStrFxd10y = String.valueOf(Integer.parseInt(tdList.get(0).getText().replaceAll("[^0-9]", "")) * 1_0000);
        String tempStrWhl10y = String.valueOf(Integer.parseInt(tdList.get(1).getText().replaceAll("[^0-9]", "")) * 1_0000);

        logger.info("tempStrWhl10y :: {}", tempStrWhl10y);
        logger.info("tempStrFxd10y :: {}", tempStrFxd10y);
        planAnnuityMoney.setFxd10Y(tempStrFxd10y);
        planAnnuityMoney.setWhl10Y(tempStrWhl10y);

        info.planAnnuityMoney = planAnnuityMoney;
        info.annuityPremium = tempStrWhl10y;
        info.fixedAnnuityPremium = tempStrFxd10y;

        WaitUtil.waitFor(5);

        logger.info("해약환급금 확인");
        WebElement returnMoneyTable = driver.findElement(By.xpath("//caption[text()='해약환급금 예시표']//parent::table"));
        List<WebElement> trList = returnMoneyTable.findElements(By.xpath(".//tbody//tr"));
        List<PlanReturnMoney> prmList = new ArrayList<>();

        for (WebElement tr : trList) {
            PlanReturnMoney prm = new PlanReturnMoney();
            String tempTerm = tr.findElement(By.xpath(".//td[1]")).getText();
            String tempSum = tr.findElement(By.xpath(".//td[2]")).getText();
            String tempMinAmt = tr.findElement(By.xpath(".//td[4]")).getText().replaceAll("[^0-9]", "") + "0000";
            String tempMinRate = tr.findElement(By.xpath(".//td[5]")).getText();
            String tempAvgAmt = tr.findElement(By.xpath(".//td[6]")).getText().replaceAll("[^0-9]", "") + "0000";
            String tempAvgRate = tr.findElement(By.xpath(".//td[7]")).getText();
            String tempAmt = tr.findElement(By.xpath(".//td[8]")).getText().replaceAll("[^0-9]", "") + "0000";
            String tempRate = tr.findElement(By.xpath(".//td[9]")).getText();

            if (tempTerm.contains("연금개시")) {
                break;
            }

            prm.setTerm(tempTerm);
            prm.setPremiumSum(tempSum);
            prm.setReturnMoneyMin(tempMinAmt);
            prm.setReturnRateMin(tempMinRate);
            prm.setReturnMoneyAvg(tempAvgAmt);
            prm.setReturnRateAvg(tempAvgRate);
            prm.setReturnMoney(tempAmt);
            prm.setReturnRate(tempRate);

            prmList.add(prm);

            info.returnPremium = tempAmt;

            logger.info("===================================");
            logger.info("기간         : " + tempTerm);
            logger.info("납입보험료누계 : " + tempSum);
            logger.info("해약환급금    : " + tempAmt);
            logger.info("환급률       : " + tempRate);
            logger.info("tempMinAmt : " + tempMinAmt);
            logger.info("tempMinRate : " + tempMinRate);
            logger.info("tempAvgAmt : " + tempAvgAmt);
            logger.info("tempAvgRate : " + tempAvgRate);
        }

        logger.info("===================================");
        logger.info("순수보장형 상품이 아니므로 환급정보가 존재합니다 ");
        logger.info("연금보험 상품이므로 환급정보가 존재합니다 ");
        logger.info("info.returnPremium :: {}", info.returnPremium);
        logger.info("===================================");
        info.setPlanReturnMoneyList(prmList);

        return true;

    }

}
