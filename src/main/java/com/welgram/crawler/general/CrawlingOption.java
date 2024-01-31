package com.welgram.crawler.general;

import com.welgram.crawler.common.ext.CrawlingVpn;
import com.welgram.crawler.common.ext.NonVpn;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrawlingOption {

    public boolean isTouchEnPc() {
        return touchEnPc;
    }

    public void setTouchEnPc(boolean touchEnPc) {
        this.touchEnPc = touchEnPc;
    }


    public enum BrowserType {IE, Chrome, FireFox, MsEdge}

    private BrowserType browserType;            // 브라우저 타입
    private boolean imageLoad = true;           // 크롤링시 이미지 로딩옵션 사용여부
    private boolean userData = false;           // 사용자별 확장프로그램 로딩이 필요할경우사용
    private boolean delayTime = false;          // 크롤링 딜레이 타임사용
    private boolean iniSafe = false;            // INISAFE CrossWeb EX 확장도구
    private boolean touchEnPc = false;          // TouchEn PC보안확장도구
    private boolean headless = false;           // 브라우저 사용 X
    private boolean alertCheck = false;         // 시작하자마자 Alert 가 발생하는 사이트 있음 (현대해상)
    private boolean mobile = false;             // 모바일 전용상품인경우 처리
    private boolean scale = true;               // 기본적으로 스케일 0.7 세팅
    private CrawlingVpn vpn;                    // Vpn 사용옵션

    @Deprecated
    private boolean randomUserAgent = false;



    // VPN option
    public CrawlingOption() {
        this.vpn = new NonVpn(); // 기본으로 VPN 사용하지 않음.
    }



    public BrowserType getBrowserType() {
        return browserType;
    }

    public void setBrowserType(BrowserType browserType) {
        this.browserType = browserType;
    }

    public boolean isImageLoad() {
        return imageLoad;
    }

    public void setImageLoad(boolean imageLoad) {
        this.imageLoad = imageLoad;
    }

    public boolean isUserData() {
        return userData;
    }

    public void setUserData(boolean userData) {
        this.userData = userData;
    }

    public CrawlingVpn getVpn() {
        return vpn;
    }

    public void setVpn(CrawlingVpn vpn) {
        this.vpn = vpn;
    }

    public boolean isDelayTime() {
        return delayTime;
    }

    public void setDelayTime(boolean delayTime) {
        this.delayTime = delayTime;
    }

    public boolean isIniSafe() {
        return iniSafe;
    }

    public void setIniSafe(boolean iniSafe) {
        this.iniSafe = iniSafe;
    }

    public boolean isHeadless() {
        return headless;
    }

    public void setHeadless(boolean headless) {
        this.headless = headless;
    }

    public boolean isAlertCheck() {
        return alertCheck;
    }

    public void setAlertCheck(boolean alertCheck) {
        this.alertCheck = alertCheck;
    }

    public boolean isMobile() {
        return mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    @Deprecated
    public boolean isRandomUserAgent() {
        return randomUserAgent;
    }

    @Deprecated
    public void setRandomUserAgent(boolean randomUserAgent) {
        this.randomUserAgent = randomUserAgent;
    }

    public boolean isScale() {
        return scale;
    }

    public void setScale(boolean scale) {
        this.scale = scale;
    }
}
