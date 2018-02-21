package no.nav.foreldrepenger.mottak;

import static no.nav.foreldrepenger.mottak.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.mottak.util.Jaxb.context;
import static no.nav.foreldrepenger.mottak.util.Jaxb.unmarshal;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.xml.bind.JAXBContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.foreldrepenger.mottak.config.AppConfig;
import no.nav.foreldrepenger.mottak.dokmot.DokmotEngangsstønadXMLGenerator;
import no.nav.foreldrepenger.mottak.dokmot.DokmotEngangsstønadXMLKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.domain.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.ValgfrittVedlegg;
import no.nav.foreldrepenger.mottak.pdf.PdfGenerator;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.Bruker;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.FoedselEllerAdopsjon;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Dokumentforsendelse;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Dokumentinnhold;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { AppConfig.class, PdfGenerator.class, DokmotEngangsstønadXMLGenerator.class,
        DokmotEngangsstønadXMLKonvoluttGenerator.class })
public class TestDokmotSerialization {

    private static JAXBContext SØKNADCTX = context(SoeknadsskjemaEngangsstoenad.class);
    private static JAXBContext FORSENDELSECTX = context(Dokumentforsendelse.class);

    @Autowired
    DokmotEngangsstønadXMLGenerator søknadXMLGenerator;
    @Autowired
    DokmotEngangsstønadXMLKonvoluttGenerator søknadXMLKonvoluttGenerator;

    @Test
    public void testSøknadUtlandXML() throws Exception {
    }

    @Test
    public void testKonvoluttXML() throws Exception {
        Søknad engangssøknad = engangssøknad(true, TestUtils.fremtidigFødsel(), TestUtils.valgfrittVedlegg());
        Engangsstønad engangs = (Engangsstønad) engangssøknad.getYtelse();
        String konvolutt = søknadXMLKonvoluttGenerator.toXML(engangssøknad);
        System.out.println(konvolutt);
        Dokumentforsendelse unmarshalled = unmarshal(konvolutt, FORSENDELSECTX, Dokumentforsendelse.class);
        Dokumentinnhold pdf = unmarshalled.getHoveddokument().getDokumentinnholdListe().get(0);
        assertTrue(TestUtils.hasPdfSignature(pdf.getDokument()));
        Dokumentinnhold søknadsXML = unmarshalled.getHoveddokument().getDokumentinnholdListe().get(1);
        SoeknadsskjemaEngangsstoenad deserializedSøknadModel = unmarshal(søknadsXML.getDokument(), SØKNADCTX,
                SoeknadsskjemaEngangsstoenad.class);
        System.out.println(søknadXMLGenerator.toXML(deserializedSøknadModel));
        assertEquals(deserializedSøknadModel.getOpplysningerOmBarn().getAntallBarn(), 1);
        assertEquals(deserializedSøknadModel.getSoknadsvalg().getFoedselEllerAdopsjon(), FoedselEllerAdopsjon.FOEDSEL);
    }

    @Test
    public void testDokmotModelTransformation() throws Exception {
        ValgfrittVedlegg valgfrittVedlegg = TestUtils.valgfrittVedlegg();
        Søknad søknad = engangssøknad(true, TestUtils.fremtidigFødsel(), valgfrittVedlegg);
        SoeknadsskjemaEngangsstoenad dokmotModel = søknadXMLGenerator.toDokmotModel(søknad);
        assertEquals(søknad.getBegrunnelseForSenSøknad(), dokmotModel.getOpplysningerOmBarn().getBegrunnelse());
        assertEquals(søknad.getSøker().getFnr().getId(),
                Bruker.class.cast(dokmotModel.getBruker()).getPersonidentifikator());
        assertEquals(dokmotModel.getVedleggListe().getVedlegg().size(), 1);
        Engangsstønad ytelse = (Engangsstønad) søknad.getYtelse();
        assertEquals(ytelse.getRelasjonTilBarn().getAntallBarn(), dokmotModel.getOpplysningerOmBarn());
    }

    @Test
    public void testDokmotMarshalling() throws Exception {
        Søknad søknad = engangssøknad(true, TestUtils.fremtidigFødsel(), TestUtils.valgfrittVedlegg());
        SoeknadsskjemaEngangsstoenad dokmotModel = søknadXMLGenerator.toDokmotModel(søknad);
        SoeknadsskjemaEngangsstoenad unmarshalled = unmarshal(søknadXMLGenerator.toXML(søknad), SØKNADCTX,
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
