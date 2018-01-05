package no.nav.foreldrepenger.selvbetjening.inntekt.domain;

import java.time.LocalDate;

public class Income {

	private double amount;
	private LocalDate from;
	private LocalDate to;

	public Income(LocalDate from, LocalDate to, double amount) {
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
		return "Income{" + "amount=" + amount + ", from=" + from + ", to=" + to + '}';
	}
}
