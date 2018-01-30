package no.nav.foreldrepenger.oppslag.person;

import java.time.LocalDate;
import java.util.List;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.oppslag.domain.Adresse;
import no.nav.foreldrepenger.oppslag.domain.Barn;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.ID;
import no.nav.foreldrepenger.oppslag.domain.Kjonn;
import no.nav.foreldrepenger.oppslag.domain.Navn;
import no.nav.foreldrepenger.oppslag.domain.Person;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Gateadresse;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Personnavn;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.StrukturertAdresse;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;

final class PersonMapper {

    private PersonMapper() {

    }

    static Person map(ID id, no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person, List<Barn> barn) {
        return new no.nav.foreldrepenger.oppslag.domain.Person(id,
                countryCode(person), Kjonn.valueOf(person.getKjoenn().getKjoenn().getValue()),
                name(person.getPersonnavn()),
                address(person.getBostedsadresse().getStrukturertAdresse()), birthDate(person), barn);
    }

    private static Adresse address(StrukturertAdresse adresse) {
        if (adresse instanceof Gateadresse) {
            Gateadresse ga = Gateadresse.class.cast(adresse);
            if (ga.getTilleggsadresseType().equalsIgnoreCase(RequestUtils.OFFISIELL_ADRESSE)) {

                return new Adresse(countryCode(ga.getLandkode().getValue()), ga.getPoststed().getValue(),
                        ga.getGatenavn(),
                        ga.getHusnummer().toString(), ga.getHusbokstav());
            }
            throw new IllegalStateException("Address av type " + ga.getTilleggsadresseType() + " ikke støttet");
        }
        throw new IllegalStateException("Address av type " + adresse.getClass().getSimpleName() + " ikke støttet");
    }

    private static Navn name(Personnavn navn) {
        return new Navn(navn.getFornavn(), navn.getMellomnavn(), navn.getEtternavn());
    }

    private static CountryCode countryCode(no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person) {
        if (person.getStatsborgerskap() != null) {
            return countryCode(person.getStatsborgerskap().getLand().getValue());
        }
        return CountryCode.NO;
    }

    private static CountryCode countryCode(String land) {
        if (land != null) {
            CountryCode cc = CountryCode.getByCode(land);
            return cc != null ? cc : CountryCode.NO;
        }
        return CountryCode.NO;
    }

    private static LocalDate birthDate(no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person) {
        return CalendarConverter.toLocalDate(person.getFoedselsdato().getFoedselsdato());
    }

    public static Barn map(NorskIdent id, Fodselsnummer fnrMor, HentPersonResponse barn) {
        return new Barn(fnrMor, new Fodselsnummer(id.getIdent()), birthDate(barn.getPerson()));
    }

}
