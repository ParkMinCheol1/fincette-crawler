package com.welgram.rtcm.helper;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.rtcm.GeneralCrawler;
import com.welgram.rtcm.util.WaitUtil;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    public final static Logger logger = LoggerFactory.getLogger(GeneralCrawler.class);

    private WebDriver driver;

    private WebDriverWait wait;

    protected final int SLEEP_TIME = 4000;

    protected final int WAIT_TIME = 30; // 30초를 180초로 변경. by 우정 2019.10.18

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



    //해당 element가 보이게 스크롤 이동
    public void moveToElementByJavascriptExecutor(Object position) throws RuntimeException {
        WebElement element = getWebElement(position);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }



    public void waitForLoading(By... by) {
        logger.info("로딩 대기중...");
        WebDriverWait wait_10 = new WebDriverWait(driver, 10);

        try {
            if (by.length > 0) {
                for (By i : by) {
                    wait_10.until(ExpectedConditions.invisibilityOfAllElements(driver.findElements(i)));
                    WaitUtil.loading(1);
                    wait_10.until(ExpectedConditions.invisibilityOfAllElements(driver.findElements(i)));
                }
            } else {
                wait_10.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class*='loading']")));
                wait_10.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class*='Loading']")));
                wait_10.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[id*='loading']")));
                wait_10.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[id*='Loading']")));
            }
        } catch (NoSuchElementException e) {
            logger.info("NoSuchElementException : " + e.getMessage());
        } catch (Exception e) {
            logger.info(e.getMessage());
            logger.info(Arrays.toString(e.getStackTrace()));
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



    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }



    public void setWait(WebDriverWait wait) {
        this.wait = wait;
    }



    // locator로 element 찾아서 클릭
    public void doClick(Object position, String... elementDesc) {

        if (elementDesc.length != 0) {
            logger.info(Arrays.toString(elementDesc));
        }

        WebElement el = getWebElement(position);
        el.click();
    }



    // inputBox에 키 입력
    public void doInputBox(By by, String value) throws InterruptedException {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));

        this.doClick(element);
        // 기존에 쓰여있는 값 지우기.
        element.clear();
        // 아래코드 크롬에서 안먹음
