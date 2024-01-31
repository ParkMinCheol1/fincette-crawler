package com.welgram.crawler.direct.life.shl;

import com.welgram.common.enums.Job;
import com.welgram.crawler.direct.life.CrawlingSHL;
import com.welgram.crawler.direct.life.shl.CrawlingSHL.CrawlingSHLAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



// 2022.10.24       | 최우진           | 대면_암
// SHL_CCR_F004     | 신한진심을품은또받는생활비암보험(무배당, 갱신형)
public class SHL_CCR_F004 extends CrawlingSHLAnnounce {

    public static void main(String[] args) { executeCommand(new SHL_CCR_F004(), args); }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // NOTIFICAITON (담당자 확인사항)
        // 해당상품의 특약 '진심을품은일반암진단특약A15(무배당, 갱신형)'은 유동 가입금액이 적용됩니다
        // 보험료 계산후에 특약에 대한 가입금액을 확인할 수 있습니다

        // INFORMATION
        String title = info.getInsuName();
        String planDistinguisher = "진품Plan";                        // todo | tType으로 받아올지 고민필요
        String tempSaleStr = this.getClass().getSimpleName();
        int gender = info.getGender();
        String insType = (info.getInsTerm().equals("30년")) ? "30년갱신형" : "15년갱신형" ;

        String birth = info.getFullBirth();     // 생년월일8자리
        String driveType = "승용차(자가용)";
        String salesType = "일반Plan";
        String job = Job.MANAGER.getCodeValue();
        String prodType = (info.getInsTerm().equals("15년")) ? "15년갱신형" : "30년갱신형";
        String refundOption = "BASE";
        String[] arrTextType = getArrTextType(info);
        String mainAssAmt = String.valueOf(Integer.parseInt(info.getAssureMoney()) / 10_000);

        // PROCESS
        initSHL(info, "04");
        // todo | 작업중 | 현행화 일시정지 | 빌드 금지

        return true;
    }
}


//        logger.info("▉▉▉ STEP00 [ 검색창, '상품명:[{}] 입력 ] ▉▉▉", title);
//        initSHL(info);
//
//        logger.info("▉▉▉ STEP01 [ '고객정보(피보험자)' 입력 ] ▉▉▉");
//        inputCustomerInfo(birth, gender, driveType, strJob);
//
//        logger.info("▉▉▉ STEP02 [ '주계약계산' 입력 ] ▉▉▉ ");
//        logger.info("SHL 공시실 '주계약계산'은 주계약에 대한 설정입니다");
//        inputMainTreatyInfo(
//            prodCode,                        // 상품코드 (ex.SHL_CCR_F004)
//            insType,                         // 1. 보험종류
//            info.getInsTerm(),               // 2. 보험기간
//            planDistinguisher,               // 3. 직종구분
//            info.getNapTerm(),               // 4. 납입기간
//            info.getNapCycleName(),          // 5. 납입주기
//            info.getAssureMoney()            // 6. 가입금액
//        );
//
//        logger.info("▉▉▉ STEP03 [ '특약계산' 입력 ] ▉▉▉ ");
//        logger.info("SHL 공시실 '특약계산'은 선택특약에 대한 설정입니다");
//        inputSubTreatyInfo(info);
//
//        logger.info("▉▉▉ STEP04 [ '보험료계산' (크롤링 결과 확인)] ▉▉▉ ");
//        checkResult(info, "BASE", salesType, prodCode);      // 보험료 확인, 스크린샷, 환급금 확인
//
//        logger.info("SHL_CCR_F004의 경우 특이한 케이스 입니다");
//        logger.info("해당상품의 특약 '진심을품은일반암진단특약A15(무배당, 갱신형)'은 유동적인 가입금액을 갖고 있습니다");
//        logger.info("보험료계산 이후 특약에 대한 가입금액을 확인할 수 있습니다");
//        logger.info("변동되는 가입금액에 대한 정보는 [ fincette > plan_calc > annuity_money ] 에 저장됩니다 ");
//        exceptionalFunc_shl_ccr_f004(info);
