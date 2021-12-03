package no.nav.foreldrepenger.mottak.innsyn.fpinfoV2;

import com.fasterxml.jackson.annotation.JsonGetter;

import java.util.Set;

interface Sak {

    Saksnummer saksnummer();

    Familiehendelse familiehendelse();

    @SuppressWarnings("unused")
    boolean sakAvsluttet();

    Set<AktørId> barn();

    @SuppressWarnings("unused")
    @JsonGetter
    default boolean gjelderAdopsjon() {
        return familiehendelse().fødselsdato() == null && familiehendelse().termindato() == null;
    }
}
