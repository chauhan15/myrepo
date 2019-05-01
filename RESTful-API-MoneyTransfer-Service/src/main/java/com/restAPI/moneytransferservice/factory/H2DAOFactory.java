package com.restAPI.moneytransferservice.factory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;
import org.h2.tools.RunScript;

import com.restAPI.moneytransferservice.dao.CustomerAccountDAO;
import com.restAPI.moneytransferservice.dao.ICustomerAccount;
import com.restAPI.moneytransferservice.loader.MoneyTransferServiceLoader;

public class H2DAOFactory extends DAOFactory {
	private static final String h2_driver = MoneyTransferServiceLoader.getStringProperty("h2_driver");
	private static final String h2_connection_url = MoneyTransferServiceLoader.getStringProperty("h2_connection_url");
	private static final String h2_user = MoneyTransferServiceLoader.getStringProperty("h2_user");
	private static final String h2_password = MoneyTransferServiceLoader.getStringProperty("h2_password");
	private final ICustomerAccount customerAccountDao = new CustomerAccountDAO();

	H2DAOFactory() {
		DbUtils.loadDriver(h2_driver);
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(h2_connection_url, h2_user, h2_password);

	}

	public ICustomerAccount getCustomerAccountDAO() {
		return customerAccountDao;
	}

	@Override
	public void populateTestData() {
		Connection conn = null;
		try {
			conn = H2DAOFactory.getConnection();
			RunScript.execute(conn, new FileReader("src/test/resources/account_data.sql"));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}

}
