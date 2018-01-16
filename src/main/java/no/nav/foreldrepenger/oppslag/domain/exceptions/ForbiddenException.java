package no.nav.foreldrepenger.oppslag.domain.exceptions;

public class ForbiddenException extends RuntimeException {

	public ForbiddenException(Throwable cause) {
		this(null,cause);
	}
	
	public ForbiddenException(String msg,Throwable cause) {
		super(msg,cause);
	}

}
