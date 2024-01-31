package com.welgram.crawler.direct.life.dgl;

import com.welgram.common.MoneyUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy0;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



public class DGL_ACD_D001 extends CrawlingDGLMobile {

    public static void main(String[] args) {
        executeCommand(new DGL_ACD_D001(), args);
    }



    // todo | 애초에 특약이 없다면 가설이 안만들어지지 않나??? 담당자와 함께 확인필요
    @Override
    protected boolean preValidation(CrawlingProduct info) {
        return info.getTreatyList().size() > 0;
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        WebElement $button = null;

        logger.info("보험료 알아보기 버튼 클릭");
        $button = driver.findElement(By.xpath("//a[normalize-space()='보험료 알아보기']"));
        click($button);

        logger.info("생년월일 설정");
        setBirthday(info.getBirth());

        logger.info("성별 설정");
        setGender(info.getGender());

        logger.info("보험료 계산 버튼 클릭");
        $button = driver.findElement(By.xpath("//a[normalize-space()='보험료 계산']"));
        click($button);

        logger.info("보험기간 비교");
        setInsTerm(info.getInsTerm());

        logger.info("특약 비교");
        setTreaties(info);

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info);

        return true;
    }



    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        String title = "보험기간";
        String expected = (String) obj[0];
        String actual = "";

        try {
            String insTerm = "";
            WebElement $premiumDiv = driver.findElement(By.xpath("//div[@class='toggle-anchor']"));
            $premiumDiv = $premiumDiv.findElement(By.xpath(".//div[@class='row2']"));
            insTerm = $premiumDiv.getText().trim();

            //텍스트에서 보험기간만 추출하기
            int idx = -1;
            String text = "보장기간";
            idx = insTerm.indexOf(text);
            actual = insTerm.substring(idx + text.length()).trim();

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            throw new SetInsTermException(e.getMessage());
        }
    }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream()
            .filter(t -> t.productGubun == CrawlingTreaty.ProductGubun.주계약)
            .findFirst()
            .get();

        try {
            WebElement $myPlanUl = driver.findElement(By.id("rcmrDgnUl"));
            WebElement $premiumDiv = $myPlanUl.findElement(By.xpath(".//div[@class='row2'][contains(., '보험료')]"));
            String[] premium = $premiumDiv.getText().split("\n");
            premium = new String[]{premium[0].replaceAll("[^0-9]", "")};
            mainTreaty.monthlyPremium = premium[0];

            if ("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                String msg = "주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.";
                logger.info(msg);
                throw new Exception(msg);
            } else {
                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
            }

            logger.info("스크린샷 찍기");
            takeScreenShot(info);

        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getMessage());
        }
    }



    public void setTreaties(CrawlingProduct info) throws CommonCrawlerException {

        List<CrawlingTreaty> welgramTreatyList = info.getTreatyList();
        List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

        try {
            //가입설계에 선택특약이 있을 경우에만
            WebElement $treatyUl = driver.findElement(By.xpath("//ul[@class='details']"));
            List<WebElement> $treatyLiList = $treatyUl.findElements(By.tagName("li"));
            for (WebElement $li : $treatyLiList) {
                WebElement $treatyNameSpan = $li.findElement(By.xpath("./span[@class='item']"));
                WebElement $treatyAssureMoneySpan = $li.findElement(By.xpath("./span[@class='data']"));
                String treatyName = $treatyNameSpan.getText().trim();
                String treatyAssureMoney = $treatyAssureMoneySpan.getText().trim();
                treatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(treatyAssureMoney));

                CrawlingTreaty targetTreaty = new CrawlingTreaty();
                targetTreaty.setTreatyName(treatyName);
                targetTreaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));
                targetTreatyList.add(targetTreaty);
            }

            //원수사와 가입설계 특약 정보를 비교하기 전에 가입금액이 매번 달라지는 특약 정보는 제외시키고 비교를 진행
            logger.info("원수사 특약 정보 vs 가입설계 특약 정보 비교");
            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy0());
            if (result) {
                logger.info("특약 정보 모두 일치");
            } else {
                logger.info("특약 정보 불일치");
                throw new Exception();
            }

        } catch (Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.ERR_BY_TREATY, e.getMessage());
        }
    }
}