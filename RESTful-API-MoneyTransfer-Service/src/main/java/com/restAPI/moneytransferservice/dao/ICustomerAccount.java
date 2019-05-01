package com.restAPI.moneytransferservice.dao;

import java.math.BigDecimal;
import java.util.List;

import com.restAPI.moneytransferservice.exception.MoneyTransferServiceException;
import com.restAPI.moneytransferservice.model.Account;
import com.restAPI.moneytransferservice.model.AccountTransferObject;

public interface ICustomerAccount {

	List<Account> getAllAccounts() throws MoneyTransferServiceException;

	Account getAccountById(long accountId) throws MoneyTransferServiceException;

	int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws MoneyTransferServiceException;

	int transferAccountBalance(AccountTransferObject accountTransferObject) throws MoneyTransferServiceException;
}
