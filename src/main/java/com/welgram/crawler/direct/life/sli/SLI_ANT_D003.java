package com.welgram.crawler.direct.life.sli;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SLI_ANT_D003 extends CrawlingSLIDirect {

    public static void main(String[] args) {
        executeCommand(new SLI_ANT_D003(), args);
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) {
        option.setImageLoad(false);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $button = null;
        WebElement $a = null;

        waitLoadingBar();
        WaitUtil.loading(2);

        logger.info("생년월일");
        setBirthday(info.getFullBirth());

        logger.info("성별");
        setGender(info.getGender());

        logger.info("내 보험료 확인 버튼 선택");
        $button = driver.findElement(By.id("calculate"));
        click($button);

        logger.info("연금개시나이 선택");
        By location = By.id("annAge");
        setAnnuityAge(info.annuityAge + "세", location);

        logger.info("납입 기간 선택");
        location = By.id("napTerm");
        setNapTerm(info.napTerm + "납", location);

        logger.info("납입금액 입력");
        location = By.id("napMoney");
        setInputAssureMoney(info, location);

        logger.info("주계약 보험료 세팅");
        info.treatyList.get(0).monthlyPremium = info.assureMoney;

        logger.info("다시계산 버튼 클릭");
        location = By.id("reCalc");
        reCalculate(location);

        logger.info("알럿 확인");
        alert();

        logger.info("해약환급금 버튼 클릭");
        $a = driver.findElement(By.xpath("//a[text()='보장내용/해약환급금']"));
        click($a);

        logger.info("스크린샷");
        takeScreenShot(info);

        logger.info("연금수령액 크롤링");
        crawlAnnuityPremium(info);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList3(info);

        return true;
    }

    @Override
    protected boolean preValidation(CrawlingProduct info) {

        boolean result = true;
        try {
            logger.info("연금저축 가입 연령 체크");
            if (info.napTerm.indexOf("년") > -1) {

                // 최대 가입 연령 = (연금개시나이 - 납입기간)세
                int maxAge = Integer.parseInt(info.annuityAge) - Integer.parseInt(info.napTerm.replaceAll("년", "").trim());
                logger.info("최대 가입 연령 : " + maxAge);
                logger.info("가입 나이 : " + info.age);

                if (maxAge < Integer.parseInt(info.age)) {
                    logger.info("최대 가입 연령 초과");
                    result = false;
                }
            }
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }


}
