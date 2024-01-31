package com.welgram.crawler.direct.life.shl;

import com.welgram.common.enums.Job;
import com.welgram.crawler.direct.life.CrawlingSHL;
import com.welgram.crawler.general.CrawlingProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// todo | CommonCrawlerException의 샘플케이스 - 주석처리된 내용들로 바꾸어 실행시 관련예외 케이스들을 확인해 봅시다
// 2022.07.29           | 최우진               | 대면_치아
// SHL_DTL_F001         | 신한참좋은치아보험PlusⅡ(무배당, 갱신형)
public class SHL_DTL_F001 extends CrawlingSHL {

    public static void main(String[] args) { executeCommand(new SHL_DTL_F001(), args); }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        /** NOTIFICATION
         * 대면보험의 경우, 신한라이프(SHL)보험사는 공시실 크롤링을 기본으로 합니다.
         * 카테고리:치아 의 경우, 기본 크롤링과 같습니다.
         */

        // INFORMATION
        String productCode = this.getClass().getName();
        String salesType = productCode.substring(productCode.length() -4, productCode.length() - 3).toUpperCase();
        String title = "신한참좋은치아보험PlusⅡ(무배당, 갱신형)";      // 공시실, 약관내 상품이름이 일치하지 않음
        String driveYn = "승용차(자가용)";                           // init 고려할 것, textType
        String birth = info.getFullBirth();
        String strJob = Job.MANAGER.getCodeValue();
        int gender = info.getGender();
        String[] arrTType = checkTextType(info);

        // PROCESS
        logger.info("START SCRAP :: SHL_DTL_F001 :: " + title);
        initShlAnnc(info);

        logger.info("▉▉▉ STEP00 [ 검색창, '상품명:[{}] 입력 ] ▉▉▉ ", title);
        searchProdByTitle(salesType, title);

        logger.info("▉▉▉ STEP01 [ '고객정보(피보험자)' 입력 ] ▉▉▉ ");
        inputCustomerInfo(birth, gender, driveYn, strJob);

        logger.info("▉▉▉ STEP02 [ '주계약계산' 입력 ] ▉▉▉ ");
        inputMainTreatyInfo(
            this.getClass().getSimpleName(),    // 상품코드
            "10년만기(갱신형)",                             // 보험형태
            info.getInsTerm(),                            // 보험기간
            "일반Plan",                                    // 직종구분
            info.getNapTerm(),                            // 납입기간
            info.getNapCycleName(),                       // 납입주기
            info.getAssureMoney()                         // 가입금액
        );

        logger.info("▉▉▉ STEP03 [ '특약계산' 입력 ] ▉▉▉ ");
        logger.info("SHL 공시실 '특약계산'은 선택특약에 대한 설정입니다");
        inputSubTreatyInfo(info);

        logger.info("▉▉▉ STEP04 [ '보험료계산' (크롤링 결과 확인)] ▉▉▉ ");
        checkResult(info, "BASE", salesType, productCode);      // 보험료 확인, 스크린샷, 환급금 확인

        return true;
    }
}
