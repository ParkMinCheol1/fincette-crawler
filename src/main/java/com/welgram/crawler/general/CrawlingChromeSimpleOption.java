package com.welgram.crawler.general;

/**
 * CrawlingChromeSimpleOption(크롬 간단 옵션)
 * 
 * @author gusfo
 *
 */
public class CrawlingChromeSimpleOption extends CrawlingOption {

	/**
	 * (크롬 간단 옵션 설정)
	 * 
	 * browserType: Chrome, imageLoaad: true, userData: false
	 */
	public CrawlingChromeSimpleOption() {
		super();
		this.setBrowserType(BrowserType.Chrome);
		this.setImageLoad(true);
		this.setUserData(false);
	}

}
