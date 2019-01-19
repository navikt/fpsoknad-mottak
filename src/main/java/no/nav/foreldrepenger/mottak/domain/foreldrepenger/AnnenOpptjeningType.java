package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static java.util.Arrays.asList;
import static no.nav.foreldrepenger.mottak.util.Versjon.V1;
import static no.nav.foreldrepenger.mottak.util.Versjon.V2;

import java.util.List;

import no.nav.foreldrepenger.mottak.util.Versjon;

public enum AnnenOpptjeningType {

    LØNN_UNDER_UTDANNING(V1, V2), ETTERLØNN_ARBEIDSGIVER(V1), MILITÆR_ELLER_SIVILTJENESTE(V1,
            V2), VENTELØNN(V1), VARTPENGER(V1), SLUTTPAKKE(V1), VENTELØNN_VARTPENGER(V2), ETTERLØNN_SLUTTPAKKE(V2);

    public final List<Versjon> versjoner;

    AnnenOpptjeningType(Versjon... versjoner) {
        this(asList(versjoner));
    }

    AnnenOpptjeningType(List<Versjon> versjoner) {
        this.versjoner = versjoner;
    }
}
