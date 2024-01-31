package com.welgram.crawler.direct.fire.nhf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

// (무)NH335굿패스건강보험[1종:갱신형(간편심사/납면)]2304
public class NHF_DSS_F017 extends CrawlingNHFAnnounce {

    public static void main(String[] args) {
        executeCommand(new NHF_DSS_F017(), args);
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setIniSafe(true);
//        option.setTouchEnPc(true);

    }


    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        crawlFromHomepage(info);

        return true;
    }


    private void crawlFromHomepage(CrawlingProduct info) throws Exception {

        By $byElement = null;
        String genderOpt = "";

        logger.info("NHF_DSS_F017 :: (무)NH335굿패스건강보험[1종:갱신형(간편심사/납면)]2304");
        WaitUtil.waitFor(3);

        logger.info("상품유형 설정 : {}", info.getTextType());
        $byElement = By.id("ctrCmdCd");
        setProductType($byElement, info.getTextType());

        logger.info("생년월일 :: {}", info.getFullBirth());
        $byElement = By.id("juminno");
        setBirthday($byElement, info.getFullBirth());

        logger.info("성별 설정 :: {}", (info.getGender() == 0) ? "남자" : "여자");
        genderOpt = (info.getGender() == MALE) ? "1" : "2";
        $byElement = By.cssSelector("input[type=radio]:nth-child(" + genderOpt + ")");
        setGender($byElement, info.getGender());

        logger.info("직업 : (Fixed)보험 사무원");
        setJob();

        logger.info("차량 운전여부는 디폴트값 사용 :: {} ");

        logger.info("보험기간 설정 :: {}", info.getInsTerm());
        $byElement = By.id("insPrdCd");
        setInsTerm($byElement, info.getInsTerm());

        logger.info("납입기간 설정 :: {}", info.getNapTerm());
        $byElement = By.id("rvpdCd");
        setNapTerm($byElement, info.getNapTerm());

        logger.info("납입주기 설정 :: {}", getNapCycleName(info.getNapCycle()));
        $byElement = By.id("rvcyCd");
        setNapCycle($byElement, info.getNapCycle());

        logger.info("담보 보기 버튼 클릭");
        btnClick(By.linkText("담보 보기"), 2);

        logger.info("특약 설정");
        setTreaties(info);

        logger.info("보험료확인 버튼 클릭");
        calcBtnClick();

        logger.info("합계보험료 및 주계약 보험료 설정");
        setAndCrawlPremium(info);

        logger.info("스크린샷");
        takeScreenShot(info);

        logger.info("해약환급금 조회");
        $byElement = By.cssSelector("#HykRetTable .Listbox");
        crawlReturnMoneyList($byElement, info);
    }
}