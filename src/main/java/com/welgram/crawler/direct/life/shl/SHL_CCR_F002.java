package com.welgram.crawler.direct.life.shl;

import com.welgram.common.enums.Job;
import com.welgram.crawler.direct.life.shl.CrawlingSHL.CrawlingSHLAnnounce;
import com.welgram.crawler.general.CrawlingProduct;


// 2023.05.22 | 최우진 | 신한딱하나만묻는암보험(무배당, 갱신형)
public class SHL_CCR_F002 extends CrawlingSHLAnnounce {

    public static void main(String[] args) {
        executeCommand(new SHL_CCR_F002(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String tempSaleStr = info.getProductCode();
        String salesType = tempSaleStr.substring(tempSaleStr.length() - 4, tempSaleStr.length() - 3).toUpperCase();
        String birth = info.getFullBirth();
        String driveType = "승용차(자가용)";
        String job = Job.MANAGER.getCodeValue();
        int gender = info.getGender();
        String prodCode = this.getClass().getSimpleName();
        String insType = "순수보장형";                           // todo | info 내에서 확인필요

        // PROCESS
        logger.info("▉▉▉▉ STEP00 [ 검색창, '상품명 입력 ] ▉▉▉▉");
        initSHL(info, "04");

        logger.info("▉▉▉▉ STEP01 [ '고객정보(피보험자)' 입력 ] ▉▉▉▉");
        inputCustomerInfo(birth, gender, driveType, job);

        logger.info("▉▉▉▉ STEP02 [ '주계약계산' 입력 ] ▉▉▉▉");
        logger.info("SHL 공시실 '주계약계산'은 주계약에 대한 설정입니다");
        inputMainTreatyInfo (
            info.getProductCode(),          // 상품코드 (ex.SHL_CCR_F003)
            insType,                        // 1. 보험형태
            info.getInsTerm(),              // 2. 보험기간
            info.getNapCycleName(),         // 5. 납입주기
            info.getNapTerm(),              // 4. 납입기간
            info.getAssureMoney()           // 6. 가입금액
        );

        logger.info("▉▉▉▉ STEP03 [ '특약계산' 입력 ] ▉▉▉▉");
        logger.info("SHL 공시실 '특약계산'은 선택특약에 대한 설정입니다");
        inputSubTreatyInfo(info);

        // todo 확인필요
        logger.info("▉▉▉▉ STEP04 [ '보험료계산' (크롤링 결과 확인)] ▉▉▉▉");
        checkResult(info, "BASE", salesType, prodCode);      // 보험료 확인, 스크린샷, 환급금 확인

        return true;
    }
}