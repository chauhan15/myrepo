package com.restAPI.moneytransferservicetest.services;

import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restAPI.moneytransferservice.factory.DAOFactory;
import com.restAPI.moneytransferservice.service.AccountService;
import com.restAPI.moneytransferservice.service.TransactionServiceExceptionMapper;

public abstract class ServiceConnectionTest {
	protected static Server server = null;
	protected static PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

	protected static HttpClient client;
	protected static DAOFactory h2DaoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);
	protected ObjectMapper mapper = new ObjectMapper();
	protected URIBuilder builder = new URIBuilder().setScheme("http").setHost("localhost:8084");

	@BeforeClass
	public static void setup() throws Exception {
		h2DaoFactory.populateTestData();
		startServer();
		connManager.setDefaultMaxPerRoute(100);
		connManager.setMaxTotal(200);
		client = HttpClients.custom().setConnectionManager(connManager).setConnectionManagerShared(true).build();

	}

	@AfterClass
	public static void closeClient() throws Exception {
		HttpClientUtils.closeQuietly(client);
	}

	private static void startServer() throws Exception {
		if (server == null) {
			server = new Server(8084);
			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context.setContextPath("/");
			server.setHandler(context);
			ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/*");
			servletHolder.setInitParameter("jersey.config.server.provider.classnames",
					AccountService.class.getCanonicalName() + "," + TransactionServiceExceptionMapper.class.getCanonicalName()
							);
			server.start();
		}
	}
}
