package no.nav.foreldrepenger.oppslag.lookup.ws.person;

import com.neovisionaries.i18n.CountryCode;
import no.nav.foreldrepenger.oppslag.lookup.Pair;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;

import java.time.LocalDate;
import java.util.List;

final class PersonMapper {

    private PersonMapper() {

    }

    public static Person map(ID id, no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person, List<Barn> barn) {
        return new Person(id,
            countryCode(person), Kjonn.valueOf(person.getKjoenn().getKjoenn().getValue()),
            name(person.getPersonnavn()),
            målform(person),
            bankkonto(person),
            birthDate(person),
            barn);
    }

    public static Barn map(NorskIdent id, Fodselsnummer fnrMor, HentPersonResponse barn) {
        return new Barn(fnrMor, new Fodselsnummer(id.getIdent()), birthDate(barn.getPerson()));
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

    private static no.nav.foreldrepenger.oppslag.lookup.ws.person.Bankkonto bankkonto(no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person) {
        if (person instanceof Bruker) {
            Bruker bruker = (Bruker) person;
            no.nav.tjeneste.virksomhet.person.v3.informasjon.Bankkonto bankkonto = bruker.getBankkonto();
            if (bankkonto == null) {
                return null;
            }
            Pair<String, String> kontoinfo = kontoinfo(bankkonto);
            return new no.nav.foreldrepenger.oppslag.lookup.ws.person.Bankkonto(kontoinfo.getFirst(), kontoinfo.getSecond());
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
