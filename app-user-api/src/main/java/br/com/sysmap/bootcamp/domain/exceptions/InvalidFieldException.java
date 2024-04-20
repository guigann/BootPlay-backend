package br.com.sysmap.bootcamp.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidFieldException extends NullPointerException {

	private static final long serialVersionUID = 1L;

	public InvalidFieldException(String exception) {
		super(exception);
	}
}
