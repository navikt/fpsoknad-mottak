package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static no.nav.foreldrepenger.common.domain.felles.Kjønn.K;
import static no.nav.foreldrepenger.common.domain.felles.TestUtils.serialize;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLForelderBarnRelasjon.PDLRelasjonsRolle.BARN;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLForelderBarnRelasjon.PDLRelasjonsRolle.FAR;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLForelderBarnRelasjon.PDLRelasjonsRolle.MOR;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLKjønn.Kjønn.KVINNE;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLKjønn.Kjønn.MANN;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLKjønn.mann;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Barn;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Navn;
import no.nav.foreldrepenger.common.domain.felles.AnnenPart;
import no.nav.foreldrepenger.common.domain.felles.Bankkonto;
import no.nav.foreldrepenger.common.domain.felles.Kjønn;
import no.nav.foreldrepenger.common.domain.felles.Person;
import no.nav.foreldrepenger.common.oppslag.dkif.Målform;
import no.nav.foreldrepenger.mottak.config.JacksonConfiguration;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLAdresseBeskyttelse.PDLAdresseGradering;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JacksonConfiguration.class)
class PDLSerializationTest {

    private static final Målform BOKMÅL = Målform.NB;
    private static final String ID_ANNEN = "33333333333";
    private static final String ID_SØKER = "22222222222";
    private static final String ID_BARN = "11111111111";
    private static final Logger LOG = LoggerFactory.getLogger(PDLSerializationTest.class);
    private static final Fødselsnummer FNR_BARN = new Fødselsnummer(ID_BARN);
    private static final Fødselsnummer FNR_SØKER = new Fødselsnummer(ID_SØKER);
    private static final Fødselsnummer FNR_ANNEN = new Fødselsnummer(ID_ANNEN);

    private static final LocalDate BARNFØDT = LocalDate.now().minusYears(1);
    private static final LocalDate BARNDØDT= LocalDate.now().minusYears(1);
    private static final LocalDate ANNENFØDT = LocalDate.now().minusYears(30);
    private static final LocalDate MORFØDT = LocalDate.now().minusYears(29);

    private static final AktørId AKTØR_SØKER = AktørId.valueOf("22222222222");
    private static final AktørId AKTØR_ANNEN = AktørId.valueOf("33333333333");

    @Autowired
    private ObjectMapper mapper;

    @Test
    void testWrappedNavn() {
        test(new PDLWrappedNavn(Set.of(new PDLNavn("a", "b", "c"))));
    }

    @Test
    void testStatsborgerskapPDL() {
        test(ettStatsborgerskap());
    }

    @Test
    void testNavnDTO() {
        test(kvinneNavn());
    }

    @Test
    void testNavnMapping() {
        assertEquals(PDLMapper.navnFra(Set.of(kvinnePDLNavn())), kvinneNavn());
    }

    @Test
    void testNavnPDL() {
        test(kvinnePDLNavn());
    }

    @Test
    void testBarnDTO() {
        test(barn());
    }

    @Test
    void testBarnPDL() {
        test(pdlBarn());
    }

    @Test
    void testFødselPDL() {
        test(fødsel());
    }

    @Test
    void testFprelderBarnRelasjonPDL() {
        test(forelderbarnrelasjon());
    }

    @Test
    void testAdresseBeskyttelsePDL() {
        test(adresseBeskyttelse());
    }

    private static PDLAdresseBeskyttelse adresseBeskyttelse() {
        return new PDLAdresseBeskyttelse(PDLAdresseBeskyttelse.PDLAdresseGradering.FORTROLIG);
    }

    @Test
    void testBarnMapping() {
        assertEquals(PDLMapper.barnFra(pdlBarn().withId(ID_BARN)), barn());
    }

    @Test
    void testAnnenDTO() {
        test(annenPart());
    }

    @Test
    void testAnnenPDL() {
        test(pdlAnnenPart().withId(ID_ANNEN));
    }

    @Test
    void testSøkerPDL() {
        test(pdlSøker());
    }

    @Test
    void testDødsfallPDL() {
        test(etDødsfall());
    }

    @Test
    void testSøkerDTO() {
        assertEquals(søker(), PDLMapper
            .map(FNR_SØKER, AKTØR_SØKER, BOKMÅL, bankkonto(), List.of(pdlBarn().withId(ID_BARN)), pdlSøker()));
    }

    @Test
    void testAnnenMapping() {
        assertEquals(PDLMapper.annenPartFra(pdlAnnenPart()), annenPart());
    }

