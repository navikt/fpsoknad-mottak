package no.nav.foreldrepenger.mottak.dokmot;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.FoedselEllerAdopsjon.FOEDSEL;
import static no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.Stoenadstype.ENGANGSSTOENADMOR;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.AnnenForelder;
import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.FramtidigOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.Fødsel;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.NorskForelder;
import no.nav.foreldrepenger.mottak.domain.PåkrevdVedlegg;
import no.nav.foreldrepenger.mottak.domain.RelasjonTilBarn;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.TidligereOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.UkjentForelder;
import no.nav.foreldrepenger.mottak.domain.UtenlandskForelder;
import no.nav.foreldrepenger.mottak.domain.ValgfrittVedlegg;
import no.nav.foreldrepenger.mottak.pdf.PdfGenerator;
import no.nav.foreldrepenger.mottak.util.Jaxb;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.Aktoer;
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
public class DokmotEngangsstønadXMLGenerator {

    private static final Logger LOG = getLogger(DokmotEngangsstønadXMLGenerator.class);

    private static final JAXBContext CONTEXT = Jaxb.context(SoeknadsskjemaEngangsstoenad.class);
    private final PdfGenerator pdfGenerator;

    @Inject
    public DokmotEngangsstønadXMLGenerator(PdfGenerator pdfGenerator) {
        this.pdfGenerator = pdfGenerator;
    }

    public byte[] toPdf(Søknad søknad) {
        return pdfGenerator.generate(søknad);
    }

    public String toXML(Søknad søknad) {
        return toXML(toDokmotModel(søknad));
    }

    public String toXML(SoeknadsskjemaEngangsstoenad model) {
        return Jaxb.marshall(CONTEXT, model);
    }

    public SoeknadsskjemaEngangsstoenad toDokmotModel(Søknad søknad) {

        // Mor er det samme som bruker i dette use-caset
        Engangsstønad engangsstønad = Engangsstønad.class.cast(søknad.getYtelse());
        return new SoeknadsskjemaEngangsstoenad()
                .withBruker(brukerFra(søknad.getSøker().getFnr()))
                .withOpplysningerOmBarn(barnFra(søknad, engangsstønad))
                .withSoknadsvalg(søknadsvalgFra(søknad, engangsstønad))
                .withTilknytningNorge((tilknytningFra(engangsstønad.getMedlemsskap())))
                .withOpplysningerOmFar(farFra(engangsstønad.getAnnenForelder()))
                .withTilleggsopplysninger(søknad.getTilleggsopplysninger())
                .withVedleggListe(
                        vedleggFra(JukseVedlegg.påkrevdVedlegg(søknad)/* søknad.getPåkrevdeVedlegg() */,
                                søknad.getFrivilligeVedlegg()));
    }

    private VedleggListe vedleggFra(List<PåkrevdVedlegg> påkrevdeVedlegg, List<ValgfrittVedlegg> valgfrieVedlegg) {
        return new VedleggListe()
                .withVedlegg(Stream.concat(påkrevdeVedlegg.stream(), valgfrieVedlegg.stream())
                        .map(DokmotEngangsstønadXMLGenerator::vedleggFra)
                        .collect(toList()));
    }

    private static Vedlegg vedleggFra(no.nav.foreldrepenger.mottak.domain.Vedlegg vedlegg) {
        return new Vedlegg()
                .withSkjemanummer(vedlegg.getMetadata().getSkjemanummer().id)
                .withInnsendingsvalg(Innsendingsvalg.LASTET_OPP)
                .withErPaakrevdISoeknadsdialog(vedlegg instanceof PåkrevdVedlegg);
    }

    private static Aktoer brukerFra(Fødselsnummer søker) {
        return new no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.Bruker(søker.getFnr());
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
                .withUtenlandsopphold(tidligere.getUtenlandsOpphold()
                        .stream()
                        .map(DokmotEngangsstønadXMLGenerator::utenlandsoppholdFra)
                        .collect(toList()));
    }

    private static FremtidigOppholdUtenlands framtidigOppholdUtenlandsFra(FramtidigOppholdsInformasjon framtid) {
        return new FremtidigOppholdUtenlands()
                .withUtenlandsopphold(framtid.getUtenlandsOpphold()
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
                .withFoedselsdato(fødsel.getFødselsdato());
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
            return ukjentFar();
        }
        if (annenForelder instanceof NorskForelder) {
            return norskFar(annenForelder);
        }
        if (annenForelder instanceof UtenlandskForelder) {
            return utenlandskFar(annenForelder);
        }
        throw new IllegalArgumentException("Dette skal aldri skje, hva har du gjort nå da ?");
    }

    private static OpplysningerOmFar ukjentFar() {
        return new OpplysningerOmFar()
                .withKanIkkeOppgiFar(new KanIkkeOppgiFar()
                        .withAarsak("Ukjent annen forelder"));
    }

    private static OpplysningerOmFar utenlandskFar(AnnenForelder annenForelder) {
        UtenlandskForelder utenlandsskFar = UtenlandskForelder.class.cast(annenForelder);
        // TODO her er det muligens noe rart
        OpplysningerOmFar far = new OpplysningerOmFar()
                .withKanIkkeOppgiFar(new KanIkkeOppgiFar()
                        .withUtenlandskfnrLand(new Landkoder()
                                .withKode(utenlandsskFar.getLand().getAlpha2())));
        return farMedNavnHvisSatt(far, utenlandsskFar.getNavn());
    }

    private static OpplysningerOmFar norskFar(AnnenForelder annenForelder) {
        NorskForelder norskFar = NorskForelder.class.cast(annenForelder);
        OpplysningerOmFar far = new OpplysningerOmFar()
                .withPersonidentifikator(norskFar.getFnr().getFnr());
        return farMedNavnHvisSatt(far, norskFar.getNavn());
    }

    private static OpplysningerOmFar farMedNavnHvisSatt(OpplysningerOmFar far, Navn navn) {
        if (navn != null) {
            return far.withFornavn(navn.getFornavn())
                    .withEtternavn(navn.getMellomnavn() + " " + navn.getEtternavn());
        }
        return far;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pdfGenerator=" + pdfGenerator + "]";
    }

}
