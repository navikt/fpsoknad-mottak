package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = ANY)
public record PDLIdentInformasjon(String ident, PDLIdentGruppe gruppe, boolean historikk) {

}
