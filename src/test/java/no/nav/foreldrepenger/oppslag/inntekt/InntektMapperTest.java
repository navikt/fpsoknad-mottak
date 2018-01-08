package no.nav.foreldrepenger.oppslag.inntekt;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.xml.datatype.*;

import org.junit.jupiter.api.*;

import no.nav.foreldrepenger.oppslag.domain.*;
import no.nav.foreldrepenger.oppslag.inntekt.InntektMapper;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.*;

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
