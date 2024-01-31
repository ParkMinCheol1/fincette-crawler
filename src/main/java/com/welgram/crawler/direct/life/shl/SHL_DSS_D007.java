package com.welgram.crawler.direct.life.shl;

import com.welgram.crawler.direct.life.CrawlingSHL;
import com.welgram.crawler.general.CrawlingProduct;



// 2023.03.22           | 최우진               | 다이렉트_질병보험
// SHL_DSS_D007         | 신한슬기로운직장생활건강보험M(무배당)
public class SHL_DSS_D007 extends CrawlingSHL {

    public static void main(String[] args) { executeCommand(new SHL_DSS_D007(), args); }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String prdCode = this.getClass().getSimpleName();     // SHL_DSS_D007
        String salesType = prdCode.substring(prdCode.length() -4, prdCode.length() - 3).toUpperCase();      // D or F (다이렉트/대면)
        String title = "신한슬기로운직장생활건강보험M(무배당)";
        String gender = (info.getGender() == 0) ? "남" : "여";
        String birth = info.getFullBirth();

        // PROCESS
        logger.info("START :: {}", title);
        logger.info("다이렉트 상품의 경우 기본적으로 원수사를 크롤링 합니다");

        logger.info("▉▉▉▉ STEP00 [ 원수사.상품리스트 ] - '{}' 검색 ▉▉▉ ", title);
        searchProdByTitle(salesType, title);

        logger.info("▉▉▉▉ STEP01 [ '내보험료는 얼마일까요?' ] 고객정보 입력 ▉▉▉ ");
        inputCustomerInfo(gender, birth);

        logger.info("▉▉▉▉ STEP02.5[ '내보험료는 얼마일까요?' ] 보험옵션 입력 ▉▉▉ ");
        checkOptions3(info);

        logger.info("▉▉▉▉ STEP03 [ '보험료,환급금정보' ] 결과확인");
        checkResult(info, "BASE", salesType, prdCode);      // 보험료 확인, 스크린샷, 환급금 확인

        return true;
    }
}
