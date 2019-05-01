package com.restAPI.moneytransferservicetest.dao;

import static junit.framework.TestCase.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.restAPI.moneytransferservice.dao.ICustomerAccount;
import com.restAPI.moneytransferservice.exception.MoneyTransferServiceException;
import com.restAPI.moneytransferservice.factory.DAOFactory;
import com.restAPI.moneytransferservice.factory.H2DAOFactory;
import com.restAPI.moneytransferservice.model.Account;
import com.restAPI.moneytransferservice.model.AccountTransferObject;

public class MultiThreadedBehaviourTest {

	private static Logger log = Logger.getLogger(CustomerAccountDAOTest.class);
	private static final DAOFactory h2DaoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);
	private static final int NUMBER_OF_THREADS = 100;

	@BeforeClass
	public static void setup() {
		h2DaoFactory.populateTestData();
	}

	@Test
	public void testSingleTransferAtATime() throws MoneyTransferServiceException {

		final ICustomerAccount accountDAO = h2DaoFactory.getCustomerAccountDAO();
		BigDecimal amountToBeTransferred = new BigDecimal(50.01234).setScale(4, RoundingMode.HALF_EVEN);
		AccountTransferObject accountTransferObject = new AccountTransferObject(amountToBeTransferred, 3L, 4L);

		accountDAO.transferAccountBalance(accountTransferObject);
		Account senderAccountNumber = accountDAO.getAccountById(3);
		Account beneficiaryAccountNumber = accountDAO.getAccountById(4);

		assertTrue(
				senderAccountNumber.getAccountBalance().compareTo(new BigDecimal(449.9877).setScale(4, RoundingMode.HALF_EVEN)) == 0);
		assertTrue(beneficiaryAccountNumber.getAccountBalance().equals(new BigDecimal(550.0123).setScale(4, RoundingMode.HALF_EVEN)));

	}

	@Test
	public void testMultipleTransfers() throws InterruptedException, MoneyTransferServiceException {
		final ICustomerAccount customerAccountDao = h2DaoFactory.getCustomerAccountDAO();
		final CountDownLatch latch = new CountDownLatch(NUMBER_OF_THREADS);
		for (int i = 0; i < NUMBER_OF_THREADS; i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						AccountTransferObject accountTransferObject = new AccountTransferObject(
								new BigDecimal(2).setScale(4, RoundingMode.HALF_EVEN), 1L, 2L);
						customerAccountDao.transferAccountBalance(accountTransferObject);
					} catch (Exception e) {
						log.error("There was an error while transfering the amount ", e);
					} finally {
						latch.countDown();
					}
				}
			}).start();
		}

		latch.await();
		Account senderAccountNumber = customerAccountDao.getAccountById(1);
		Account beneficiaryAccountNumber = customerAccountDao.getAccountById(2);

		assertTrue(senderAccountNumber.getAccountBalance().equals(new BigDecimal(0).setScale(4, RoundingMode.HALF_EVEN)));
		assertTrue(beneficiaryAccountNumber.getAccountBalance().equals(new BigDecimal(300).setScale(4, RoundingMode.HALF_EVEN)));

	}

	@Test
	public void testDBLockErrorScenario() throws MoneyTransferServiceException, SQLException {
		final String SQL_LOCK_ACC = "SELECT * FROM Account WHERE AccountNumber = 5 FOR UPDATE";
		Connection conn = null;
		PreparedStatement lockStmt = null;
		ResultSet rs = null;
		Account senderAccountNumber = null;

		try {
			conn = H2DAOFactory.getConnection();
			conn.setAutoCommit(false);
			lockStmt = conn.prepareStatement(SQL_LOCK_ACC);
			rs = lockStmt.executeQuery();
			if (rs.next()) {
				senderAccountNumber = new Account(rs.getLong("AccountNumber"), rs.getString("AccountHolderName"),
						rs.getBigDecimal("AccountBalance"));
				if (log.isDebugEnabled())
					log.debug("Locked Account: " + senderAccountNumber);
			}

			if (senderAccountNumber == null) {
				throw new MoneyTransferServiceException("Locking error during test, SQL = " + SQL_LOCK_ACC);
			}
			BigDecimal amountToBeTransferred = new BigDecimal(50).setScale(4, RoundingMode.HALF_EVEN);

			AccountTransferObject transaction = new AccountTransferObject(amountToBeTransferred, 6L, 5L);
			h2DaoFactory.getCustomerAccountDAO().transferAccountBalance(transaction);
			conn.commit();
		} catch (Exception e) {
			log.error("Initiate the rollback, as there was an error");
			try {
				if (conn != null)
					conn.rollback();
			} catch (SQLException re) {
				log.error("Transaction could not be rolled back", re);
			}
		} finally {
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(lockStmt);
		}

		BigDecimal originalBalance = new BigDecimal(500).setScale(4, RoundingMode.HALF_EVEN);
		assertTrue(h2DaoFactory.getCustomerAccountDAO().getAccountById(6).getAccountBalance().equals(originalBalance));
		assertTrue(h2DaoFactory.getCustomerAccountDAO().getAccountById(5).getAccountBalance().equals(originalBalance));
	}

}
