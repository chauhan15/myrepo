package com.restAPI.moneytransferservice.validator;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum AmountValidator {

	AMOUNT;

	public static final BigDecimal validatorAmount = new BigDecimal(0).setScale(4, RoundingMode.HALF_EVEN);

}
