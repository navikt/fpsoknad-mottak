package no.nav.foreldrepenger.mottak.domain.engangsstønad;

import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.fødsel;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.hasPdfSignature;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.norskForelder;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.person;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.termin;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.utenlandskForelder;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.valgfrittVedlegg;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jboss.logging.MDC;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.mottak.config.MottakConfiguration;
import no.nav.foreldrepenger.mottak.domain.CallIdGenerator;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.felles.ValgfrittVedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils;
import no.nav.foreldrepenger.mottak.http.Constants;
import no.nav.foreldrepenger.mottak.innsending.engangsstønad.DokmotEngangsstønadXMLGenerator;
import no.nav.foreldrepenger.mottak.innsending.engangsstønad.DokmotEngangsstønadXMLKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;
import no.nav.foreldrepenger.mottak.innsending.pdf.EngangsstønadPDFGenerator;
import no.nav.foreldrepenger.mottak.innsending.pdf.ForeldrepengeInfoRenderer;
import no.nav.foreldrepenger.mottak.innsending.pdf.PDFElementRenderer;
import no.nav.foreldrepenger.mottak.innsending.pdf.SøknadTextFormatter;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskaper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadInspektør;
import no.nav.foreldrepenger.mottak.innsyn.XMLStreamSøknadInspektør;
import no.nav.foreldrepenger.mottak.util.JAXBESV1Helper;
import no.nav.foreldrepenger.mottak.util.Versjon;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.Bruker;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.FoedselEllerAdopsjon;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Dokumentforsendelse;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Dokumentinnhold;
import no.nav.security.spring.oidc.SpringOIDCRequestContextHolder;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { MottakConfiguration.class, EngangsstønadPDFGenerator.class, JAXBESV1Helper.class,
        DokmotEngangsstønadXMLGenerator.class,
        DokmotEngangsstønadXMLKonvoluttGenerator.class,
        CallIdGenerator.class,
        PDFElementRenderer.class,
        ForeldrepengeInfoRenderer.class,
        SøknadTextFormatter.class,
        SpringOIDCRequestContextHolder.class })

@AutoConfigureJsonTesters
public class TestDokmotSerialization {
    @Autowired
    JAXBESV1Helper jaxb;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    DokmotEngangsstønadXMLGenerator søknadXMLGenerator;
    @Autowired
    DokmotEngangsstønadXMLKonvoluttGenerator søknadXMLKonvoluttGenerator;

    private static final SøknadInspektør INSPEKTOR = new XMLStreamSøknadInspektør();

    @Test
    public void testSøknadUtlandXML() throws Exception {
    }

    @Test
    public void testKonvoluttXML() throws Exception {
        MDC.put(Constants.NAV_CALL_ID, "42");
        Søknad engangssøknad = engangssøknad(Versjon.V1, true, fødsel(), norskForelder(Versjon.V1),
                valgfrittVedlegg(ForeldrepengerTestUtils.ID142, InnsendingsType.LASTET_OPP));
        String konvolutt = søknadXMLKonvoluttGenerator.tilXML(engangssøknad, person());
        System.out.println(konvolutt);
        Dokumentforsendelse unmarshalled = jaxb.unmarshal(konvolutt, Dokumentforsendelse.class);
        Dokumentinnhold pdf = unmarshalled.getHoveddokument().getDokumentinnholdListe().get(0);
        assertTrue(hasPdfSignature(pdf.getDokument()));
        Dokumentinnhold søknadsXML = unmarshalled.getHoveddokument().getDokumentinnholdListe().get(1);
        SoeknadsskjemaEngangsstoenad deserializedSøknadModel = jaxb.unmarshal(søknadsXML.getDokument(),
                SoeknadsskjemaEngangsstoenad.class);
        assertEquals(deserializedSøknadModel.getOpplysningerOmBarn().getAntallBarn(), 1);
        assertEquals(deserializedSøknadModel.getSoknadsvalg().getFoedselEllerAdopsjon(), FoedselEllerAdopsjon.FOEDSEL);
    }

    @Test
    public void testDokmotModelTransformation() throws Exception {
        ValgfrittVedlegg valgfrittVedlegg = valgfrittVedlegg(ForeldrepengerTestUtils.ID142, InnsendingsType.LASTET_OPP);
        Søknad søknad = engangssøknad(Versjon.V1, true, termin(), norskForelder(Versjon.V1), valgfrittVedlegg);
        Person søker = person();
        SoeknadsskjemaEngangsstoenad dokmotModel = søknadXMLGenerator.tilDokmotModel(søknad, søker);
        Bruker bruker = Bruker.class.cast(dokmotModel.getBruker());
        assertEquals(søker.fnr.getFnr(), bruker.getPersonidentifikator());
        assertEquals(søknad.getBegrunnelseForSenSøknad(), dokmotModel.getOpplysningerOmBarn().getBegrunnelse());
        assertEquals(dokmotModel.getVedleggListe().getVedlegg().size(), 1);
        Engangsstønad ytelse = (Engangsstønad) søknad.getYtelse();
        assertEquals(ytelse.getRelasjonTilBarn().getAntallBarn(), dokmotModel.getOpplysningerOmBarn().getAntallBarn());
    }

    @Test
    public void testDokmotMarshalling() throws Exception {
        Søknad søknad = engangssøknad(Versjon.V1, true, termin(), utenlandskForelder(),
                valgfrittVedlegg(ForeldrepengerTestUtils.ID142, InnsendingsType.LASTET_OPP));
        Person søker = person();
        String xml = søknadXMLGenerator.tilXML(søknad, søker);
        System.out.println(xml);
        SøknadEgenskaper inspiser = INSPEKTOR.inspiser(xml);
        assertEquals(Versjon.V1, inspiser.getVersjon());
        assertEquals(SøknadType.ENGANGSSØKNAD, inspiser.getType());
        SoeknadsskjemaEngangsstoenad dokmotModel = søknadXMLGenerator.tilDokmotModel(søknad, søker);
        SoeknadsskjemaEngangsstoenad unmarshalled = jaxb.unmarshal(xml,
                SoeknadsskjemaEngangsstoenad.class);
        assertEquals(dokmotModel.getSoknadsvalg().getStoenadstype(), unmarshalled.getSoknadsvalg().getStoenadstype());
        assertEquals(dokmotModel.getSoknadsvalg().getFoedselEllerAdopsjon(),
                unmarshalled.getSoknadsvalg().getFoedselEllerAdopsjon());
        assertEquals(dokmotModel.getTilknytningNorge().isOppholdNorgeNaa(),
                unmarshalled.getTilknytningNorge().isOppholdNorgeNaa());
        assertEquals(dokmotModel.getTilknytningNorge().isTidligereOppholdNorge(),
                unmarshalled.getTilknytningNorge().isTidligereOppholdNorge());

        Engangsstønad ytelse = (Engangsstønad) søknad.getYtelse();
        assertEquals(unmarshalled.getTilknytningNorge().getFremtidigOppholdUtenlands().getUtenlandsopphold().size(),
                ytelse.getMedlemsskap().getFramtidigOppholdsInfo().getUtenlandsOpphold().size());
        assertEquals(unmarshalled.getTilknytningNorge().getTidligereOppholdUtenlands().getUtenlandsopphold().size(),
                ytelse.getMedlemsskap().getTidligereOppholdsInfo().getUtenlandsOpphold().size());
        assertEquals(unmarshalled.getOpplysningerOmBarn().getAntallBarn(),
                ytelse.getRelasjonTilBarn().getAntallBarn());
    }

}
