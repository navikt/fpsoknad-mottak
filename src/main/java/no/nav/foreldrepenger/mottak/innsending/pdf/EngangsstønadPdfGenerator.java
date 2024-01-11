package no.nav.foreldrepenger.mottak.innsending.pdf;

import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_ENGANGSSTØNAD;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.common.domain.felles.Kjønn;
import no.nav.foreldrepenger.common.domain.felles.Vedlegg;
import no.nav.foreldrepenger.common.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Adopsjon;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.FremtidigFødsel;
import no.nav.foreldrepenger.common.domain.felles.relasjontilbarn.Fødsel;
import no.nav.foreldrepenger.common.innsending.SøknadEgenskap;
import no.nav.foreldrepenger.common.innsending.mappers.MapperEgenskaper;
import no.nav.foreldrepenger.common.util.StreamUtil;
import no.nav.foreldrepenger.common.util.TokenUtil;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.InnsendingPersonInfo;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.Blokk;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.DokumentBestilling;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.DokumentPerson;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.FritekstBlokk;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.GruppeBlokk;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.ListeBlokk;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.MottattDato;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.TabellRad;
import no.nav.foreldrepenger.mottak.innsending.pdf.modell.TemaBlokk;
import no.nav.foreldrepenger.mottak.innsending.pdf.pdftjeneste.PdfGenerator;

