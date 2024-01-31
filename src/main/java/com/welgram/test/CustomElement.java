package com.welgram.test;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;


/**
 * 해당 객체의 직접 생성을 막고, 자식클래스에서 생성하도록 강제하기 위해
 * 추상 클래스로 선언한다.
 */
@Getter
@NoArgsConstructor
public abstract class CustomElement implements SearchContext {
    protected WebElement element;
    protected By position;

    protected CustomElement(By position) {
        this.position = position;
//        this.element = driver.findElement(this.position);
    }

    @Override
    public WebElement findElement(By by) {
        return syncElement().findElement(by);
    }

    @Override
    public List<WebElement> findElements(By by) {
        return syncElement().findElements(by);
    }

    /**
     * StaleReferenceException 예외를 피하기 위해
     * 항상 DOM으로부터 가장 최신의 element를 읽어오게 한다.
     * @return
     */
    protected WebElement syncElement() {
//        this.element = driver.findElement(this.position);
        return this.element;
    }

    public CustomElement getParent()                   { return null; }
    public List<CustomElement> getAncestor()           { return null; }
    public List<CustomElement> getPreviousSibling()    { return null; }
    public List<CustomElement> getNextSibling()        { return null; }
    public List<CustomElement> getSibling()            { return null; }
    public List<CustomElement> getChildren()           { return null; }
    public List<CustomElement> getDescendant()         { return null; }
}
