package no.nav.foreldrepenger.selvbetjening;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.xml.datatype.DatatypeFactory;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.selvbetjening.inntekt.domain.Income;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Inntekt;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Periode;

class InntektMapperTest {

	@Test
	public void allValuesSet() throws Exception {
		Inntekt inntekt = inntekt();
		Income income = InntektMapper.map(inntekt);
		assertEquals(LocalDate.of(2017, 12, 13), income.from());
		assertEquals(LocalDate.of(2017, 12, 14), income.to());
		assertEquals(1234.5, income.amount());
	}

	private Inntekt inntekt() throws Exception {
		Inntekt inntekt = new Inntekt();
		Periode periode = new Periode();
		periode.setStartDato(DatatypeFactory.newInstance().newXMLGregorianCalendar("2017-12-13"));
		periode.setSluttDato(DatatypeFactory.newInstance().newXMLGregorianCalendar("2017-12-14"));
		inntekt.setOpptjeningsperiode(periode);
		inntekt.setBeloep(BigDecimal.valueOf(1234.5));
		return inntekt;
	}

}
