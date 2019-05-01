package com.restAPI.moneytransferservice.factory;

import com.restAPI.moneytransferservice.dao.ICustomerAccount;

public abstract class DAOFactory {

	public static final int H2 = 1;

	public abstract ICustomerAccount getCustomerAccountDAO();

	public abstract void populateTestData();

	public static DAOFactory getDAOFactory(int factoryCode) {

		switch (factoryCode) {
		case H2:
			return new H2DAOFactory();
		default:
			return new H2DAOFactory();
		}
	}
}
