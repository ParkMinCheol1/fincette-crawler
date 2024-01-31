package com.welgram.crawler.direct.fire.shf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.fire.CrawlingSHF;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductKind;
import com.welgram.crawler.general.PlanReturnMoney;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;


// 2023.04.09           | 최우진               | 대면_운전자보험
// SHF_DRV_F001         | 운전자보험은 신한이지(무배당)
public class SHF_DRV_F001 extends CrawlingSHF {

    public static void main(String[] args) { executeCommand(new SHF_DRV_F001(), args); }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("START :: 운전자보험은 신한이지(무배당)");

        driver.findElement(By.xpath("//a[@title='운전자보험은 신한이지(무배당)']")).click();
        WaitUtil.waitFor(2);

        logger.info("BIRTH :: {}", info.getFullBirth());
        String yearOfBirth = info.getFullBirth().substring(0, 4);
        String monthOfBirth = info.getFullBirth().substring(4, 6);
        String dayOfBirth = info.getFullBirth().substring(6, 8);

        logger.info("피보험자 정보");
        logger.info("생년월일 설정");
        Select selYOB = new Select(driver.findElement(By.id("i_sTgisrYear")));
        selYOB.selectByVisibleText(yearOfBirth);
        Select selMOB = new Select(driver.findElement(By.id("i_sTgisrMonth")));
        selMOB.selectByVisibleText(monthOfBirth);
        Select selDOB = new Select(driver.findElement(By.id("i_sTgisrDay")));
        selDOB.selectByVisibleText(dayOfBirth);
        WaitUtil.waitFor(2);

        logger.info("성별 설정");
        if(info.getGender() == MALE) {
            driver.findElement(By.id("male")).click();
        } else {
            driver.findElement(By.id("female")).click();
        }
        WaitUtil.waitFor(2);

        logger.info("직업검색");
        driver.findElement(By.xpath("//a[text()='찾아보기']")).click();
        WaitUtil.waitFor(1);
        List<String> winList= new ArrayList<>(driver.getWindowHandles());
        for(String win : winList) {
//            logger.info("WIN INFO :: {}", win);
            logger.info("WIN SIZE :: {}", winList.size());
//            645486A1A569C4C7A5429B8B327E411E
//            D91A9EFBFA2DDE156D59D3C48BA4F107
        }
        driver.switchTo().window(winList.get(1));
        WaitUtil.waitFor(1);

        driver.findElement(By.id("i_sJobNm")).sendKeys("경영지원");
        driver.findElement(By.xpath("//a[text()='조회']")).click();
        WaitUtil.waitFor(1);

        driver.findElement(By.xpath("//*[@id='jobListLayer']/table/tbody/tr/td/input")).click();
        WaitUtil.waitFor(1);
        driver.findElement(By.xpath("//a[text()='확인']")).click();
        WaitUtil.waitFor(2);

        driver.switchTo().window(winList.get(0));
        WaitUtil.waitFor(2);

        logger.info("가입조건 지정");

        logger.info("운전형태");
        Select selDriveOption = new Select(driver.findElement(By.id("i_sDrvTypCd")));
        selDriveOption.selectByVisibleText("자가용");

        logger.info("보험기간");
        Select $selInsTerm = new Select(driver.findElement(By.id("i_sPrdinsCd")));
        $selInsTerm.selectByVisibleText(info.getInsTerm());

        logger.info("납입기간");
        Select $selNapTerm = new Select(driver.findElement(By.id("i_sPmtTermCd")));
        $selNapTerm.selectByVisibleText(info.getNapTerm());

        logger.info("납입방법");
        Select $selNap = new Select(driver.findElement(By.id("i_sPmtCycDvcd")));
        $selNap.selectByVisibleText("월납");

        logger.info("보험료 계산하기 클릭!!");
//        driver.findElement(By.xpath("//span[text()='보험료 계산하기'/parent::a]")).click();
        driver.findElement(By.xpath("//*[@id='contentsWrap2']/div[2]/form/div/div[2]/a")).click();
        WaitUtil.waitFor(1);

