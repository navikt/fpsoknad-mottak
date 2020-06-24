package no.nav.foreldrepenger.mottak.domain.felles.opptjening;

import static no.nav.foreldrepenger.mottak.util.Versjon.V1;
import static no.nav.foreldrepenger.mottak.util.Versjon.V2;
import static no.nav.foreldrepenger.mottak.util.Versjon.V3;

import no.nav.foreldrepenger.mottak.util.Versjon;

public enum AnnenOpptjeningType {
    LØNN_UNDER_UTDANNING(V1, V2, V3),
    ETTERLØNN_ARBEIDSGIVER(V1),
    MILITÆR_ELLER_SIVILTJENESTE(V1, V2, V3),
    VENTELØNN(V1),
    VARTPENGER(V1),
    SLUTTPAKKE(V1),
    VENTELØNN_VARTPENGER(V2, V3),
    ETTERLØNN_SLUTTPAKKE(V2, V3);

    AnnenOpptjeningType(Versjon... versjoner) {

    }
}
