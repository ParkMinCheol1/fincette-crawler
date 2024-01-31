package com.welgram.crawler.direct.life.hdl;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

public class HDL_WLF_F026 extends CrawlingHDLAnnounce {

    public static void main(String[] args) {
        executeCommand(new HDL_WLF_F026(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        enterPage(info);
        setPlan(info);
        helper.invisibilityOfElementLocated(By.className("c-loading__bars"));
        setBirthday(info);
        helper.invisibilityOfElementLocated(By.className("c-loading__bars"));
        setGender(info);

        setAssureMoney(info);   // 주계약
        setInsTerm(info);       // 주계약
        setNapTerm(info);       // 주계약
        setNapCycle(info);      // 주계약
        setOptionalTreaty(info); // 선택특약


        crawlPremium(info);
        crawlReturnMoneyList(info);
        crawlReturnPremium(info);

        return true;
    }

}

