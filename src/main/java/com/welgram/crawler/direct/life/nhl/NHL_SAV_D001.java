package com.welgram.crawler.direct.life.nhl;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;



/**
 * NH온라인플러스저축보험(무배당)
 * -> 보험명 변경 : NH부자습관저축보험(Self가입형,무배당) 21.01.15. 확인 (차후 변경 예정)
 * <p>
 * NHL_SAV_D001 상품의 공시실은 존재하지않아 Homepage크롤링 코드만 작성
 * (공시실에서 현 상품의 보험료계산 클릭 시 Homepage크롤링 브라우저와 동일한 Web브라우저가 화면에 뜬다. - 21.01.15. 확인)
 */
public class NHL_SAV_D001 extends CrawlingNHLDirect {



    public static void main(String[] args) {

        executeCommand(new NHL_SAV_D001(), args);

    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        String genderOpt = (info.getGender() == MALE) ? "cal_gender1" : "cal_gender2";
        String genderText = (info.getGender() == MALE) ? "남자" : "여자";

        logger.info("NHL_SAV_D001 :: {}", info.getProductName());
        WaitUtil.waitFor(2);

        logger.info("생년월일 :: {}", info.getFullBirth());
        setBirthday(By.id("birth"), info.getFullBirth());

        logger.info("성별 :: {}", genderText);
        setGender(By.xpath("//input[@id='" + genderOpt + "']/parent::label"), genderText);

        logger.info("보험료 확인");
        btnClick(By.id(("calcPremium")), 2);
        helper.waitForCSSElement("#uiPOPLoading1");

        logger.info("보험기간 :: {}", info.getInsTerm());
        setInsTerm(By.id("insTermMy"), info.getInsTerm() + "만기");

        logger.info("납입기간 :: {}", info.getNapTerm());
        setNapTerm(By.id("napTermMy"), info.getNapTerm());

        logger.info("월 납입금액 :: {}", info.getAssureMoney());
        setPremium(By.id("premiumMy"), info.getAssureMoney());

        logger.info("다시 계산하기");
        calcBtnClickforPremium(By.id("reCalcPremium"));

        // 월 보험료
        // 사이트에서 입력된 값을 얻을 수 없어 부득이 가설의 가입금액을 넣어준다.
        info.treatyList.get(0).monthlyPremium = info.getAssureMoney();

        logger.info("스크린샷");
        takeScreenShot(info);

        logger.info("해약환급금 가져오기");
        crawlReturnMoneyList2(info, By.cssSelector("#savingReturn_uiPOPRefund1 .ui-tab-con.active tbody tr"));

        return true;

    }

}