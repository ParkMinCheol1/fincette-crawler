package com.welgram.crawler.helper;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.SeleniumCrawler;
import java.time.Duration;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.Setter;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeleniumCrawlingHelper {

    public final static Logger logger = LoggerFactory.getLogger(SeleniumCrawler.class);

    @Setter
    private WebDriver driver;

    @Setter
    private WebDriverWait wait;

    protected final int SLEEP_TIME = 4000;

    protected final int WAIT_TIME = 30;



    public SeleniumCrawlingHelper(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }



    public boolean isAlertShowed() {
        try {
            Alert alert = new WebDriverWait(driver, 5).until(ExpectedConditions.alertIsPresent());
            if (alert != null) {
                // driver.switchTo().alert().accept();
                return true;
            } else {
                throw new Throwable();
            }
        } catch (Throwable e) {
            return false;
        }
    }



    public WebElement getWebElement(Object position) {
        WebElement element;
        By by;
        if (position instanceof By) {
            by = (By) position;

            new WebDriverWait(driver, 5)
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
            element = driver.findElement(by);

        } else if (position instanceof WebElement) {
            element = (WebElement) position;
        } else {
            throw new RuntimeException("첫번째 파라미터가 By 또는 WebElement 여야 합니다. ");
        }
        return element;
    }



    public List<WebElement> getWebElements(By by) {
        List<WebElement> elements;

        new WebDriverWait(driver, 5)
            .until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
        elements = driver.findElements(by);

        return elements;
    }



    //해당 element가 존재하는지 여부를 리턴
    public boolean existElement(By element) {

        boolean isExist = true;

        try {
            driver.findElement(element);
        } catch (NoSuchElementException e) {
            isExist = false;
        }

        return isExist;
    }


    //해당 element가 존재하는지 여부를 리턴
    public boolean existElement(WebElement rootEl, By element) {

        boolean isExist = true;

        try {
            rootEl.findElement(element);
        } catch (NoSuchElementException e) {
            isExist = false;
        }

        return isExist;
    }

    /**
     *     해당 element가
     *     존재하면 Optional로 리턴,
     *     존재하지 않으면 빈 Optional로 리턴
     */
    public Optional<WebElement> findExistentElement(By by, Long... waitTime) {
        try {
            // waitTime 인수있으면 by로 찾는 element가 존재할 때까지 waitTime 만큼 대기
            new WebDriverWait(driver, waitTime.length > 0 ? waitTime[0] : 0)
                .until(ExpectedConditions.presenceOfElementLocated(by));

            return Optional.of(driver.findElement(by));
        } catch (TimeoutException | NoSuchElementException e) {
            return Optional.empty();
        }
    }

    /**
     *      해당 by로 elements를 찾았을 때,
     *      요소가 존재하고, 화면상에 하나라도 보여지면 그중 첫번째를 Optional로 리턴
     *      요소가 존재하지 않거나, 존재하더라도 화면상에 보여지는 elements가 없으면 빈 Optional로 리턴
     */
    public Optional<WebElement> findFirstDisplayedElement(By by, Long... waitTime) {
        try {
            // waitTime 인수있으면 by로 찾는 element가 존재할 때까지 waitTime 만큼 대기
            new WebDriverWait(driver, waitTime.length > 0 ? waitTime[0] : 0)
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));

            return Optional.ofNullable(
                driver.findElements(by).stream()
                    .filter(WebElement::isDisplayed)
                    .findFirst()
                    .orElseThrow(java.util.NoSuchElementException::new));

        } catch (TimeoutException | NoSuchElementException e) { // selenium의 NoSuchElementException
            logger.info(" method : findFirstDisplayedElement, message : 해당 by로 위치를 찾는 데 실패했습니다.");
        } catch (java.util.NoSuchElementException e) {
            logger.info(" method : findFirstDisplayedElement, message : 해당 by로 위치를 찾는 데 성공했으나, 그중 화면 상에 보여지는 요소가 없습니다.");
        }
        return Optional.empty();
    }



    public void waitForLoading(By... by) {

        logger.info("SeleniumCrawlingHelper.waitForLoading :: start");
        WebDriverWait wait_10 = new WebDriverWait(driver, 10);

        try {
            if (by.length > 0) {
                for (By i : by) {
                    wait_10.until(ExpectedConditions.invisibilityOfAllElements(driver.findElements(i)));
                    WaitUtil.loading(1);
                    wait_10.until(ExpectedConditions.invisibilityOfAllElements(driver.findElements(i)));
                }
            } else {
                wait_10.until(ExpectedConditions.invisibilityOfElementLocated
                    (By.cssSelector("[(class*='loading') || (class*='Loading) || (id*='loading') || (id*='Loading')]")));
                logger.info("SeleniumCrawlingHelper.waitForLoading :: end");
//                wait_10.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[matches(@*,'loading')]")));
//                wait_10.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[contains(@*,translate('loading' 'abcdedfghijklmnopqrstuvxyz',))]")));
            }
        } catch (NoSuchElementException e) {
            logger.info("NoSuchElementException : " + e.getMessage());
        } catch (Exception e) {
            logger.info(e.getMessage());
            logger.info(Arrays.toString(e.getStackTrace()));
        } finally {
            logger.info("SeleniumCrawlingHelper.waitForLoading :: finally end");
        }

    }

    public void waitForCSSElement(String css) throws Exception {
        int time = 0;
        boolean result = true;
        try {
            WaitUtil.loading(1);
            while (result) {
                //logger.debug("displayed :: " + driver.findElement(By.cssSelector(css)).isDisplayed());
                if (driver.findElement(By.cssSelector(css)).isDisplayed()) {
//                    logger.info("로딩 중....");
                    Thread.sleep(500);
                    time += 500;
                } else {
//                    logger.info("로딩 끝....");
                    WaitUtil.loading(2);
                    break;
                }
                if (time > 120000) {
                    result = false;
                    throw new Exception("무한루프 오류 입니다.");
                }
            }
        } catch (Exception e) {
            if (!result) {
                throw new Exception(e);
            }
//            logger.info("####### 로딩 끝....");
            WaitUtil.loading(1);
        }
    }



    public void oppositionWaitForLoading(String element) throws InterruptedException {
        int time = 0;
        try {
            while (true) {
                WaitUtil.waitFor();
                if (!(driver.findElement(By.id(element)).isDisplayed())) {
                    logger.info("로딩 중....");
                    time += SLEEP_TIME;
                    if (time > 60000) {
                        throw new Exception("무한루프 오류 입니다.");
                    }
                } else {
                    logger.info("로딩 끝....");
                    break;
                }
            }
            WaitUtil.waitFor();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }



    public void elementWaitFor(String element) throws Exception {
        int time = 0;
        boolean result = true;
        while (true) {
            try {
                if (driver.findElement(By.cssSelector(element)).isDisplayed()) {
                    logger.info(element + ":element 로딩 끝!");
                    break;
                } else {
                    logger.info(element + ":element 로딩 중....");
                    Thread.sleep(500);
                    time += 500;
                    if (time > 120000) {
                        result = false;
                        break;
                    }
                }
            } catch (Exception e) {
                logger.info(element + ":element 로딩 중....");
                Thread.sleep(500);
                time += 500;
                if (time > 120000) {
                    result = false;
                    break;
                }
            }
        }

        if (!result) {
            throw new Exception("무한루프 오류입니다.");
        }
    }



    public void switchToWindow(String currentHandle, Set<String> windowId, boolean value) {

        Iterator<String> handles = windowId.iterator();
        // 메인 윈도우 창 확인
        String subHandle = null;

        while (handles.hasNext()) {
            subHandle = handles.next();
            if (subHandle.equals(currentHandle)) {
                continue;
            } else {
                // true : 이전 창을 닫지 않음, false : 이전 창을 닫음
                if (!value) {
                    driver.close();
                }
                driver.switchTo().window(subHandle);
                wait = new WebDriverWait(driver, WAIT_TIME);
                break;
            }
        }
    }

    public void switchToWindow2(boolean throwIfNotAppearAfter3seconds) {

        String currentHandle = driver.getWindowHandle();
        logger.info("currentHandle : {}", currentHandle);

        try {
            new WebDriverWait(driver, 3L)
                .pollingEvery(Duration.ofSeconds(1L))
                .until(driver -> driver.getWindowHandles().size() > 1);
        } catch (TimeoutException e) {
            logger.info("새창이 뜨지 않았습니다.");
            if (throwIfNotAppearAfter3seconds) throw e;
        }

        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(currentHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }

    }


    /**
     * 메인페이지 이외의 팝업페이지를 모두 닫는다.
     */
    public void closeOtherWindows() {
        String currentHandle = driver.getWindowHandles().iterator().next();
        // 현재창
        if (driver.getWindowHandles().size() > 1) {

            for (String handle : driver.getWindowHandles()) {
                if (!handle.equals(currentHandle)) {
                    driver.switchTo().window(handle).close();
                }
            }

            driver.switchTo().window(currentHandle);
        }
    }



    // locator로 element 찾아서 클릭
    public void click(Object position, String... title) {

        if (title.length != 0) {
            logger.info(Arrays.toString(title));
        }

        WebElement el = getWebElement(position);
        el.click();

        if (title.length != 0) {
            logger.info("==========================================");
            logger.info("{} 클릭", title[0]);
            logger.info("==========================================");
        }
    }



    public void clickByJavascriptExecutor(WebElement element, String... title) {
        String script = "arguments[0].click();";

        try {
            executeJavascript(script, element);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (title.length != 0) {
            logger.info("==========================================");
            logger.info("{} 클릭", title[0]);
            logger.info("==========================================");
        }
    }



    public void sendKeys1_check(By by, String value, String... title) throws Exception { // done
        WebElement $input = wait.until(ExpectedConditions.elementToBeClickable(by));

        this.click($input);
        $input.clear();

        WaitUtil.loading(1);
        $input.sendKeys(value);

        String inputText = getInputText($input);

        // 입력 검증
        printLogAndCompare((title.length > 0 ? title[0] : ""), value, inputText);
    }



    public void sendKeys2_check(Object position, String value, String... title) throws Exception { // done

        WebElement $element = getWebElement(position);
        this.click($element);

        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("arguments[0].value='" + value + "';", $element);

        String inputText = getInputText($element);

        // 입력 검증
        printLogAndCompare((title.length > 0 ? title[0] : ""), value, inputText);
    }



    public void sendKeys3_check(Object position, String value, String... title)  throws Exception {  // done

        WebElement $element = getWebElement(position);
        this.click($element);

        WaitUtil.loading(1);

        // 커서를 오른쪽으로 이동 todo END 키로 이동하기 시도하자.
        new Actions(driver).keyDown(Keys.CONTROL).sendKeys($element, Keys.ARROW_RIGHT).perform();

        // 기존에 입력된 찾기. (input 태그가 아닌 경우에는 하위 태그 중 input 태그를 찾아서 value 값을 가져온다.)
        WebElement $input = $element.getTagName().equals("input") ? $element : $element.findElement(By.tagName("input"));
        String alreadyEntered = $input.getAttribute("value");

        // 기존에 입력된 값 지우기
        for (int i = 0; i < alreadyEntered.length(); i++) {
            $element.sendKeys(Keys.BACK_SPACE);
        }

        // 3회 시도 (한번에 성공하지 못하는 경우가 있음)
        int attempts = 3;

        do {
            $element.sendKeys(value);
            attempts--;
            WaitUtil.loading(1);

            logger.info("input value 현재값 - {} ", $element.getAttribute("value"));

            if (!$element.getAttribute("value").contains(value) && attempts > 0) {
                logger.info("남은 시도 횟수 - {} ", attempts);
            }

        } while ($element.getAttribute("value").isEmpty() && attempts > 0);


        // 입력 검증
        printLogAndCompare((title.length > 0 ? title[0] : ""), value, getInputText($element));
    }



    /**
     * input에 text 입력
     *
     * @param $input input element
     * @param text   입력할 텍스트
     * @return 실제 input에 입력된 value
     * @throws Exception
     */
    public String sendKeys4_check(WebElement $input, String text, String... title) throws Exception { // done

        String actualValue = sendKeysAndGetInputString(text, $input).replaceAll("[^0-9]", "");

        // 입력 검증
        printLogAndCompare(title.length > 0 ? title[0] : "", text, actualValue);

        return actualValue;
    }



    /**
     * input에 text 입력
     *
     * @param by   by
     * @param text 입력할 텍스트
     * @return 실제 input에 입력된 value
     * @throws Exception
     */
    public String sendKeys4_check(By by, String text, String... title) throws Exception { // done
        String actualValue = "";

        WebElement element = new WebDriverWait(driver, 5L).until(ExpectedConditions.presenceOfElementLocated(by)); // driver.findElement(by);
        actualValue = sendKeys4_check(element, text, (title.length > 0 ? title[0] : ""));

        return actualValue;
    }


    public String sendKeys4Birthday_check(Object position, String text, String... title) throws Exception { // done

        String actualValue = "";
        WebElement $input = this.getWebElement(position);

        actualValue = sendKeysAndGetInputString(text, $input);

        // 입력 검증
        String textForComparing = text.replaceAll("\\.", "");
        String actualValueForComparing = actualValue.replaceAll("\\.", "");
        printLogAndCompare(title.length > 0 ? title[0] : "", textForComparing, actualValueForComparing);

        return actualValue;
    }


    public String sendKeys4MoneyAmount_check(Object position, String text, String... title) throws Exception { // done

        String actualValue = "";
        WebElement $input = this.getWebElement(position);

        actualValue = sendKeysAndGetInputString(text, $input);

        // 입력 검증
        String textForComparing = text.replaceAll(",", "");
        String actualValueForComparing = actualValue.replaceAll(",", "");
        printLogAndCompare(title.length > 0 ? title[0] : "", textForComparing, actualValueForComparing);

        return actualValue;
    }



    private String sendKeysAndGetInputString(String text, WebElement $input) throws CommonCrawlerException {
        String actualValue;
        if ("input".equals($input.getTagName())) {
            //text 입력
            waitElementToBeClickable($input);
            $input.click();

            $input.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
            $input.sendKeys(text);

            //실제 input에 입력된 value 읽어오기
            actualValue = getInputText($input);
            logger.info("actual input value :: {}", actualValue);

        } else {
            logger.error("파라미터로 input element를 전달해주세요");
            throw new CommonCrawlerException(ExceptionEnum.ERR_BY_ELEMENT);
        }
        return actualValue;
    }


    public void checkRadioBtn(Object position, String... title) throws Exception {

        WebElement $element = getWebElement(position);
        this.click($element);

        String checked = $element.getAttribute("checked");

        // 입력 검증
        printLogAndCompare((title.length > 0 ? title[0] : ""), "true", checked);
    }


    /**
     * select box에서 option 선택
     * : click으로 선택 (selenium 에서 제공하는 select 클래스를 사용하지 않음. selenium 의 select가 잘 동작하지 않는 경우를 위한 method)
     *
     * @param position  by 또는 WebElement
     * @param optionTextOrValue     선택의 기준이 되는 option의 text 또는 value
     * @return 실제 input에 입력된 value
     * @throws Exception
     */
    public String selectOptionByClick(Object position, String optionTextOrValue, String... title) throws Exception { // done (contains)

        String selected;
        String selectCriteria = "";
        boolean matchedWithText = false;
        boolean matchedWithValue = false;

        WebElement $element = getWebElement(position);
        this.click($element);

        List<WebElement> options = $element.findElements(By.tagName("option"));
        for (WebElement option : options) {

            String opText = option.getText();
            String opValue = option.getAttribute("value");

            logger.info("옵션 텍스트 : " + opText);
            logger.info("옵션 값 : " + opValue);

            matchedWithText = opText.contains(optionTextOrValue);
            matchedWithValue = opValue.contains(optionTextOrValue);

            if (matchedWithText || matchedWithValue) {
                this.click(option);
                break;
            }
        }

        if (matchedWithText) {
            selected = getSelectedText($element);
            selectCriteria = "text";
        } else if (matchedWithValue) {
            selected = getSelectedValue($element);
            selectCriteria = "value";
        } else {
            throw new Exception("해당 " + selectCriteria + "=" + optionTextOrValue + " 을/를 찾을수 없습니다.");
        }

        // 입력 검증 (contains)
        printLogAndCompareByContainLogic((title.length > 0 ? title[0] : ""), optionTextOrValue, selected);

        return selected;
    }


    /**
     * selectbox text로 option 선택
     *
     * @param $select select 객체
     * @param text    선택할 text
     * @param title    선택할 항목명
     * @return 선택된 option의 text
     */
    public String selectByText_check(Select $select, String text, String... title) throws Exception { // done

        text = text.replaceAll("\n", "");
        // option 선택
        $select.selectByVisibleText(text);
        WaitUtil.waitFor(1);

        // 실제로 선택된 option의 text 값 읽어오기
        String selectedText = getSelectedText($select.getWrappedElement()).trim();

        // 입력 검증
        printLogAndCompare((title.length > 0 ? title[0] : ""), text, selectedText);

        return selectedText;
    }

    /**
     * selectbox text로 option 선택
     *
     * @param $element select element
     * @param text     선택할 text
     * @param title    선택할 항목명
     * @return 선택된 option의 text
     * @throws Exception
     */
    public String selectByText_check(WebElement $element, String text, String... title) throws Exception { // done
        String selectedText = "";

        Select $select = new Select($element);
        selectedText = selectByText_check($select, text, (title.length > 0 ? title[0] : ""));

        return selectedText;
    }

    /**
     * selectbox text로 option 선택
     *
     * @param by   by
     * @param text 선택할 text
     * @param title    선택할 항목명
     * @throws Exception
     */
    public String selectByText_check(By by, String text, String... title) throws Exception { // done
        String selectedText = "";

        WebElement $element = driver.findElement(by);
        selectedText = selectByText_check($element, text, (title.length > 0 ? title[0] : ""));

        return selectedText;
    }



    /**
     * selectbox value로 option 선택
     *
     * @param $select select 객체
     * @param value   선택할 value
     * @return 선택된 option의 value
     */
    public String selectByValue_check(Select $select, String value, String... title) throws Exception { // done

        //option 선택
        $select.selectByValue(value);

        //실제 선택된 option의 value값 읽어오기
        String selectedValue = getSelectedValue($select.getWrappedElement());

        // 입력 검증
        printLogAndCompare(title.length > 0 ? title[0] : "", value, selectedValue);

        return selectedValue;
    }


    /**
     * selectbox value로 option 선택
     *
     * @param $element select element
     * @param value    선택할 value
     * @return 선택된 option의 value
     */
    public String selectByValue_check(WebElement $element, String value, String... title) throws Exception { // done
        String selectedValue = "";

        Select $select = new Select($element);
        selectedValue = selectByValue_check($select, value, title.length > 0 ? title[0] : "");

        return selectedValue;
    }

    /**
     * selectbox value로 option 선택
     *
     * @param by    by
     * @param value 선택할 value
     */
    public String selectByValue_check(By by, String value, String... title) throws Exception { // done
        String selectedValue = "";

        WebElement $element = driver.findElement(by);
        selectedValue = selectByValue_check( $element, value, (title.length > 0 ? title[0] : ""));

        return selectedValue;
    }

    public String selectOptionContainsValue(WebElement $element, String value, String... title) throws Exception { // done (contains)
        String selectedValue = "";

        List<WebElement> $options = $element.findElements(By.tagName("option"));

        for (WebElement $option : $options) {
            String optionValue = $option.getAttribute("value");

            if (optionValue.contains(value)) {
                waitElementToBeClickable($option).click();
                break;
            }
        }

        // 입력 검증 (contains)
        selectedValue = getSelectedValue($element);
        printLogAndCompareByContainLogic((title.length > 0 ? title[0] : ""), value, selectedValue);

        return selectedValue;
    }

    public String selectOptionContainsValue(By by, String value, String... title) throws Exception { // done (contains)
        String selectedValue = "";

        WebElement $element = driver.findElement(by);
        selectedValue = selectOptionContainsValue($element, value);

        return selectedValue;
    }

    /*
     * select 태그에서 해당 text를 포함하고 있는 option 태그를 클릭한다.
     *  => 매개변수로 select 객체가 By 타입으로 전달받은 경우
     * @param1 element : 선택하고자 하는 select 태그값
     * @param2 text : 선택하고자 하는 option의 text값
     */
    public String selectOptionContainsText(WebElement $element, String value, String... title) throws Exception { // done

        String selectedText = "";
        List<WebElement> $options = $element.findElements(By.tagName("option"));

        for (WebElement $option : $options) {
            String optionText = $option.getText();

            if (optionText.contains(value)) {
                waitElementToBeClickable($option).click();
                break;
            }
        }

        // 입력 검증 (contains)
        selectedText = getSelectedText($element);
        printLogAndCompareByContainLogic(title.length > 0 ? title[0] : "", value, selectedText);

        return selectedText;
    }



    /**
     * selectbox 에서 선택된 option의 text 가져오기
     *
     * @param $select select element
     * @return 선택된 option의 text
     * @throws Exception
     */
    private String getSelectedText(WebElement $select) {
        String selectedText;

        String script = "return $(arguments[0]).find('option:selected').text();";
        selectedText = String.valueOf(executeJavascript(script, $select));

        return selectedText;
    }



    /**
     * selectbox 에서 선택된 option의 value 가져오기
     *
     * @param $select select element
     * @return 선택된 option의 value
     * @throws Exception
     */
    private String getSelectedValue(WebElement $select) {
        String selectedValue;

        String script = "return $(arguments[0]).find('option:selected').val();";
        selectedValue = String.valueOf(this.executeJavascript(script, $select));

        return selectedValue;
    }


    /**
     * input에 입력된 text 가져오기
     * @param $input
     * @return 입력된 text
     */
    private String getInputText(WebElement $input) {
        String enteredText;
        String script = "return $(arguments[0]).val();";
        enteredText = (String) this.executeJavascript(script, $input);
        return enteredText;
    }



    //해당 element가 보이게 스크롤 이동
    public void moveToElementByJavascriptExecutor(Object position) {
        WebElement element = getWebElement(position);
        String script = "arguments[0].scrollIntoView(true);";
        this.executeJavascript(script, element);
    }



    //javascript 문법 실행
    public Object executeJavascript(String script, Object... element) {
        return ((JavascriptExecutor) driver).executeScript(script, element);
    }



    public Object executeJavascript(String script) {
        Object result = null;

        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            result = js.executeScript(script);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return result;
    }



    // 명시적 대기 : 클릭 가능해질 때까지 By
    public WebElement waitElementToBeClickable(By by) {
        return wait.until(ExpectedConditions.elementToBeClickable(by));
    }

    // 명시적 대기 : 클릭 가능해질 때까지 WebElement
    public WebElement waitElementToBeClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    // 명시적 대기 : 보일 때까지 WebElement
    public WebElement waitVisibilityOf(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    // 명시적 대기 : 보일 때까지 By
    public WebElement waitVisibilityOfElementLocated(By by) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    // 명시적 대기 : 존재 By
    public WebElement waitPresenceOfElementLocated(By by) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    // 명시적 대기 : element들 보일 때까지 By
    public List<WebElement> waitVisibilityOfAllElementsLocatedBy(By by) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
    }

    // 명시적 대기 : element들 보일 때까지 WebElement
    public List<WebElement> waitVisibilityOfAllElements(List<WebElement> elements) {
        return wait.until(ExpectedConditions.visibilityOfAllElements(elements));
    }

    // 명시적 대기 : element들 존재할 때까지 By
    public List<WebElement> waitPesenceOfAllElementsLocatedBy(By by) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }

    // 명시적 대기 : 안보일 때까지 By
    public boolean invisibilityOfElementLocated(By by) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
    }






    /**
     * 로그 출력 및 비교
     *
     * ex)
     * params
     * (title = 생년월일, expected = 961109, actual = 991212)
     *
     * [출력결과]
     * ==========================================
     * expected 생년월일 : 961109
     * actual 생년월일 : 991212
     * ==========================================
     *
     * @param title 항목명
     * @param expected 세팅할 값
     * @param actual 실제 세팅된 값
     * @throws Exception
     */
    protected void printLogAndCompare(String title, String expected, String actual) throws Exception {
        logger.info("==========================================");
        logger.info("expected {} : {}", title, expected);
        logger.info("actual   {} : {}", title, actual);
        logger.info("==========================================");

        if (expected.equals(actual)) {
            logger.info("result : {} 일치", title);
            logger.info("==========================================");

        } else {
            throw new Exception(title + " 값 불일치");
        }
    }

    protected void printLogAndCompareByContainLogic(String title, String expected, String actual) throws Exception {
        logger.info("==========================================");
        logger.info("expected {} : {}", title, expected);
        logger.info("actual   {} : {}", title, actual);
        logger.info("==========================================");

        if (actual.contains(expected)) {
            logger.info("result : {} (contains 비교결과) 일치", title);
            logger.info("==========================================");

        } else {
            throw new Exception(title + " 값 불일치");
        }
    }

}
