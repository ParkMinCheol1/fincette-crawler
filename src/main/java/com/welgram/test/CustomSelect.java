package com.welgram.test;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;


/**
 * 해당 클래스에서는 기존 Select 객체 기능중에서 자주 사용하는 기능들만
 * 다시 정의해서 사용하겠습니다. 기존 Selenium이 제공하는 Select 객체의 기능을
 * 사용하고 싶을 때는 .getSelect()로 해당 객체를 가져다가 사용하시기 바랍니다.
 */
public class CustomSelect extends CustomElement {
    private Select select;

    /**
     * ※※※※※※※※※※※※ [주의사항] ※※※※※※※※※※※※
     * WebElement 타입으로 받는 생성자는 절대로 만들지 않도록 한다.
     *
     * 추후 StaleReferenceException 예외 발생을 막기 위해 객체 생성을 할 때는
     * element를 DOM에서 다시 찾을 수 있도록 자신의 위치 정보를(By 타입) 가지고 있어야 한다.
     * @param position
     */
    public CustomSelect(By position) {
        super(position);
        this.select = getSelect();
    }

    public Select getSelect() {
        return new Select(super.syncElement());
    }

    public static void main(String[] args) {
        By selectPosition = By.tagName("select");
        By optionPosition = By.tagName("option");


        //기대효과 case 1 :: StaleReferenceException 예외 발생 상황을 막을 수 있다.
        CustomSelect $select = new CustomSelect(selectPosition);
        WebElement $option = $select.findElement(optionPosition);
        $option.click();        //이 시점에 select element가 동적 렌더링 됐다고 가정해보자.

        /**
         * 원래라면 해당 코드를 실행했을 때 StaleReferenceException 예외가 발생하게 됨.
         * 하지만 CustomSelect 객체를 만듦으로써 select element를 사용하기 전에 항상
         * 가장 최신의 element 정보를 DOM으로부터 읽어오기 때문에 더이상 예외가 발생하지 않음.
         */
        $select.findElement(By.xpath("./option"));

    }


    /**
     * =================================================================================================================
     * 여기 아래부터는 Selenium에서 제공되는 기존 Select 객체의 기능중에서 자주 사용되는 기능을 재정의합니다.
     * =================================================================================================================
     */

    public void selectByVisibleText(String text) {
        Select $select = getSelect();
        $select.selectByVisibleText(text);
    }
    public void selectByValue(String value) {
        Select $select = getSelect();
        $select.selectByValue(value);
    }
    public List<WebElement> getOptions() {
        return null;
    }

    /**
     * =================================================================================================================
     * 여기 아래부터는 추가적으로 추가된 기능이 정의됩니다.
     * =================================================================================================================
     */
    public void selectContainsText(String text) {}
    public void selectContainsValue(String value) {}
    public WebElement getSelectedOption() { return null; }
    public String getSelectedOptionText() { return null; }
    public String getSelectedOptionValue() { return null; }


}
