package com.welgram.crawler.direct.fire.hdf;

import com.google.gson.Gson;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.fire.CrawlingHDF;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

public class HDF_ACD_F005 extends CrawlingHDFAnnounce {
    public static void main(String[] args) {
        executeCommand(new HDF_ACD_F005(), args);
    }

}
