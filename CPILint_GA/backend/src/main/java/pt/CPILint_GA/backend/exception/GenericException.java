package pt.CPILint_GA.backend.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@Setter
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class GenericException extends RuntimeException{

	private String message;

	public GenericException(String message) {
		super(message);
		this.message = message;
	}

	public GenericException() {
		super("");
	}
}
