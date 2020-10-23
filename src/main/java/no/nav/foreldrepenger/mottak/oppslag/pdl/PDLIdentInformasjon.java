package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = ANY)
record PDLIdentInformasjon(String ident, PDLIdentInformasjon.PDLIdentGruppe gruppe, boolean historikk) {

    enum PDLIdentGruppe {
        AKTORID,
        FOLKEREGISTERIDENT,
        NPID
    }

}