//		Actions builder = new Actions(driver);
//		builder.keyDown(Keys.CONTROL)
//				.sendKeys("a")
//				.keyUp(Keys.CONTROL)
//				.sendKeys(Keys.DELETE)
//				.build().perform();
        WaitUtil.loading(1);
        logger.info("value :: " + value);
        element.sendKeys(value);
    }



    public void doSendKeys(Object position, String value, String... elementDesc) throws InterruptedException {

        logger.debug(
            "{} :: value={}",
            Arrays.toString(elementDesc),
            value);

        WebElement element = getWebElement(position);
        this.doClick(element);

        WaitUtil.loading(1);

        new Actions(driver).keyDown(Keys.CONTROL).sendKeys(element, Keys.ARROW_RIGHT).perform();

        WebElement input = element.getTagName().equals("input") ? element
            : element.findElement(By.cssSelector("input"));
        String inputVal = input.getAttribute("value");
        for (int i = 0; i < inputVal.length(); i++) {
            element.sendKeys(Keys.BACK_SPACE);
        }

        int attempts = 3;

        do {
            element.sendKeys(value);
            attempts--;
            WaitUtil.loading(1);

            logger.info("input value 현재값 - {} ", element.getAttribute("value"));

            if (!element.getAttribute("value").contains(value) && attempts > 0) {
                logger.info("남은 시도 횟수 - {} ", attempts);
            }

        } while (element.getAttribute("value").isEmpty() && attempts > 0);

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



    // sendKeys()를 사용할 수 없을 때 JavascriptExecutor 사용하여 value 값 세팅
    public void doSendKeys2(Object position, String value, String... elementDesc) throws InterruptedException, CommonCrawlerException {

        logger.debug(
            "{} :: value={}",
            Arrays.toString(elementDesc), value);

        WebElement element = getWebElement(position);
        this.doClick(element);

        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("arguments[0].value='" + value + "';", element);
    }


    public void doRadioButton(By by, String value) throws InterruptedException {

        List<WebElement> elements = wait.until(
            ExpectedConditions.presenceOfAllElementsLocatedBy(by));

        WebElement element = elements.get(Integer.parseInt(value));
        this.doClick(element);

        WaitUtil.waitFor("doRadioButton");
    }



    // 라디오버튼의 텍스트 요소를 클릭
    public void doRadioButtonTextClick(By byRadioBtn, String value, By byClickTargetText) throws InterruptedException {

        waitVisibilityOfElementLocated(byRadioBtn);
        List<WebElement> radioBtns = driver.findElements(byRadioBtn);
        for (WebElement radioBtn : radioBtns) {
            if (radioBtn.getAttribute("value").equals(value)) {
                logger.info("라디오 버튼 클릭 : " + radioBtn.getAttribute("value"));

                // 해당 요소의 부모 요소 내 지정한 대상 요소를 클릭
                radioBtn.findElement(By.xpath("parent::*")).findElement(byClickTargetText).click();

                break;
            }
        }
    }



    public void doSelectBox(Object position, String value, boolean... wait) throws Exception {

        WebElement select = getWebElement(position);
        this.doClick(select);

        List<WebElement> options = select.findElements(By.tagName("option"));

        WebElement matchedOption = options.stream().filter(
            option -> {
                String opText = option.getText();
                String opValue = option.getAttribute("value");

                logger.info("옵션 텍스트 : " + opText);
                logger.info("옵션 값 : " + opValue);

                return opText.contains(value) || opValue.contains(value);
            }
        ).findFirst().orElseThrow(
            () -> new Exception("해당 value=" + value + " 을/를 찾을수 없습니다."));

        this.doClick(matchedOption);

        if (wait.length == 0 || wait[0]) {
            WaitUtil.waitFor("doSelectBox");
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



    public void elementWait(String value) {

        try {
            logger.info(value + "element 찾는 중...");
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(value)));
        } catch (Exception e) {
            logger.error(value + " 요소를 찾을 수 없습니다.");
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



    //javascript 문법 실행
    public Object executeJavascript(String script, Object... element) throws Exception {

        return ((JavascriptExecutor) driver).executeScript(script, element);
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



    /**
     * input에 text 입력
     *
     * @param $input input element
     * @param text   입력할 텍스트
     * @return 실제 input에 입력된 value
     * @throws Exception
     */
    public String setTextToInputBox(WebElement $input, String text) throws Exception {

        String script = "return $(arguments[0]).val();";
        String actualValue = "";

        if ("input".equals($input.getTagName())) {
            //text 입력
            waitElementToBeClickable($input);
            $input.click();
            $input.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
            $input.sendKeys(text);

            //실제 input에 입력된 value 읽어오기
            actualValue = String.valueOf(executeJavascript(script, $input));
//            logger.info("actual input value :: {}", actualValue);

        } else {
            logger.error("파라미터로 input element를 전달해주세요");
            throw new CommonCrawlerException(ExceptionEnum.ERR_BY_ELEMENT);
        }

        return actualValue;
    }



    protected void clickByJavascriptExecutor(WebElement element) {

        String script = "arguments[0].click();";

        try {
            executeJavascript(script, element);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * input에 text 입력
     *
     * @param by   by
     * @param text 입력할 텍스트
     * @return 실제 input에 입력된 value
     * @throws Exception
     */
    public String setTextToInputBox(By by, String text) throws Exception {

        String actualValue = "";

        WebElement element = driver.findElement(by);
        actualValue = setTextToInputBox(element, text);

        return actualValue;
    }



    /**
     * selectbox text로 option 선택
     *
     * @param $select select 객체
     * @param text    선택할 text
     * @return 선택된 option의 text
     */
    public String selectOptionByText(Select $select, String text) throws Exception {

        String selectedText = "";
        String script = "return $(arguments[0]).find('option:selected').text();";

        //option 선택
        $select.selectByVisibleText(text);
        WaitUtil.waitFor(1);

        //실제로 선택된 option의 text 값 읽어오기
        selectedText = String.valueOf(executeJavascript(script, $select));
//        logger.info("selected option text :: {}", selectedText);

        return selectedText;
    }



    /**
     * selectbox text로 option 선택
     *
     * @param $element select element
     * @param text     선택할 text
     * @return 선택된 option의 text
     * @throws Exception
     */
    public String selectOptionByText(WebElement $element, String text) throws Exception {

        String selectedText = "";

        Select $select = new Select($element);
        selectedText = selectOptionByText($select, text);

        return selectedText;
    }



    /**
     * selectbox text로 option 선택
     *
     * @param by   by
     * @param text 선택할 text
     * @throws Exception
     */
    public String selectOptionByText(By by, String text) throws Exception {

        String selectedText = "";

        WebElement $element = driver.findElement(by);
        selectedText = selectOptionByText($element, text);

        return selectedText;
    }



    /**
     * selectbox value로 option 선택
     *
     * @param $select select 객체
     * @param value   선택할 value
     * @return 선택된 option의 value
     */
    public String selectOptionByValue(Select $select, String value) throws Exception {

        String selectedValue = "";
        String script = "return $(arguments[0]).find('option:selected').val();";

        //option 선택
        $select.selectByValue(value);

        //실제 선택된 option의 value값 읽어오기
        selectedValue = String.valueOf(executeJavascript(script, $select));
//        logger.info("selected option value :: {}", selectedValue);

        return selectedValue;
    }



    /**
     * selectbox value로 option 선택
     *
     * @param $element select element
     * @param value    선택할 value
     * @return 선택된 option의 value
     */
    public String selectOptionByValue(WebElement $element, String value) throws Exception {

        String selectedValue = "";

        Select $select = new Select($element);
        selectedValue = selectOptionByValue($select, value);

        return selectedValue;
    }



    /**
     * selectbox value로 option 선택
     *
     * @param by    by
     * @param value 선택할 value
     */
    public String selectOptionByValue(By by, String value) throws Exception {

        String selectedValue = "";

        WebElement $element = driver.findElement(by);
        selectedValue = selectOptionByValue($element, value);

        return selectedValue;
    }



    public String selectOptionContainsValue(WebElement $element, String value) throws Exception {

        String selectedValue = "";

        List<WebElement> $options = $element.findElements(By.tagName("option"));

        for (WebElement $option : $options) {
            String optionValue = $option.getAttribute("value");

            if (optionValue.contains(value)) {
                waitElementToBeClickable($option).click();
                selectedValue = optionValue;
                break;
            }
        }

        logger.info("selected option value :: {}", selectedValue);

        return selectedValue;
    }



    public String selectOptionContainsValue(By by, String value) throws Exception {

        String selectedValue = "";

        WebElement $element = driver.findElement(by);
        selectedValue = selectOptionContainsValue($element, value);

        return selectedValue;
    }



    /**
     * select 태그에서 해당 text를 포함하고 있는 option 태그를 클릭한다.
     *  => 매개변수로 select 객체가 By 타입으로 전달받은 경우
     * @param1 element : 선택하고자 하는 select 태그값
     * @param2 text : 선택하고자 하는 option의 text값
     */
    public String selectOptionContainsText(WebElement $element, String value) throws Exception {

        String selectedValue = "";
        List<WebElement> $options = $element.findElements(By.tagName("option"));

        for (WebElement $option : $options) {
            String optionText = $option.getText();

            if (optionText.contains(value)) {
                waitElementToBeClickable($option).click();
                selectedValue = optionText;
                break;
            }
        }
        logger.info("selected option value :: {}", selectedValue);

        return selectedValue;
    }
}
