package com.welgram.crawler.direct.fire.hwf;


import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class HWF_SAV_F003 extends CrawlingHWFAnnounce {

    // 드림모아저축보험2310 무배당 - 상해사망특약제외
    public static void main(String[] args) {
        executeCommand(new HWF_SAV_F003(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        crawlFromAnnounce(info);

        return true;

    }



    private void crawlFromAnnounce(CrawlingProduct info) throws Exception {

        driver.manage().window().maximize();

        logger.info("생년월일 설정");
        WebElement $birthDayInput = driver.findElement(By.id("i_jumin"));
        setBirthday($birthDayInput, info.fullBirth);

        logger.info("성별 설정");
        WebElement $genderSelect = driver.findElement(By.id("i_no"));
        setGender($genderSelect, info.gender);

        logger.info("직업 설정");
        WebElement $jobSearch = driver.findElement(By.id("jobSearch"));
        setJob($jobSearch);

        logger.info("보험기간 설정 : {}", info.insTerm);
        WebElement $insTermSelect = driver.findElement(By.cssSelector("select[name=bogi]"));
        setInsTerm($insTermSelect, info.insTerm);

        logger.info("납입기간 설정: {}", info.napTerm);
        WebElement $napTermSelect = driver.findElement(By.cssSelector("select[name=napgi]"));
        String napTerm = (info.insTerm.equals(info.napTerm)) ? "전기납" : info.napTerm;
        setNapTerm($napTermSelect, napTerm);

        logger.info("납입주기 설정: 월납");
        WebElement $napCycleSelect = driver.findElement(By.cssSelector("select[name=napbang]"));
        setNapCycle($napCycleSelect, "월납");

        logger.info("납입보험료 설정");
        WebElement $assureMoneyInput = driver.findElement(By.id("smPrm"));
        setAssureMoney($assureMoneyInput, info.assureMoney);

        logger.info("특약 설정");
        List<CrawlingTreaty> treaties = info.treatyList.stream().filter(t -> t.productGubun == ProductGubun.선택특약).collect(Collectors.toList());
        setTreatiesNew(treaties);

        logger.info("보험료 계산 버튼 클릭");
        announceBtnClick(By.id("btnCalc"));

        logger.info("주계약 보험료 크롤링");
        crawlAnnouncePagePremiums(info);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("해약환급금 크롤링");
        crawlAnnouncePageReturnPremiums(info);
        
    }



    private void setTreatiesNew(List<CrawlingTreaty> treaties) throws CommonCrawlerException {

        List<CrawlingTreaty> targetTreaties = new ArrayList<>();
        String script = "";

        /**
         * 한화손해보험 공시실 대면상품의 경우 ui가 굉장히 번거롭다.
         * 미가입하는 특약들에 대해서 전부 0만원으로 세팅해줘야하는 작업을 해야한다.
         * 손쉽게 작업하기 위해서 처음부터 모든 특약을 미가입처리한채로 시작한다.
         */
        // 활성화된 input값 0만원으로 초기화 (= 특약 미가입 처리)
        script = "$('tr:visible input[name*=ainsure]:not(:disabled)').val('0');";
        executeJavascript(script);

        // 활성화된 select "선택" 값으로 초기화 (= 특약 미가입 처리)
        script = "$('tr:visible select[name*=ainsure]:not(:disabled) option[value=\"0\"]').prop('selected', true);";
        executeJavascript(script);

        try {
            String treatyName = treaties.get(0).treatyName;
            String treatyAssureMoney = String.valueOf(treaties.get(0).assureMoney);

            WebElement $th = driver.findElement(By.xpath("//th[text()='" + treatyName + "']"));
            WebElement $tr = $th.findElement(By.xpath("./parent::tr"));
            WebElement $input = $tr.findElement(By.xpath(".//input[contains(@name, 'ainsure')]"));

            logger.info("특약명 : {}", treatyName);

            // 가입금액 입력
            // TODO 가입금액 단위 읽어서 세팅하도록
            treatyAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney) / 10000);

            // 한화손해보험 공시실에서는 input을 초기화할 때 ctrl + a + delete가 작동하지 않는다.
            // 무조건 backspace로 지워야함. 현재 입력된 text의 길이만큼 backspace를 누른다.
            script = "return $(arguments[0]).val();";
            String currentValue = String.valueOf(executeJavascript(script, $input));
            for (int i = 0; i < currentValue.length(); i++) {
                $input.sendKeys(Keys.BACK_SPACE);
            }
            helper.sendKeys4_check($input, treatyAssureMoney);

        } catch (Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.ERROR_BY_CRAWL_TREATIES);
        }

    }



    @Override
    protected void crawlAnnouncePagePremiums(Object...obj) throws Exception {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        String monthlyPremium = info.assureMoney;

        try {
            moveToElement(By.id("btnReCalc"));
            logger.info("크롤링 위해 화면이동");
        } catch (Exception e) {
            logger.info("화면이동 필요없음");
        }

        info.treatyList.get(0).monthlyPremium = monthlyPremium;
        logger.info("월 보험료 : {}", monthlyPremium + "원");

        if ("0".equals(info.treatyList.get(0).monthlyPremium)) {
            throw new Exception("주계약 보험료는 0원일 수 없습니다");
        }

    }

}
