package no.nav.foreldrepenger.mottak.innsyn.fpinfoV2;

import java.util.Set;

interface Sak {

    Saksnummer saksnummer();

    Familiehendelse familiehendelse();

    Set<PersonDetaljer> barn();

    boolean gjelderAdopsjon();

    boolean sakAvsluttet();

}
