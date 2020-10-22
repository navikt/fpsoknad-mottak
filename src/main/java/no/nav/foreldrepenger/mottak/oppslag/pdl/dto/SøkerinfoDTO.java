package no.nav.foreldrepenger.mottak.oppslag.pdl.dto;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.mottak.oppslag.arbeidsforhold.EnkeltArbeidsforhold;

@JsonAutoDetect(fieldVisibility = ANY)
public record SøkerinfoDTO(SøkerDTO søker, List<EnkeltArbeidsforhold> arbeidsforhold) {
}
