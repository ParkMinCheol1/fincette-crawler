package com.welgram.crawler.direct.life.tyl;

import com.welgram.common.PersonNameGenerator;
import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.direct.life.CrawlingTYL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



// 2022.11.22           | 최우진               | 대면_연금저축
// TYL_ASV_F001         | 연금저축수호천사더블파워연금보험
public class TYL_ASV_F001 extends CrawlingTYL {

    public static void main(String[] args) { executeCommand(new TYL_ASV_F001(), args); }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String title = info.getInsuName();
        String mainTreatyName = "연금저축 수호천사 더블파워연금(10년납이상)";
        String tempName = PersonNameGenerator.generate();
        String birth = info.getFullBirth();
        String gender = (info.getGender() == MALE)? "남자" : "여자";
        String category = "ASV";        // todo | by init()
        String salesType = "F";         // todo | by init()
        String comapany = "TYL";        // todo | by init()

        // PROCESS
        logger.info("▉▉▉▉ #000 | 공시실에서 '{}'를 검색합니다", title);
        searchProdByTitle(title);

        logger.info("▉▉▉▉ #001 | 'step1.고객정보를입력해주세요'를 입력합니다");
        inputCustomerInfo(tempName, birth, gender);

        logger.info("▉▉▉▉ #002 | 'step2 주상품을 선택해주세요'를 입력합니다");
        inputMainTretyInfo(mainTreatyName);

        logger.info("▉▉▉▉ #003 | 'step3 특약을 선택해주세요'를 입력합니다");
        inputTreatiesInfoASV(info);

        logger.info("▉▉▉▉ #004 | '보험료 계산'버튼을 클릭합니다");
        checkMonthlyPremium(info);           // 1. 월보험료 확인,  2. 스크린샷 촬영

        logger.info("▉▉▉▉ #005 | '보장내용상세보기'를 클릭합니다");
        checkDetails(info, "FULL");     // 1. 해약환급금 확인,  2. 연금수령액 확인

        return true;

    }



    @Override
    //  DEPTH.1 공시실 | 특약을 선택해주세요 | 특약들에 대한 조건 설정
    protected void submitSubTreaties(WebElement eachTr, CrawlingTreaty eachTrt) throws Exception {

        String trtInsTerm = eachTrt.getInsTerm();
        String trtNapTerm = eachTrt.getNapTerm();
        String trtNapTermUnit = eachTrt.getNapCycleName();
        String trtAssureAmt = String.valueOf(eachTrt.assureMoney / 1_0000);
        String trtAnnAge = eachTrt.annAge;

        try {
            // 보험기간
            Select selectInsTerm = new Select(eachTr.findElement(By.xpath(".//td[4]//select")));
            // todo | 수정필수 annuitytype 으로 수정필수
            // 동양생명 연금저축보험의 특이사항 '보험기간'이라고 적힌 input값에 연금개시나이를 넣어야 합니다
            selectInsTerm.selectByVisibleText(trtAnnAge + "세");
            logger.info("▉ 보험기간 :: '{}'(으)로 설정하였습니다", trtInsTerm);

            // 납입기간
            Select selectNapTerm = new Select(eachTr.findElement(By.xpath(".//td[5]//select")));
            selectNapTerm.selectByVisibleText(trtNapTerm);
            logger.info("▉ 납입기간 :: '{}'(으)로 설정하였습니다", trtNapTerm);

            // 납입주기
            Select selctNapCycleName = new Select(eachTr.findElement(By.xpath(".//td[6]//select")));
            selctNapCycleName.selectByVisibleText(trtNapTermUnit);
            logger.info("▉ 납입주기 :: '{}'(으)로 설정하였습니다", trtNapTermUnit);

            // 가입금액
            // todo | 심각한 수정필요 TYL 공통화는 다 한다음에 할 것
            if (!trtInsTerm.equals("종신보장") && StringUtils.isEmpty(trtAnnAge)) {
                WebElement inputAssureAmt = eachTr.findElement(By.xpath(".//td[7]//input"));
                inputAssureAmt.click();
                inputAssureAmt.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
                inputAssureAmt.sendKeys(trtAssureAmt);
                logger.info("▉ 가입금액 :: '{}만원' 으로 설정하였습니다", trtAssureAmt);

            } else {
                trtAssureAmt = String.valueOf(Integer.parseInt(trtAssureAmt) * 1_0000);
                WebElement inputPremium = eachTr.findElement(By.xpath(".//td[8]//input"));
                inputPremium.click();
                inputPremium.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
                inputPremium.sendKeys(trtAssureAmt);
                logger.info("▉ 보험료 :: '{}원' 으로 설정하였습니다", trtAssureAmt);
                logger.info("▉ 연금_저축의 경우, 현 구조상 가입금액(input)을 보험료(output)를 대신해 해당되는 내용을 입력(input)합니다.");
            }
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            throw new CommonCrawlerException("▉ 특약 옵션 설정중 에러가 발생하였습니다");
        }

    }

}
