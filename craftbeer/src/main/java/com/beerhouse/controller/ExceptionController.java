package com.beerhouse.controller;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.com.beerhouse.exception.ValidationException;
import br.com.beerhouse.exception.DetailsError;

@ControllerAdvice
@RestController
public class ExceptionController extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ValidationException.class)
	public final ResponseEntity<DetailsError> handleUserNotFoundException(ValidationException ex,
			WebRequest request) {
		DetailsError detErros = new DetailsError(new Date(), ex.getMessage(), 
				request.getDescription(false));
		return new ResponseEntity<DetailsError>(detErros, HttpStatus.BAD_REQUEST);
	}
}
