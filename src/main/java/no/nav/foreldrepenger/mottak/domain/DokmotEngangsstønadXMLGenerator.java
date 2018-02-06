package no.nav.foreldrepenger.mottak.domain;

import java.io.StringWriter;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.Aktoer;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.AktoerId;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.FoedselEllerAdopsjon;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.KanIkkeOppgiFar;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.Landkoder;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.OpplysningerOmBarn;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.OpplysningerOmFar;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.OpplysningerOmMor;
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
        SoeknadsskjemaEngangsstoenad dokmotModel = new SoeknadsskjemaEngangsstoenad();
        dokmotModel.setBruker(aktør(søknad.getSøker()));
        Engangsstønad engangsstønad = (Engangsstønad) (søknad.getYtelse());
        RelasjonTilBarn relasjonTilBarn = engangsstønad.getRelasjonTilBarn();
        Soknadsvalg soknadsvalg = new Soknadsvalg();
        soknadsvalg.setStoenadstype(rolleFra(søknad));
        dokmotModel.setOpplysningerOmBarn(barnFra(søknad, relasjonTilBarn, soknadsvalg));
        if (engangsstønad.getAnnenForelder() != null) {
            dokmotModel.setOpplysningerOmFar(farFra(engangsstønad.getAnnenForelder()));
        }
        dokmotModel.setOpplysningerOmMor(morFra(søknad));
        dokmotModel.setSoknadsvalg(soknadsvalg);
        dokmotModel.setTilknytningNorge((tilknytningFra(engangsstønad.getMedlemsskap())));
        return dokmotModel;
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

    private TilknytningNorge tilknytningFra(Medlemsskap medlemsskap) {
        TilknytningNorge tilknytning = new TilknytningNorge();
        tilknytning.setOppholdNorgeNaa(true);
        tilknytning.setTidligereOppholdNorge((medlemsskap.getTidligereOppholdsInfo().isBoddINorge()));
        tilknytning.setTidligereOppholdUtenlands(tidligereOppholdUtenlands(medlemsskap.getTidligereOppholdsInfo()));
        if (!medlemsskap.getFremtidigOppholdsInfo().isNorgeNeste12()) {
            tilknytning.setFremtidigOppholdNorge(false);
            tilknytning.setFremtidigOppholdUtenlands(FremtidigOppholdUtenlands(medlemsskap.getFremtidigOppholdsInfo()));
        } else {
            tilknytning.setFremtidigOppholdNorge(true);
        }
        return tilknytning;
    }

    private FremtidigOppholdUtenlands FremtidigOppholdUtenlands(FramtidigOppholdsInformasjon fremtidigOppholdsInfo) {
        FremtidigOppholdUtenlands framtidigUtenlands = new FremtidigOppholdUtenlands();
        Utenlandsopphold uo = new Utenlandsopphold();
        framtidigUtenlands.getUtenlandsoppholds().add(uo);
        return framtidigUtenlands;
    }

    private TidligereOppholdUtenlands tidligereOppholdUtenlands(TidligereOppholdsInformasjon tidligere) {
        TidligereOppholdUtenlands utenlands = new TidligereOppholdUtenlands();
        utenlands.getUtenlandsoppholds()
                .addAll(tidligere.getUtenlandsOpphold().stream().map(this::land).collect(Collectors.toList()));
        return utenlands;
    }

    private Utenlandsopphold land(no.nav.foreldrepenger.mottak.domain.Utenlandsopphold opphold) {
        Utenlandsopphold uo = new Utenlandsopphold();
        Landkoder land = new Landkoder();
        land.setValue(opphold.getLand().getAlpha2());
        uo.setLand(land);
        Periode periode = new Periode();
        periode.setFom(opphold.getVarighet().getFom());
        periode.setTom(opphold.getVarighet().getTom());
        uo.setPeriode(periode);
        return uo;

    }

    private OpplysningerOmMor morFra(Søknad søknad) {
        OpplysningerOmMor mor = new OpplysningerOmMor();
        mor.setPersonidentifikator(søker(søknad.getSøker()));
        return mor;
    }

    private Aktoer aktør(Søker søker) {
        return new AktoerId(søker(søker));
    }

    private static String søker(Søker søker) {
        return søker.getAktorid().getValue();
    }

    private OpplysningerOmBarn barnFra(Søknad søknad, RelasjonTilBarn relasjonTilBarn, Soknadsvalg soknadsvalg) {
        OpplysningerOmBarn barn = new OpplysningerOmBarn();
        barn.setAntallBarn(relasjonTilBarn.getAntallBarn());
        barn.setBegrunnelse(søknad.getBegrunnelseForSenSøknad());
        if (relasjonTilBarn instanceof FremtidigFødsel) {
            FremtidigFødsel ff = FremtidigFødsel.class.cast(relasjonTilBarn);
            barn.setTermindato(ff.getTerminDato());
            barn.setTerminbekreftelsedato(ff.getUtstedtDato());
            soknadsvalg.setFoedselEllerAdopsjon(FoedselEllerAdopsjon.FOEDSEL);
        }
        if (relasjonTilBarn instanceof Fødsel) {
            Fødsel f = Fødsel.class.cast(relasjonTilBarn);
            soknadsvalg.setFoedselEllerAdopsjon(FoedselEllerAdopsjon.FOEDSEL);
        }
        return barn;
    }

    private Stoenadstype rolleFra(Søknad søknad) {
        switch (søknad.getSøker().getSøknadsRolle()) {
        case MOR:
            return Stoenadstype.ENGANGSSTOENADMOR;
        default:
            throw new IllegalArgumentException("Far ikke støttet foreløpig");
        }
    }

    private OpplysningerOmFar farFra(AnnenForelder annenForelder) {
        OpplysningerOmFar far = new OpplysningerOmFar();
        if (annenForelder instanceof UkjentForelder) {
            KanIkkeOppgiFar kanikke = new KanIkkeOppgiFar();
            kanikke.setAarsak("Far ukjent");
            far.setKanIkkeOppgiFar(kanikke);
        }
        if (annenForelder instanceof NorskForelder) {
            NorskForelder nf = NorskForelder.class.cast(annenForelder);
            far.setPersonidentifikator(nf.getAktørId().getValue());
        }
        if (annenForelder instanceof UtenlandskForelder) {
            UtenlandskForelder uf = UtenlandskForelder.class.cast(annenForelder);
        }
        return far;
    }

}
