package no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.dto;


import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Orgnummer;

public record ArbeidsgiverDTO(ArbeidsgiverType type,
                              Orgnummer organisasjonsnummer,
                              Fødselsnummer offentligIdent){
}
