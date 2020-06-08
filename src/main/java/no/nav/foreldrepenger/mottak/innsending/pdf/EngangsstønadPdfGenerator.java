package no.nav.foreldrepenger.mottak.innsending.pdf;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.felles.annenforelder.AnnenForelder;
import no.nav.foreldrepenger.mottak.domain.felles.annenforelder.NorskForelder;
import no.nav.foreldrepenger.mottak.domain.felles.annenforelder.UkjentForelder;
import no.nav.foreldrepenger.mottak.domain.felles.annenforelder.UtenlandskForelder;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.Fødsel;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;
import no.nav.foreldrepenger.mottak.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.Blokk;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.DokumentBestilling;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.DokumentPerson;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.FeltBlokk;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.FritekstBlokk;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.GruppeBlokk;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.MottattDato;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.TabellRad;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.TemaBlokk;
import no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste.PdfGenerator;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.util.Pair;

@Component
public class EngangsstønadPdfGenerator implements MappablePdfGenerator {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final SøknadTextFormatter textFormatter;
    private final PdfGenerator pdfGenerator;

    public EngangsstønadPdfGenerator(SøknadTextFormatter textFormatter, PdfGenerator pdfGenerator) {
        this.textFormatter = textFormatter;
        this.pdfGenerator = pdfGenerator;
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return new MapperEgenskaper(SøknadType.INITIELL_ENGANGSSTØNAD);
    }

    @Override
    public byte[] generer(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        DokumentBestilling engangsstønadSøknad = engangsstønadSøknadFra(søknad, søker);
        return pdfGenerator.generate(engangsstønadSøknad);
    }

    private DokumentBestilling engangsstønadSøknadFra(Søknad søknad, Person søker) {
        return DokumentBestilling.builder()
                .dokument(txt("søknad_engang"))
                .søker(personFra(søker))
                .mottattDato(mottattDato())
                .temaer(lagOverskrifter(søknad))
                .build();
    }

    private MottattDato mottattDato() {
        return new MottattDato(txt("mottattid"), FMT.format(LocalDateTime.now()));
    }

    private List<TemaBlokk> lagOverskrifter(Søknad søknad) {
        var stønad = Engangsstønad.class.cast(søknad.getYtelse());
        var medlemsskap = stønad.getMedlemsskap();
        var annenForelder = stønad.getAnnenForelder();
        List<TemaBlokk> grupper = new ArrayList<>();

        // info om barn
        grupper.add(omBarn(søknad, stønad));

        // info om annen forelder
        if (annenForelder.hasId()) {
            grupper.add(omAnnenForelder(annenForelder));
        }

        // info om utenlandsopphold og trygdetilknytning
        grupper.add(tilknytning(medlemsskap, stønad));

        return grupper;
    }

    private TemaBlokk omAnnenForelder(AnnenForelder annenForelder) {
        List<Blokk> farInfo = new ArrayList<>();
        if (annenForelder instanceof NorskForelder) {
            farInfo.addAll(norskForelder(annenForelder));
        }
        if (annenForelder instanceof UtenlandskForelder) {
            farInfo.addAll(utenlandskForelder(annenForelder));
        }
        if (annenForelder instanceof UkjentForelder) {
            farInfo.add(new FritekstBlokk(txt("annenforelderukjent")));
        }
        return TemaBlokk.builder()
            .medOverskrift(txt("omannenforelder"))
            .medUnderBlokker(farInfo)
            .build();
    }

    private List<FeltBlokk> utenlandskForelder(AnnenForelder annenForelder) {
        var utenlandsForelder = (UtenlandskForelder) annenForelder;
        List<FeltBlokk> info = new ArrayList<>();
        info.add(new FeltBlokk(txt("nasjonalitet"),
                textFormatter.countryName(utenlandsForelder.getLand(), utenlandsForelder.getLand().getName())));
        info.add(new FeltBlokk(txt("navn"), utenlandsForelder.getNavn()));
        if (utenlandsForelder.getId() != null) {
            info.add(new FeltBlokk(txt("utenlandskid"), utenlandsForelder.getId()));
        }
        return info;
    }

    private List<FeltBlokk> norskForelder(AnnenForelder annenForelder) {
        var norskForelder = (NorskForelder) annenForelder;
        List<FeltBlokk> info = new ArrayList<>();
        info.add(new FeltBlokk(txt("nasjonalitet"), txt("nasjonalitet.norsk")));
        info.add(new FeltBlokk(txt("navn"), norskForelder.getNavn()));
        info.add(new FeltBlokk(txt("fødselsnummer"), norskForelder.getFnr().getFnr()));
        return info;
    }