    private static Person søker() {
        return Person.builder()
            .fnr(FNR_SØKER)
            .aktørId(AKTØR_SØKER)
            .målform(BOKMÅL)
            .bankkonto(bankkonto())
            .barn(List.of(barn()))
            .fødselsdato(MORFØDT)
            .kjønn(K)
            .land(CountryCode.NO)
            .navn(kvinneNavn())
            .build();
    }

    private static PDLSøker pdlSøker() {
        return new PDLSøker(Set.of(kvinnePDLNavn()), pdlKvinne(), norsk(), fødsel(MORFØDT), familierelasjoner(),
            List.of(new PDLDødfødtBarn(LocalDate.now())));
    }

    private static PDLFødsel fødsel() {
        return new PDLFødsel(LocalDate.now().minusYears(1));
    }

    private static PDLAnnenPart pdlAnnenPart() {
        return new PDLAnnenPart(Set.of(mannsPDLNavn()), fødsel(ANNENFØDT), Set.of(mann()), dødsfall(), Set.of(beskyttelse())).withId(ID_ANNEN);
    }

    private static Set<PDLDødsfall> dødsfall() {
        return Set.of(etDødsfall());
    }

    private static PDLDødsfall etDødsfall() {
        return new PDLDødsfall(LocalDate.now().minusYears(1));
    }

    private static AnnenPart annenPart() {
        return new AnnenPart(FNR_ANNEN, null, mannsNavn(), ANNENFØDT);
    }

    private static PDLBarn pdlBarn() {
        return new PDLBarn(fødsel(BARNFØDT), familierelasjoner(), barnPDLNavn(), pdlMann(), Set.of(beskyttelse()), dødsfall())
                .withAnnenPart(pdlAnnenPart());
    }

    private static Set<PDLFødsel> fødsel(LocalDate født) {
        return Set.of(new PDLFødsel(født));
    }

    private static Set<PDLForelderBarnRelasjon> familierelasjoner() {
        return Set.of(
                new PDLForelderBarnRelasjon(ID_ANNEN, FAR, BARN),
                new PDLForelderBarnRelasjon(ID_SØKER, MOR, BARN));
    }

    private static PDLForelderBarnRelasjon forelderbarnrelasjon() {
        return new PDLForelderBarnRelasjon(ID_ANNEN, FAR, BARN);
    }

    private static Barn barn() {
        return new Barn(FNR_BARN, BARNFØDT, BARNDØDT, barnsNavn(), Kjønn.M, annenPart());
    }

    private static PDLAdresseBeskyttelse beskyttelse() {
        return new PDLAdresseBeskyttelse(PDLAdresseGradering.FORTROLIG);
    }

    private static Set<PDLStatsborgerskap> norsk() {
        return Set.of(ettStatsborgerskap());
    }

    private static PDLStatsborgerskap ettStatsborgerskap() {
        return new PDLStatsborgerskap(CountryCode.NO.getAlpha3());
    }

    private static Set<PDLKjønn> pdlMann() {
        return Set.of(new PDLKjønn(MANN));
    }

    private static Set<PDLKjønn> pdlKvinne() {
        return Set.of(new PDLKjønn(KVINNE));
    }

    private static Navn mannsNavn() {
        return new Navn("Ole", "Olemann", "Olsen");
    }

    private static Navn barnsNavn() {
        return new Navn("Barn", "Barnslig", "Barnesen");
    }

    private static Navn kvinneNavn() {
        return new Navn("Mor", "Mellommor", "Morsen");
    }

    private static PDLNavn kvinnePDLNavn() {
        return new PDLNavn("Mor", "Mellommor", "Morsen");
    }

    private static PDLNavn mannsPDLNavn() {
        return new PDLNavn("Ole", "Olemann", "Olsen");
    }

    private static Set<PDLNavn> barnPDLNavn() {
        return Set.of(new PDLNavn("Barn", "Barnslig", "Barnesen"));
    }

    private static Bankkonto bankkonto() {
        return new Bankkonto("22222222222", "DNB");
    }

    void test(Object object) {
        test(object, true);

    }

    void test(Object expected, boolean log) {
        String serialized = serialize(expected, log, mapper);
        if (log) {
            LOG.info("Expected {}", expected);
            LOG.info("Serialized {}", serialized);
        }
        try {
            Object deserialized = mapper.readValue(serialized, expected.getClass());
            if (log) {
                LOG.info("Deserialized {}", deserialized);
            }
            assertEquals(expected, deserialized);
        } catch (IOException e) {
            LOG.error("{}", e);
            fail(expected.getClass().getSimpleName() + " failed");
        }
    }

}
