package no.nav.foreldrepenger.mottak.oppslag.pdl;

import static no.nav.foreldrepenger.mottak.domain.felles.Kjønn.K;
import static no.nav.foreldrepenger.mottak.domain.felles.Kjønn.M;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.serialize;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLFamilierelasjon.PDLRelasjonsRolle.BARN;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLFamilierelasjon.PDLRelasjonsRolle.FAR;
import static no.nav.foreldrepenger.mottak.oppslag.pdl.PDLFamilierelasjon.PDLRelasjonsRolle.MOR;
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

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.felles.AnnenPart;
import no.nav.foreldrepenger.mottak.domain.felles.Bankkonto;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLAdresseBeskyttelse.PDLAdresseGradering;
import no.nav.foreldrepenger.mottak.oppslag.pdl.PDLSivilstand.PDLSivilstandType;
import no.nav.foreldrepenger.mottak.oppslag.pdl.dto.BarnDTO;
import no.nav.foreldrepenger.mottak.oppslag.pdl.dto.SøkerDTO;

@AutoConfigureJsonTesters
@SpringJUnitConfig
public class PDLSerializationTest {

    private static final String BOKMÅL = "NB";
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

    private static final AktørId AKTØR_ANNEN = null; // AktørId.valueOf("a" + ID_ANNEN);

    @Inject
    private ObjectMapper mapper;

    @Test
    public void testStatsborgerskapPDL() {
        test(ettStatsborgerskap());
    }

    @Test
    public void testNavnDTO() {
        test(kvinneNavn());
    }

    @Test
    public void testNavnMapping() {
        assertEquals(PDLMapper.navnFra(Set.of(kvinnePDLNavn()), pdlKvinne()), kvinneNavn());
    }

    @Test
    public void testNavnPDL() {
        test(kvinnePDLNavn());
    }

    @Test
    public void testBarnDTO() {
        test(barn());
    }

    @Test
    public void testBarnPDL() {
        test(pdlBarn());
    }

    @Test
    public void testFødselPDL() {
        test(fødsel());
    }

    @Test
    public void testFamilierelasjonPDL() {
        test(familierelasjon());
    }

    @Test
    public void testAdresseBeskyttelsePDL() {
        test(adresseBeskyttelse());
    }

    private static PDLAdresseBeskyttelse adresseBeskyttelse() {
        return new PDLAdresseBeskyttelse(PDLAdresseBeskyttelse.PDLAdresseGradering.FORTROLIG);
    }

    @Test
    public void testBarnMapping() {
        assertEquals(PDLMapper.barnFra(ID_SØKER, pdlBarn().withId(ID_BARN)), barn());
    }

    @Test
    public void testAnnenDTO() {
        test(annenPart());
    }

    @Test
    public void testAnnenPDL() {
        test(pdlAnnenPart().withId(ID_ANNEN));
    }

    @Test
    public void testSøkerPDL() {
        test(pdlSøker());
    }

    @Test
    public void testDødsfallPDL() {
        test(etDødsfall());
    }

    @Test
    public void testSivilstandPDL() {
        test(new PDLSivilstand(PDLSivilstandType.GIFT, ID_ANNEN));
    }

    @Test
    public void testSøkerDTO() throws Exception {
        assertEquals(søker(), PDLMapper.map(ID_SØKER, BOKMÅL, bankkonto(), Set.of(pdlBarn().withId(ID_BARN)), pdlSøker()));
    }

    @Test
    public void testAnnenMapping() {
        assertEquals(PDLMapper.annenPartFra(pdlAnnenPart()), annenPart());
    }

    private static SøkerDTO søker() {
        return SøkerDTO.builder()
                .bankkonto(bankkonto())
                .barn(Set.of(barn()))
                .målform(BOKMÅL)
                .fødselsdato(MORFØDT)
                .id(ID_SØKER)
                .kjønn(K)
                .landKode(CountryCode.NO)
                .navn(kvinneNavn())
                .build();
    }

    private static PDLSøker pdlSøker() {
        return new PDLSøker(Set.of(kvinnePDLNavn()), pdlKvinne(), norsk(), fødsel(MORFØDT), familierelasjoner(),
                sivilstand());
    }

    private static Set<PDLSivilstand> sivilstand() {
        return Set.of(new PDLSivilstand(PDLSivilstandType.GIFT, ID_ANNEN));
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
        return new PDLBarn(fødsel(BARNFØDT), familierelasjoner(), barnPDLNavn(), pdlMann(), dødsfall(), Set.of(beskyttelse()))
                .withAnnenPart(pdlAnnenPart());
    }

    private static Set<PDLFødsel> fødsel(LocalDate født) {
        return Set.of(new PDLFødsel(født));
    }

    private static Set<PDLFamilierelasjon> familierelasjoner() {
        return Set.of(
                new PDLFamilierelasjon(ID_ANNEN, FAR, BARN),
                new PDLFamilierelasjon(ID_SØKER, MOR, BARN));
    }

    private static PDLFamilierelasjon familierelasjon() {
        return new PDLFamilierelasjon(ID_ANNEN, FAR, BARN);
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
