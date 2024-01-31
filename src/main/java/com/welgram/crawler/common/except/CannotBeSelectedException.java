package com.welgram.crawler.common.except;

/**
 * 선택할 수 없는 예외
 * @author gusfo
 *
 */
public class CannotBeSelectedException extends Exception {

	public CannotBeSelectedException(String msg) {
		super(msg);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
