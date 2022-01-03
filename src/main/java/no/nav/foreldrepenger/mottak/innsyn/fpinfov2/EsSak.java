package no.nav.foreldrepenger.mottak.innsyn.fpinfov2;

import java.util.Set;

public record EsSak(Saksnummer saksnummer,
             Familiehendelse familiehendelse,
             EsVedtak gjeldendeVedtak,
             EsÅpenBehandling åpenBehandling,
             Set<PersonDetaljer> barn,
             boolean sakAvsluttet,
             boolean gjelderAdopsjon) implements Sak {
}
