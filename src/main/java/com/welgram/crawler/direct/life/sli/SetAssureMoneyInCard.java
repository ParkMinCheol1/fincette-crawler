package com.welgram.crawler.direct.life.sli;

import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.crawler.AbstractCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.helper.SeleniumCrawlingHelper;
import java.text.DecimalFormat;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class SetAssureMoneyInCard extends SetAssureMoneyBehavior {

    public SetAssureMoneyInCard(SeleniumCrawlingHelper helper, Class<? extends AbstractCrawler> productClass) {
        super(helper, productClass);
    }

    @Override
    public void setAssureMoney(Object obj) throws SetAssureMoneyException {
        try {
            CrawlingInfo crawlingInfo = (CrawlingInfo) obj;
            CrawlingProduct info = crawlingInfo.getInfo();
            Object position = crawlingInfo.getPosition();

            // 현재 플랜의 카드 요소
            WebElement card = helper.getWebElement(position);

            // 특약명과 가입금액이 들어있는 요소
            WebElement ul = card.findElements(By.cssSelector("ul.info2.list-data1")).get(1);

            // 특약명과 가입금액 목록
            List<WebElement> lis = ul.findElements(By.tagName("li"));

            List<CrawlingTreaty> treatyList = info.getTreatyList();
            for (CrawlingTreaty treaty : treatyList) {

                // 특약명 (ex: 일반암, 고액암..)
                String trimmedName = treaty.getTreatyName()
                    .substring(0, treaty.getTreatyName().indexOf("(")).trim();

                // 특약 가입금액
                int assureMoney = treaty.getAssureMoney();

                lis.stream().filter(li -> { // 특약명과 가입금액 목록 순회

                    WebElement firstDiv = li.findElements(By.tagName("div")).get(0);
                    String firstDivText = firstDiv.getText();
                    return trimmedName.contains(firstDivText);

                }).findFirst().ifPresent(li -> { // 특약명과 일치하는 목록이 있을 때 가입금액 선택을 시도함

                    // 가입금액 정보가 있는 div
                    WebElement div = li.findElements(By.tagName("div")).get(1);

                    try { // select tag가 있다면 선택한다
                        Select select = new Select(div.findElement(By.tagName("select")));
                        int dividedBy10000 = assureMoney / 10000;

                        DecimalFormat df = new DecimalFormat("#,###");
                        String format = df.format(dividedBy10000);
                        String visibleTxt = format + "만원";

                        String selectedOptionTxt = select.getFirstSelectedOption().getText();
                        if (!selectedOptionTxt.equals(visibleTxt)) {
                            select.selectByVisibleText(visibleTxt);
                            logger.info("선택되어 있던 값 : " + selectedOptionTxt );
                            logger.info("새로 선택한 값 : " + visibleTxt );

                        } else {
                            logger.info("이미 선택되어있는 값입니다 : " + selectedOptionTxt );
                        }

                        // 다시 계산 버튼
                        try {
                            WebElement reClacButton = card.findElement(
                                By.xpath(".//button[contains(.,'다시계산')]"));
                            if (reClacButton.isEnabled()) {
                                helper.click(reClacButton);
                                helper.waitForLoading();
                            }

                        } catch(Exception e) {
                            e.printStackTrace();
                        }

                    } catch (NoSuchElementException e) { // <-- select tag

                        // select tag가 없다면, 해당 특약에 대해서 선택할 수 없는 ui이다.
                        logger.info(trimmedName + "은 가입금액을 선택하지 않습니다. ");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (Exception e) {
            throw new SetAssureMoneyException(e);
        }
    }
}
