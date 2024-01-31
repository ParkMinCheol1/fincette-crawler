package com.welgram.crawler.direct.life.kyo;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.crawler.direct.life.CrawlingKYO;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductKind;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


// 2023.05.17           | 최우진               | 대면_암
// KYO_CCR_F003         | (무)교보암케어보험(서비스선택형)
public class KYO_CCR_F003 extends CrawlingKYO {

    public static void main(String[] args) { executeCommand(new KYO_CCR_F003(), args); }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        CrawlingTreaty mainTreaty = info.getTreatyList().get(0);

        logger.info("START :: (무)교보암케어보험(서비스선택형)");

        logger.info("공시실 진입 후 건강/암 버튼 클릭");
        element = driver.findElement(By.linkText("건강/암"));
        waitElementToBeClickable(element).click();
        WaitUtil.waitFor(2);

        logger.info("상품명 :: [{}] 클릭", info.getProductName());
        element = driver.findElement(By.xpath("//td[text()='" + info.getProductName() + "']/parent::tr//button"));
        waitElementToBeClickable(element).click();
        waitAnnouncePageLoadingBar();
        waitAnnouncePageLoadingBar();
        WaitUtil.waitFor(3);

        logger.info("생년월일 설정");
        try {
            String title = "생년월일";
            String welgramBirth = info.getFullBirth();

            //생년월일 입력
            WebElement input = driver.findElement(By.id("ins0_id0"));
            WebElement label = driver.findElement(By.xpath("//label[@for='" + input.getAttribute("id") + "']"));
            waitElementToBeClickable(label).click();
            setTextToInputBox(input, welgramBirth);

            //실제로 입력된 생년월일 읽어오기
            String script = "return $(arguments[0]).val();";
            String targetBirth = String.valueOf(executeJavascript(script, input));

            //비교
            printAndCompare(title, welgramBirth, targetBirth);
        } catch (Exception e) {
            throw new SetBirthdayException(e.getMessage());
        }


        logger.info("성별 설정");
//        setGenderNew(info.getGender());
        try {
            if(info.getGender() == MALE) {
                logger.info("성별 선택 :: 남자");
                driver.findElement(By.xpath("//input[@id='sdt1']//parent::label")).click();

            } else {
                logger.info("성별 선택 :: 여자");
                driver.findElement(By.xpath("//input[@id='sdt2']//parent::label")).click();

            }
            WaitUtil.waitFor(2);

        } catch(Exception e) {
            throw new SetGenderException(e.getMessage());
        }

        logger.info("보험료 계산 버튼 클릭");
        try {
            driver.findElement(By.id("isPrcClc0")).click();
            WaitUtil.waitFor(4);

        } catch(Exception e) {
            throw new CommonCrawlerException("버튼 클릭에러\n" + e.getMessage());
        }

        logger.info("주계약::보험종류 확인");
        try {
            String prodKind = driver.findElement(By.xpath("(//div[text()='보험종류']//parent::div//div[2])[2]"))
                .getText();
            logger.info("홈페이지(보험종류) :: {}", prodKind);
            logger.info("가입설계(보험종류) :: {}", mainTreaty.getTreatyName());

            if(prodKind.equals(mainTreaty.getTreatyName())) {
                logger.info("보험종류 정상");
            } else {
                throw new CommonCrawlerException("주계약::보험종류 불일치");
            }
            WaitUtil.waitFor(1);

        } catch(Exception e) {
            throw new CommonCrawlerException("주계약 확인중 에러발생 (보험종류)");
        }

        logger.info("주계약::가입금액 확인");
        try {
            String assureMoney = driver.findElement(By.xpath("(//div[text()='가입금액']//parent::div//div[2])[2]"))
                .getText();
            assureMoney = String.valueOf(Integer.parseInt(assureMoney.replaceAll("[^0-9]", "")) * 1_0000);  // todo | 단위 유의
            logger.info("홈페이지(가입금액) :: {}", assureMoney);
            logger.info("가입설계(가입금액) :: {}", mainTreaty.getAssureMoney());

            if(assureMoney.equals(String.valueOf(mainTreaty.getAssureMoney()))) {
                logger.info("가입금액 정상");

            } else {
                throw new CommonCrawlerException("주계약::가입금액 불일치");
            }
            WaitUtil.waitFor(1);

        } catch(Exception e) {
            throw new CommonCrawlerException("주계약 확인중 에러발생 (가입금액)");
        }

        logger.info("주계약::보험기간 확인");
        try {
            String insTerm = driver.findElement(By.xpath("(//div[text()='보험기간']//parent::div//div[2])[2]"))
                .getText();
            mainTreaty.setInsTerm(mainTreaty.getInsTerm() + "만기");
            logger.info("홈페이지(보험기간) :: {}", insTerm);
            logger.info("가입설계(보험기간) :: {}", mainTreaty.getInsTerm());

            if(insTerm.equals(mainTreaty.getInsTerm())) {
                logger.info("보험기간 정상");

            } else {
                throw new CommonCrawlerException("주계약::보험기간 불일치");
            }
            WaitUtil.waitFor(1);

        } catch(Exception e) {
            throw new CommonCrawlerException("주계약 확인중 에러발생 (보험기간)");
        }

