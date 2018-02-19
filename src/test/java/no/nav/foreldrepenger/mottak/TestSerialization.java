package no.nav.foreldrepenger.mottak;

import static no.nav.foreldrepenger.mottak.TestUtils.adopsjon;
import static no.nav.foreldrepenger.mottak.TestUtils.aktoer;
import static no.nav.foreldrepenger.mottak.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.mottak.TestUtils.engangstønad;
import static no.nav.foreldrepenger.mottak.TestUtils.framtidigOppHoldIUtlandetHeleåret;
import static no.nav.foreldrepenger.mottak.TestUtils.framtidigOppholdINorge;
import static no.nav.foreldrepenger.mottak.TestUtils.fremtidigFødsel;
import static no.nav.foreldrepenger.mottak.TestUtils.fødsel;
import static no.nav.foreldrepenger.mottak.TestUtils.medlemsskap;
import static no.nav.foreldrepenger.mottak.TestUtils.norskForelder;
import static no.nav.foreldrepenger.mottak.TestUtils.omsorgsovertakelse;
import static no.nav.foreldrepenger.mottak.TestUtils.påkrevdVedlegg;
import static no.nav.foreldrepenger.mottak.TestUtils.søker;
import static no.nav.foreldrepenger.mottak.TestUtils.utenlandskForelder;
import static no.nav.foreldrepenger.mottak.TestUtils.utenlandsopphold;
import static no.nav.foreldrepenger.mottak.TestUtils.varighet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import no.nav.foreldrepenger.mottak.dokmot.DokmotEngangsstønadXMLGenerator;
import no.nav.foreldrepenger.mottak.dokmot.DokmotXMLKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.Fodselsnummer;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.XMLKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.pdf.PdfGenerator;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Dokumentforsendelse;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Dokumentinnhold;

public class TestSerialization {

    private static DokmotEngangsstønadXMLGenerator DOKMOT_ENGANGSSTØNAD_XML_GENERATOR;
    private static XMLKonvoluttGenerator DOKMOT_ENGANGSSTØNAD_KONVOLUTT_XML_GENERATOR;

    private static ObjectMapper mapper;
    private static Unmarshaller søknadUnmarshaller, forsendelsesMarshaller;

    @BeforeClass
    public static void beforeClass() throws Exception {
        mapper = new ObjectMapper();
        configureMapper(mapper);
        søknadUnmarshaller = unmarshaller(SoeknadsskjemaEngangsstoenad.class);
        forsendelsesMarshaller = unmarshaller(Dokumentforsendelse.class);
        DOKMOT_ENGANGSSTØNAD_XML_GENERATOR = new DokmotEngangsstønadXMLGenerator();
        DOKMOT_ENGANGSSTØNAD_KONVOLUTT_XML_GENERATOR = new DokmotXMLKonvoluttGenerator(
                DOKMOT_ENGANGSSTØNAD_XML_GENERATOR, new PdfGenerator());
    }

    private static Unmarshaller unmarshaller(Class<?> clazz) throws Exception {
        return JAXBContext.newInstance(clazz).createUnmarshaller();
    }

    private static void configureMapper(ObjectMapper mapper) {
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
    }

    @Test
    public void testVedlegg() throws IOException {
        test(påkrevdVedlegg("vedlegg.pdf"), true);
    }

    @Test
    public void testSøknadNorge() throws Exception {
        Søknad engangssøknad = engangssøknad(false);
        test(engangssøknad, false);

    }

    @Test
    public void testSøknadUtlandXML() throws Exception {
        Søknad engangssøknad = engangssøknad(true);
        SoeknadsskjemaEngangsstoenad dokmotModel = DOKMOT_ENGANGSSTØNAD_XML_GENERATOR.toDokmotModel(engangssøknad);
        String xml = DOKMOT_ENGANGSSTØNAD_XML_GENERATOR.toXML(engangssøknad);
        SoeknadsskjemaEngangsstoenad deserialized = (SoeknadsskjemaEngangsstoenad) søknadUnmarshaller
                .unmarshal(new StringReader(xml));
        assertEquals(dokmotModel.getSoknadsvalg().getStoenadstype(), deserialized.getSoknadsvalg().getStoenadstype());
        assertEquals(dokmotModel.getSoknadsvalg().getFoedselEllerAdopsjon(),
                deserialized.getSoknadsvalg().getFoedselEllerAdopsjon());
        assertEquals(dokmotModel.getTilknytningNorge().isOppholdNorgeNaa(),
                deserialized.getTilknytningNorge().isOppholdNorgeNaa());
        assertEquals(dokmotModel.getTilknytningNorge().isTidligereOppholdNorge(),
                deserialized.getTilknytningNorge().isTidligereOppholdNorge());
        assertEquals(deserialized.getTilknytningNorge().getFremtidigOppholdUtenlands().getUtenlandsopphold().size(), 1);
        assertEquals(deserialized.getTilknytningNorge().getTidligereOppholdUtenlands().getUtenlandsopphold().size(), 1);
        assertEquals(deserialized.getOpplysningerOmBarn().getAntallBarn(), 1);
    }

