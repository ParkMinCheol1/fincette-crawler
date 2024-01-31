package com.welgram.crawler.direct.fire.mgf;

import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author user MG손해보험 (무)간편한 실손의료비보장보험
 */
public class MGF_AMD_F001 extends CrawlingMGFAnnounce {

    public static void main(String[] args) {
        executeCommand(new MGF_AMD_F001(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        crawlFromHomepage(info);

        return true;
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setBrowserType(BrowserType.Chrome);
        option.setImageLoad(true);
        option.setUserData(false);
    }


    private void crawlFromHomepage(CrawlingProduct info) throws Exception {

        String gender = (info.getGender() == MALE) ? "man" : "woman";
        String birthIdArr[] = {"Year", "Month", "Day"};

        logger.info("장기손해보험탭으로 이동");
        selectTab(driver.findElement(By.linkText("장기손해보험")));

        logger.info("계산하기 버튼 클릭");
        selectTargetProduct(driver.findElement(By.xpath("//td[contains(.,'" + info.getProductName() + "')]/following-sibling::td/a")));

        logger.info("생년월일 :: {}", info.getFullBirth());
        setBirthday(info.getFullBirth(), birthIdArr);

        logger.info("성별 :: {}", (info.getGender() == MALE) ? "남성" : "여성");
        setGender(driver.findElement(By.xpath("//*[@id=\"step1\"]//table//label[@for='" + gender + "']")));

        logger.info("월보험료 가져오기");
        crawlPremium(driver.findElement(By.cssSelector("#insSum")), info);

        logger.info("특약 확인");
        checkTreaties(driver.findElements(By.cssSelector("#InList > tr")), info.getTreatyList());

        logger.info("스크린샷 찍기");
        takeScreenShot(info);
    }

}