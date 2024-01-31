package com.welgram.crawler.scraper;

import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetRenewTypeException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetRefundTypeException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import java.util.concurrent.Callable;

public interface Scrapable {

  /**
   * 생년월일 세팅
   * @param obj
   * @throws SetBirthdayException
   */
  void setBirthdayNew(Object obj) throws SetBirthdayException;

//  int setBirthdayNew2(Object obj, Callable method) throws SetBirthdayException;

  /**
   * 성별
   * @param obj
   * @throws SetGenderException
   */
  void setGenderNew(Object obj) throws SetGenderException;

  /**
   * 직업 세팅
   * @param obj
   * @throws SetJobException
   */
  void setJobNew(Object obj) throws SetJobException;

  /**
   * 보험기간 세팅
   * @param obj
   * @throws SetInsTermException
   */
  void setInsTermNew(Object obj) throws SetInsTermException;

  /**
   * 납입기간 세팅
   * @param obj
   * @throws SetNapTermException
   */
  void setNapTermNew(Object obj) throws SetNapTermException;

  /**
   * 납입주기 세팅
   * @param obj
   * @throws SetNapCycleException
   */
  void setNapCycleNew(Object obj) throws SetNapCycleException;

  /**
   * 갱신형 세팅
   * @param obj
   * @throws SetRenewTypeException
   */
  void setRenewTypeNew(Object obj) throws SetRenewTypeException;

  /**
   * 가입금액 세팅
   * @param obj
   * @throws SetAssureMoneyException
   */
  void setAssureMoneyNew(Object obj) throws SetAssureMoneyException;
  /**
   * 환급형태 세팅
   * @param obj
   * @throws SetRefundTypeException
   */
  void setRefundTypeNew(Object obj) throws SetRefundTypeException;

  /**
   * 보험료 데이터 획득
   * @param obj
   * @throws PremiumCrawlerException
   */
  void crawlPremiumNew(Object obj) throws PremiumCrawlerException;

  /**
   * 해약환급금 테이블 데이터 획득
   * @param obj
   * @throws ReturnMoneyListCrawlerException
   */
  void crawlReturnMoneyListNew(Object obj) throws ReturnMoneyListCrawlerException;

  /**
   * 만기환급금 데이터 획득
   * @param obj
   * @throws ReturnPremiumCrawlerException
   */
  void crawlReturnPremiumNew(Object obj) throws ReturnPremiumCrawlerException;

  /**
   * info정보와 선택한 값 비교
   * @param obj
   * @throws MismatchValueException
   */
//  void compareValue(Object obj) throws MismatchValueException;

}
