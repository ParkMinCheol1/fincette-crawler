package com.welgram.crawler.direct.fire.mez;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;


public class MEZ_OST_D001 extends CrawlingMEZMobile {

    public static void main(String[] args) {
        executeCommand(new MEZ_OST_D001(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $button = null;
        WebElement $span = null;
        WebElement $a = null;
        waitLoadingBar();

        logger.info("팝업 확인");
        popUpAlert();

        logger.info("더보기 선택");
        $a = driver.findElement(By.id("mainList_productList"));
        click($a);

        logger.info("다이렉트 해외여행보험 선택");
        WebElement target = driver.findElement(By.xpath("//span[normalize-space()='다이렉트 해외여행보험']"));
        moveToElement(target);
        helper.click(target);     //click() 사용 시 timeout

        logger.info("여행일정 선택");
        setTravelDate();

        logger.info("다음 버튼 클릭");
        $button = driver.findElement(By.xpath("//div[@class='btnWrap']//span[text()='다음']"));
        helper.click($button);
        WaitUtil.waitFor(2);

        logger.info("생년월일 설정");
        setMobileBirthday(info.getFullBirth());

        logger.info("성별 설정");
        setMobileGender(info.getGender());

        logger.info("다음 버튼 클릭");
        $button = driver.findElement(By.xpath("//div[@class='btnWrap']"));
        helper.click($button);
        WaitUtil.waitFor(1);

        logger.info("유의 사항 확인 버튼 클릭");
        $button = driver.findElement(By.xpath("//div[@class='popBtn']//button"));
        helper.click($button);
        helper.waitForCSSElement("#q-loading");

        logger.info("플랜 선택 :: {}", info.textType);
        setMobilePlan(info.textType);

        logger.info("플랜 자세히보기 선택");
        $span = driver.findElement(By.xpath("//span[normalize-space()='플랜 자세히보기']"));
        helper.click($span);

        logger.info("국내 실손 의비료 특약 선택 해지");
        confirmTreaty();

        logger.info("확인 버튼 클릭");
        $button = driver.findElement(By.xpath("//div[@class='popBtn']//span"));
        helper.click($button);

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("스크린샷");
        takeScreenShot(info);

        return true;
    }

    protected void confirmTreaty() throws Exception {
        WaitUtil.waitFor(2);
        moveToElement(driver.findElement(By.xpath("//div[@class='tblStyle3 mgt24']")));
        WaitUtil.waitFor(2);
        try{
            String id = driver.findElement(By.cssSelector("input[id=btnToggle]:checked")).getAttribute("id");
            WebElement btnToggle = driver.findElement(By.xpath("//input[@id='"+id+"']//..//label"));
            helper.click(btnToggle);
            WaitUtil.waitFor(2);
        } catch (NoSuchElementException e){
            logger.info("국내 실손 의료비 특약가입 선택이 되어있지않습니다.");
        }

    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj[0];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream()
            .filter(t -> t.productGubun.equals(CrawlingTreaty.ProductGubun.주계약))
            .findFirst()
            .get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {

            //보험료 크롤링 전에는 넉넉하게 대기시간을 준다
            WaitUtil.waitFor(3);

            WebElement $planUl = driver.findElement(By.xpath("//ul[@class='calcRadio']"));
            List<WebElement> $planLabel = $planUl.findElements(By.xpath(".//li//label"));
            String premium = "";

            for(int i = 0; i < $planLabel.size(); i++){
                if($planLabel.get(i).getText().contains(info.textType)){
                    //플랜 선택
                    helper.click($planLabel.get(i));
                    premium = $planLabel.get(i).getText().replaceAll("[^0-9]", "");
                    break;
                }
            }

            //보험료 정보 세팅
            mainTreaty.monthlyPremium = premium;

            if("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
                throw new PremiumCrawlerException(exceptionEnum.getMsg());
            } else {
                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
            }
        }  catch (Exception e) {
            throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }
}