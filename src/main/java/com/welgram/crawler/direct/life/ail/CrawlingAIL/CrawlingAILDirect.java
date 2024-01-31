package com.welgram.crawler.direct.life.ail.CrawlingAIL;

import com.welgram.common.except.crawler.crawl.ExpectedSavePremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAnnuityAgeException;
import com.welgram.common.except.crawler.setPlanInfo.SetAnnuityTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetPrevalenceTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetProductTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRefundTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRenewTypeException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetDueDateException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetInjuryLevelException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.common.except.crawler.setUserInfo.SetTravelPeriodException;
import com.welgram.common.except.crawler.setUserInfo.SetUserNameException;
import com.welgram.common.except.crawler.setUserInfo.SetVehicleException;
import com.welgram.crawler.general.CrawlingOption;


// 2023.05.19 | 최우진 | AIL 원수사 크롤링
public abstract class CrawlingAILDirect extends CrawlingAILNew {

    // todo | AIL 다이렉트채널용 오버라이딩
    @Override public void setBirthday(Object... obj) throws SetBirthdayException { }
    @Override public void setGender(Object... obj) throws SetGenderException { }
    @Override public void setInjuryLevel(Object... obj) throws SetInjuryLevelException { }
    @Override public void setJob(Object... obj) throws SetJobException { }
    @Override public void setInsTerm(Object... obj) throws SetInsTermException { }
    @Override public void setNapTerm(Object... obj) throws SetNapTermException { }
    @Override public void setNapCycle(Object... obj) throws SetNapCycleException { }
    @Override public void setRenewType(Object... obj) throws SetRenewTypeException { }
    @Override public void setAssureMoney(Object... obj) throws SetAssureMoneyException { }
    @Override public void setRefundType(Object... obj) throws SetRefundTypeException { }
    @Override public void crawlPremium(Object... obj) throws PremiumCrawlerException { }
    @Override public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException { }
    @Override public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException { }
    @Override public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException { }
    @Override public void setAnnuityType(Object... obj) throws SetAnnuityTypeException { }
    @Override public void crawlExpectedSavePremium(Object... obj) throws ExpectedSavePremiumCrawlerException { }
    @Override public void setUserName(Object... obj) throws SetUserNameException { }
    @Override public void setDueDate(Object... obj) throws SetDueDateException { }
    @Override public void setTravelDate(Object... obj) throws SetTravelPeriodException { }
    @Override public void setProductType(Object... obj) throws SetProductTypeException { }
    @Override public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException { }
    @Override public void setVehicle(Object... obj) throws SetVehicleException { }

    // todo | AIL 원수사 공통내용

    // 01.원수사 크롤링 옵션 설정
    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {

        option.setMobile(true);
    }

    
}
