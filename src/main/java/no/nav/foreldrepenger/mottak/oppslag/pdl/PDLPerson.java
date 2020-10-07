package no.nav.foreldrepenger.mottak.oppslag.pdl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record PDLPerson(List<PDLNavn> navn) {
}
