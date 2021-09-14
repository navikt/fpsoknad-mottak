package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static no.nav.foreldrepenger.common.domain.felles.Kjønn.K;
import static no.nav.foreldrepenger.common.domain.felles.Kjønn.M;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.serialize;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLForelderBarnRelasjon.PDLRelasjonsRolle.BARN;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLForelderBarnRelasjon.PDLRelasjonsRolle.FAR;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLForelderBarnRelasjon.PDLRelasjonsRolle.MOR;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLKjønn.mann;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLKjønn.Kjønn.KVINNE;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLKjønn.Kjønn.MANN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Navn;
import no.nav.foreldrepenger.common.domain.felles.AnnenPart;
import no.nav.foreldrepenger.common.domain.felles.Bankkonto;
import no.nav.foreldrepenger.common.oppslag.dkif.Målform;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLAdresseBeskyttelse.PDLAdresseGradering;
import no.nav.foreldrepenger.common.oppslag.pdl.dto.BarnDTO;
import no.nav.foreldrepenger.mottak.oppslag.pdl.dto.SøkerDTO;

@AutoConfigureJsonTesters
@SpringJUnitConfig
class PDLSerializationTest {

    private static final Målform BOKMÅL = Målform.NB;
    private static final String ID_ANNEN = "33333333333";
    private static final String ID_SØKER = "22222222222";
    private static final String ID_BARN = "11111111111";
    private static final Logger LOG = LoggerFactory.getLogger(PDLSerializationTest.class);
    private static final Fødselsnummer FNR_BARN = Fødselsnummer.valueOf(ID_BARN);
    private static final Fødselsnummer FNR_SØKER = Fødselsnummer.valueOf(ID_SØKER);
    private static final Fødselsnummer FNR_ANNEN = Fødselsnummer.valueOf(ID_ANNEN);

    private static final LocalDate BARNFØDT = LocalDate.now().minusYears(1);
    private static final LocalDate ANNENFØDT = LocalDate.now().minusYears(30);
    private static final LocalDate MORFØDT = LocalDate.now().minusYears(29);

    private static final AktørId AKTØR_SØKER = AktørId.valueOf("22222222222");
    private static final AktørId AKTØR_ANNEN = AktørId.valueOf("33333333333");

    @Inject
    private ObjectMapper mapper;

    @Test
    void testWrappedNavn() throws Exception {
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
        assertEquals(PDLMapper.navnFra(Set.of(kvinnePDLNavn()), pdlKvinne()), kvinneNavn());
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
        assertEquals(PDLMapper.barnFra(ID_SØKER, pdlBarn().withId(ID_BARN)), barn());
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
    void testSøkerDTO() throws Exception {
        assertEquals(søker(), PDLMapper.map(ID_SØKER, AKTØR_SØKER, BOKMÅL, bankkonto(), Set.of(pdlBarn().withId(ID_BARN)), pdlSøker()));
    }

    @Test
    void testAnnenMapping() {
        assertEquals(PDLMapper.annenPartFra(pdlAnnenPart()), annenPart());
    }

    private static SøkerDTO søker() {
        return SøkerDTO.builder()
                .bankkonto(bankkonto())
                .barn(Set.of(barn()))
                .målform(BOKMÅL.name())
                .fødselsdato(MORFØDT)
                .id(FNR_SØKER)
                .kjønn(K)
                .landKode(CountryCode.NO)
                .navn(kvinneNavn())
                .build();
    }

    private static PDLSøker pdlSøker() {
        return new PDLSøker(Set.of(kvinnePDLNavn()), pdlKvinne(), norsk(), fødsel(MORFØDT), familierelasjoner());
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
        return new AnnenPart(FNR_ANNEN, AKTØR_ANNEN, mannsNavn(), ANNENFØDT);
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

    private static BarnDTO barn() {
        return BarnDTO.builder()
                .fnr(FNR_BARN)
                .fnrSøker(FNR_SØKER)
                .fødselsdato(BARNFØDT)
                .navn(barnsNavn())
                .annenPart(annenPart())
                .build();
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
        return new Navn("Ole", "Olemann", "Olsen", M);
    }

    private static Navn barnsNavn() {
        return new Navn("Barn", "Barnslig", "Barnesen", M);
    }

    private static Navn kvinneNavn() {
        return new Navn("Mor", "Mellommor", "Morsen", K);
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
