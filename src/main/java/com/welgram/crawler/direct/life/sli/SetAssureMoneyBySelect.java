package com.welgram.crawler.direct.life.sli;

import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.crawler.AbstractCrawler;
import com.welgram.crawler.helper.SeleniumCrawlingHelper;

public class SetAssureMoneyBySelect extends SetAssureMoneyBehavior {

    public SetAssureMoneyBySelect(SeleniumCrawlingHelper helper, Class<? extends AbstractCrawler> productClass) {
        super(helper, productClass);
    }

    @Override
    public void setAssureMoney(Object obj) throws SetAssureMoneyException {
        try {
            CrawlingInfo crawlingInfo = (CrawlingInfo) obj;
            Object position = crawlingInfo.getPosition();
            String value = crawlingInfo.getValue();

            helper.selectOptionByClick( position, value);

            logger.info("가입금액 선택: {}", value);

        } catch (Exception e) {
            throw new SetAssureMoneyException(e);
        }
    }
}
