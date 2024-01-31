package com.welgram.crawler.direct.life.kyo;

import com.welgram.crawler.direct.life.kyo.CrawlingKYO.CrawlingKYOAnnounce2;
import com.welgram.crawler.general.CrawlingProduct;


// 2023.11.14 | 최우진 | 교보e감염케어보험 23.11 (무배당)
public class KYO_DSS_D013 extends CrawlingKYOAnnounce2 {

    public static void main(String[] args) {
        executeCommand(new KYO_DSS_D013(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        findProductName(info.getProductNamePublic());

        setUserInfo(info);

        setMainTreatyInfo(info);

        setSubTreatiesInfo(info);

        crawlPremium(info);

        crawlReturnMoneyList(info);

        return true;
    }
}
