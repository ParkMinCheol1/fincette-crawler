package com.welgram.crawler.direct.life.lin;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

public class LIN_CHL_D001 extends CrawlingLINDirect { // 무배당라이나다이렉트키즈보험(갱신형)

    public static void main(String[] args) {
        executeCommand(new LIN_CHL_D001(), args);
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

    // 사이트웹 버전 ( https://direct.lina.co.kr/product/ess/dtc03/easy )
    private void webCrawling(CrawlingProduct info) throws Exception {

        String genderOpt = (info.getGender() == MALE) ? "남자" : "여자";

        logger.info("LIN_CHL_D001 :: {}", info.getProductName());
        WaitUtil.waitFor(1);

        logger.info("생년월일 :: {}", info.getBirth());
        setBirthday(By.cssSelector(".el-input__inner"), info.getBirth());

        logger.info("성별 :: {}", genderOpt);
        setGender(By.xpath("//div[@class='inner-sm']//span[text()='" + genderOpt + "']"), genderOpt);

        logger.info("보험료 확인 버튼 클릭");
        btnClick(By.xpath("//span[contains(.,'보험료 확인하고 가입하기')]"), 6);
        waitLoadingImg();

        logger.info("월 보험료 가져오기");
        crawlPremium(By.xpath("//div[@class='price-wrap']//strong"), info);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("해약환급금 버튼 클릭");
        btnClick(By.xpath("//button[@class='el-button el-button--black el-button--small is-plain title-btn-']//span[contains(.,'해약환급금')]"), 2);

        logger.info("해약환급금 가져오기");
        crawlReturnMoneyList(info, By.cssSelector("div.l-table.mt12 > div > div.el-table__body-wrapper.is-scrolling-none > table > tbody > tr"));
    }

}