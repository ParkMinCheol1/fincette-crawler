package com.welgram.crawler.direct.life.sli;

import com.welgram.common.MoneyUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanAnnuityMoney;
import java.util.LinkedHashMap;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SLI_ASV_F005 extends CrawlingSLIAnnounce {

    public static void main(String[] args) {
        executeCommand(new SLI_ASV_F005(), args);
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

        //step1 : 공시실 상품명 찾기
        logger.info("상품명 찾기");
        findProduct(info);

        //step2 : 사용자 정보 입력
        logger.info("생년월일 설정");
        setBirthday(info.getFullBirth());

        logger.info("성별 설정");
        setGender(info.getGender());

        logger.info("다음 버튼 클릭");
        $button = driver.findElement(By.xpath("//button[normalize-space()='다음']"));
        click($button);

        //step3 : 가입조건 설정
        logger.info("보험종류 설정");
        setProductType(info.getTextType().split(",")[0].trim());

        logger.info("연금개시연령 설정");
        setAnnuityAge(info.annuityAge);

        logger.info("연금유형 설정");
        setAnnuityType("종신형 10회");

        logger.info("다음 버튼 클릭");
        $button = driver.findElement(By.xpath("//button[normalize-space()='다음']"));
        click($button);

        //step4 : 주보험조건 설정
        /**
         * 해당 상품의 주계약 보험기간은 연금개시나이와 같다.
         */
        logger.info("주계약 보험기간 설정");
        setInsTerm(info.getAnnuityAge(), By.id("insrPrdTypValCd_A023601ANN"));

        logger.info("주계약 납입기간 설정");
        setNapTerm(info.getNapTerm(), By.id("padPrd_A023601ANN"));

        logger.info("주계약 보험료(가입금액) 설정");
        setAssureMoney(info.getAssureMoney(), By.id("price_A023601ANN"));
        info.getTreatyList().get(0).monthlyPremium = info.getAssureMoney();

        logger.info("보험료 계산 버튼 클릭");
        $button = driver.findElement(By.xpath("//button[normalize-space()='보험료 계산'][@class[contains(., 'primary')]]"));
        click($button);

        logger.info("연금수령액 크롤링");
        crawlAnnuityPremium(info, By.cssSelector("#content1 > div:nth-child(2) > section > div > table > tbody > tr:nth-child(1) > td:nth-child(4)"));

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        return true;
    }

    @Override
    public void crawlAnnuityPremium(Object... obj) throws CommonCrawlerException {
        String title = "연금수령액 크롤링";
        CrawlingProduct info = (CrawlingProduct) obj[0];

        String annuityType = info.getAnnuityType();
        PlanAnnuityMoney planAnnuityMoney = info.getPlanAnnuityMoney();

        try{
            //종신연금형, 확정기간연금플러스형 영역에 해당하는 tr 범위만 추려내기 위한 jquery script
            String script = "return $(arguments[0]).nextAll().addBack().slice(0, arguments[1]).get();";

            //종신 연금수령액 크롤링
            LinkedHashMap<String, String> whlMap = new LinkedHashMap<>();
            String[] counts = {"10년", "20년", "30년", "100세"};

            WebElement $whlTh = driver.findElement(By.xpath("//th[normalize-space()='종신연금형']"));
            WebElement $whlTr = $whlTh.findElement(By.xpath("./parent::tr"));
            WebElement $whlTd = null;
            String whlPremium = "";
            String rowspan = $whlTh.getAttribute("rowspan");
            List<WebElement> $whlTrRange = (List<WebElement>) helper.executeJavascript(script, $whlTr, rowspan);

            /**
             * 원수사에서 모든 연령대에서 고정된 연금수령액 테이블을 제공하면 좋겠지만,
             * 특정 나이에서 연금수령액 N회 보증이 안나오는 경우도 있음.
             *
             * ex) 30세 남자의 경우 종신연금형 10회 보증 / 30회 보증 정보를 제공하지만
             *     14세 남자의 경우 종신연금형 10회 보증만 제공한다.
             *
             * 따라서 cssSelector로 위치를 고정해서 가져오는 방법은 잘못된 위치의
             * 연금수령액을 크롤링하게 될 수 있어 매우 위험하다. 이에 대해 유연하게 대처하기 위해
             * xpath로 적절하게 처리한다.
             *
             */
            boolean isExist = false;
            for(WebElement $tr : $whlTrRange) {
                for(String count : counts) {
                    By position = By.xpath("./th[contains(., '" + count + "')]");
                    isExist = helper.existElement($tr, position);

                    if(isExist) {
                        $whlTh = $tr.findElement(position);
                        $whlTd = $whlTh.findElement(By.xpath("./following-sibling::td[last()]"));
                        helper.moveToElementByJavascriptExecutor($whlTd);
                        whlPremium = String.valueOf(MoneyUtil.toDigitMoney($whlTd.getText()));

                        whlMap.put(count, whlPremium);
                        break;
                    }
                }
            }

            String whl10Y = whlMap.getOrDefault("10년", "0");
            String whl20Y = whlMap.getOrDefault("20년", "0");
            String whl30Y = whlMap.getOrDefault("30년", "0");
            String whl100A = whlMap.getOrDefault("100세", "0");
            planAnnuityMoney.setWhl10Y(whl10Y);
            planAnnuityMoney.setWhl20Y(whl20Y);
            planAnnuityMoney.setWhl30Y(whl30Y);
            planAnnuityMoney.setWhl100A(whl100A);


            //확정 연금수령액 크롤링
            LinkedHashMap<String, String> fxdMap = new LinkedHashMap<>();
            counts = new String[]{"10년", "15년", "20년", "25년", "30년"};

            WebElement $fxdTh = driver.findElement(By.xpath("//th[normalize-space()='확정기간연금형']"));
            WebElement $fxdTr = $fxdTh.findElement(By.xpath("./parent::tr"));
            WebElement $fxdTd = null;
            String fxdPremium = "";
            rowspan = $fxdTh.getAttribute("rowspan");
            List<WebElement> $fxdTrRange = (List<WebElement>) helper.executeJavascript(script, $fxdTr, rowspan);

            isExist = false;
            for(WebElement $tr : $fxdTrRange) {
                for(String count : counts) {
                    By position = By.xpath("./th[contains(., '" + count + "')]");
                    isExist = helper.existElement($tr, position);

                    if(isExist) {
                        $fxdTh = $tr.findElement(position);
                        $fxdTd = $fxdTh.findElement(By.xpath("./following-sibling::td[last()]"));
                        helper.moveToElementByJavascriptExecutor($fxdTd);
                        fxdPremium = String.valueOf(MoneyUtil.toDigitMoney($fxdTd.getText()));

                        fxdMap.put(count, fxdPremium);
                        break;
                    }
                }
            }

            String fxd10Y = fxdMap.getOrDefault("10년", "0");
            String fxd15Y = fxdMap.getOrDefault("15년", "0");
            String fxd20Y = fxdMap.getOrDefault("20년", "0");
            String fxd25Y = fxdMap.getOrDefault("25년", "0");
            String fxd30Y = fxdMap.getOrDefault("30년", "0");
            planAnnuityMoney.setFxd10Y(fxd10Y);
            planAnnuityMoney.setFxd15Y(fxd15Y);
            planAnnuityMoney.setFxd20Y(fxd20Y);
            planAnnuityMoney.setFxd25Y(fxd25Y);
            planAnnuityMoney.setFxd30Y(fxd30Y);

            if (annuityType.contains("종신")) {
                info.annuityPremium = planAnnuityMoney.getWhl10Y();
                logger.info("종신연금수령액 : {}원", info.annuityPremium);
            } else if (annuityType.contains("확정")) {
                info.fixedAnnuityPremium = planAnnuityMoney.getFxd10Y();
                logger.info("확정연금수령액 : {}원", info.fixedAnnuityPremium);
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_ANNUITY_MONEY;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

}