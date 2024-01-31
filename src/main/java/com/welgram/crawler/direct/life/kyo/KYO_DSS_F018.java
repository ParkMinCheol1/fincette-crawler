package com.welgram.crawler.direct.life.kyo;

import com.welgram.crawler.direct.life.kyo.CrawlingKYO.CrawlingKYOAnnounce;
import com.welgram.crawler.general.CrawlingProduct;


// todo | By 값 임의 입력 상태 :: 진행이 불가능한 상품이라 @Test불가능
// 2023.11.06 | 최우진 | 교보맞춤건강보험(무배당)
public class KYO_DSS_F018 extends CrawlingKYOAnnounce {



    public static void main(String[] args) { executeCommand(new KYO_DSS_F018(), args); }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String[] textType = info.getTextType().split("#");
        String refundOption = "FULL";

        // PROCESS
        logger.info("▉▉▉▉ 시작 ▉▉▉▉");
        initKYO(info, textType[1]);

        return true;
    }
}
