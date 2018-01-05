package no.nav.foreldrepenger.selvbetjening.domain.exceptions;

public class NotFoundException extends RuntimeException {

	public NotFoundException() {
		this(null, null);
	}

	public NotFoundException(String msg) {
		this(msg, null);
	}

	public NotFoundException(Throwable cause) {
		this(null, cause);
	}

	public NotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
