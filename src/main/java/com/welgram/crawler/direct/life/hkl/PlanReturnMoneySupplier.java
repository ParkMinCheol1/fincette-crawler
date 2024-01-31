package com.welgram.crawler.direct.life.hkl;

import com.welgram.crawler.general.PlanReturnMoney;
import java.util.Map;



@FunctionalInterface
interface PlanReturnMoneySupplier {

    PlanReturnMoney getPlanReturnMoney(Map<PlanReturnMoneyFieldEnum, Integer> map);
}


