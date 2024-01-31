package com.welgram.crawler.direct.life.lin;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

public class LIN_DSS_D001 extends CrawlingLINMobile { // (무)안심되는아나필락시스쇼크진단보험

    public static void main(String[] args) {
        executeCommand(new LIN_DSS_D001(), args);
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

        By by = null;
        String genderOpt = (info.getGender() == MALE) ? "남자" : "여자";

        logger.info("LIN_DSS_D001 :: {}", info.getProductName());
        WaitUtil.waitFor(1);

        logger.info("보험료 확인하고 가입하기 클릭");
        btnClick(By.xpath("//span[contains(.,'보험료 확인하고 가입하기')]"), 2);

        logger.info("생년월일 :: {}", info.getBirth());
        setBirthday(By.cssSelector(".el-input__inner"), info.getBirth());

        logger.info("성별 :: {}", genderOpt);
        setGender(By.xpath("//div[@class='el-form-item form-group-xs']//span[text()='" + genderOpt + "']"), genderOpt);

        logger.info("확인 버튼 클릭");
        btnClick(By.cssSelector("div.drawer-bottom__buttons > button > span"), 5);
        waitLoadingImg();

        logger.info("보장 유형 :: 기본보장");
        setPlanType(By.xpath("//div[@class='tab-header']//span[contains(.,'기본보장')]"), "기본보장");

        logger.info("특약 세팅 및 확인");
        setTreaties(info.getTreatyList());

        logger.info("변경 적용하기 클릭");
        btnClick(By.xpath("//*[@class='el-button el-button--black el-button--medium title-btn-']"), 2);
        waitLoadingImg();

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("월 보험료 가져오기");
        crawlPremium(By.xpath("//button[@class='el-button tab-item title-btn- active']//*[@class='price']"), info);

        logger.info("해약환급금 가져오기");
        // 보장내용/해약환급금 버튼
        by = By.xpath("//div[@class='tab-content'][not(@style[contains(.,'display: none;')])]//button[@class='el-button bottom-btn el-button--text el-button--small title-btn- el-button--text-icon']");
        btnClick(by, 2);
        // 해약환급금 탭
        btnClick(By.xpath("//div[@class='el-tabs__item is-top'][normalize-space()='해약환급금']"), 2);

        crawlReturnMoneyList(info, By.cssSelector("#pane-second > div > section > div.price-table > div.l-table.l-table > div > div.el-table__body-wrapper.is-scrolling-none > table > tbody > tr"));

    }
}