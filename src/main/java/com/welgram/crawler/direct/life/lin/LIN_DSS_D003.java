package com.welgram.crawler.direct.life.lin;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

public class LIN_DSS_D003 extends CrawlingLINMobile { // 무배당 준비된 건강검진 미니보험

    public static void main(String[] args) {
        executeCommand(new LIN_DSS_D003(), args);
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
        String insTerm = info.getInsTerm() + "만기";

        logger.info("LIN_DSS_D003 :: {}", info.getProductName());
        WaitUtil.waitFor(1);

        logger.info("보험료 확인하고 가입하기 클릭");
        btnClick(By.xpath("//span[contains(.,'보험료 확인하고 가입하기')]"), 2);

        logger.info("생년월일 :: {}", info.getBirth());
        setBirthday(By.cssSelector(".el-input__inner"), info.getBirth());

        logger.info("성별 :: {}", genderOpt);
        setGender(By.xpath("//div[@class='el-form-item form-group-xs']//span[text()='" + genderOpt + "']"), genderOpt);

        logger.info("보험료 확인하고 가입하기 클릭");
        btnClick(By.xpath("//div[@class='drawer-bottom__buttons']//span"), 2);

        logger.info("보험기간 :: {}", info.getInsTerm());
        // 보험기간변경이 필요할 경우
        if(!insTerm.equals("1년만기")){
            setInsTerm(insTerm);
        }

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("월 보험료 가져오기");
        crawlPremium(By.xpath("//*[@class='price']"), info);

        logger.info("해약환급금 가져오기");
        // 보장내용/해약환급금 버튼
        by = By.xpath("//*[@class='el-button bottom-btn el-button--text el-button--small title-btn- el-button--text-icon']");
        helper.moveToElementByJavascriptExecutor(by);
        WaitUtil.waitFor(1);
        btnClick(by, 2);

        // 해약환급금 탭
        btnClick(By.xpath("//div[@class='el-tabs__item is-top'][normalize-space()='해약환급금']"), 2);
        crawlReturnMoneyList(info, By.cssSelector("div.price-table > div.l-table.l-table > div > div.el-table__body-wrapper.is-scrolling-none > table > tbody > tr"));
    }
}