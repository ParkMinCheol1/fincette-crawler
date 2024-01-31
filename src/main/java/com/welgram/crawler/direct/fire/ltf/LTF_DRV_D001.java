package com.welgram.crawler.direct.fire.ltf;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LTF_DRV_D001 extends CrawlingLTFDirect {
    public static void main(String[] args) { executeCommand(new LTF_DRV_D001(), args); }

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


            logger.info("캘린더 상에서 내일 날짜에 해당하는 날을 클릭함");

            LocalDate tomorrow = LocalDate.now().plusDays(1);
            String tomorrowFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd").format(tomorrow);
            helper.waitPesenceOfAllElementsLocatedBy(
                    By.xpath("//button[@aria-label='" + tomorrowFormat + "']")
            ).stream().filter(
                    WebElement::isDisplayed // 보이는 요소만 필터링 (숨겨진 날짜가 있을 때가 있음)
            ).findFirst().ifPresent(
                    element -> {
                        helper.click(element, "내일 클릭");
                        helper.click(element, "내일 클릭"); // 2번 클릭
                    }
            );
            WaitUtil.loading(1);

            helper.click(By.id("btnNext"), "다음 클릭");

        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }

}
