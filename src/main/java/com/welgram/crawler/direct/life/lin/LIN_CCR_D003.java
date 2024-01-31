package com.welgram.crawler.direct.life.lin;

import com.welgram.common.WaitUtil;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class LIN_CCR_D003 extends CrawlingLINMobile { // 무배당라이나다이렉트건강맞춤암보험(갱신형)

    public static void main(String[] args) {
        executeCommand(new LIN_CCR_D003(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        mobileCrawling(info);
        return true;
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setImageLoad(true);
        option.setMobile(true);
    }

    // 모바일
    private void mobileCrawling(CrawlingProduct info) throws Exception {

        // 남성 전용 가설
        if(info.getTextType().contains("남성")){

            // 남자 전용 가설이지만 여성일 경우 종료
            if(info.getGender() != 0){
                throw new Exception("남성 전용 가설입니다.");
            }

        } else { // 여성 전용 가설

            // 여성 전용 가설이지만 남자일 경우 종료
            if(info.getGender() == 0){
                throw new Exception("여성 전용 가설입니다.");
            }
        }

        By $byElement = null;
        String genderOpt = (info.getGender() == MALE) ? "남자" : "여자";
        DecimalFormat df = new DecimalFormat("###,###");
        String money = ""; // 기본 암 진단비

        logger.info("LIN_CCR_D003 :: {}", info.getProductName());
        WaitUtil.waitFor(1);

        logger.info("보험료 확인하고 가입하기 클릭 ");
        btnClick(By.xpath("//span[contains(.,'보험료 확인하고 가입하기')]"), 2);

        logger.info("생년월일 :: {}", info.getBirth());
        setBirthday(By.cssSelector(".el-input__inner"), info.getBirth());

        logger.info("성별 :: {}", genderOpt);
        setGender(By.xpath("//div[@class='el-form-item form-group-xs']//span[text()='" + genderOpt + "']"), genderOpt);

        logger.info("보험료 확인하고 가입하기 클릭");
        btnClick(By.xpath("//div[@class='drawer-bottom__buttons']//span"), 10);
        waitLoadingImg();

        if(helper.isAlertShowed()){
            waitLoadingImg();
            WaitUtil.waitFor(8);
        }

        logger.info("플랜 선택 :: {}", info.textType);
        setPlanType(By.xpath("//div[@class='tab-header']//span[contains(.,'" + info.textType + "')]"), info.textType);
        WaitUtil.waitFor(8);

        logger.info("다음 버튼 클릭");
        btnClick(By.xpath("//div[@class='l-bottom']//span[contains(.,'다음')]"), 2);

        // 기본 암 진단비 ( 3천만원 or 5천만원 클릭! )
        logger.info("기본 암 진단비 선택 :: {}", info.getAssureMoney());
        money = df.format(Integer.parseInt(info.getAssureMoney()) / 10000);
        btnClick(By.xpath("//div[@class='tab-header']//span[contains(.,'" + money + "')]"), 2);

        if(info.getGender() !=0) {
            driver.findElement(By.xpath(".//p[contains(., '" + "위암" + "')]")).click();
            driver.findElement(By.xpath(".//p[contains(., '" + "폐암" + "')]")).click();
        }

        logger.info("스크린샷");
        takeScreenShot(info);

        logger.info("월 보험료");
        crawlPremium(By.xpath("//div[@class='l-bottom']//span[contains(.,'총 보험료')]"), info);

        logger.info("해약환급금/보장내용 버튼");
        $byElement = By.xpath("//div[@class='l-section gap-bottom_large']//span[contains(.,'해약환급금')]");
        helper.moveToElementByJavascriptExecutor($byElement);
        WaitUtil.waitFor(1);
        btnClick($byElement, 5);

        logger.info("특약 확인");
        getHomepageTreaties(info.getTreatyList());

        logger.info("해약환급금 탭");
        btnClick(By.xpath("//div[@class='el-tabs__item is-top'][normalize-space()='해약환급금']"), 2);

        logger.info("해약환급금 조회");
        crawlReturnMoneyList(info, By.cssSelector("div.price-table > div.l-table.l-table > div > div.el-table__body-wrapper.is-scrolling-none > table > tbody > tr"));
    }

    // 원수사 페이지에 세팅된 특약리스트
    @Override
    protected List<CrawlingTreaty> getHomepageTreaties(List<CrawlingTreaty> welgramTreatyList) throws Exception {

        // 가입설계-원수사 특약 일치여부 확인용
        List<CrawlingTreaty> targetTreatyList = new ArrayList<>(); // 홈페이지에서 선택된 특약리스트
        CrawlingTreaty targetTreaty = new CrawlingTreaty();

        // 특약 확인
        for (CrawlingTreaty welgramTreaty : welgramTreatyList) {
            String wTreatyName = welgramTreaty.getTreatyName();

            WebElement $treatyNameEl = driver.findElement(By.xpath("//h3[@class='c-title_title fs-18']/span[contains(.,'" + wTreatyName + "')]"));
            WebElement $div = $treatyNameEl.findElement(By.xpath("./ancestor::div[@class='c-title_content']"));
            WebElement $assureMoneyEl = $div.findElement(By.cssSelector("div > p"));

            // 특약명
            String $targetTreatyName = $treatyNameEl.getText().trim();

            // 가입금액
            String treatyMoney = $assureMoneyEl.getText().replaceAll("[^0-9]", "");
            int $targetTreatyMoney = Integer.valueOf(treatyMoney) * 10000;

            targetTreaty = new CrawlingTreaty();
            targetTreaty.setTreatyName($targetTreatyName);
            targetTreaty.setAssureMoney($targetTreatyMoney);

            logger.info("==============================");
            logger.info("선택된 특약명 : {}", $targetTreatyName);
            logger.info("선택된 가입금액 : {}", $targetTreatyMoney);
            logger.info("==============================");

            targetTreatyList.add(targetTreaty);
        }
        logger.info("특약 비교 및 확인");
        boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());

        if (result) {
            logger.info("특약 정보가 모두 일치합니다");
        } else {
            logger.error("특약 정보 불일치");
            throw new Exception();
        }

        return targetTreatyList;
    }
}