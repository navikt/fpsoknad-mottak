package no.nav.foreldrepenger.mottak.domain;

import static java.util.stream.Collectors.toList;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.FoedselEllerAdopsjon.FOEDSEL;
import static no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.Stoenadstype.ENGANGSSTOENADMOR;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.Aktoer;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.FoedselEllerAdopsjon;
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
        return toXML(toDokmotModel(søknad));
    }

    @Override
    public SoeknadsskjemaEngangsstoenad toDokmotModel(Søknad søknad) {
        Engangsstønad engangsstønad = Engangsstønad.class.cast(søknad.getYtelse());
        return new SoeknadsskjemaEngangsstoenad()
                .withBruker(brukerFra(søknad.getSøker().getBruker()))
                .withOpplysningerOmBarn(barnFra(søknad, engangsstønad))
                .withSoknadsvalg(søknadsvalgFra(søknad, engangsstønad))
                .withTilknytningNorge((tilknytningFra(engangsstønad.getMedlemsskap())))
                .withOpplysningerOmFar(farFra(engangsstønad.getAnnenForelder()))
                .withTilleggsopplysninger(søknad.getTilleggsopplysninger());

        // TODO vedlegg

    }

    private static Soknadsvalg søknadsvalgFra(Søknad søknad, Engangsstønad engangsstønad) {
        return new Soknadsvalg()
                .withStoenadstype(rolleFra(søknad.getSøker().getSøknadsRolle()))
                .withFoedselEllerAdopsjon(typeFra(engangsstønad.getRelasjonTilBarn()));
    }

    private static FoedselEllerAdopsjon typeFra(RelasjonTilBarn relasjonTilBarn) {

        if (relasjonTilBarn instanceof FremtidigFødsel || relasjonTilBarn instanceof Fødsel) {
            return FOEDSEL;
        }
        throw new IllegalArgumentException(relasjonTilBarn.getClass().getSimpleName() + " er foreløpig ikke støttet");
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
            marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
            return marshaller;
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private String toXML(SoeknadsskjemaEngangsstoenad model) {
        try {
            StringWriter sw = new StringWriter();
            marshaller.marshal(model, sw);
            return sw.toString();
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static TilknytningNorge tilknytningFra(Medlemsskap medlemsskap) {
        return new TilknytningNorge()
                .withOppholdNorgeNaa(true) // What else ?
                .withTidligereOppholdNorge(medlemsskap.getTidligereOppholdsInfo().isBoddINorge())
                .withTidligereOppholdUtenlands(tidligereOppholdUtenlandsFra(medlemsskap.getTidligereOppholdsInfo()))
                .withFremtidigOppholdNorge(medlemsskap.getFremtidigOppholdsInfo().isNorgeNeste12())
                .withFremtidigOppholdUtenlands(framtidigOppholdUtenlandsFra(medlemsskap.getFremtidigOppholdsInfo()));
    }

    private static TidligereOppholdUtenlands tidligereOppholdUtenlandsFra(TidligereOppholdsInformasjon tidligere) {
        return new TidligereOppholdUtenlands()
                .withUtenlandsoppholds(tidligere.getUtenlandsOpphold()
                        .stream()
                        .map(DokmotEngangsstønadXMLGenerator::utenlandsopphold)
                        .collect(toList()));
    }

    private static FremtidigOppholdUtenlands framtidigOppholdUtenlandsFra(FramtidigOppholdsInformasjon framtid) {
        return new FremtidigOppholdUtenlands()
                .withUtenlandsoppholds(framtid.getUtenlandsOpphold()
                        .stream()
                        .map(DokmotEngangsstønadXMLGenerator::utenlandsopphold)
                        .collect(toList()));
    }

    private static Utenlandsopphold utenlandsopphold(no.nav.foreldrepenger.mottak.domain.Utenlandsopphold opphold) {
        return new Utenlandsopphold()
                .withLand(new Landkoder()
                        .withValue(opphold.getLand().getAlpha3()))
                .withPeriode(new Periode()
                        .withFom(opphold.getVarighet().getFom())
                        .withTom(opphold.getVarighet().getTom()));
    }

    private static OpplysningerOmBarn barnFra(Søknad søknad, Engangsstønad engangsstønad) {
        RelasjonTilBarn relasjon = engangsstønad.getRelasjonTilBarn();
        if (relasjon instanceof FremtidigFødsel) {
            return fremtidigFødsel(FremtidigFødsel.class.cast(engangsstønad));
        }
        if (relasjon instanceof Fødsel) {
            return fødsel(Fødsel.class.cast(engangsstønad));
        }
        throw new IllegalArgumentException(
                "Relasjon " + engangsstønad.getClass().getSimpleName() + " fireløpig ikke støttet");
    }

    private static OpplysningerOmBarn fødsel(Fødsel fødsel) {
        return barn(fødsel)
                .withFoedselsdatoes(fødsel.getFødselsdato());
    }

    private static OpplysningerOmBarn barn(RelasjonTilBarn relasjonTilBarn) {
        return new OpplysningerOmBarn()
                .withAntallBarn(relasjonTilBarn.getAntallBarn());
    }

    private static OpplysningerOmBarn fremtidigFødsel(FremtidigFødsel fremtidigFødsel) {
        return barn(fremtidigFødsel)
                .withTermindato(fremtidigFødsel.getTerminDato())
                .withTerminbekreftelsedato(fremtidigFødsel.getUtstedtDato());
    }

    private static Stoenadstype rolleFra(BrukerRolle rolle) {
        switch (rolle) {
        case MOR:
            return ENGANGSSTOENADMOR;
        default:
            throw new IllegalArgumentException(rolle + " foreløpig ikke støttet");
        }
    }

    private static OpplysningerOmFar farFra(AnnenForelder annenForelder) {
        if (annenForelder == null) {
            return null;
        }
        if (annenForelder instanceof UkjentForelder) {
            return new OpplysningerOmFar()
                    .withKanIkkeOppgiFar(new KanIkkeOppgiFar()
                            .withAarsak("Ukjent annen forelder"));
        }
        if (annenForelder instanceof NorskForelder) {
            // Todo Navn ?
            return new OpplysningerOmFar()
                    .withPersonidentifikator(NorskForelder.class.cast(annenForelder).getBruker().getValue());
        }
        if (annenForelder instanceof UtenlandskForelder) {
            // TODO navn og eller utenlandsk FNR?
            return new OpplysningerOmFar()
                    .withKanIkkeOppgiFar(new KanIkkeOppgiFar()
                            .withUtenlandskfnrLand(new Landkoder()
                                    .withKode(UtenlandskForelder.class.cast(annenForelder).getLand().getAlpha3())));
        }
        throw new IllegalArgumentException("This should never happen");
    }

    private static String søker(Søker søker) {
        return søker.getBruker().getValue();
    }

}
