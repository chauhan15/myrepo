package com.restAPI.moneytransferservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class AccountTransferObject {

	@JsonProperty(required = true)
	private BigDecimal amount;

	@JsonProperty(required = true)
	private Long fromAccountId;

	@JsonProperty(required = true)
	private Long toAccountId;

	public AccountTransferObject() {
	}

	public AccountTransferObject(BigDecimal amount, Long fromAccountId, Long toAccountId) {
		this.amount = amount;
		this.fromAccountId = fromAccountId;
		this.toAccountId = toAccountId;
	}


	public BigDecimal getAmount() {
		return amount;
	}

	public Long getFromAccountId() {
		return fromAccountId;
	}

	public Long getToAccountId() {
		return toAccountId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((fromAccountId == null) ? 0 : fromAccountId.hashCode());
		result = prime * result + ((toAccountId == null) ? 0 : toAccountId.hashCode());
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
		AccountTransferObject other = (AccountTransferObject) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (fromAccountId == null) {
			if (other.fromAccountId != null)
				return false;
		} else if (!fromAccountId.equals(other.fromAccountId))
			return false;
		if (toAccountId == null) {
			if (other.toAccountId != null)
				return false;
		} else if (!toAccountId.equals(other.toAccountId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AccountTransferObject [amount=" + amount + ", fromAccountId=" + fromAccountId + ", toAccountId=" + toAccountId
				+ "]";
	}


}
