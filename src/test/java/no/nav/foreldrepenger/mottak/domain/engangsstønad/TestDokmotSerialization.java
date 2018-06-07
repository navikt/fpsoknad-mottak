package no.nav.foreldrepenger.mottak.domain.engangsstønad;

import static no.nav.foreldrepenger.mottak.domain.TestUtils.engangssøknad;
import static no.nav.foreldrepenger.mottak.domain.TestUtils.fødsel;
import static no.nav.foreldrepenger.mottak.domain.TestUtils.hasPdfSignature;
import static no.nav.foreldrepenger.mottak.domain.TestUtils.norskForelder;
import static no.nav.foreldrepenger.mottak.domain.TestUtils.person;
import static no.nav.foreldrepenger.mottak.domain.TestUtils.serialize;
import static no.nav.foreldrepenger.mottak.domain.TestUtils.termin;
import static no.nav.foreldrepenger.mottak.domain.TestUtils.utenlandskForelder;
import static no.nav.foreldrepenger.mottak.domain.TestUtils.valgfrittVedlegg;
import static no.nav.foreldrepenger.mottak.util.Jaxb.context;
import static no.nav.foreldrepenger.mottak.util.Jaxb.unmarshal;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.xml.bind.JAXBContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.mottak.config.MottakConfiguration;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.UUIDIdGenerator;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.felles.ValgfrittVedlegg;
import no.nav.foreldrepenger.mottak.innsending.dokmot.DokmotEngangsstønadXMLGenerator;
import no.nav.foreldrepenger.mottak.innsending.dokmot.DokmotEngangsstønadXMLKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.pdf.EngangsstønadPDFGenerator;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.Bruker;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.FoedselEllerAdopsjon;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Dokumentforsendelse;
import no.nav.melding.virksomhet.dokumentforsendelse.v1.Dokumentinnhold;

@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration(classes = { MottakConfiguration.class, EngangsstønadPDFGenerator.class,
        DokmotEngangsstønadXMLGenerator.class,
        DokmotEngangsstønadXMLKonvoluttGenerator.class, UUIDIdGenerator.class })
@AutoConfigureJsonTesters
public class TestDokmotSerialization {

    private static JAXBContext SØKNADCTX = context(SoeknadsskjemaEngangsstoenad.class);
    private static JAXBContext FORSENDELSECTX = context(Dokumentforsendelse.class);

    @Autowired
    ObjectMapper mapper;
    @Autowired
    UUIDIdGenerator refGenerator;
    @Autowired
    DokmotEngangsstønadXMLGenerator søknadXMLGenerator;
    @Autowired
    DokmotEngangsstønadXMLKonvoluttGenerator søknadXMLKonvoluttGenerator;

    @Test
    public void testSøknadUtlandXML() throws Exception {
    }

    @Test
    public void testKonvoluttXML() throws Exception {
        Søknad engangssøknad = engangssøknad(true, fødsel(), norskForelder(), valgfrittVedlegg());
        String konvolutt = søknadXMLKonvoluttGenerator.toXML(engangssøknad, person(),
                refGenerator.getOrCreate());
        Dokumentforsendelse unmarshalled = unmarshal(konvolutt, FORSENDELSECTX, Dokumentforsendelse.class);
        Dokumentinnhold pdf = unmarshalled.getHoveddokument().getDokumentinnholdListe().get(0);
        assertTrue(hasPdfSignature(pdf.getDokument()));
        Dokumentinnhold søknadsXML = unmarshalled.getHoveddokument().getDokumentinnholdListe().get(1);
        SoeknadsskjemaEngangsstoenad deserializedSøknadModel = unmarshal(søknadsXML.getDokument(), SØKNADCTX,
                SoeknadsskjemaEngangsstoenad.class);
        assertEquals(deserializedSøknadModel.getOpplysningerOmBarn().getAntallBarn(), 1);
        assertEquals(deserializedSøknadModel.getSoknadsvalg().getFoedselEllerAdopsjon(), FoedselEllerAdopsjon.FOEDSEL);
    }

    @Test
    public void testDokmotModelTransformation() throws Exception {
        ValgfrittVedlegg valgfrittVedlegg = valgfrittVedlegg();
        Søknad søknad = engangssøknad(true, termin(), norskForelder(), valgfrittVedlegg);
        Person søker = person();
        SoeknadsskjemaEngangsstoenad dokmotModel = søknadXMLGenerator.toDokmotModel(søknad, søker);
        Bruker bruker = Bruker.class.cast(dokmotModel.getBruker());
        assertEquals(søker.fnr.getFnr(), bruker.getPersonidentifikator());
        assertEquals(søknad.getBegrunnelseForSenSøknad(), dokmotModel.getOpplysningerOmBarn().getBegrunnelse());
        assertEquals(dokmotModel.getVedleggListe().getVedlegg().size(), 1);
        Engangsstønad ytelse = (Engangsstønad) søknad.getYtelse();
        assertEquals(ytelse.getRelasjonTilBarn().getAntallBarn(), dokmotModel.getOpplysningerOmBarn().getAntallBarn());
    }

    @Test
    public void testDokmotMarshalling() throws Exception {
        Søknad søknad = engangssøknad(true, termin(), utenlandskForelder(),
                valgfrittVedlegg());
        Person søker = person();
        serialize(søknad, true, mapper);
        SoeknadsskjemaEngangsstoenad dokmotModel = søknadXMLGenerator.toDokmotModel(søknad, søker);
        System.out.println(søknadXMLGenerator.toXML(søknad, søker));
        Søknad søknad1 = engangssøknad(true, fødsel(), utenlandskForelder(), valgfrittVedlegg());
        System.out.println(søknadXMLGenerator.toXML(søknad1, søker));
        SoeknadsskjemaEngangsstoenad unmarshalled = unmarshal(søknadXMLGenerator.toXML(søknad, søker), SØKNADCTX,
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
