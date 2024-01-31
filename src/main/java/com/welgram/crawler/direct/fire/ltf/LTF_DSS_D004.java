package com.welgram.crawler.direct.fire.ltf;


import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingProduct.Gender;
import org.openqa.selenium.By;

public class LTF_DSS_D004 extends CrawlingLTFDirect {
    public static void main(String[] args) { executeCommand(new LTF_DSS_D004(), args); }

    @Override
    protected void setMarry(Object... obj) throws CommonCrawlerException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            String selectValue = info.textType.split(":")[1];

            // LTF_DSS_D002 여성의 경우 결혼여부를 묻는다
            // 대답에 따라 특약 갯수가 달라진다
            // 2023.08.24 대답을 N (아니요)로 해야 대표가설의 특약이 모두 나옴
            if (info.getGenderEnum().equals(Gender.F)) {
                helper.click(By.xpath(
                        "//input[@name='marry' and @value='" + selectValue + "']/ancestor::label"));
            }

        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }

    }
}
