package com.welgram.crawler.direct.life.tyl;

import com.welgram.common.PersonNameGenerator;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.direct.life.tyl.CrawlingTYL.CrawlingTYLAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.util.InsuranceUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;


// 2023.08.23 | 최우진 | (무)꿈나무우리아이보험(해약환급금미지급형-종합설계형)_태아보장기간
public class TYL_BAB_F006 extends CrawlingTYLAnnounce {

    public static void main(String[] args) { executeCommand(new TYL_BAB_F006(), args); }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String fetusBirth = InsuranceUtil.getDateOfBirth(12);
        String fetusBirthYear = fetusBirth.substring(0, 4);
        String fetusBirthMonth = fetusBirth.substring(4, 6);
        String fetusBirthDay = fetusBirth.substring(6, 8);
        String motherName = PersonNameGenerator.generate();
        String birthYear = info.getFullBirth().substring(0, 4);
        String birthMonth = info.getFullBirth().substring(4, 6);
        String birthDay = info.getFullBirth().substring(6, 8);
        String refundOption = "BAB";
        String[] arrTextType = info.getTextType().split("#");

        // PROCESS
        logger.info("▉▉▉▉ 시작 ▉▉▉▉");
        initTYL(info);

        logger.info("▉▉▉▉ [01] 고객정보를 입력해 주세요 ▉▉▉▉");
        pushButton(driver.findElement(By.xpath("//*[@id='fetusCheckbox_21']")), 1);
        setBABBirthday(
            driver.findElement(By.id("dueday_Y_21")),   fetusBirthYear,
            driver.findElement(By.id("dueday_M_21")),   fetusBirthMonth,
            driver.findElement(By.id("dueday_D_21")),   fetusBirthDay
        );
        setUserName(driver.findElement(By.id("name_31")), motherName);
        setBirthday(
            driver.findElement(By.id("birthday_Y_31")),     birthYear,
            driver.findElement(By.id("birthday_M_31")),     birthMonth,
            driver.findElement(By.id("birthday_D_31")),     birthDay
        );
        // setGender(info.getGender());      // default :: 산모가 선택된 상태
        pushButton("주상품 조회", 5);

        logger.info("▉▉▉▉ [02] 주상품을 선택해 주세요 ▉▉▉▉");
        submitMainProduct(driver.findElement(By.id("policycd_l")), arrTextType[0]);
        pushButton("특약 조회", 7);

        logger.info("▉▉▉▉ [03] 특약을 선택해 주세요 ▉▉▉▉");
        submitBABTreatiesInfo(
            driver.findElements(By.xpath("//*[@id='step3_tbody1']/tr")),
            driver.findElements(By.xpath("//*[@id='step3_2_tbody1']/tr")),
            info.getTreatyList()
        );
        pushButton("보험료 계산", 7);

        logger.info("▉▉▉▉ 결과확인 ▉▉▉▉");
        crawlBABPremium(
            driver.findElement(By.xpath("//*[@id='step5_div']/table/tfoot/tr/td/span")),
            driver.findElement(By.xpath("//*[@id='step5_2_div']/table/tfoot/tr/td/span")),
            info
        );
        pushButton("보장내용상세보기", 5);
        snapPicture(info);
        crawlReturnMoneyList(
            driver.findElements(By.xpath("//caption[text()='해약환급금 예시표']/parent::table/tbody/tr")),
            info,
            refundOption
        );

