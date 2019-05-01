package com.restAPI.moneytransferservice.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.restAPI.moneytransferservice.exception.MoneyTransferServiceException;
import com.restAPI.moneytransferservice.factory.DAOFactory;
import com.restAPI.moneytransferservice.model.Account;
import com.restAPI.moneytransferservice.model.AccountTransferObject;
import com.restAPI.moneytransferservice.validator.AmountValidator;

@Path("/moneyTransferService")
@Produces(MediaType.APPLICATION_JSON)
public class AccountService {

	private final DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);

	private static Logger log = Logger.getLogger(AccountService.class);

	@GET
	@Path("/allAccounts")
	public List<Account> getAllAccounts() throws MoneyTransferServiceException {
		return daoFactory.getCustomerAccountDAO().getAllAccounts();
	}

	@GET
	@Path("/{accountNumber}")
	public Account getAccount(@PathParam("accountNumber") long accountNumber) throws MoneyTransferServiceException {
		return daoFactory.getCustomerAccountDAO().getAccountById(accountNumber);
	}

	@GET
	@Path("/{accountNumber}/checkBalance")
	public BigDecimal getBalance(@PathParam("accountNumber") long accountNumber) throws MoneyTransferServiceException {
		final Account account = daoFactory.getCustomerAccountDAO().getAccountById(accountNumber);

		if (account == null) {
			throw new WebApplicationException("Account not found", Response.Status.NOT_FOUND);
		}
		return account.getAccountBalance();
	}

	@GET
	@Path("/{accountNumber}/depositMoney/{amount}")
	public Account deposit(@PathParam("accountNumber") long accountNumber, @PathParam("amount") BigDecimal amount)
			throws MoneyTransferServiceException {

		if (amount.compareTo(AmountValidator.validatorAmount) <= 0) {
			throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
		}

		daoFactory.getCustomerAccountDAO().updateAccountBalance(accountNumber, amount.setScale(4, RoundingMode.HALF_EVEN));
		return daoFactory.getCustomerAccountDAO().getAccountById(accountNumber);
	}

	@GET
	@Path("/{accountId}/withdrawMoney/{amount}")
	public Account withdraw(@PathParam("accountId") long accountId, @PathParam("amount") BigDecimal amount)
			throws MoneyTransferServiceException {

		if (amount.compareTo(AmountValidator.validatorAmount) <= 0) {
			throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
		}
		BigDecimal delta = amount.negate();
		if (log.isDebugEnabled())
			log.debug("Withdraw service: delta change to account  " + delta + " Account ID = " + accountId);
		daoFactory.getCustomerAccountDAO().updateAccountBalance(accountId, delta.setScale(4, RoundingMode.HALF_EVEN));
		return daoFactory.getCustomerAccountDAO().getAccountById(accountId);
	}

	@GET
	@Path("/transferMoney/{accountIdFrom}/{amount}/{accountIdTo}")
	public String transfer(@PathParam("accountIdFrom") long accountIdFrom, @PathParam("amount") BigDecimal amount,
			@PathParam("accountIdTo") long accountIdTo) throws MoneyTransferServiceException {
		AccountTransferObject accountTransferObject = new AccountTransferObject(amount, accountIdFrom, accountIdTo);

		if (amount.compareTo(AmountValidator.validatorAmount) <= 0) {
			throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
		}
		int result = daoFactory.getCustomerAccountDAO().transferAccountBalance(accountTransferObject);
		if (result > 0) {
			return "Amount Transferred Successfully :)";
		} else {

			return "Unable to transfer the Amount, Please check the logs";
		}

	}

}
