package com.welgram.crawler.common.except;

public class CrawlingException extends Exception {

	public CrawlingException(String msg) {
		super(msg);
//		SlackClient.post("insu-crawler",this.getMessage(), "crawler_monitor");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
