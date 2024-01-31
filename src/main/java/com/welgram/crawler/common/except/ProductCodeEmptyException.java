package com.welgram.crawler.common.except;

/**
 * ProductCodeEmptyException
 * (상품코드가 없을 경우)
 * @author gusfo
 *
 */
public class ProductCodeEmptyException extends Exception {

	public ProductCodeEmptyException(String message) {
		super(message);
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1181270500856770830L;

}