    private TemaBlokk tilknytning(Medlemsskap medlemsskap, Engangsstønad stønad) {
        var fødselssted = fødselssted(medlemsskap, stønad);
        var tidligereUtenlandsopphold = textFormatter
                .utenlandsPerioder(medlemsskap.getTidligereOppholdsInfo().getUtenlandsOpphold());
        List<Blokk> tabeller = new ArrayList<>();
        if (!tidligereUtenlandsopphold.isEmpty()) {
            tabeller.add(new GruppeBlokk(txt("siste12"), tabellRader(tidligereUtenlandsopphold)));
        }
        var fremtidige = textFormatter.utenlandsPerioder(medlemsskap.getFramtidigOppholdsInfo().getUtenlandsOpphold());
        if (!fremtidige.isEmpty()) {
            tabeller.add(new GruppeBlokk(txt("neste12"), tabellRader(fremtidige)));
        }
        tabeller.add(new FritekstBlokk(fødselssted));
        return TemaBlokk.builder()
            .medOverskrift(txt("tilknytning"))
            .medUnderBlokker(tabeller)
            .build();
    }

    private static List<TabellRad> tabellRader(List<Pair<String, String>> rader) {
        return rader.stream()
                .map(r -> new TabellRad(r.getFirst(), r.getSecond(), null))
                .collect(Collectors.toList());
    }

    private String fødselssted(Medlemsskap medlemsskap, Engangsstønad stønad) {
        if (erFremtidigFødsel(stønad)) {
            return textFormatter.fromMessageSource("terminføderi",
                    textFormatter.countryName(medlemsskap.landVedDato(stønad.getRelasjonTilBarn().relasjonsDato())),
                    stønad.getRelasjonTilBarn().getAntallBarn() > 1 ? "a" : "et");
        } else {
            Fødsel fødsel = (Fødsel) stønad.getRelasjonTilBarn();
            CountryCode land = stønad.getMedlemsskap().landVedDato(fødsel.getFødselsdato().get(0));
            return textFormatter.fromMessageSource("fødtei",
                    textFormatter.countryName(land),
                    fødsel.getAntallBarn() > 1 ? "a" : "et");
        }
    }

    private TemaBlokk omBarn(Søknad søknad, Engangsstønad stønad) {
        return TemaBlokk.builder()
            .medOverskrift(txt("ombarn"))
            .medUnderBlokker(omFødsel(søknad, stønad))
            .build();
    }

    private List<FritekstBlokk> omFødsel(Søknad søknad, Engangsstønad stønad) {
        if (erFremtidigFødsel(stønad)) {
            return fødsel(søknad, stønad);
        } else {
            return født(stønad);
        }
    }

    private List<FritekstBlokk> født(Engangsstønad stønad) {
        var ff = (Fødsel) stønad.getRelasjonTilBarn();
        var tekst = txt("gjelderfødselsdato", stønad.getRelasjonTilBarn().getAntallBarn(),
                textFormatter.datoer(ff.getFødselsdato()));
        return Collections.singletonList(new FritekstBlokk(tekst));
    }

    private List<FritekstBlokk> fødsel(Søknad søknad, Engangsstønad stønad) {
        var ff = (FremtidigFødsel) stønad.getRelasjonTilBarn();
        var antallBarn = stønad.getRelasjonTilBarn().getAntallBarn();
        var termindato = textFormatter.dato(ff.getTerminDato());
        var barnInfo = new FritekstBlokk(txt("gjeldertermindato", antallBarn, termindato));
        if (!søknad.getPåkrevdeVedlegg().isEmpty()) {
            var vedleggInfo = new FritekstBlokk(textFormatter.fromMessageSource("terminbekreftelsedatert",
                    textFormatter.dato(ff.getUtstedtDato())));
            return List.of(barnInfo, vedleggInfo);
        }
        return Collections.singletonList(barnInfo);
    }

    private static boolean erFremtidigFødsel(Engangsstønad stønad) {
        return stønad.getRelasjonTilBarn() instanceof FremtidigFødsel;
    }

    private String txt(String gjelder, Object... values) {
        return textFormatter.fromMessageSource(gjelder, values);
    }

    private String txt(String key) {
        return textFormatter.fromMessageSource(key);
    }

    private DokumentPerson personFra(Person person) {
        var navn = textFormatter.sammensattNavn(new Navn(person.getFornavn(),
            person.getMellomnavn(), person.getEtternavn(), person.getKjønn()));
        return DokumentPerson.builder().navn(navn).id(person.getFnr().getFnr()).build();
    }

    private FeltBlokk feltFra(String felt, String verdi) {
        return FeltBlokk.builder()
            .medFelt(felt)
            .medVerdi(verdi)
            .build();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [textFormatter=" + textFormatter + "]";
    }
}
