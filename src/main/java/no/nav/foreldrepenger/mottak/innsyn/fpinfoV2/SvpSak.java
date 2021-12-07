package no.nav.foreldrepenger.mottak.innsyn.fpinfoV2;

import java.util.Set;

record SvpSak(Saksnummer saksnummer,
              Familiehendelse familiehendelse,
              Set<AktørId> barn,
              boolean sakAvsluttet,
              boolean gjelderAdopsjon) implements Sak {
}
