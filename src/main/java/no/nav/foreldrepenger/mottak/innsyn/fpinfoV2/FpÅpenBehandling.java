package no.nav.foreldrepenger.mottak.innsyn.fpinfoV2;

import java.util.Set;

record FpÅpenBehandling(BehandlingTilstand tilstand,
                        Set<Søknadsperiode> søknadsperioder) {

}
