package no.nav.foreldrepenger.mottak.oversikt;

import java.util.List;

import no.nav.foreldrepenger.common.innsending.mappers.AktørIdTilFnrConverter;

public interface Oversikt extends AktørIdTilFnrConverter {

    List<EnkeltArbeidsforhold> hentArbeidsforhold();

    PersonDto personinfo(Ytelse ytelse);
}
