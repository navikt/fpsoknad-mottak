package no.nav.foreldrepenger.mottak.innsyn.fpinfov2;

import java.util.Set;

record SvpSak(Saksnummer saksnummer,
              Familiehendelse familiehendelse,
              Set<PersonDetaljer> barn,
              boolean sakAvsluttet,
              boolean gjelderAdopsjon) implements Sak {
}