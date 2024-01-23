package org.project.backend.exception;

import lombok.Getter;
import lombok.Setter;
import org.project.backend.exception.enumeration.ExceptionType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@Setter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

	private ExceptionType type;

	public BadRequestException(String msg) {
		super(msg);
	}

	public BadRequestException() {
		super();
	}

	public BadRequestException(String msg, ExceptionType type) {
		super(msg);

		this.type = type;
	}
}
