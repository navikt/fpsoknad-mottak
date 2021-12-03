package no.nav.foreldrepenger.mottak.innsyn.fpinfoV2;

import java.util.Set;

record EsSak(Saksnummer saksnummer,
             boolean sakAvsluttet,
             Familiehendelse familiehendelse,
             EsVedtak gjeldendeVedtak,
             EsÅpenBehandling åpenBehandling,
             Set<AktørId> barn) implements Sak {
}
