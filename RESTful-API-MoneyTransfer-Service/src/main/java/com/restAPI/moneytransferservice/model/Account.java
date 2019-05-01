package com.restAPI.moneytransferservice.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Account {

	@JsonIgnore
	private long accountNumber;

	@JsonProperty(required = true)
	private String accountHolderName;

	@JsonProperty(required = true)
	private BigDecimal accountBalance;

	public Account() {
	}

	public Account(String accountHolderName, BigDecimal accountBalance) {
		this.accountHolderName = accountHolderName;
		this.accountBalance = accountBalance;
	}

	public Account(long accountNumber, String accountHolderName, BigDecimal accountBalance) {
		this.accountNumber = accountNumber;
		this.accountHolderName = accountHolderName;
		this.accountBalance = accountBalance;
	}



	public long getAccountNumber() {
		return accountNumber;
	}

	public String getAccountHolderName() {
		return accountHolderName;
	}

	public BigDecimal getAccountBalance() {
		return accountBalance;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountBalance == null) ? 0 : accountBalance.hashCode());
		result = prime * result + ((accountHolderName == null) ? 0 : accountHolderName.hashCode());
		result = prime * result + (int) (accountNumber ^ (accountNumber >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (accountBalance == null) {
			if (other.accountBalance != null)
				return false;
		} else if (!accountBalance.equals(other.accountBalance))
			return false;
		if (accountHolderName == null) {
			if (other.accountHolderName != null)
				return false;
		} else if (!accountHolderName.equals(other.accountHolderName))
			return false;
		if (accountNumber != other.accountNumber)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Account [accountNumber=" + accountNumber + ", accountHolderName=" + accountHolderName
				+ ", accountBalance=" + accountBalance + "]";
	}

}