        logger.info("'담보' 설정하기");
        List<WebElement> $trList = driver.findElements(By.xpath("//table[@id='trAllamt']//tbody//tr"));
        List<CrawlingTreaty> trtList = info.getTreatyList();
        DecimalFormat df = new DecimalFormat("###,###");
        int matchcount = 0;
        for(WebElement $tr : $trList) {
            String trName = $tr.findElement(By.xpath(".//td[1]")).getText();
            for(CrawlingTreaty treaty : trtList) {
                if(trName.equals(treaty.getTreatyName())) {
                    matchcount++;
                    logger.info("============ TREATIES ============");
                    logger.info("$TR    NAME :: {}", trName);
                    logger.info("TREATY NAME :: {}", treaty.getTreatyName());
                    logger.info("INPUT   AMT :: {}", df.format(treaty.getAssureMoney()));

                    $tr.findElement(By.xpath(".//td[1]//input")).click();
                    new Select($tr.findElement(By.xpath(".//td[4]//select")))
                        .selectByVisibleText(df.format(treaty.getAssureMoney()));
                    continue;
                }
                if(trName.equals("총 보험료")) {
                    logger.info("====================================");
                    logger.info("DONE");
                    break;
                }
            }
        }
        logger.info("====================================");
        logger.info("TR & TRT MATCH COUNT :: {}", matchcount);

        logger.info("보험료 계산하기 클릭!!");                   // 다시 눌러줘야 제대로된 보험료 계산 가능
//        driver.findElement(By.xpath("//span[text()='보험료 계산하기'/parent::a]")).click();
        driver.findElement(By.xpath("//*[@id='contentsWrap2']/div[2]/form/div/div[2]/a")).click();
        WaitUtil.waitFor(1);

        logger.info("월 보험료 확인 ");
        String monthlyPremium = driver.findElement(By.xpath("//table[@id='trAllamt']//tbody//td[text()='총 보험료']/parent::tr//td[5]"))
            .getText()
            .replaceAll("[^0-9]", "");
        info.getTreatyList().get(0).monthlyPremium = monthlyPremium;
        logger.info("monthlyPremium :: {}", trtList.get(0).monthlyPremium);

        logger.info("스크린샷");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight / 10.0 * 8.0);");
        takeScreenShot(info);
        logger.info("찰칵!");
        WaitUtil.waitFor(2);

        logger.info("해약환급금 확인");
        List<WebElement> tr$returnMoneyList = driver.findElements(By.xpath("//table[@id='trAllamt4']//tbody//tr"));
        List<PlanReturnMoney> prmList = new ArrayList<>();
        for(WebElement tr : tr$returnMoneyList) {
            PlanReturnMoney prm = new PlanReturnMoney();
            String term = tr.findElement(By.xpath(".//td[1]")).getText();
            String returnMoney = "";
            if (tr.findElement(By.xpath(".//td[3]")).equals("-")) {
                returnMoney = "0";
            } else {
                returnMoney = tr.findElement(By.xpath(".//td[3]"))
                    .getText()
                    .replaceAll("[^0-9]", "");
            }
            String returnRate = tr.findElement(By.xpath(".//td[4]")).getText();

            logger.info("============ REFUND INFO ============");
            logger.info("TERM  :: {}", term);
            logger.info("rAMT  :: {}", returnMoney);
            logger.info("rRATE :: {}", returnRate);

            prm.setPlanId(Integer.parseInt(info.getPlanId()));
            prm.setGender((info.getGender() == MALE) ? "M" : "F");
            prm.setInsAge(Integer.parseInt(info.getAge()));
            prm.setTerm(term);
            prm.setReturnMoney(returnMoney);
            prm.setReturnRate(returnRate);

            prmList.add(prm);
        }
        logger.info("=================================");
        logger.error("더 이상 참조할 차트가 존재하지 않습니다");
        logger.info("=================================");

        if(info.treatyList.get(0).productKind.equals(ProductKind.순수보장형)) {
            logger.info("보험형태 : {} 상품이므로 만기환급금을 0원으로 설정합니다", info.treatyList.get(0).productKind);
            info.returnPremium = "0";
        }

        info.setPlanReturnMoneyList(prmList);

        return true;
    }
}