    @Test
    public void testKonvoluttXML() throws Exception {
        Søknad engangssøknad = engangssøknad(true);
        Dokumentforsendelse model = DOKMOT_ENGANGSSTØNAD_KONVOLUTT_XML_GENERATOR.toDokmotModel(engangssøknad);
        String konvolutt = DOKMOT_ENGANGSSTØNAD_KONVOLUTT_XML_GENERATOR.toXML(engangssøknad);
        System.out.println(konvolutt);
        Dokumentforsendelse deserialized = (Dokumentforsendelse) forsendelsesMarshaller
                .unmarshal(new StringReader(konvolutt));
        Dokumentinnhold pdf = deserialized.getHoveddokument().getDokumentinnholdListe().get(0);
        assertTrue(TestUtils.hasPdfSignature(pdf.getDokument()));
        Dokumentinnhold søknadsXML = deserialized.getHoveddokument().getDokumentinnholdListe().get(1);
        SoeknadsskjemaEngangsstoenad deserializedSøknad = (SoeknadsskjemaEngangsstoenad) søknadUnmarshaller
                .unmarshal(new StringReader(new String(søknadsXML.getDokument())));
        // assertEquals(model.get deserializedSøknad.getSoknadsvalg().getFoedselEllerAdopsjon());

    }

    private static void writeBytesToFileNio(byte[] bFile, String fileDest) {

        try {
            Path path = Paths.get(fileDest);
            Files.write(path, bFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSøknadUtland() throws Exception {
        Søknad engangssøknad = engangssøknad(true, fødsel());
        test(engangssøknad, true);
        System.out.println(DOKMOT_ENGANGSSTØNAD_XML_GENERATOR.toXML(engangssøknad));

    }

    @Test
    public void testEngangsstønadNorge() {
        Engangsstønad engangstønad = engangstønad(false);
        test(engangstønad, true);
    }

    @Test
    public void testEngangsstønadUtland() {
        test(engangstønad(true));
    }

    @Test
    public void testNorskAnnenForelder() {
        test(norskForelder());
    }

    @Test
    public void testUtenlandskAnnenForelder() {
        test(utenlandskForelder());
    }

    @Test
    public void testMedlemsskap() {
        test(medlemsskap(), true);
    }

    @Test
    public void testMedlemsskapUtland() {
        test(medlemsskap(true));
    }

    @Test
    public void testFnr() throws JsonProcessingException {
        test(new Fodselsnummer("03016536325"), true);
    }

    @Test
    public void testAktør() throws JsonProcessingException {
        test(new AktorId("111111111"), true);
    }

    @Test
    public void testAdopsjon() {
        test(adopsjon());
    }

    @Test
    public void testFødsel() {
        test(fødsel(), true);
    }

    @Test
    public void testFremtidigOppholdNorge() {
        test(framtidigOppholdINorge());
    }

    @Test
    public void testFremtidigOppholdUtland() {
        test(framtidigOppHoldIUtlandetHeleåret(), true);
    }

    @Test
    public void testOmsorgsovertagkelse() {
        test(omsorgsovertakelse());
    }

    @Test
    public void testSøker() {
        test(søker());
    }

    @Test
    public void testAktorId() {
        test(aktoer());
    }

    @Test
    public void testTermin() {
        test(fremtidigFødsel());
    }

    @Test
    public void testUtenlandsopphold() {
        test(utenlandsopphold(), true);
    }

    @Test
    public void testVarighet() {
        test(varighet());
    }

    private static void test(Object object, boolean print) {
        test(object, print, mapper);
    }

    static void test(Object object) {
        test(object, false);

    }

    private static void test(Object object, boolean print, ObjectMapper mapper) {
        try {
            String serialized = write(object, print, mapper);
            Object deserialized = mapper.readValue(serialized, object.getClass());
            if (print) {
                System.out.println(deserialized);
            }
            assertEquals(object, deserialized);
        } catch (IOException e) {
            e.printStackTrace();
            fail(object.getClass().getSimpleName() + " failed");
        }
    }

    static String write(Object obj, boolean print, ObjectMapper mapper) throws JsonProcessingException {
        String serialized = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        if (print) {
            System.out.println(serialized);
            return serialized;
        }
        return serialized;
    }

}
