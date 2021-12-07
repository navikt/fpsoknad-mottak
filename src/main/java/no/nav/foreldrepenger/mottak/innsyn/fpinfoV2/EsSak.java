package no.nav.foreldrepenger.mottak.innsyn.fpinfoV2;

import java.util.Set;

record EsSak(Saksnummer saksnummer,
             Familiehendelse familiehendelse,
             EsVedtak gjeldendeVedtak,
             EsÅpenBehandling åpenBehandling,
             Set<PersonDetaljer> barn,
             boolean sakAvsluttet,
             boolean gjelderAdopsjon) implements Sak {
}
