package com.welgram.crawler.direct.life.shl.deleted;


import com.welgram.common.enums.Job;
import com.welgram.crawler.direct.life.CrawlingSHL;
import com.welgram.crawler.general.CrawlingProduct;



// 2022.09.05           | 최우진           | 대면_치매보험
// SHL_DMN_F004         | 신한진심을품은간편가입찐치매보험(무배당, 해약환급금 미지급형) 2종(일반심사형)
public class SHL_DMN_F004 extends CrawlingSHL {

    public static void main(String[] args) {
        executeCommand(new SHL_DMN_F004(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // FIELDS
        String tempSaleStr = this.getClass().getName();
        String salesType = tempSaleStr.substring(tempSaleStr.length() -4, tempSaleStr.length() - 3).toUpperCase();      // D or F (다이렉트/대면)
        String title = "신한진심을품은간편가입찐치매보험(무배당, 해약환급금 미지급형)";
        String birth = info.getFullBirth();
        int gender = info.getGender();
        String driveYn = "승용차(자가용)";
        String strJob = Job.MANAGER.getCodeValue();
        String[] arrTextType = checkTextType(info);
// todo | 1. 보험형태 텍스트 타입으로 전달하기 
// todo | 2. 보험종류 텍스트 타입으로 전달하기

        // PROCESS
        logger.info("START :: SHL_DMN_F004 :: {}", title);
        logger.info("대면 상품의 경우 기본적으로 공시실을 크롤링 합니다");
        logger.info("신한라이프 상품의 경우, STEP단위로 내용이 변경될 수 있습니다");

        logger.info("▉▉▉ STEP00 [ 공시실 ] - '{}' 검색 ▉▉▉ ", title);
        searchProdByTitle(salesType, title);

        logger.info("▉▉▉ STEP01 [ '고객정보(피보험자)' ] 정보입력 ▉▉▉ ");
        inputCustomerInfo(birth, gender, driveYn, strJob);

        logger.info("▉▉▉ STEP02 [ '주계약계산' ] 정보입력 ▉▉▉ ");
        inputMainTreatyInfo(
            this.getClass().getSimpleName(),   // 상품코드
            "일반심사형",                                 // 보험형태
            info.getInsTerm(),                          // 보험기간
            "(5년보증형)일반형",                           // 보험종류
            info.getNapTerm(),                          // 납입기간
            info.getNapCycleName(),                     // 납입주기
            info.getAssureMoney()                       // 가입금액
        );

        logger.info("▉▉▉ STEP03 [ '특약계산' ] 정보입력 ▉▉▉ ");
        inputSubTreatyInfo(info);

        logger.info("▉▉▉ STEP04 [ '보험료계산' ] 결과확인 ▉▉▉ ");
        checkResult(info, "BASE", salesType, tempSaleStr);      // 보험료 확인, 스크린샷, 환급금 확인

        return true;
    }
}
