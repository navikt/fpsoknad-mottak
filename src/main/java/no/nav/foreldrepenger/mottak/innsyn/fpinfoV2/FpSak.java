package no.nav.foreldrepenger.mottak.innsyn.fpinfoV2;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.Set;

record FpSak(Saksnummer saksnummer,
             boolean sakAvsluttet,
             boolean kanSøkeOmEndring,
             boolean sakTilhørerMor,
             RettighetType rettighetType,
             AnnenPart annenPart,
             Familiehendelse familiehendelse,
             FpVedtak gjeldendeVedtak,
             FpÅpenBehandling åpenBehandling,
             @JsonIgnore LocalDateTime opprettetTidspunkt,
             Set<AktørId> barn,
             Dekningsgrad dekningsgrad) implements Sak {

}
