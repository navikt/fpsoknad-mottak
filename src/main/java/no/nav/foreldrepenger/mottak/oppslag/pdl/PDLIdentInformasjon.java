package no.nav.foreldrepenger.mottak.oppslag.pdl;

record PDLIdentInformasjon(String ident, PDLIdentInformasjon.PDLIdentGruppe gruppe, boolean historikk) {

    enum PDLIdentGruppe {
        AKTORID,
        FOLKEREGISTERIDENT,
        NPID
    }
}
