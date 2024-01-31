package com.welgram.crawler.direct.fire.nhf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class NHF_DSS_D009 extends CrawlingNHFDirect {


    public static void main(String[] args) {
        executeCommand(new NHF_DSS_D009(), args);
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setIniSafe(true);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        boolean result = false;

        result = frontPage(info);

        return result;
    }


    protected boolean frontPage(CrawlingProduct info) throws Exception {

        boolean _result = false;
        String genderOpt = (info.getGender() == 0) ? "sexDcd1" : "sexDcd2";
        String genderText = (info.getGender() == MALE) ? "남" : "여";

        logger.info("NHF_DSS_D009 :: {}", info.getProductName());
        // 서버에서 모니터링을 돌릴 경우 타임아웃으로 실패가 많아 대기시간을 많이 준다.
        WaitUtil.waitFor(30);
        chkSecurityProgram();

        logger.info("생년월일 :: {}", info.getFullBirth());
        setBirthday(By.id("iptBirth"), info.getFullBirth());

        logger.info("성별 :: {}", genderText);
        setGender(By.xpath("//label[@for = '" + genderOpt + "']"), genderText);

        logger.info("직업 설정");
        setJob();

        logger.info("보험료 계산하기 선택");
        btnClick(By.id("btnNext"), 3);
        waitHomepageLoadingImg();

        logger.info("보험기간 설정 :: {}", info.getInsTerm());
        setInsTerm(By.xpath("//ul[@id='pdtInsPrdListArea']//parent::li//span[contains(., '" + info.getInsTerm() + "')]"), info.getInsTerm());

        logger.info("납입기간 설정 :: {}", info.getNapTerm());
        setNapTerm(By.xpath("//ul[@id='pdtRvpdListArea']//parent::li//span[contains(., '" + info.getNapTerm() + "')]"), info.getNapTerm());

        logger.info("플랜유형 설정 :: {}", info.getTextType());
        setPlanType(By.xpath("//strong[text()='" + info.getTextType() + "']/parent::a"), info.getTextType());

        logger.info("특약 설정");
        setTreaties(info);

        logger.info("다시 계산하기 버튼 클릭");
        clickBtnReCalc(By.xpath("//em[text()='" + info.getTextType() + "']/ancestor::div[@name='tabBoxArea']//a[text()='다시 계산하기']"));
        waitHomepageLoadingImg();

        logger.info("주계약 보험료 크롤링");
        crawlPremium(By.id("sumPremAmt"), info);

        logger.info("스크린샷 찍기");
        helper.executeJavascript("window.scrollTo(0,0);");
        takeScreenShot(info);

        logger.info("해약환급금 크롤링");
        //최대 60초 기다려보기
        wait = new WebDriverWait(driver, 60);
        waitHomepageLoadingImg();
        crawlReturnMoneyList(By.xpath("//tbody[@id='srdtRfListBody_1']/tr"), info);

        _result = true;
        return _result;
    }

    @Override
    public void setTreaties(CrawlingProduct info) throws SetTreatyException {

        String welgramPlanType = info.getTextType();
        List<CrawlingTreaty> welgramTreatyList = info.getTreatyList();

        try {
            // 하단 고정 nav바 높이 구하기
            int height = getBottomNavHeight(By.cssSelector("#contents > div.btmNav"));

            //특약 그룹 펼침 버튼 모두 펼치기
            List<WebElement> $aList = driver.findElements(
                By.xpath("//ul[@id='barGrpArea']/li[@style='display:block']//a[@class='btnAcc']"));
            for (WebElement $a : $aList) {

                /*
                 * 펼침 버튼이 보이도록 스크롤 이동
                 * 단, scrollIntoView(true)를 통해 element를 상단에 맞춰 스크롤할 경우 위에 헤더바와 고정영역 nav?에 가려져 클릭이 안되므로
                 * scrollIntoView(false)를 통해 element를 하단에 맞춰 스크롤한다. 하지만 이래도 하단의 nav바에 가려져 클릭이 안되는데,
                 * 하단의 nav바 높이를 구해 그만큼 스크롤을 하단으로 이동시킨다.
                 *
                 * */

                //펼침 버튼이 보이도록 element를 하단에 맞춰 스크롤 이동
                helper.executeJavascript("arguments[0].scrollIntoView(false);", $a);

                //펼침 버튼이 보이게 스크롤 이동했어도, 하단 고정 nav바에 가려져 클릭이 안되므로 nav바 높이만큼 스크롤 하단으로 이동
                helper.executeJavascript("window.scrollBy(0, " + height + ")");

                //펼침 버튼 클릭
                helper.waitElementToBeClickable($a).click();
                WaitUtil.waitFor(1);
            }

            //플랜 유형에 따라 내가 특약을 직접 세팅해야하는 경우가 있고, 고정인 경우가 있다.
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
            CrawlingTreaty targetTreaty = null;

            boolean toSetTreaty = "자유설계".equals(welgramPlanType);

            //특약이 고정인 경우
            List<WebElement> $liList = driver.findElements(By.xpath("//ul[@id='cvgListArea']//a[@class='btnAcc td custom']"));

            for (WebElement $li : $liList) {
                WebElement $label = $li.findElement(By.xpath(".//ancestor::div[1]//span[@class='td title']"));
                WebElement $span = $li.findElement(By.xpath(".//span[@class='price']"));
                String targetTreatyName = $label.getText().trim();
                if (targetTreatyName.contains("\n")) {
                    int end = targetTreatyName.indexOf("\n");
                    targetTreatyName = targetTreatyName.substring(0, end);
                }
                String targetTreatyAssureMoney = $span.getText().trim();

                try {
                    targetTreatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(targetTreatyAssureMoney));

                    if (Integer.parseInt(targetTreatyAssureMoney) != 0) {
                        targetTreaty = new CrawlingTreaty();

                        targetTreaty.treatyName = targetTreatyName;
                        targetTreaty.assureMoney = Integer.parseInt(targetTreatyAssureMoney);
                        targetTreatyList.add(targetTreaty);
                    }

                } catch (NumberFormatException e) {
                    logger.info("특약명 : {} , 가입금액 : {}", targetTreatyName, targetTreatyAssureMoney);
                }
            }

            //가입설계 특약정보와 원수사 특약정보 비교
            logger.info("가입하는 특약은 총 {}개입니다.", targetTreatyList.size());

            boolean result = compareTreaties(targetTreatyList, welgramTreatyList);

            if (result) {
                logger.info("특약 정보가 모두 일치합니다");
            } else {
                throw new Exception("특약 불일치");
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    // 보험료 다시 계산하기 버튼 클릭
    protected void clickBtnReCalc(By by) throws  Exception {

        try {
            driver.findElement(by).click();
            WaitUtil.waitFor(2);
            waitHomepageLoadingImg();

        } catch (NoSuchElementException e) {
            logger.info("특약 가입금액 변동사항이 없어 바로 보험료를 크롤링하면 됩니다.\n" + e.getMessage());
        }
    }
}