@Component
public class EngangsstønadPdfGenerator implements MappablePdfGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(EngangsstønadPdfGenerator.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final SøknadTextFormatter textFormatter;
    private final PdfGenerator pdfGenerator;
    private final TokenUtil tokenUtil;

    public EngangsstønadPdfGenerator(SøknadTextFormatter textFormatter, PdfGenerator pdfGenerator, TokenUtil tokenUtil) {
        this.textFormatter = textFormatter;
        this.pdfGenerator = pdfGenerator;
        this.tokenUtil = tokenUtil;
    }

    @Override
    public MapperEgenskaper mapperEgenskaper() {
        return MapperEgenskaper.of(INITIELL_ENGANGSSTØNAD);
    }

    @Override
    public byte[] generer(Søknad søknad, SøknadEgenskap egenskap, InnsendingPersonInfo person) {
        return pdfGenerator.generate(esFra(søknad, person));
    }

    private DokumentBestilling esFra(Søknad søknad, InnsendingPersonInfo person) {
        return new DokumentBestilling(
                txt("søknad_engang"),
                personFra(person),
                mottattDato(),
                lagOverskrifter(søknad));
    }

    private MottattDato mottattDato() {
        return new MottattDato(txt("mottattid"), FMT.format(LocalDateTime.now()));
    }

    private List<TemaBlokk> lagOverskrifter(Søknad søknad) {
        var stønad = (Engangsstønad) søknad.getYtelse();
        var medlemsskap = stønad.medlemsskap();
        List<TemaBlokk> grupper = new ArrayList<>();

        var kjønn = tokenUtil.autentisertBrukerOrElseThrowException().kjønn();
        LOG.info("KJØNN {}", kjønn);
        // info om barn
        grupper.add(omBarn(søknad, kjønn, stønad));

        // info om utenlandsopphold og trygdetilknytning
        if (medlemsskap != null) {
            grupper.add(tilknytning(medlemsskap, stønad));
        } else {
            grupper.add(tilknytning(stønad));
        }

        return grupper;
    }

    private TemaBlokk tilknytning(Engangsstønad stønad) {
        var utenlandsopphold = stønad.utenlandsopphold();
        List<Blokk> tabeller = new ArrayList<>();
        if (utenlandsopphold.opphold().isEmpty()) {
            tabeller.add(new FritekstBlokk(textFormatter.fromMessageSource("medlemsskap.norge.fortiden")));
            tabeller.add(new FritekstBlokk(textFormatter.fromMessageSource("medlemsskap.norge.fremtiden")));
        } else {
            tabeller.addAll(tabellRader(textFormatter.utenlandsPerioder(utenlandsopphold.opphold())));
        }
        return new TemaBlokk(txt("medlemsskap"), tabeller);
    }

    private TemaBlokk tilknytning(Medlemsskap medlemsskap, Engangsstønad stønad) {
        var fødselssted = fødselssted(medlemsskap, stønad);
        var tidligereUtenlandsopphold = textFormatter
                .utenlandsPerioder(medlemsskap.tidligereUtenlandsopphold());
        List<Blokk> tabeller = new ArrayList<>();
        if (!tidligereUtenlandsopphold.isEmpty()) {
            tabeller.add(new GruppeBlokk(txt("siste12"), tabellRader(tidligereUtenlandsopphold)));
        }
        var fremtidige = textFormatter.utenlandsPerioder(medlemsskap.framtidigUtenlandsopphold());
        if (!fremtidige.isEmpty()) {
            tabeller.add(new GruppeBlokk(txt("neste12"), tabellRader(fremtidige)));
        }
        tabeller.add(new FritekstBlokk(fødselssted));
        return new TemaBlokk(txt("tilknytning"), tabeller);
    }

    private static List<TabellRad> tabellRader(List<UtenlandsoppholdFormatert> rader) {
        return rader.stream()
                .map(r -> new TabellRad(r.land(), r.datointervall(), null))
                .toList();
    }

    private String fødselssted(Medlemsskap medlemsskap, Engangsstønad stønad) {
        if (erFremtidigFødsel(stønad) || erAdopsjon(stønad)) {
            return textFormatter.fromMessageSource("terminføderi",
                    textFormatter.countryName(medlemsskap.landVedDato(stønad.relasjonTilBarn().relasjonsDato())),
                stønad.relasjonTilBarn().getAntallBarn() > 1 ? "a" : "et");
        }

        var fødsel = (Fødsel) stønad.relasjonTilBarn();
        var land = stønad.medlemsskap().landVedDato(fødsel.getFødselsdato().get(0));
        return textFormatter.fromMessageSource("fødtei",
                textFormatter.countryName(land),
                fødsel.getAntallBarn() > 1 ? "a" : "et");
    }

    private TemaBlokk omBarn(Søknad søknad, Kjønn kjønn, Engangsstønad stønad) {
        return new TemaBlokk(txt("ombarn"), omFødsel(søknad, kjønn, stønad));
    }

    private List<Blokk> omFødsel(Søknad søknad, Kjønn kjønn, Engangsstønad stønad) {
        if (erAdopsjon(stønad)) {
            return adopsjon(stønad, kjønn, søknad.getVedlegg());
        }
        if (erFremtidigFødsel(stønad)) {
            return fødsel(søknad, stønad);
        }
        return født(stønad);
    }

    private List<Blokk> adopsjon(Engangsstønad stønad, Kjønn kjønn, List<Vedlegg> v) {
        var a = (Adopsjon) stønad.relasjonTilBarn();
        var blokker = new ArrayList<Blokk>();
        blokker.add(new FritekstBlokk(txt("adopsjonsdato", textFormatter.dato(a.getOmsorgsovertakelsesdato()))));
        blokker.add(new FritekstBlokk(txt("fødselsdato", textFormatter.datoer(a.getFødselsdato()))));
        blokker.add(new FritekstBlokk(txt("ektefellesbarn", textFormatter.yesNo(a.isEktefellesBarn()))));
        if (Kjønn.M.equals(kjønn)) {
            blokker.add(new FritekstBlokk(txt("søkeradopsjonalene", textFormatter.yesNo(a.isSøkerAdopsjonAlene()))));
        }

        blokker.add(new FritekstBlokk(txt("antallbarn", a.getAntallBarn())));
        blokker.add(new ListeBlokk(txt("vedlegg1"), StreamUtil.safeStream(v)
                .map(Vedlegg::getBeskrivelse)
                .toList()));
        return blokker;
    }

    private List<Blokk> født(Engangsstønad stønad) {
        var ff = (Fødsel) stønad.relasjonTilBarn();
        var tekst = txt("gjelderfødselsdato", stønad.relasjonTilBarn().getAntallBarn(),
                textFormatter.datoer(ff.getFødselsdato()));
        return List.of(new FritekstBlokk(tekst));
    }

    private List<Blokk> fødsel(Søknad søknad, Engangsstønad stønad) {
        var ff = (FremtidigFødsel) stønad.relasjonTilBarn();
        var antallBarn = stønad.relasjonTilBarn().getAntallBarn();
        var termindato = textFormatter.dato(ff.getTerminDato());
        var barnInfo = new FritekstBlokk(txt("gjeldertermindato", antallBarn, termindato));
        if (!søknad.getPåkrevdeVedlegg().isEmpty()) {
            var vedleggInfo = new FritekstBlokk(textFormatter.fromMessageSource("terminbekreftelsedatert",
                    textFormatter.dato(ff.getUtstedtDato())));
            return List.of(barnInfo, vedleggInfo);
        }
        return List.of(barnInfo);
    }

    private static boolean erFremtidigFødsel(Engangsstønad stønad) {
        return stønad.relasjonTilBarn() instanceof FremtidigFødsel;
    }

    private static boolean erAdopsjon(Engangsstønad stønad) {
        return stønad.relasjonTilBarn() instanceof Adopsjon;
    }

    private String txt(String gjelder, Object... values) {
        return textFormatter.fromMessageSource(gjelder, values);
    }

    private String txt(String key) {
        return textFormatter.fromMessageSource(key);
    }

    private DokumentPerson personFra(InnsendingPersonInfo person) {
        return new DokumentPerson(
            person.fnr().value(),
            null, textFormatter.sammensattNavn(person.navn()),
            null,
            null,
            null
        );
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [textFormatter=" + textFormatter + "]";
    }
}
