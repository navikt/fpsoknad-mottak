package no.nav.foreldrepenger.mottak.domain;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.FoedselEllerAdopsjon.FOEDSEL;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.Aktoer;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.AktoerId;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.KanIkkeOppgiFar;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.Landkoder;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.OpplysningerOmBarn;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.OpplysningerOmFar;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.Periode;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.Soknadsvalg;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.Stoenadstype;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.TilknytningNorge;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.TilknytningNorge.FremtidigOppholdUtenlands;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.TilknytningNorge.TidligereOppholdUtenlands;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.Utenlandsopphold;

@Service
public class DokmotEngangsstønadXMLGenerator extends DokmotXMLGenerator {

    private final Marshaller marshaller;

    public DokmotEngangsstønadXMLGenerator() {
        this(marshaller());

    }

    public DokmotEngangsstønadXMLGenerator(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    @Override
    public String toXML(Søknad søknad) {
        return toXML(toModel(søknad));
    }

    @Override
    public SoeknadsskjemaEngangsstoenad toModel(Søknad søknad) {
        SoeknadsskjemaEngangsstoenad dokmotModel = new SoeknadsskjemaEngangsstoenad()
                .withBruker(brukerFra(søknad.getSøker().getBruker()));
        Engangsstønad engangsstønad = (Engangsstønad) (søknad.getYtelse());
        RelasjonTilBarn relasjonTilBarn = engangsstønad.getRelasjonTilBarn();
        Soknadsvalg soknadsvalg = new Soknadsvalg();
        soknadsvalg.setStoenadstype(rolleFra(søknad));
        dokmotModel.setOpplysningerOmBarn(barnFra(søknad, relasjonTilBarn, soknadsvalg));
        if (engangsstønad.getAnnenForelder() != null) {
            dokmotModel.setOpplysningerOmFar(farFra(engangsstønad.getAnnenForelder()));
        }
        dokmotModel.setSoknadsvalg(soknadsvalg);
        dokmotModel.setTilknytningNorge((tilknytningFra(engangsstønad.getMedlemsskap())));
        return dokmotModel;
    }

    private static Aktoer brukerFra(Bruker bruker) {
        if (bruker instanceof Fodselsnummer) {
            return new no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.Bruker(bruker.getValue());
        }
        if (bruker instanceof AktorId) {
            return new no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.AktoerId(bruker.getValue());
        }
        throw new IllegalArgumentException("This will never happen");
    }

    private static JAXBContext context() {
        try {
            return JAXBContext.newInstance(SoeknadsskjemaEngangsstoenad.class);
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static Marshaller marshaller() {
        try {
            Marshaller marshaller = context().createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            return marshaller;
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private String toXML(SoeknadsskjemaEngangsstoenad xml) {
        try {
            StringWriter sw = new StringWriter();
            marshaller.marshal(xml, sw);
            return sw.toString();
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static TilknytningNorge tilknytningFra(Medlemsskap medlemsskap) {
        return new TilknytningNorge()
                .withOppholdNorgeNaa(true)
                .withTidligereOppholdNorge(medlemsskap.getTidligereOppholdsInfo().isBoddINorge())
                .withTidligereOppholdUtenlands(tidligereOppholdUtenlands(medlemsskap.getTidligereOppholdsInfo()))
                .withFremtidigOppholdNorge(medlemsskap.getFremtidigOppholdsInfo().isNorgeNeste12())
                .withFremtidigOppholdUtenlands(framtidigOppholdUtenlands(medlemsskap.getFremtidigOppholdsInfo()));
    }

    private static TidligereOppholdUtenlands tidligereOppholdUtenlands(TidligereOppholdsInformasjon tidligere) {
        return new TidligereOppholdUtenlands()
                .withUtenlandsoppholds(tidligere.getUtenlandsOpphold()
                        .stream()
                        .map(DokmotEngangsstønadXMLGenerator::utenlandsopphold)
                        .collect(toList()));
    }

    private static FremtidigOppholdUtenlands framtidigOppholdUtenlands(FramtidigOppholdsInformasjon framtid) {
        return new FremtidigOppholdUtenlands()
                .withUtenlandsoppholds(framtid.getUtenlandsOpphold()
                        .stream()
                        .map(DokmotEngangsstønadXMLGenerator::utenlandsopphold)
                        .collect(toList()));
    }

    private static Utenlandsopphold utenlandsopphold(no.nav.foreldrepenger.mottak.domain.Utenlandsopphold opphold) {
        return new Utenlandsopphold()
                .withLand(new Landkoder()
                        .withValue(opphold.getLand().getAlpha2()))
                .withPeriode(new Periode()
                        .withFom(opphold.getVarighet().getFom())
                        .withTom(opphold.getVarighet().getTom()));
    }

    private static OpplysningerOmBarn barnFra(Søknad søknad, RelasjonTilBarn relasjonTilBarn, Soknadsvalg soknadsvalg) {

        if (relasjonTilBarn instanceof FremtidigFødsel) {
            soknadsvalg.setFoedselEllerAdopsjon(FOEDSEL);
            return fremtidigFødsel(FremtidigFødsel.class.cast(relasjonTilBarn), soknadsvalg);
        }
        if (relasjonTilBarn instanceof Fødsel) {
            soknadsvalg.setFoedselEllerAdopsjon(FOEDSEL);
            return fødsel(Fødsel.class.cast(relasjonTilBarn), soknadsvalg);
        }
        throw new IllegalArgumentException(
                "Relasjon " + relasjonTilBarn.getClass().getSimpleName() + " fireløpig ikke støttet");
    }

    private static OpplysningerOmBarn fødsel(Fødsel fødsel, Soknadsvalg soknadsvalg) {
        return barn(fødsel).withFoedselsdatoes(fødsel.getFødselsdato());
    }

    private static OpplysningerOmBarn barn(RelasjonTilBarn relasjonTilBarn) {
        return new OpplysningerOmBarn().withAntallBarn(relasjonTilBarn.getAntallBarn());
    }

    private static OpplysningerOmBarn fremtidigFødsel(FremtidigFødsel fremtidigFødsel, Soknadsvalg soknadsvalg) {
        return barn(fremtidigFødsel)
                .withTermindato(fremtidigFødsel.getTerminDato())
                .withTerminbekreftelsedato(fremtidigFødsel.getUtstedtDato());
    }

    private static Stoenadstype rolleFra(Søknad søknad) {
        switch (søknad.getSøker().getSøknadsRolle()) {
        case MOR:
            return Stoenadstype.ENGANGSSTOENADMOR;
        default:
            throw new IllegalArgumentException(søknad.getSøker().getSøknadsRolle() + " foreløpig ikke støttet");
        }
    }

    private static OpplysningerOmFar farFra(AnnenForelder annenForelder) {
        OpplysningerOmFar far = new OpplysningerOmFar();
        if (annenForelder instanceof UkjentForelder) {
            KanIkkeOppgiFar kanikke = new KanIkkeOppgiFar();
            kanikke.setAarsak("Far ukjent");
            far.setKanIkkeOppgiFar(kanikke);
        }
        if (annenForelder instanceof NorskForelder) {
            NorskForelder nf = NorskForelder.class.cast(annenForelder);
            far.setPersonidentifikator(nf.getBruker().getValue());
        }
        if (annenForelder instanceof UtenlandskForelder) {
            UtenlandskForelder uf = UtenlandskForelder.class.cast(annenForelder);
        }
        return far;
    }

    private static AktoerId aktør(Søker søker) {
        return new AktoerId(søker(søker));
    }

    private static String søker(Søker søker) {
        return søker.getBruker().getValue();
    }

}
