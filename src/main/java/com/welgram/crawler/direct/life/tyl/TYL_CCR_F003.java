package com.welgram.crawler.direct.life.tyl;


import com.welgram.common.PersonNameGenerator;
import com.welgram.crawler.direct.life.CrawlingTYL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.List;


// 2022.10.05           | 최우진               | 대면_암보험
// TYL_CCR_F003         | 무배당수호천사NEW실속하나로암보험 실속형
public class TYL_CCR_F003 extends CrawlingTYL {

    public static void main(String[] args) {
        executeCommand(new TYL_CCR_F003(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String birth = info.getFullBirth();
        String gender = (info.getGender() == MALE)? "남자" : "여자";
        List<CrawlingTreaty> trtList = info.getTreatyList();
        String title = "무배당수호천사NEW실속하나로암보험";
        String mainTreatyName = "(무)수호천사NEW실속하나로암보험(실속형)";
        String tempName = PersonNameGenerator.generate();

        // PROCESS
        logger.info("▉▉▉▉ #000 | 공시실에서 '{}'를 검색합니다", title);
        searchProdByTitle( title );

        logger.info("▉▉▉▉ #001 | 'step1.고객정보를입력해주세요'를 입력합니다");
        inputCustomerInfo( tempName, birth, gender );

        logger.info("▉▉▉▉ #002 | 'step2 주상품을 선택해주세요'를 입력합니다");
        inputMainTretyInfo( mainTreatyName );

        logger.info("▉▉▉▉ #003 | 'step3 특약을 선택해주세요'를 입력합니다");
        inputSubTreatyInfo( trtList );

        logger.info("▉▉▉▉ #004 | '보험료 계산'버튼을 클릭합니다");
        checkMonthlyPremium( info ); // 1. 월보험료 확인,    2. 스크린샷 촬영

        logger.info("▉▉▉▉ #005 | '보장내용상세보기'를 클릭합니다");
        checkDetails( info, "BASE" );   // 해약환급금 확인

        return true;

    }

}
