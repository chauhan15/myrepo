package com.restAPI.moneytransferservice.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import com.restAPI.moneytransferservice.exception.MoneyTransferServiceException;
import com.restAPI.moneytransferservice.factory.H2DAOFactory;
import com.restAPI.moneytransferservice.model.Account;
import com.restAPI.moneytransferservice.model.AccountTransferObject;
import com.restAPI.moneytransferservice.validator.AmountValidator;

public class CustomerAccountDAO implements ICustomerAccount {

	private static Logger log = Logger.getLogger(CustomerAccountDAO.class);
	private final static String SQL_GET_ACC_BY_ID = "SELECT * FROM Account WHERE AccountNumber = ? ";
	private final static String SQL_LOCK_ACC_BY_ID = "SELECT * FROM Account WHERE AccountNumber = ? FOR UPDATE";
	private final static String SQL_CREATE_ACC = "INSERT INTO Account (AccountHolderName, AccountBalance) VALUES (?, ?)";
	private final static String SQL_UPDATE_ACC_BALANCE = "UPDATE Account SET AccountBalance = ? WHERE AccountNumber = ? ";
	private final static String SQL_GET_ALL_ACC = "SELECT * FROM Account";

	public List<Account> getAllAccounts() throws MoneyTransferServiceException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Account> allAccounts = new ArrayList<Account>();
		try {
			conn = H2DAOFactory.getConnection();
			stmt = conn.prepareStatement(SQL_GET_ALL_ACC);
			rs = stmt.executeQuery();
			while (rs.next()) {
				Account acc = new Account(rs.getLong("AccountNumber"), rs.getString("AccountHolderName"),
						rs.getBigDecimal("AccountBalance"));
				if (log.isDebugEnabled())
					log.debug("getAllAccounts(): Get  Account " + acc);
				allAccounts.add(acc);
			}
			return allAccounts;
		} catch (SQLException e) {
			throw new MoneyTransferServiceException("getAccountById(): Error reading account data", e);
		} finally {
			DbUtils.closeQuietly(conn, stmt, rs);
		}
	}

	public Account getAccountById(long accountNumber) throws MoneyTransferServiceException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Account acc = null;
		try {
			conn = H2DAOFactory.getConnection();
			stmt = conn.prepareStatement(SQL_GET_ACC_BY_ID);
			stmt.setLong(1, accountNumber);
			rs = stmt.executeQuery();
			if (rs.next()) {
				acc = new Account(rs.getLong("AccountNumber"), rs.getString("AccountHolderName"), rs.getBigDecimal("AccountBalance"));
				if (log.isDebugEnabled())
					log.debug("Retrieve Account By Id: " + acc);
			}
			return acc;
		} catch (SQLException e) {
			throw new MoneyTransferServiceException("getAccountById(): Error reading account data", e);
		} finally {
			DbUtils.closeQuietly(conn, stmt, rs);
		}

	}

	public long createAccount(Account account) throws MoneyTransferServiceException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet generatedKeys = null;
		try {
			conn = H2DAOFactory.getConnection();
			stmt = conn.prepareStatement(SQL_CREATE_ACC);
			stmt.setString(1, account.getAccountHolderName());
			stmt.setBigDecimal(2, account.getAccountBalance());

			int affectedRows = stmt.executeUpdate();
			if (affectedRows == 0) {
				log.error("createAccount(): Creating account failed, no rows affected.");
				throw new MoneyTransferServiceException("Account Cannot be created");
			}
			generatedKeys = stmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				return generatedKeys.getLong(1);
			} else {
				log.error("Creating account failed, no ID obtained.");
				throw new MoneyTransferServiceException("Account Cannot be created");
			}
		} catch (SQLException e) {
			log.error("Error Inserting Account  " + account);
			throw new MoneyTransferServiceException("createAccount(): Error creating user account " + account, e);
		} finally {
			DbUtils.closeQuietly(conn, stmt, generatedKeys);
		}
	}

	public int updateAccountBalance(long accountNumber, BigDecimal deltaAmount) throws MoneyTransferServiceException {
		Connection conn = null;
		PreparedStatement preparedStatementForLocking = null;
		PreparedStatement preparedStatementForUpdate = null;
		ResultSet rs = null;
		Account targetAccount = null;
		int updateCount = -1;
		try {
			conn = H2DAOFactory.getConnection();
			conn.setAutoCommit(false);
			preparedStatementForLocking = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
			preparedStatementForLocking.setLong(1, accountNumber);
			rs = preparedStatementForLocking.executeQuery();
			if (rs.next()) {
				targetAccount = new Account(rs.getLong("AccountNumber"), rs.getString("AccountHolderName"),
						rs.getBigDecimal("AccountBalance"));
				if (log.isDebugEnabled())
					log.debug("updateAccountBalance from Account: " + targetAccount);
			}

			if (targetAccount == null) {
				throw new MoneyTransferServiceException("updateAccountBalance(): fail to lock account : " + accountNumber);
			}
			BigDecimal accountBalance = targetAccount.getAccountBalance().add(deltaAmount);
			if (accountBalance.compareTo(AmountValidator.validatorAmount) < 0) {
				throw new MoneyTransferServiceException("Not sufficient Fund for account: " + accountNumber);
			}

			preparedStatementForUpdate = conn.prepareStatement(SQL_UPDATE_ACC_BALANCE);
			preparedStatementForUpdate.setBigDecimal(1, accountBalance);
			preparedStatementForUpdate.setLong(2, accountNumber);
			updateCount = preparedStatementForUpdate.executeUpdate();
			conn.commit();
			if (log.isDebugEnabled())
				log.debug("New AccountBalance after Update: " + targetAccount);
			return updateCount;
		} catch (SQLException se) {
			log.error("updateAccountBalance(): User Transaction Failed, rollback initiated for: " + accountNumber, se);
			try {
				if (conn != null)
					conn.rollback();
			} catch (SQLException re) {
				throw new MoneyTransferServiceException("Fail to rollback transaction", re);
			}
		} finally {
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(preparedStatementForLocking);
			DbUtils.closeQuietly(preparedStatementForUpdate);
		}
		return updateCount;
	}

	public int transferAccountBalance(AccountTransferObject accountTransferObject) throws MoneyTransferServiceException {
		int result = -1;
		Connection conn = null;
		PreparedStatement preparedStatementForLocking = null;
		PreparedStatement preparedStatementForUpdate = null;
		ResultSet rs = null;
		Account fromAccount = null;
		Account toAccount = null;

		try {
			conn = H2DAOFactory.getConnection();
			conn.setAutoCommit(false);
			preparedStatementForLocking = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
			preparedStatementForLocking.setLong(1, accountTransferObject.getFromAccountId());
			rs = preparedStatementForLocking.executeQuery();
			if (rs.next()) {
				fromAccount = new Account(rs.getLong("AccountNumber"), rs.getString("AccountHolderName"),
						rs.getBigDecimal("AccountBalance"));
				if (log.isDebugEnabled())
					log.debug("transferAccountBalance from Account: " + fromAccount);
			}
			preparedStatementForLocking = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
			preparedStatementForLocking.setLong(1, accountTransferObject.getToAccountId());
			rs = preparedStatementForLocking.executeQuery();
			if (rs.next()) {
				toAccount = new Account(rs.getLong("AccountNumber"), rs.getString("AccountHolderName"),
						rs.getBigDecimal("AccountBalance"));
				if (log.isDebugEnabled())
					log.debug("transferAccountBalance to Account: " + toAccount);
			}
			if (fromAccount == null || toAccount == null) {
				throw new MoneyTransferServiceException("Fail to lock both accounts for write");
			}
			BigDecimal fromAccountLeftOver = fromAccount.getAccountBalance().subtract(accountTransferObject.getAmount());
			if (fromAccountLeftOver.compareTo(AmountValidator.validatorAmount) < 0) {
				throw new MoneyTransferServiceException("Not enough Fund from source Account ");
			}
			
			preparedStatementForUpdate = conn.prepareStatement(SQL_UPDATE_ACC_BALANCE);
			preparedStatementForUpdate.setBigDecimal(1, fromAccountLeftOver);
			preparedStatementForUpdate.setLong(2, accountTransferObject.getFromAccountId());
			preparedStatementForUpdate.addBatch();
			preparedStatementForUpdate.setBigDecimal(1, toAccount.getAccountBalance().add(accountTransferObject.getAmount()));
			preparedStatementForUpdate.setLong(2, accountTransferObject.getToAccountId());
			preparedStatementForUpdate.addBatch();
			int[] rowsUpdated = preparedStatementForUpdate.executeBatch();
			result = rowsUpdated[0] + rowsUpdated[1];
			if (log.isDebugEnabled()) {
				log.debug("Number of rows updated for the transfer : " + result);
			}
			conn.commit();
		} catch (SQLException se) {
			log.error("transferAccountBalance(): User Transaction Failed, rollback initiated for: " + accountTransferObject,
					se);
			try {
				if (conn != null)
					conn.rollback();
			} catch (SQLException re) {
				throw new MoneyTransferServiceException("Fail to rollback transaction", re);
			}
		} finally {
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(preparedStatementForLocking);
			DbUtils.closeQuietly(preparedStatementForUpdate);
		}
		return result;
	}

}
