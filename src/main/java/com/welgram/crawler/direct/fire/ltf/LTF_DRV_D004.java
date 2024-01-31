package com.welgram.crawler.direct.fire.ltf;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

public class LTF_DRV_D004 extends CrawlingLTFDirect {
    public static void main(String[] args) { executeCommand(new LTF_DRV_D004(), args); }

    @Override
    protected void setDriverType(Object... obj) throws CommonCrawlerException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            String driverType = info.textType.split("#")[0];

            helper.click(By.xpath(
                    "//input[@name='driver_type']/following-sibling::span[contains(.,'"
                            + driverType
                            + "')]/ancestor::label")
                    , "운전자 유형 클릭"
            );

            WaitUtil.loading(1);

            helper.click(By.id("btnNext"), "다음 클릭");

        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }

    @Override
    protected void setPlan(CrawlingProduct info) throws CommonCrawlerException {
        try {
            String planStr = info.textType.split("#")[1];

            helper.click(By.xpath("//a[contains(.,'" + planStr +"')]/ancestor::li"),
                    info.textType + "플랜 클릭");

            logger.info("플랜 :: {}", info.textType);
            WaitUtil.loading(1);

        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }
}
