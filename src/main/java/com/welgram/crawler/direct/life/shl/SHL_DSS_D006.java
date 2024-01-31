package com.welgram.crawler.direct.life.shl;

import com.welgram.crawler.direct.life.CrawlingSHL;
import com.welgram.crawler.general.CrawlingProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



// 2023.01.17       | 최우진           | 다이렉트_질병
// SHL_DSS_D006     | 신한인터넷2대건강보험(무배당)
public class SHL_DSS_D006 extends CrawlingSHL {

    public static void main(String[] args) { executeCommand(new SHL_DSS_D006(), args); }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String prdCode = this.getClass().getName();     // SHL_DSS_D006
        String salesType = prdCode.substring(prdCode.length() -4, prdCode.length() - 3).toUpperCase();      // D or F (다이렉트/대면)
        String title = "신한인터넷2대건강보험(무배당)";
        String gender = (info.getGender() == 0) ? "남" : "여";
        String birth = info.getFullBirth();

        // PROCESS
        logger.info("START :: SHL_DSS_D006 :: {}", title);
        logger.info("다이렉트 상품의 경우 기본적으로 원수사를 크롤링 합니다");
        // init();

        logger.info("▉▉▉▉ STEP00 [ 원수사.상품리스트 ] - '{}' 검색 ▉▉▉ ", title);
        searchProdByTitle(salesType, title);

        logger.info("▉▉▉▉ STEP01 [ '내보험료는 얼마일까요?' ] 고객정보 입력 ▉▉▉ ");
        inputCustomerInfo(gender, birth);

        logger.info("▉▉▉▉ STEP02.5[ '내보험료는 얼마일까요?' ] 고객정보 입력 ▉▉▉ ");
        checkOptions2(info);

        logger.info("▉▉▉▉ STEP03 [ '보험료,환급금정보' ] 결과확인");
        checkResult(info, "BASE", salesType, prdCode);      // 보험료 확인, 스크린샷, 환급금 확인

        return true;
    }
}
