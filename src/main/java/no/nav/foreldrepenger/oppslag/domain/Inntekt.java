package no.nav.foreldrepenger.oppslag.domain;

import java.time.LocalDate;

public class Inntekt {

	private double amount;
	private LocalDate from;
	private LocalDate to;

	public Inntekt(LocalDate from, LocalDate to, double amount) {
		this.amount = amount;
		this.from = from;
		this.to = to;
	}

	public double amount() {
		return amount;
	}

	public LocalDate from() {
		return from;
	}

	public LocalDate to() {
		return to;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" + "amount=" + amount + ", from=" + from + ", to=" + to + '}';
	}
}
