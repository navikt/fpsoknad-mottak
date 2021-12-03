package no.nav.foreldrepenger.mottak.innsyn.fpinfoV2;

import java.util.Set;

record SvpSak(Saksnummer saksnummer,
              boolean sakAvsluttet,
              Familiehendelse familiehendelse,
              Set<AktørId> barn) implements Sak {
}
