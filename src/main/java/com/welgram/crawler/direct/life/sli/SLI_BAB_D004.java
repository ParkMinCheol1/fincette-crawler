package com.welgram.crawler.direct.life.sli;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SLI_BAB_D004 extends CrawlingSLIDirect {

    public static void main(String[] args) {
        executeCommand(new SLI_BAB_D004(), args);
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) {
        option.setImageLoad(false);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $label = null;
        WebElement $button = null;
        WebElement $a = null;

        waitLoadingBar();
        WaitUtil.loading(2);

        logger.info("상품 [태아] 선택");
        $label = driver.findElement(By.xpath("//label[@for='stdPrdtGrp1']"));
        click($label);

        logger.info("출산 예정일");
        setDueDate(info);

        logger.info("산모 생년월일");
        setBirthday(info.getFullBirth());

        logger.info("내 보험료 확인 버튼 선택");
        $button = driver.findElement(By.id("calculate"));
        click($button);

        logger.info("보험 기간 선택");
        By location = By.id("insTerm1");
        setInsTerm(info.insTerm + "만기", location);

        logger.info("납입 기간 선택");
        location = By.id("napTerm1");
        setNapTerm(info.napTerm + "납", location);

        logger.info("보장금액 선택");
        location = By.id("reCalcPrice1");
        setSelectBoxAssureMoney(info, location);

        logger.info("다시계산 버튼 클릭");
        location = By.id("recalcHrzntlBtn");
        reCalculate(location);

        logger.info("선택할 플랜 number");
        int planNum = getPlanNum(info);

        logger.info("플랜 선택");
        location = By.id("planArea" + planNum);
        setPlan(info, location);

        logger.info("보험료 크롤링");
        location = By.id("monthPremium");
        crawlPremium(info, location, planNum);

        logger.info("해약환급금 버튼 클릭");
        $a = driver.findElement(By.xpath("//button[@onclick='setGrntAndRtnDtChild(" + planNum + ");']"));
        click($a);

        logger.info("해약환급금 스크랩");
        location = By.xpath("//tbody[contains(@id, 'pReturnCancel')]//tr");
        crawlReturnMoneyList1(info, location);

        logger.info("스크린샷");
        takeScreenShot(info);

        return true;
    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        String title = "보험료 크롤링";
        WebElement $button = null;

        CrawlingProduct info = (CrawlingProduct) obj[0];
        By monthlyPremium = (By) obj[1];
        int planNum = (int) obj[2];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(
            ProductGubun.주계약)).findFirst().get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {

            WebElement $label = helper.waitElementToBeClickable(By.xpath("//label[contains(.,'출산지원금')]"));
            WebElement $input = $label.findElement(By.xpath("./preceding-sibling::input"));

            if ($input.isSelected()) {
                click($label);

                // 변경된 내용으로 다시 계산하기
                $button = driver.findElement(By.id("recalcHrzntlBtn"));
                click($button);
            }

            mainTreaty.monthlyPremium = driver.findElement(By.xpath("//strong[@id='monthPremium" + planNum + "']")).getAttribute("innerHTML").replaceAll("\\D", "");
            logger.info("초회 보험료 스크랩: {}",mainTreaty.monthlyPremium);

            info.nextMoney = driver.findElement(By.xpath("//strong[@id='aftrBrthMnthlPrm" + planNum + "']")).getAttribute("innerHTML").replaceAll("\\D", "");
            logger.info("계속 보험료 스크랩: {}",info.nextMoney);

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

    @Override
    protected boolean preValidation(CrawlingProduct info) {

        boolean result = true;

        try {
            int calcInsAge = Integer.parseInt(info.age);// 계산테이블에서 정의한 나이
            int minInsAge = info.minInsAge;                // 가입설계에서 정의한 최소 나이
            int maxInsAge = info.maxInsAge;                // 가입설계에서 정의한 최대 나이

            if(info.gender == MALE) {
                throw new Exception("산모가 가입하는 상품입니다.");
            }

            if (calcInsAge >= minInsAge && calcInsAge <= maxInsAge){
                logger.info("가입설계에서 정한 나이 확인!!");
            }else{
                throw new Exception("가입설계에서 정한 나이가 아닙니다.");
            }
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }

        return result;
    }
}
