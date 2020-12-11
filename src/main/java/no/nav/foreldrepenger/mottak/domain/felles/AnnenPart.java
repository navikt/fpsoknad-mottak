package no.nav.foreldrepenger.mottak.domain.felles;

import java.time.LocalDate;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;

public record AnnenPart(Fødselsnummer fnr, AktørId aktørId, Navn navn, LocalDate fødselsdato) {

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AnnenPart other = (AnnenPart) obj;
        if (fnr == null) {
            if (other.fnr != null)
                return false;
        } else if (!fnr.equals(other.fnr))
            return false;
        if (fødselsdato == null) {
            if (other.fødselsdato != null)
                return false;
        } else if (!fødselsdato.equals(other.fødselsdato))
            return false;
        if (navn == null) {
            if (other.navn != null)
                return false;
        } else if (!navn.equals(other.navn))
            return false;
        return true;
    }
}
