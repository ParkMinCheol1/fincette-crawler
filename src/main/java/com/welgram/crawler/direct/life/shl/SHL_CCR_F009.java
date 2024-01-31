package com.welgram.crawler.direct.life.shl;

import com.welgram.common.enums.Job;
import com.welgram.crawler.direct.life.CrawlingSHL;
import com.welgram.crawler.general.CrawlingProduct;


// 2023.03.22       | 최우진           | 대면_암
// SHL_CCR_F009     | 신한케어받는암보험(무배당, 갱신형) 생활비지급형
public class SHL_CCR_F009 extends CrawlingSHL {

    public static void main(String[] args) { executeCommand(new SHL_CCR_F009(), args); }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String prodCode = this.getClass().getSimpleName();
        String insType = (info.getInsTerm().equals("30년")) ? "30년갱신형" : "15년갱신형" ;
        String title = "신한케어받는암보험(무배당, 갱신형)";
        String birth = info.getFullBirth();
        int gender = info.getGender();
        String driveType = "승용차(자가용)";
        String strJob = Job.MANAGER.getCodeValue();
        String tempSaleStr = this.getClass().getSimpleName();
        String salesType = tempSaleStr.substring(tempSaleStr.length() - 4, tempSaleStr.length() - 3).toUpperCase();

        // PROCESS
        logger.info("▉▉▉ STEP00 [ 검색창, '상품명:[{}] 입력 ] ▉▉▉", title);
        searchProdByTitle(salesType, title);

        logger.info("▉▉▉ STEP01 [ '고객정보(피보험자)' 입력 ] ▉▉▉");
        inputCustomerInfo(birth, gender, driveType, strJob);

        logger.info("▉▉▉ STEP02 [ '주계약계산' 입력 ] ▉▉▉ ");
        logger.info("SHL 공시실 '주계약계산'은 주계약에 대한 설정입니다");
        inputMainTreatyInfo (
            prodCode,                        // 상품코드 (ex.SHL_CCR_F009)
            insType,                         // 1. 보험종류
            info.getInsTerm(),               // 2. 보험기간
            info.getNapTerm(),               // 4. 납입기간
            info.getNapCycleName(),          // 5. 납입주기
            info.getAssureMoney()            // 6. 가입금액
        );

        logger.info("▉▉▉ STEP03 [ '특약계산' 입력 ] ▉▉▉ ");
        logger.info("SHL 공시실 '특약계산'은 선택특약에 대한 설정입니다");
        inputSubTreatyInfo(info);

        logger.info("▉▉▉ STEP04 [ '보험료계산' (크롤링 결과 확인)] ▉▉▉ ");
        checkResult(info, "BASE", salesType, prodCode);

        return true;
    }
}
