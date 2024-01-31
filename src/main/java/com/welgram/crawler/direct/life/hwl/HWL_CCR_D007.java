package com.welgram.crawler.direct.life.hwl;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;



/**
 * 한화생명 - 한화생명 e암보험(갱신형) 무배당
 */
public class HWL_CCR_D007 extends CrawlingHWLDirect {

    public static void main(String[] args) {
        executeCommand(new HWL_CCR_D007(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        driver.manage().window().maximize();
        setBirthday(By.xpath("//label[text()='생년월일']/following-sibling::div//input"), info.fullBirth);
        setGender(info.gender, null);
        clickCalcButton(By.xpath("//div[@id='ComputeAndResultArea']//span[contains(.,'보험료 계산하기') or contains(.,'으로 가입하기')]/ancestor::button"));
        setDiscountType(info);
        setTreaties(info.getTreatyList());
        crawlPremium(info, null);
        crawlReturnMoneyList(info, CrawlingHWLDirect.returnMoneyFields_4); // 으 여기서부터!
        crawlReturnPremium(info);

        return true;
    }

}

