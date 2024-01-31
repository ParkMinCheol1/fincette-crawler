package com.welgram.crawler.direct.fire.mgf;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author user MG손해보험 (무)이조은치아보험(23.01)
 */
public class MGF_DTL_F002 extends CrawlingMGFAnnounce {

    public static void main(String[] args) {
        executeCommand(new MGF_DTL_F002(), args);
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

        logger.info("납기/보기 :: {}", info.getNapTerm());
        setInsTerm(driver.findElement(By.xpath("//*[@id=\"step1\"]//table//label[contains(.,'" + info.getNapTerm() + "')]")));

        logger.info("청약예정일 :: 디폴트값으로 오늘 날짜");

        logger.info("월보험료 가져오기");
        crawlPremium(driver.findElement(By.cssSelector("#insSum")), info);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

    }
    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        try {
            WebElement $label = (WebElement) obj[0];

            $label.click();
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            throw new SetInsTermException("보험기간 설정 오류 :: " + e.getMessage());
        }
    }
}
