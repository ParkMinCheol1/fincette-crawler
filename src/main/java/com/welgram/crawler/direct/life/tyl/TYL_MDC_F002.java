package com.welgram.crawler.direct.life.tyl;

import com.welgram.common.PersonNameGenerator;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingTYL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;


// 2023.03.14           | 최우진               | 대면_실손의료보험
// TYL_MDC_F002         | 무배당수호천사급여실손의료비보장보험(갱신형)
public class TYL_MDC_F002 extends CrawlingTYL {

    public static void main(String[] args) { executeCommand(new TYL_MDC_F002(), args); }



    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("START :: 무배당수호천사급여실손의료비보장보험(갱신형)");

        logger.info("공시실에서 상품찾기");
        findInsuFromAnnounce("무배당수호천사급여실손의료비보장보험(갱신형)"); // "무배당수호천사급여실손의료비보장보험(갱신형)"

        String tempName = PersonNameGenerator.generate();
        logger.info("이름 설정 :: {}", tempName);
        setAnnounceName(tempName);

        logger.info("생년월일 설정 : {}", info.getFullBirth());
        setAnnounceBirth(info.getFullBirth());

        logger.info("성별 설정");
        setAnnounceGender(info.getGender());

        logger.info("주상품 조회 버튼 클릭!");
        announceBtnClick(By.xpath("//span[contains(., '주상품 조회')]"));

        logger.info("주상품 설정");
        setPlanType("(무)수호천사급여실손의료비보장보험(갱신형)-최초계약-상해급여형-비위험"); // (무)수호천사급여실손의료비보장보험(갱신형)-최초계약-상해급여형-비위험

        logger.info("특약 조회 버튼 클릭!");
        announceBtnClick(By.xpath("//span[contains(., '특약 조회')]"));

        logger.info("주계약 보험기간 설정");
        setAnnounceInsTerm(info.getInsTerm());

        logger.info("주계약 납입기간 설정");
        setAnnounceNapTerm(info.getNapTerm());

        logger.info("주계약 납입주기 설정");
        setAnnounceNapCycle(info.getNapCycleName());

        CrawlingTreaty mainTreaty = info.getTreatyList().get(0);
        logger.info("주계약 가입금액 설정");
        setAnnounceAssureMoney(mainTreaty.getAssureMoney());

        logger.info("선택특약 설정");
        setSubTreaties(info);

        logger.info("▉▉ '보험료 계산'버튼을 클릭합니다");
        pushButton("보험료 계산");

        logger.info("▉▉ 합계보험료를 확인합니다(2/3)");
        noteMonthlyPremium(info.getTreatyList().get(0));

        logger.info("▉▉ 스크린샷을 촬영합니다(3/3)");
        twinkleScreenShot(info);

        logger.info("▉▉ 보장내용상세보기 버튼 클릭");
        pushButton("보장내용상세보기");

        logger.info("pKind :: {}", info.getProductKind());
        logger.info("pKind :: {}", info.getProductKind());
        logger.info("pKind :: {}", info.getProductKind());

        logger.info("해약환급금 내용 확인");
        checkReturnMoneyPGT(info);

        return true;

    }



    public void setSubTreaties(CrawlingProduct info) throws Exception {

        List<CrawlingTreaty> trtList = info.getTreatyList();
        List<WebElement> trList = driver.findElements(By.xpath("//tbody[@id='step3_tbody1']/tr"));
        logger.info("SIZE TR  :: {}", trList.size());
        logger.info("SIZE TRT :: {}", trList.size());
        info.getTreatyList().forEach(idx -> { logger.info("TRT NAME :: {}", idx.getTreatyName()); });

        for (WebElement tr : trList) {
            for (CrawlingTreaty trt : trtList) {
                if (trt.getTreatyName().trim().equals(tr.findElement(By.xpath("./td[2]")).getText().trim())) {

                    // 체크표시
                    tr.findElement(By.xpath("./td[1]/input")).click();
                    WaitUtil.waitFor(1);

                    // 값 설정 - 보험기간
                    logger.info("INSTERM :: {}", info.getInsTerm());
                    Select selctInsTerm = new Select(tr.findElement(By.xpath("./td[4]//select")));
                    selctInsTerm.selectByVisibleText(info.getInsTerm());

                    // 값 설정 - 납입기간
                    logger.info("NAPTERM :: {}", info.getNapTerm());
                    Select selctNapTerm = new Select(tr.findElement(By.xpath("./td[5]//select")));
                    selctNapTerm.selectByVisibleText(info.getNapTerm());

                    // 값 설정 - 납입주기
                    logger.info("NAPCYCLENAME :: {}", info.getNapCycleName());
                    Select selctNapCycle = new Select(tr.findElement(By.xpath("./td[6]//select")));
                    selctNapCycle.selectByVisibleText(info.getNapCycleName());

                    // 값 설정 - 가입금액
                    logger.info("ASSUREMONEY :: {}", info.getAssureMoney());
                    tr.findElement(By.xpath("./td[7]//input")).sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
                    WaitUtil.waitFor(1);
                    tr.findElement(By.xpath("./td[7]//input")).sendKeys(String.valueOf(trt.getAssureMoney() / 1_0000));

                    break;
                }
            }
            logger.info("TEST FLAG      :: {}", tr.findElement(By.xpath("./td[1]")).isSelected());
            logger.info("TEST TITLE     :: {}", tr.findElement(By.xpath("./td[2]")).getText().trim());
            // logger.info("TEST 3 :: {}", tr.findElement(By.xpath("./td[3]")).getText().trim());
            logger.info("TEST INSTERM   :: {}", tr.findElement(By.xpath("./td[4]")).getText().trim());
            logger.info("TEST NAPTERM   :: {}", tr.findElement(By.xpath("./td[5]")).getText().trim());
            logger.info("TEST NAPCYCLE  :: {}", tr.findElement(By.xpath("./td[6]")).getText().trim());
            logger.info("TEST ASSAMT    :: {}", tr.findElement(By.xpath("./td[7]")).getText().trim());
            // logger.info("TEST 8 :: {}", tr.findElement(By.xpath("./td[8]")).getText().trim());
        }

    }

}
