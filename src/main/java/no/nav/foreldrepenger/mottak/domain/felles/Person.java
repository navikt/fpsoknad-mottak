package no.nav.foreldrepenger.mottak.domain.felles;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.neovisionaries.i18n.CountryCode;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;

@JsonInclude(NON_NULL)
@Data
public class Person {
    private final Fødselsnummer fnr;
    private final String fornavn;
    private final String mellomnavn;
    private final String etternavn;
    private final String kjønn;
    private final LocalDate fødselsdato;
    private final String målform;
    private final CountryCode land;
    private final Boolean ikkeNordiskEøsLand;
    private final Bankkonto bankkonto;
    private  AktørId aktørId;
}