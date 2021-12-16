package no.nav.foreldrepenger.mottak.innsyn.fpinfov2;

import java.util.Set;

record FpÅpenBehandling(BehandlingTilstand tilstand,
                        Set<Søknadsperiode> søknadsperioder) { }
