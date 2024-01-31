package com.welgram.crawler.direct.life.hnl;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HNL_CCR_D013 extends CrawlingHNLMobile {

    public static void main(String[] args) {
        executeCommand(new HNL_CCR_D013(), args);
    }


    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $button = null;

        modalCheck();

        logger.info("내 보험료 알아보기 버튼 클릭");
        $button = driver.findElement(By.id("btnCalcShow"));
        click($button);

        logger.info("생년월일 설정");
        setBirthday(info.getFullBirth());

        logger.info("성별 설정");
        setGender(info.getGender());

        logger.info("확인 버튼 클릭");
        $button = driver.findElement(By.id("btnCalc"));
        click($button);

        logger.info("보험기간 설정");
        setInsTerm(info.getInsTerm());

        logger.info("특약 설정 및 비교");
        setTreaties(info.getTreatyList());

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("해약환급금 조회 버튼 클릭");
        $button = driver.findElement(By.id("surrBtn"));
        helper.moveToElementByJavascriptExecutor($button);
        click($button);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info);

        return true;
    }

    @Override
    public void modalCheck() throws Exception {
        logger.info("모달창이 떴는지를 확인합니다.");

        boolean isDomExist = false;
        WebElement $button = null;
        By modalPosition = By.xpath("//article[@id='eventPopup']");

        isDomExist = helper.existElement(modalPosition);
        if(isDomExist) {
            WebElement $modal = driver.findElement(modalPosition);

            if($modal.isDisplayed()) {
                logger.info("안내 모달창이 떴습니다~~");
                $button = $modal.findElement(By.xpath(".//button[text()='닫기']"));
                helper.waitElementToBeClickable($button).click();
            }
        }
    }


    @Override
    public void setTreaties(List<CrawlingTreaty> treaties) throws SetTreatyException {
        try {

            WebElement $targetTreatyAreaDiv = driver.findElement(By.xpath("//div[@class='ipt_check_cancer']"));

            //특약명 선택을 쉽게 하기 위해서 특약 보험료 element 제거
            String script = "$('span.opt-txt1 > em').remove()";
            helper.executeJavascript(script);

            //가입설계 특약 정보 세팅하기
            for(CrawlingTreaty treaty : treaties) {
                String treatyName = treaty.getTreatyName();

                //특약명과 동일한 element 찾기
                WebElement $targetTreatySpan = $targetTreatyAreaDiv.findElement(By.xpath(".//span[@class[contains(., 'opt-txt1')]][text()='" + treatyName + "']"));
                WebElement $targetTreatyLabel = $targetTreatySpan.findElement(By.xpath("./parent::label"));
                click($targetTreatyLabel);

                logger.info("특약명 : {} 선택완료", treatyName);
            }

            //실제 선택된 원수사 특약 조회
            List<CrawlingTreaty> targetTreaties = new ArrayList<>();
            List<WebElement> $checkedInputs = $targetTreatyAreaDiv.findElements(By.cssSelector("input[type=checkbox][id^=option]:checked"));

            for(WebElement $checkedInput : $checkedInputs) {
                String id = $checkedInput.getAttribute("id");
                WebElement $checkedLabel = driver.findElement(By.xpath("//label[@for='" + id + "']"));
                WebElement $checkedSpan = $checkedLabel.findElement(By.xpath("./span"));

                String targetTreatyName = $checkedSpan.getText();

                CrawlingTreaty targetTreaty = new CrawlingTreaty();
                targetTreaty.setTreatyName(targetTreatyName);
                targetTreaties.add(targetTreaty);
            }

            //특약 비교
            List<String> treatyNames = treaties.stream().map(CrawlingTreaty::getTreatyName).collect(Collectors.toList());
            List<String> targetTreatyNames = targetTreaties.stream().map(CrawlingTreaty::getTreatyName).collect(Collectors.toList());

            //TODO 추후에 특약 비교 메서드 고도화 완료되면 갈아끼우기
            boolean isCorrect = treatyNames.containsAll(targetTreatyNames) && targetTreatyNames.containsAll(treatyNames);

            if(isCorrect) {
                logger.info("특약 정보 일치 ^0^");
            } else {
                logger.info("특약 정보 불일치");

                logger.info("■■■■■■■■ 실제 원수사에서 선택된 특약 정보 ■■■■■■■■");
                targetTreatyNames.forEach(treatyName -> logger.info("특약명 : {}", treatyName));

                logger.info("■■■■■■■■ 가입설계 특약 정보 ■■■■■■■■");
                treatyNames.forEach(treatyName -> logger.info("특약명 : {}", treatyName));
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        String title = "보험료 크롤링";

        CrawlingProduct info = (CrawlingProduct) obj[0];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {
            //보험료를 크롤링 하기 전에는 충분한 대기시간을 갖는다.
            WaitUtil.waitFor(3);

            //보험료 크롤링
            String premium = driver.findElement(By.id("totalPrem")).getText();
            premium = String.valueOf(MoneyUtil.toDigitMoney(premium));

            //주계약 보험료 세팅
            mainTreaty.monthlyPremium = premium;

            if("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
                throw new PremiumCrawlerException(exceptionEnum.getMsg());
            } else {
                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
            }

        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }
}