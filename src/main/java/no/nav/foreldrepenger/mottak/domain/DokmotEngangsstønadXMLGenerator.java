package no.nav.foreldrepenger.mottak.domain;

import static java.util.stream.Collectors.toList;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.FoedselEllerAdopsjon.FOEDSEL;
import static no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.Stoenadstype.ENGANGSSTOENADMOR;

import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.Aktoer;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.AktoerId;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.FoedselEllerAdopsjon;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.Innsendingsvalg;
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
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.Vedlegg;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.VedleggListe;

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

        // Mor er det samme som bruker i dette use-caset
        Engangsstønad engangsstønad = Engangsstønad.class.cast(søknad.getYtelse());
        return new SoeknadsskjemaEngangsstoenad()
                .withBruker(brukerFra(søknad.getSøker().getBruker()))
                .withOpplysningerOmBarn(barnFra(søknad, engangsstønad))
                .withSoknadsvalg(søknadsvalgFra(søknad, engangsstønad))
                .withTilknytningNorge((tilknytningFra(engangsstønad.getMedlemsskap())))
                .withOpplysningerOmFar(farFra(engangsstønad.getAnnenForelder()))
                .withTilleggsopplysninger(søknad.getTilleggsopplysninger())
                .withVedleggListe(vedleggFra(søknad.getPåkrevdeVedlegg()));
    }

    private VedleggListe vedleggFra(List<PåkrevdVedlegg> påkrevdeVedlegg) {
        return new VedleggListe()
                .withVedleggs(påkrevdeVedlegg
                        .stream()
                        .map(DokmotEngangsstønadXMLGenerator::vedleggFra)
                        .collect(toList()));
    }

    private static Vedlegg vedleggFra(PåkrevdVedlegg vedlegg) {
        return new Vedlegg()
                .withSkjemanummer(vedlegg.getMetadata().getSkjemanummer().dokumentTypeId())
                .withInnsendingsvalg(Innsendingsvalg.LASTET_OPP)
                .withErPaakrevdISoeknadsdialog(true);
    }

    private static Aktoer brukerFra(Bruker bruker) {
        if (bruker instanceof Fodselsnummer) {
            return new no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.Bruker(bruker.getValue());
        }
        if (bruker instanceof AktorId) {
            return new AktoerId(bruker.getValue());
        }
        throw new IllegalArgumentException("Dette vil aldri skje");
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
                .withFremtidigOppholdNorge(medlemsskap.getFramtidigOppholdsInfo().isNorgeNeste12())
                .withFremtidigOppholdUtenlands(framtidigOppholdUtenlandsFra(medlemsskap.getFramtidigOppholdsInfo()));
    }

    private static TidligereOppholdUtenlands tidligereOppholdUtenlandsFra(TidligereOppholdsInformasjon tidligere) {
        return new TidligereOppholdUtenlands()
                .withUtenlandsoppholds(tidligere.getUtenlandsOpphold()
                        .stream()
                        .map(DokmotEngangsstønadXMLGenerator::utenlandsoppholdFra)
                        .collect(toList()));
    }

    private static FremtidigOppholdUtenlands framtidigOppholdUtenlandsFra(FramtidigOppholdsInformasjon framtid) {
        return new FremtidigOppholdUtenlands()
                .withUtenlandsoppholds(framtid.getUtenlandsOpphold()
                        .stream()
                        .map(DokmotEngangsstønadXMLGenerator::utenlandsoppholdFra)
                        .collect(toList()));
    }

    private static Utenlandsopphold utenlandsoppholdFra(no.nav.foreldrepenger.mottak.domain.Utenlandsopphold opphold) {
        return new Utenlandsopphold()
                .withLand(new Landkoder()
                        .withValue(opphold.getLand().getAlpha2()))
                .withPeriode(new Periode()
                        .withFom(opphold.getVarighet().getFom())
                        .withTom(opphold.getVarighet().getTom()));
    }

    private static OpplysningerOmBarn barnFra(Søknad søknad, Engangsstønad engangsstønad) {
        RelasjonTilBarn relasjon = engangsstønad.getRelasjonTilBarn();
        if (relasjon instanceof FremtidigFødsel) {
            return framtidigFødselFra(FremtidigFødsel.class.cast(relasjon), søknad.getBegrunnelseForSenSøknad());
        }
        if (relasjon instanceof Fødsel) {
            return fødselFra(Fødsel.class.cast(relasjon), søknad.getBegrunnelseForSenSøknad());
        }
        throw new IllegalArgumentException(
                "Relasjon " + relasjon.getClass().getSimpleName() + " foreløpig ikke støttet");
    }

    private static OpplysningerOmBarn fødselFra(Fødsel fødsel, String begrunnelse) {
        return barnFra(fødsel, begrunnelse)
                .withFoedselsdatoes(fødsel.getFødselsdato());
    }

    private static OpplysningerOmBarn barnFra(RelasjonTilBarn relasjon, String begrunnelse) {
        return new OpplysningerOmBarn()
                .withBegrunnelse(begrunnelse)
                .withAntallBarn(relasjon.getAntallBarn());
    }

    private static OpplysningerOmBarn framtidigFødselFra(FremtidigFødsel framtidigFødsel, String begrunnelse) {
        return barnFra(framtidigFødsel, begrunnelse)
                .withTermindato(framtidigFødsel.getTerminDato())
                .withTerminbekreftelsedato(framtidigFødsel.getUtstedtDato());
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
                                    .withKode(UtenlandskForelder.class.cast(annenForelder).getLand().getAlpha2())));
        }
        throw new IllegalArgumentException("Dette skal aldri skje");
    }

}
