package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static no.nav.foreldrepenger.common.util.StringUtil.mask;

record PDLIdentInformasjon(String ident, PDLIdentInformasjon.PDLIdentGruppe gruppe, boolean historikk) {

    enum PDLIdentGruppe {
        AKTORID,
        FOLKEREGISTERIDENT,
        NPID
    }

    @Override
    public String toString() {
        return "PDLIdentInformasjon{" +
            "ident='" + mask(ident) + '\'' +
            ", gruppe=" + gruppe +
            ", historikk=" + historikk +
            '}';
    }
}
