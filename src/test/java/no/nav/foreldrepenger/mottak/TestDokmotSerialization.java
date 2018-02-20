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
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.pdf.PdfGenerator;
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
    @Autowired
    PdfGenerator pdfGenerator;

    @Test
    public void testSøknadUtlandXML() throws Exception {
        test(engangssøknad(true));
    }

    @Test
    public void testKonvoluttXML() throws Exception {
        Søknad engangssøknad = engangssøknad(true, TestUtils.fremtidigFødsel(), TestUtils.valgfrittVedlegg());
        Dokumentforsendelse model = søknadXMLKonvoluttGenerator.toDokmotModel(engangssøknad);
        String konvolutt = søknadXMLKonvoluttGenerator.toXML(engangssøknad);
        System.out.println(konvolutt);
        Dokumentforsendelse unmarshalled = unmarshal(konvolutt, FORSENDELSECTX, Dokumentforsendelse.class);
        Dokumentinnhold pdf = unmarshalled.getHoveddokument().getDokumentinnholdListe().get(0);
        assertTrue(TestUtils.hasPdfSignature(pdf.getDokument()));
        Dokumentinnhold søknadsXML = unmarshalled.getHoveddokument().getDokumentinnholdListe().get(1);
        SoeknadsskjemaEngangsstoenad deserializedSøknadModel = unmarshal(søknadsXML.getDokument(), SØKNADCTX,
                SoeknadsskjemaEngangsstoenad.class);
        System.out.println(søknadXMLGenerator.toXML(deserializedSøknadModel));
        // assertEquals(model.get deserializedSøknad.getSoknadsvalg().getFoedselEllerAdopsjon());

    }

    private void test(Søknad engangssøknad) {
        SoeknadsskjemaEngangsstoenad model = søknadXMLGenerator.toDokmotModel(engangssøknad);
        String xml = søknadXMLGenerator.toXML(engangssøknad);
        SoeknadsskjemaEngangsstoenad unmarshalled = unmarshal(xml, SØKNADCTX, SoeknadsskjemaEngangsstoenad.class);
        assertEquals(model.getSoknadsvalg().getStoenadstype(), unmarshalled.getSoknadsvalg().getStoenadstype());
        assertEquals(model.getSoknadsvalg().getFoedselEllerAdopsjon(),
                unmarshalled.getSoknadsvalg().getFoedselEllerAdopsjon());
        assertEquals(model.getTilknytningNorge().isOppholdNorgeNaa(),
                unmarshalled.getTilknytningNorge().isOppholdNorgeNaa());
        assertEquals(model.getTilknytningNorge().isTidligereOppholdNorge(),
                unmarshalled.getTilknytningNorge().isTidligereOppholdNorge());
        assertEquals(unmarshalled.getTilknytningNorge().getFremtidigOppholdUtenlands().getUtenlandsopphold().size(), 1);
        assertEquals(unmarshalled.getTilknytningNorge().getTidligereOppholdUtenlands().getUtenlandsopphold().size(), 1);
        assertEquals(unmarshalled.getOpplysningerOmBarn().getAntallBarn(), 1);
    }
}
