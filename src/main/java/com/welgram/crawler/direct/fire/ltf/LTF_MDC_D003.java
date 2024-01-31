package com.welgram.crawler.direct.fire.ltf;

import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.strategy.TreatyNameComparators;
import com.welgram.crawler.general.*;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class LTF_MDC_D003 extends CrawlingLTFAnnounce {

    public static void main(String[] args) {
        executeCommand(new LTF_MDC_D003(), args);
    }

    /*
    등록된 가입설계에서 아래 1개 특약은
    갱)질병·상해3대비급여형 실손의료비

    원수사에서 아래 1개 특약으로 선택함
    갱)질병3대비급여형 실손의료비
    갱)상해3대비급여형 실손의료비
     */

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        enterPage(info);
        setBirthday(info);
        setGender(info);
        setJob();
        clickNext();

        setInsTerm(info);
        setNapTerm(info);
        clickNext();

        setTreaties(info);
//        compareTreaties(info); // todo LTF_MDC_D003 특약 비교 로직 추가
        calculate();

        crawlPremium(info);
        crawlReturnMoneyList(info);
        crawlReturnPremium(info);

        return true;
    }

    protected void setTreaties(CrawlingProduct info) throws CommonCrawlerException {
        try {

            List<WebElement> trs = helper.waitPesenceOfAllElementsLocatedBy(
                By.cssSelector("#priceLA-step2-idambo-tbody tr")
            );

            for (WebElement tr : trs) {
                // 실손은 체크박스 다 클릭
                WebElement checkBox = tr.findElement(By.cssSelector("td.alignR.lst"));
                helper.moveToElementByJavascriptExecutor(checkBox);
                helper.click(checkBox, "체크박스 클릭");

                // 가입설계 특약명과 동일한 tr인지 확인
                Optional<CrawlingTreaty> matchedTreaty = info.treatyList.stream().filter(treaty -> {
                    String siteTreatyName = checkBox.getAttribute("title").trim();
                    String welgramTreatyName = treaty.treatyName;

                    return TreatyNameComparators.allApplied.equals(siteTreatyName, welgramTreatyName);
                }).findFirst();

                // 가입설계 특약명과 동일하다면 가입금액 설정하기
                if (matchedTreaty.isPresent()) {
                    // 가입금액 입력
                    WebElement assureMoneyEl = tr.findElement(By.cssSelector("[name='isamt']"));
                    int assureMoney = matchedTreaty.get().assureMoney / 10000;
                    switch (assureMoneyEl.getTagName()) {
                        case "input":
                            helper.sendKeys3_check(assureMoneyEl, String.valueOf(assureMoney));
                            break;
                        case "select":
                            if (assureMoneyEl.findElements(By.tagName("option")).size() > 1) {
                                helper.selectByText_check(assureMoneyEl, String.valueOf(assureMoney));
                                logger.info("가입금액:: {}만원", assureMoney);
                            } else {
                                logger.info("가입금액:: 이 고정입니다.");
                            }
                    }
                    logger.info("선택한 가입금액 :: {}만원", assureMoney);
                }
            }

        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }
}
