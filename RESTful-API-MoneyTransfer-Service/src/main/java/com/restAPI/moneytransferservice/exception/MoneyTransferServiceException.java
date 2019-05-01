package com.restAPI.moneytransferservice.exception;

public class MoneyTransferServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	public MoneyTransferServiceException(String msg) {
		super(msg);
	}

	public MoneyTransferServiceException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
