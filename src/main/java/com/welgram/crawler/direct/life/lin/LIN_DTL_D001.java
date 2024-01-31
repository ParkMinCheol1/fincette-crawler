package com.welgram.crawler.direct.life.lin;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanCalc;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

public class LIN_DTL_D001 extends CrawlingLINDirect {   // 무배당 9900 ONE치아보험

    public static void main(String[] args) {
        executeCommand(new LIN_DTL_D001(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        webCrawling(info);
        return true;
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setImageLoad(true);
    }

    // 사이트웹
    // https://direct.lina.co.kr/product/dtc005
    private void webCrawling(CrawlingProduct info) throws Exception {

        String genderOpt = (info.getGender() == MALE) ? "남자" : "여자";

        logger.info("LIN_CCR_D001 :: {}", info.getProductName());
        driver.manage().window().maximize();
        WaitUtil.waitFor(1);

        logger.info("생년월일 :: {}", info.getBirth());
        setBirthday(By.cssSelector(".el-input__inner"), info.getBirth());

        logger.info("성별 :: {}", genderOpt);
        setGender(By.xpath("//div[@class='inner-sm']//span[text()='" + genderOpt + "']"), genderOpt);

        logger.info("내 보장금액 확인하기 버튼 클릭 ");
        btnClick(By.xpath("//span[contains(.,'보험료 확인하고 가입하기')]"), 5);
        waitLoadingImg();
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        WaitUtil.waitFor(5);

        logger.info("특약 보장 추가 버튼 클릭 ");
        btnAddGuarantee(By.cssSelector("#guteePlus"));

        // 스크롤 이동
        helper.moveToElementByJavascriptExecutor(By.cssSelector(".graph-wrap"));
        WaitUtil.waitFor(1);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("월 보험료");
        crawlPremium(By.id("tel-con-3"), info);

        logger.info("가입금액변동 특약 데이터 쌓기");
        // 보장내역 버튼 클릭
        btnClick(By.xpath("//div[@class='price-info-box']//*[normalize-space()='보장내용']"), 2);
        // 특약별 가입금액 메소드
        setVariableTreaty(info);

        logger.info("해약환급금 버튼 클릭");
        btnClick(By.xpath("//div[@class='price-info-box']//*[normalize-space()='해약환급금']"), 2);

        logger.info("해약환급금 조회");
        crawlReturnMoneyList(info, By.cssSelector("div.el-dialog__body > div.l-pop > div > div:nth-child(2) > div.l-table.mt12 > div > div.el-table__body-wrapper.is-scrolling-none > table > tbody > tr"));
    }


    // 특약별 가입금액 메소드
    @Override
    protected void setVariableTreaty(CrawlingProduct info) throws CommonCrawlerException {

        try {
            WebElement $th = null;
            WebElement $tr = null;
            WebElement $assureMoneyTd = null;

            for (int i = 0; i < info.getTreatyList().size(); i++) {
                String wTreatyName = info.getTreatyList().get(i).getTreatyName();

                if (wTreatyName.substring(0, 1).equals("주")) {
                    if (wTreatyName.contains(" ")) {
                        wTreatyName = wTreatyName.substring(wTreatyName.indexOf(" "), wTreatyName.length()).trim();
                    } else {
                        wTreatyName = wTreatyName.substring(wTreatyName.indexOf(")") + 1, wTreatyName.length()).replaceAll("보험금", "").trim();
                    }

                } else {
                    wTreatyName = wTreatyName.substring(wTreatyName.indexOf(")") + 1, wTreatyName.length()).trim();
                }

                $th = driver.findElement(By.xpath("//*[@class=\"l-table mt12\"]//table//p[contains(.,'" + wTreatyName + "')]"));
                $tr = $th.findElement((By.xpath("./ancestor::tr")));
                $assureMoneyTd = $tr.findElement((By.xpath("./td[3]")));

                String targetTreatyName = $th.getText().trim().replaceAll("(\r\n|\r|\n|\n\r)", " ");
                String targetTreatyMoney = String.valueOf(MoneyUtil.toDigitMoney2($assureMoneyTd.getText().trim()));

                logger.info("===================================");
                logger.info("특약명 :: {}", targetTreatyName);
                logger.info("가입금액 :: {}", targetTreatyMoney);
                logger.info("===================================");

                int MapperId = Integer.parseInt(info.getTreatyList().get(i).mapperId);
                String gender = (info.getGender() == MALE) ? "남" : "여";
                int age = Integer.parseInt(info.getAge());

                PlanCalc planCalc = new PlanCalc();

                planCalc.setMapperId(MapperId);
                planCalc.setGender(gender);
                planCalc.setInsAge(age);
                planCalc.setAssureMoney(targetTreatyMoney);

                info.treatyList.get(i).setPlanCalc(planCalc);
            }

            // 확인 버튼
            driver.findElement(By.xpath("//div[@class='footer']//span[contains(.,'확인')]")).click();
            WaitUtil.waitFor(2);

            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollBy(0, 300);");

        } catch (Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.ERROR_BY_TREATIES_GETTING + "\n" + e.getMessage());
        }
    }

    // 특약 보장 추가 버튼
    public void btnAddGuarantee(By byElement) throws CommonCrawlerException {

        try {
            helper.executeJavascript("arguments[0].scrollIntoView(true);", driver.findElement(byElement));
            WaitUtil.waitFor(3);
            helper.executeJavascript("arguments[0].click();", driver.findElement(byElement));

        } catch (Exception e){
            throw new CommonCrawlerException("특약 보장 추가 버튼 클릭 오류가 발생했습니다.\n" + e.getMessage());
        }
    }

}