        logger.info("주계약::납입기간 확인");
        try {
            String napTerm = driver.findElement(By.xpath("(//div[text()='납입기간']//parent::div//div[2])[2]"))
                .getText();
            mainTreaty.setNapTerm(mainTreaty.getNapTerm() + "만기");
            
            logger.info("홈페이지(납입기간) :: {}", napTerm);
            logger.info("가입설계(납입기간) :: {}", mainTreaty.getNapTerm());

            if(napTerm.equals(mainTreaty.getNapTerm())
                || (napTerm.equals("전기납") && (mainTreaty.getNapTerm().equals(mainTreaty.getInsTerm())))) {
                logger.info("납입기간 정상");

            } else {
                throw new CommonCrawlerException("주계약::납입기간 불일치");
            }
            WaitUtil.waitFor(1);

        } catch(Exception e) {
            throw new CommonCrawlerException("주계약 확인중 에러발생 (납입기간)");
        }

        logger.info("주계약::납입주기 확인");
        try {
            String napCycle = driver.findElement(By.xpath("(//div[text()='납입주기']//parent::div//div[2])[2]"))
                .getText();
            logger.info("홈페이지(납입주기) :: {}", napCycle);
            logger.info("가입설계(납입주기) :: {}", mainTreaty.getNapCycleName());
            if(napCycle.equals(mainTreaty.getNapCycleName())) {
                logger.info("납입주기 정상");

            } else {
                throw new CommonCrawlerException("주계약::납입주기 불일치");
            }
            WaitUtil.waitFor(1);

        } catch(Exception e) {
            throw new CommonCrawlerException("주계약 확인중 에러발생 (납입주기)");
        }

        logger.info("보험료 확인");
        try {
//            WebElement $monthlyPremium = driver.findElement(By.xpath("//span[text()='보험료']//ancestor::li//"));
            String monthlyPremium = driver.findElement(By.xpath(
                "//*[@id='tabsld_calc']/div/ul/li[2]/div[1]/div[1]/"
                    + "article/div/ul/li[1]/div/div[2]/span/em"))
                .getText()
                .replaceAll("[^0-9]", "");

            info.treatyList.get(0).monthlyPremium = monthlyPremium;
            logger.info(" 월 보험료 :: {}", info.treatyList.get(0).monthlyPremium);
            WaitUtil.waitFor(4);

        } catch(Exception e) {
            throw new CommonCrawlerException("보험료를 확인할 수 없습니다");
        }

        logger.info("선택특약 확인");
        logger.info("_default 변경없음");
        WaitUtil.waitFor(4);

        logger.info("스크린샷 찍기");
        moveToElementByJavascriptExecutor(element);
        takeScreenShot(info);
        WaitUtil.waitFor(1);

        logger.info("해약정보 확인");
        try {
            // 버튼 클릭
            driver.findElement(By.xpath("//button[text()='해약환급금']"))
                .click();
            WaitUtil.waitFor(4);

            // 내용 확인
            List<WebElement> $tbodyTrList = driver.findElements(By.xpath("//tbody[@id='trmRview_1']//tr"));

            for(WebElement tr : $tbodyTrList) {
                String term = tr.findElement(By.xpath(".//td[1]"))
                    .getText();
                String premiumSum = tr.findElement(By.xpath(".//td[2]"))
                    .getText()
                    .replaceAll("[^0-9]", "");
                String returnMoney = tr.findElement(By.xpath(".//td[3]"))
                    .getText()
                    .replaceAll("[^0-9]", "");
                String returnRate = tr.findElement(By.xpath(".//td[4]"))
                    .getText();

                logger.info("==========================================");
                logger.info("경과기간   :: {}", term);
                logger.info("납입보험료 :: {}", premiumSum);
                logger.info("공시환급금 :: {}", returnMoney);
                logger.info("공시환급률 :: {}", returnRate);

                PlanReturnMoney p = new PlanReturnMoney();
                p.setTerm(term);
                p.setPremiumSum(premiumSum);
                p.setReturnMoney(returnMoney);
                p.setReturnRate(returnRate);

                info.returnPremium = returnMoney;
                info.planReturnMoneyList.add(p);
            }
            logger.info("==========================================");

            if(info.treatyList.get(0).productKind.equals(ProductKind.순수보장형)) {
                logger.info(
                    "보험형태 : {} 상품이므로 만기환급금을 0원으로 설정합니다",
                    info.treatyList.get(0).productKind
                );
                info.returnPremium = "0";
            }

        } catch(Exception e) {
            throw new CommonCrawlerException("해약환급금 환인 중 에러가 발생하였습니다");
        }

        return true;
    }
}
