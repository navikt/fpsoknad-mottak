package no.nav.foreldrepenger.mottak.innsyn.fpinfoV2;

import java.util.Set;

record FpSak(Saksnummer saksnummer,
             boolean sakAvsluttet,
             boolean kanSøkeOmEndring,
             boolean sakTilhørerMor,
             boolean gjelderAdopsjon,
             RettighetType rettighetType,
             AnnenPart annenPart,
             Familiehendelse familiehendelse,
             FpVedtak gjeldendeVedtak,
             FpÅpenBehandling åpenBehandling,
             Set<PersonDetaljer> barn,
             Dekningsgrad dekningsgrad) implements Sak {
}
