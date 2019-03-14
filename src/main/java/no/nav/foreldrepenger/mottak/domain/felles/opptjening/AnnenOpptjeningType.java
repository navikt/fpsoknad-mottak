package no.nav.foreldrepenger.mottak.domain.felles.opptjening;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static no.nav.foreldrepenger.mottak.util.Versjon.V1;
import static no.nav.foreldrepenger.mottak.util.Versjon.V2;
import static no.nav.foreldrepenger.mottak.util.Versjon.V3;

import java.util.Arrays;
import java.util.List;

import no.nav.foreldrepenger.mottak.util.Versjon;

public enum AnnenOpptjeningType {

    LØNN_UNDER_UTDANNING(V1, V2, V3), ETTERLØNN_ARBEIDSGIVER(V1), MILITÆR_ELLER_SIVILTJENESTE(V1, V2, V3), VENTELØNN(
            V1), VARTPENGER(V1), SLUTTPAKKE(V1), VENTELØNN_VARTPENGER(V2, V3), ETTERLØNN_SLUTTPAKKE(V2, V3);

    public final List<Versjon> versjoner;

    AnnenOpptjeningType(Versjon... versjoner) {
        this(asList(versjoner));
    }

    AnnenOpptjeningType(List<Versjon> versjoner) {
        this.versjoner = versjoner;
    }

    public boolean lovligVerdiForVersjon(Versjon v) {
        return versjoner.contains(v);
    }

    public static String lovligeVerdierForVersjon(Versjon v) {
        return Arrays.stream(values())
                .filter(t -> t.lovligVerdiForVersjon(v))
                .map(AnnenOpptjeningType::name)
                .collect(joining(","));
    }
}
