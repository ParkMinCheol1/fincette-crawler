package com.welgram.crawler.direct.life.hkl;

import java.util.Map;
import org.openqa.selenium.WebElement;



@FunctionalInterface
interface PlanReturnMoneyTdIndexFinder {

    Map<PlanReturnMoneyField, Integer> findIndex(WebElement tr, PlanReturnMoneyField field);
}
