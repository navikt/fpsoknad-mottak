package no.nav.foreldrepenger.mottak.innsyn;

import no.nav.foreldrepenger.mottak.domain.Søknad;

public interface XMLMapper extends VersjonsBevisst {

    String VERSJONSBEVISST = "versjonbevisst";
    String UKJENT_KODEVERKSVERDI = "-";

    Søknad tilSøknad(String xml);

}
