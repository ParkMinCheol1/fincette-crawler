package com.welgram.crawler.direct.fire.mgf;

import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

/**
 * @author user MG손해보험 (무)원더풀 플러스종합보험(연만기갱신형)(23.01)
 */
public class MGF_DSS_F009 extends CrawlingMGFAnnounce {

    public static void main(String[] args) {
        executeCommand(new MGF_DSS_F009(), args);
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
        selectTargetProduct(driver.findElement(By.xpath("//td[contains(.,'" + info.getProductNamePublic() + "')]/following-sibling::td/a")));

        logger.info("생년월일 :: {}", info.getFullBirth());
        setBirthday(info.getFullBirth(), birthIdArr);

        logger.info("성별 :: {}", (info.getGender() == MALE) ? "남성" : "여성");
        setGender(driver.findElement(By.xpath("//*[@id=\"step1\"]//table//label[@for='" + gender + "']")));

        logger.info("가입유형 :: {}", info.getTextType());
        setProductType(driver.findElement(By.xpath("//*[@id=\"is011064dmPdcd\"]//label[contains(.,'" + info.getTextType() + "')]")));

        logger.info("청약예정일 :: 디폴트값으로 오늘 날짜");

        logger.info("보험료 확인을 위해 1원으로 조회");
        checkPremium(driver.findElement(By.cssSelector("#monthPrem")));

        logger.info("월보험료 가져오기");
        crawlPremium(driver.findElement(By.cssSelector("#insSum")), info);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

    }
}
