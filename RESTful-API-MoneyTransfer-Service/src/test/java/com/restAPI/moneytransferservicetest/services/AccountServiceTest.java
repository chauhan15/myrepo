package com.restAPI.moneytransferservicetest.services;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.restAPI.moneytransferservice.model.Account;

public class AccountServiceTest extends ServiceConnectionTest {

	@Test
	public void testDepositMoney() throws IOException, URISyntaxException {
		URI uri = builder.setPath("/moneyTransferService/1/depositMoney/100").build();
		HttpGet request = new HttpGet(uri);
		request.setHeader("Content-type", "application/json");
		HttpResponse response = client.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		assertTrue(statusCode == 200);
		String jsonString = EntityUtils.toString(response.getEntity());
		Account afterDeposit = mapper.readValue(jsonString, Account.class);
		assertTrue(afterDeposit.getAccountBalance().equals(new BigDecimal(190).setScale(4, RoundingMode.HALF_EVEN)));

	}

	@Test
	public void testWithDrawMoneyWithSufficientFund() throws IOException, URISyntaxException {
		URI uri = builder.setPath("/moneyTransferService/2/withdrawMoney/100").build();
		HttpGet request = new HttpGet(uri);
		request.setHeader("Content-type", "application/json");
		HttpResponse response = client.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		assertTrue(statusCode == 200);
		String jsonString = EntityUtils.toString(response.getEntity());
		Account afterDeposit = mapper.readValue(jsonString, Account.class);
		assertTrue(afterDeposit.getAccountBalance().equals(new BigDecimal(110).setScale(4, RoundingMode.HALF_EVEN)));

	}

	@Test
	public void testWithDrawMoneyWithNonSufficientFund() throws IOException, URISyntaxException {
		URI uri = builder.setPath("/moneyTransferService/2/withdrawMoney/1000.23456").build();
		HttpGet request = new HttpGet(uri);
		request.setHeader("Content-type", "application/json");
		HttpResponse response = client.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		String responseBody = EntityUtils.toString(response.getEntity());
		assertTrue(statusCode == 500);
		assertTrue(responseBody.contains("Not sufficient Fund"));
	}

	@Test
	public void testTransferMoneyWithEnoughFund() throws IOException, URISyntaxException {
		URI uri = builder.setPath("/moneyTransferService/transferMoney/1/10/2").build();
		HttpGet request = new HttpGet(uri);
		request.setHeader("Content-type", "application/json");
		HttpResponse response = client.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		assertTrue(statusCode == 200);
	}

	@Test
	public void testTransferMoneyWithInsufficientFund() throws IOException, URISyntaxException {
		URI uri = builder.setPath("/moneyTransferService/transferMoney/1/1000/2").build();
		HttpGet request = new HttpGet(uri);
		request.setHeader("Content-type", "application/json");

		HttpResponse response = client.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		assertTrue(statusCode == 500);
	}

}
