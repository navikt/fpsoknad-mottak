package no.nav.foreldrepenger.mottak.dokmot;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.FoedselEllerAdopsjon.FOEDSEL;
import static no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.Innsendingsvalg.LASTET_OPP;
import static no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.Stoenadstype.ENGANGSSTOENADMOR;

import java.util.List;
import java.util.stream.Stream;

import javax.xml.bind.JAXBContext;

import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.NorskForelder;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.UkjentForelder;
import no.nav.foreldrepenger.mottak.domain.UtenlandskForelder;
import no.nav.foreldrepenger.mottak.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.felles.AnnenForelder;
import no.nav.foreldrepenger.mottak.domain.felles.FramtidigOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.felles.Fødsel;
import no.nav.foreldrepenger.mottak.domain.felles.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.PåkrevdVedlegg;
import no.nav.foreldrepenger.mottak.domain.felles.RelasjonTilBarn;
import no.nav.foreldrepenger.mottak.domain.felles.TidligereOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.ValgfrittVedlegg;
import no.nav.foreldrepenger.mottak.pdf.PdfGenerator;
import no.nav.foreldrepenger.mottak.util.Jaxb;
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
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.Vedlegg;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.VedleggListe;

@Service
public class DokmotEngangsstønadXMLGenerator {

    private static final JAXBContext CONTEXT = Jaxb.context(SoeknadsskjemaEngangsstoenad.class);
    private final PdfGenerator pdfGenerator;

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

        // Mor er bruker i dette use-caset, derfor setter vi ikke opplysninger om mor,
        // samme som Team Søknad gjør
        Engangsstønad engangsstønad = Engangsstønad.class.cast(søknad.getYtelse());
        return new SoeknadsskjemaEngangsstoenad()
                .withBruker(brukerFra(søknad.getSøker().getFnr()))
                .withOpplysningerOmBarn(barnFra(søknad, engangsstønad))
                .withSoknadsvalg(søknadsvalgFra(søknad, engangsstønad))
                .withTilknytningNorge((tilknytningFra(engangsstønad.getMedlemsskap())))
                .withOpplysningerOmFar(farFra(engangsstønad.getAnnenForelder()))
                .withTilleggsopplysninger(søknad.getTilleggsopplysninger())
                .withVedleggListe(vedleggFra(søknad.getPåkrevdeVedlegg(), søknad.getFrivilligeVedlegg()));
    }

    private static VedleggListe vedleggFra(List<PåkrevdVedlegg> påkrevdeVedlegg,
            List<ValgfrittVedlegg> valgfrieVedlegg) {
        return new VedleggListe()
                .withVedlegg(Stream.concat(påkrevdeVedlegg.stream(), valgfrieVedlegg.stream())
                        .map(DokmotEngangsstønadXMLGenerator::vedleggFra)
                        .collect(toList()));
    }

    private static Vedlegg vedleggFra(no.nav.foreldrepenger.mottak.domain.felles.Vedlegg vedlegg) {
        return new Vedlegg()
                .withSkjemanummer(vedlegg.getMetadata().getSkjemanummer().id)
                .withInnsendingsvalg(LASTET_OPP)
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
        throw new IllegalArgumentException(
                "Relasjon til barn " + relasjonTilBarn.getClass().getSimpleName() + " er foreløpig ikke støttet");
    }

    private static TilknytningNorge tilknytningFra(Medlemsskap medlemsskap) {
        return new TilknytningNorge()
                .withOppholdNorgeNaa(medlemsskap.getFramtidigOppholdsInfo().isFødselNorge())
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

    private static Utenlandsopphold utenlandsoppholdFra(no.nav.foreldrepenger.mottak.domain.felles.Utenlandsopphold opphold) {
        return new Utenlandsopphold()
                .withLand(new Landkoder()
                        .withKode(opphold.getLand().getAlpha3()))
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
                "Relasjon til barn " + relasjon.getClass().getSimpleName() + " er foreløpig ikke støttet");
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
            throw new IllegalArgumentException("Rolle " + rolle + " er foreløpig ikke støttet");
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
            return norskFarFra(annenForelder);
        }
        if (annenForelder instanceof UtenlandskForelder) {
            return utenlandskFarFra(annenForelder);
        }
        throw new IllegalArgumentException("Dette skal aldri skje, hva har du gjort nå, da ?");
    }

    private static OpplysningerOmFar ukjentFar() {
        return new OpplysningerOmFar()
                .withKanIkkeOppgiFar(new KanIkkeOppgiFar()
                        .withAarsak("ukjent"));
    }

    private static OpplysningerOmFar utenlandskFarFra(AnnenForelder annenForelder) {
        UtenlandskForelder utenlandsskFar = UtenlandskForelder.class.cast(annenForelder);
        OpplysningerOmFar far = new OpplysningerOmFar()
                .withKanIkkeOppgiFar(new KanIkkeOppgiFar()
                        .withAarsak("utenlandsk")
                        .withUtenlandskfnrLand(new Landkoder()
                                .withKode(utenlandsskFar.getLand().getAlpha3()))
                        .withUtenlandskfnrEllerForklaring(utenlandsskFar.getId()));
        return farMedNavnHvisSatt(far, utenlandsskFar.getNavn());
    }

    private static OpplysningerOmFar norskFarFra(AnnenForelder annenForelder) {
        NorskForelder norskFar = NorskForelder.class.cast(annenForelder);
        OpplysningerOmFar far = new OpplysningerOmFar()
                .withPersonidentifikator(norskFar.getFnr().getFnr());
        return farMedNavnHvisSatt(far, norskFar.getNavn());
    }

    private static OpplysningerOmFar farMedNavnHvisSatt(OpplysningerOmFar far, Navn navn) {
        return navn != null
                ? far.withFornavn(navn.getFornavn())
                        .withEtternavn(mellomnavn(navn.getMellomnavn()) + navn.getEtternavn())
                : far;
    }

    private static String mellomnavn(String mellomnavn) {
        return mellomnavn == null ? "" : mellomnavn + " ";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [pdfGenerator=" + pdfGenerator + "]";
    }
}