        return true;

    }



    @Override
    public void submitBABTreatiesInfo(Object... obj) throws Exception {

        List<WebElement> defaultList1 = driver.findElements(By.xpath("//*[@id='step3_tbody1']/tr"));        // 출생전
        List<WebElement> defaultList2 = driver.findElements(By.xpath("//*[@id='step3_2_tbody1']/tr"));      // 전환정보
        List<WebElement> elementList1 = (obj[0] == null) ? defaultList1 : (List<WebElement>) obj[0];
        List<WebElement> elementList2 = (obj[0] == null) ? defaultList2 : (List<WebElement>) obj[1];

        List<CrawlingTreaty> treatyList = (List<CrawlingTreaty>) obj[2];
        int handeldTreatyCnt = 0;

        try {

            for (WebElement tr : elementList1) {
                boolean isChecked = tr.findElement(By.xpath("./td[1]/input[1]")).isSelected();
                WebElement checker = tr.findElement(By.xpath("./td[1]/input[1]"));
                if (isChecked) {
                    String tagName = tr.findElement(By.xpath("./td[2]/div/label")).getText();
                    checker.click();
                    logger.info(tagName + " :: 선택중 >>>> 선택해제");
                }
            }
            logger.info("출생전정보 테이블 초기화완료");

            for (WebElement tr : elementList2) {
                boolean isChecked = tr.findElement(By.xpath("./td[1]/input[1]")).isSelected();
                WebElement checker = tr.findElement(By.xpath("./td[1]/input[1]"));
                if (isChecked) {
                    String tagName = tr.findElement(By.xpath("./td[2]/div/label")).getText();
                    checker.click();
                    logger.info(tagName + " :: 선택중 >>>> 선택해제");
                }
            }
            logger.info("전환정보 테이블 초기화완료");

            // 2. 선택특약 설정
            for (CrawlingTreaty treaty : treatyList) {
                for (WebElement trBeforeBirth : elementList1) {
                    boolean isChecked = trBeforeBirth.findElement(By.xpath("./td[1]/input[1]")).isSelected();
                    String trName = trBeforeBirth.findElement(By.xpath("./td[2]/div/label")).getText();
                    WebElement checker = trBeforeBirth.findElement(By.xpath("./td[1]/input[1]"));
                    if (treaty.getTreatyName().equals(trName)) {
                        logger.info("▉ 특약명11 :: {} ▉", trName);
                        if (!isChecked) {
                            checker.click();
                        }
//                        setInsTerm(trBeforeBirth.findElement(By.xpath("./td[4]//select")), treaty.getInsTerm());// 보험기간
//                        setNapTerm(trBeforeBirth.findElement(By.xpath("./td[5]//select")), treaty.getNapTerm());// 납입기간
//                        setNapCycle(trBeforeBirth.findElement(By.xpath("./td[6]//select")), treaty.getNapCycleName());// 납입주기

                        logger.info("가입금액 설정가능 여부 :: {}", helper.existElement(trBeforeBirth, By.xpath("./td[7]//input")));

                        if(helper.existElement(trBeforeBirth, By.xpath("./td[7]//input"))) {
                            setAssureMoneyTemp(trBeforeBirth.findElement(By.xpath("./td[7]//input")), treaty.getAssureMoney());  // 가입금액
                        }
                        logger.info("▉ =================================");

                        handeldTreatyCnt++;
                    }
                }

                for (WebElement trAfterBirth : elementList2) {
                    boolean isChecked = trAfterBirth.findElement(By.xpath("./td[1]/input[1]")).isSelected();
                    String trName = trAfterBirth.findElement(By.xpath("./td[2]/div/label")).getText();
                    WebElement checker = trAfterBirth.findElement(By.xpath("./td[1]/input[1]"));
                    if (treaty.getTreatyName().equals(trName)) {
                        logger.info("▉ 특약명22 :: {} ▉", trName);
                        if (!isChecked) {
                            checker.click();
                        }
                        setInsTerm(trAfterBirth.findElement(By.xpath("./td[4]//select")), treaty.getInsTerm());// 보험기간
                        setNapTerm(trAfterBirth.findElement(By.xpath("./td[5]//select")), treaty.getNapTerm());// 납입기간
                        setNapCycle(trAfterBirth.findElement(By.xpath("./td[6]//select")), treaty.getNapCycleName());// 납입주기

                        logger.info("가입금액 설정가능 여부 :: {}", helper.existElement(trAfterBirth, By.xpath("./td[7]//input")));

                        if (helper.existElement(trAfterBirth, By.xpath("./td[7]//input"))) {
                            setAssureMoneyTemp(trAfterBirth.findElement(By.xpath("./td[7]//input")), treaty.getAssureMoney());  // 가입금액
                        }
                        logger.info("▉ =================================");

                        handeldTreatyCnt++;
                    }
                }
            }
            logger.info("▉▉ 선택 특약 설정 완료 ▉▉");
            logger.info("HANDLED TREATY CNT :: {}", handeldTreatyCnt);
            logger.info("WELGRAM TREATY CNT :: {}", treatyList.size());
        } catch (Exception e) {
            throw new CommonCrawlerException("태아 선택특약 설정중 에러발생 ");
        }

    }

}
