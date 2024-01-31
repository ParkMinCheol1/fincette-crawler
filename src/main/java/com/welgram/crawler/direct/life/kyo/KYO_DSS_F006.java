package com.welgram.crawler.direct.life.kyo;

import com.welgram.crawler.direct.life.kyo.CrawlingKYO.CrawlingKYOAnnounce2;
import com.welgram.crawler.general.CrawlingProduct;



// 2023.12.04 | 최우진 | 교보뇌·심장케어보험(무배당,서비스선택형)
public class KYO_DSS_F006 extends CrawlingKYOAnnounce2 {

    public static void main(String[] args) {
        executeCommand(new KYO_DSS_F006(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        //step1 : 공시실 상품명 찾기
        findProductName(info.getProductNamePublic());

        //step2 : 고객정보 입력
        setUserInfo(info);

        //step3 : 주계약정보 입력
        setMainTreatyInfo(info);

        //step4 : 선택특약정보 입력
        setSubTreatiesInfo(info);

        //stpe5 : 보험료 크롤링
        crawlPremium(info);

        //step6 : 해약환급금 크롤링
        crawlReturnMoneyList(info);

        return true;
    }
}