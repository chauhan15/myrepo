package com.restAPI.moneytransferservice.main;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import com.restAPI.moneytransferservice.factory.DAOFactory;
import com.restAPI.moneytransferservice.service.AccountService;
import com.restAPI.moneytransferservice.service.TransactionServiceExceptionMapper;

public class MoneyTransferApplication {

	public static void main(String[] args) throws Exception {
		DAOFactory h2DaoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);
		h2DaoFactory.populateTestData();
		startService();
	}

	private static void startService() throws Exception {
		Server server = new Server(8080);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);
		ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/*");
		servletHolder.setInitParameter("jersey.config.server.provider.classnames",
				AccountService.class.getCanonicalName() + "," + TransactionServiceExceptionMapper.class.getCanonicalName());
		try {
			server.start();
			server.join();
		} finally {
			server.destroy();
		}
	}

}
