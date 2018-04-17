package no.nav.foreldrepenger.oppslag.person;

import java.time.LocalDate;
import java.util.List;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.oppslag.domain.*;
import no.nav.foreldrepenger.oppslag.domain.Bankkonto;
import no.nav.foreldrepenger.oppslag.domain.Person;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;

final class PersonMapper {

    private static final PoststedFinner POSTSTEDFINNER = new StatiskPoststedFinner();

    private PersonMapper() {

    }

    public static Person map(ID id, no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person, List<Barn> barn) {
        return new no.nav.foreldrepenger.oppslag.domain.Person(id,
            countryCode(person), Kjonn.valueOf(person.getKjoenn().getKjoenn().getValue()),
            name(person.getPersonnavn()),
            address(person.getBostedsadresse().getStrukturertAdresse()),
            målform(person),
            bankkonto(person),
            birthDate(person), barn);
    }

    public static Barn map(NorskIdent id, Fodselsnummer fnrMor, HentPersonResponse barn) {
        return new Barn(fnrMor, new Fodselsnummer(id.getIdent()), birthDate(barn.getPerson()));
    }

    private static Adresse address(StrukturertAdresse adresse) {
        if (adresse instanceof Gateadresse) {
            Gateadresse ga = Gateadresse.class.cast(adresse);
            if (ga.getTilleggsadresseType().equalsIgnoreCase(RequestUtils.OFFISIELL_ADRESSE)) {

                return new Adresse(countryCode(ga.getLandkode().getValue()), ga.getPoststed().getValue(),
                        POSTSTEDFINNER.poststed(ga.getPoststed().getValue()),
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

    private static String målform(no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person) {
        if (person instanceof Bruker) {
            Bruker bruker = (Bruker) person;
            return bruker.getMaalform() != null ? bruker.getMaalform().getValue() : null;
        }

        return null;
    }

    private static Bankkonto bankkonto(no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person) {
        if (person instanceof Bruker) {
            Bruker bruker = (Bruker) person;
            no.nav.tjeneste.virksomhet.person.v3.informasjon.Bankkonto bankkonto = bruker.getBankkonto();
            if (bankkonto == null) {
                return null;
            }
            Pair<String, String> kontoinfo = kontoinfo(bankkonto);
            return new Bankkonto(kontoinfo.getFirst(), kontoinfo.getSecond());
        }
        return null;
    }

    private static Pair<String, String> kontoinfo(no.nav.tjeneste.virksomhet.person.v3.informasjon.Bankkonto konto) {
        if (konto instanceof BankkontoNorge) {
            BankkontoNorge norskKonto = (BankkontoNorge) konto;
            return Pair.of(norskKonto.getBankkonto().getBankkontonummer(), norskKonto.getBankkonto().getBanknavn());
        } else {
            BankkontoUtland utenlandskKonto = (BankkontoUtland) konto;
            return Pair.of(utenlandskKonto.getBankkontoUtland().getSwift(), utenlandskKonto.getBankkontoUtland().getBankkode());
        }
    }

}
