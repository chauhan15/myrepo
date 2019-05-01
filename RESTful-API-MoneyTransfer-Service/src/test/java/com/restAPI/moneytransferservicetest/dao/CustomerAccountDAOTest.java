package com.restAPI.moneytransferservicetest.dao;

import static junit.framework.TestCase.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.restAPI.moneytransferservice.exception.MoneyTransferServiceException;
import com.restAPI.moneytransferservice.factory.DAOFactory;
import com.restAPI.moneytransferservice.model.Account;

public class CustomerAccountDAOTest {

	private static final DAOFactory h2DaoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);

	@BeforeClass
	public static void setup() {
		h2DaoFactory.populateTestData();
	}

	@Test
	public void testGetAllAccounts() throws MoneyTransferServiceException {
		List<Account> allAccounts = h2DaoFactory.getCustomerAccountDAO().getAllAccounts();
		assertTrue(allAccounts.size() > 1);
	}


	@Test
	public void testUpdateAccountBalanceWithSufficientFund() throws MoneyTransferServiceException {

		BigDecimal deltaDeposit = new BigDecimal(50).setScale(4, RoundingMode.HALF_EVEN);
		BigDecimal afterDeposit = new BigDecimal(150).setScale(4, RoundingMode.HALF_EVEN);
		int rowsUpdated = h2DaoFactory.getCustomerAccountDAO().updateAccountBalance(1L, deltaDeposit);
		assertTrue(rowsUpdated == 1);
		assertTrue(h2DaoFactory.getCustomerAccountDAO().getAccountById(1L).getAccountBalance().equals(afterDeposit));
		BigDecimal deltaWithDraw = new BigDecimal(-50).setScale(4, RoundingMode.HALF_EVEN);
		BigDecimal afterWithDraw = new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN);
		int rowsUpdatedW = h2DaoFactory.getCustomerAccountDAO().updateAccountBalance(1L, deltaWithDraw);
		assertTrue(rowsUpdatedW == 1);
		assertTrue(h2DaoFactory.getCustomerAccountDAO().getAccountById(1L).getAccountBalance().equals(afterWithDraw));

	}

	@Test(expected = MoneyTransferServiceException.class)
	public void testUpdateAccountBalanceWithInsufficientFund() throws MoneyTransferServiceException {
		BigDecimal deltaWithDraw = new BigDecimal(-50000).setScale(4, RoundingMode.HALF_EVEN);
		int rowsUpdatedW = h2DaoFactory.getCustomerAccountDAO().updateAccountBalance(1L, deltaWithDraw);
		assertTrue(rowsUpdatedW == 0);

	}

}