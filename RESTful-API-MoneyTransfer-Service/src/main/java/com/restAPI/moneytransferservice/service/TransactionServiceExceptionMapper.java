package com.restAPI.moneytransferservice.service;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.restAPI.moneytransferservice.exception.MoneyTransferServiceException;
import com.restAPI.moneytransferservice.exception.InvalidResponse;

@Provider
public class TransactionServiceExceptionMapper implements ExceptionMapper<MoneyTransferServiceException> {
	public TransactionServiceExceptionMapper() {
	}

	public Response toResponse(MoneyTransferServiceException daoException) {
		InvalidResponse errorResponse = new InvalidResponse();
		errorResponse.setErrorCode(daoException.getMessage());
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse)
				.type(MediaType.APPLICATION_JSON).build();
	}

}
