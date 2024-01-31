package com.welgram.crawler.direct.life.shl;

import com.welgram.common.enums.Job;
import com.welgram.crawler.direct.life.CrawlingSHL;
import com.welgram.crawler.general.CrawlingProduct;



// 2023.03.24           | 최우진               | 대면_치매보험
// SHL_DMN_F006         | 신한케어받는간편가입치매간병보험(무배당, 해약환급금 미지급형)(일반심사형)
public class SHL_DMN_F006 extends CrawlingSHL {

    public static void main(String[] args) {
        executeCommand(new SHL_DMN_F006(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String tempSaleStr = this.getClass().getName();
        String salesType = tempSaleStr.substring(tempSaleStr.length() -4, tempSaleStr.length() - 3).toUpperCase();      // D or F (다이렉트/대면)
        String title = "신한케어받는간편가입치매간병보험(무배당, 해약환급금 미지급형)";
        String birth = info.getFullBirth();
        int gender = info.getGender();
        String driveYn = "승용차(자가용)";
        String strJob = Job.MANAGER.getCodeValue();
//        String[] arrTextType = checkTextType(info);

        // PROCESS
        logger.info("START :: {}", title);
        logger.info("대면 상품의 경우 기본적으로 공시실을 크롤링 합니다");
        logger.info("신한라이프 상품의 경우, STEP단위로 내용이 변경될 수 있습니다");

        logger.info("▉▉▉ STEP00 [ 공시실 ] - '{}' 검색 ▉▉▉ ", title);
        searchProdByTitle(salesType, title);

        logger.info("▉▉▉ STEP01 [ '고객정보(피보험자)' ] 정보입력 ▉▉▉ ");
        inputCustomerInfo(birth, gender, driveYn, strJob);

        logger.info("▉▉▉ STEP02 [ '주계약계산' ] 정보입력 ▉▉▉ ");
        inputMainTreatyInfo(
                this.getClass().getSimpleName(),      // 상품코드
                "일반심사형",                                 // 보험형태
                info.getInsTerm(),                          // 보험기간
                "(3년보증형)해약환급금미지급형",                // 보험종류
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
