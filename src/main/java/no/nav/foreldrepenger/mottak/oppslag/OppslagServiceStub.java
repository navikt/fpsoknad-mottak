package no.nav.foreldrepenger.mottak.oppslag;

import static no.nav.foreldrepenger.mottak.util.EnvUtil.DEV;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.PREPROD;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.felles.Bankkonto;
import no.nav.foreldrepenger.mottak.domain.felles.Person;

@Service
@Profile({ PREPROD, DEV })
@ConditionalOnProperty(name = "oppslag.stub", havingValue = "true")
public class OppslagServiceStub implements Oppslag {

    @Override
    public Person getSøker() {
        return person();
    }

    @Override
    public AktørId getAktørId() {
        return new AktørId("11111111111");
    }

    @Override
    public AktørId getAktørId(Fødselsnummer fnr) {
        return new AktørId("11111111111");
    }

    @Override
    public Fødselsnummer getFnr(AktørId aktørId) {
        return new Fødselsnummer("01010111111");
    }

    @Override
    public List<Arbeidsforhold> getArbeidsforhold() {
        return Collections.emptyList();
    }

    private static Person person() {
        Person søker = new Person();
        søker.aktørId = new AktørId("42");
        søker.bankkonto = new Bankkonto("2000.20.20000", "Store Fiskerbank");
        søker.fnr = new Fødselsnummer("010101010101");
        søker.fornavn = "Mor";
        søker.mellomnavn = "Mellommor";
        søker.etternavn = "Moro";
        søker.fødselsdato = LocalDate.now().minusYears(25);
        søker.kjønn = "K";
        søker.ikkeNordiskEøsLand = false;
        søker.land = CountryCode.NO;
        søker.målform = "NN";
        return søker;
    }

    @Override
    public String ping() {
        return "42";
    }

}
