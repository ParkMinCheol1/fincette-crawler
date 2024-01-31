package com.welgram.crawler.direct.life.tyl;

import com.welgram.common.PersonNameGenerator;
import com.welgram.crawler.direct.life.tyl.CrawlingTYL.CrawlingTYLAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanCalc;
import com.welgram.util.InsuranceUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


// 2023.08.23 | 최우진 | (무)꿈나무우리아이보험(표준형-자유설계형)_태아보장기간
public class TYL_BAB_F003 extends CrawlingTYLAnnounce {

    public static void main(String[] args) { executeCommand(new TYL_BAB_F003(), args); }



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
        String floatingTreatyName = "(무)꿈나무우리아이보험(표준형-자유설계형)_태아적립";

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
        submitMainProduct(driver.findElement(By.id("policycd_l")), arrTextType[1]);
        pushButton("특약 조회", 7);

        logger.info("▉▉▉▉ [03] 특약을 선택해 주세요 ▉▉▉▉");
        submitBABTreatiesInfo(
            driver.findElements(By.xpath("//*[@id='step3_tbody1']/tr")),
            driver.findElements(By.xpath("//*[@id='step3_2_tbody1']/tr")),
            info.getTreatyList()
        );
        pushButton("보험료 계산", 7);


        //todo | 임시 코드 위치

        for (CrawlingTreaty treaty : info.getTreatyList()) {
            for (WebElement $tr : driver.findElements(By.xpath("//caption[text()='보험료계산 결과']//parent::table/tbody/tr"))) {
                if (treaty.getTreatyName().equals($tr.findElement(By.xpath(".//td[1]")).getText())
                    && treaty.getTreatyName().equals(floatingTreatyName)) {

                    int unit = 1;

                    String assAmt =
                        $tr.findElement(By.xpath(".//td[5]/span"))
                            .getText();

                    if (assAmt.contains("만원")) {
                        unit = 10_000;
                        logger.info("단위설정 :: {}", unit);
                    }

                    assAmt = String.valueOf(Integer.parseInt(assAmt.replaceAll("[^0-9]", "")) * unit);

                    logger.info("=========  FLOATING TREATY  =========");
                    logger.info("treatyName :: {}", treaty.getTreatyName());
                    logger.info("floatingAssAmt :: {}", assAmt);
                    logger.info("mapperId :: {}", treaty.mapperId);

                    PlanCalc planCalc = new PlanCalc();
                    planCalc.setMapperId(Integer.parseInt(treaty.mapperId));
                    planCalc.setInsAge(Integer.parseInt(info.getAge()));
                    planCalc.setGender(info.gender == MALE ? "M" : "F");
                    planCalc.setAssureMoney(assAmt);

                    treaty.setPlanCalc(planCalc);
                }
            }
        }

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

}
