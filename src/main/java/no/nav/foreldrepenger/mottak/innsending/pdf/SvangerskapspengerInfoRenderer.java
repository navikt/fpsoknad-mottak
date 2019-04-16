package no.nav.foreldrepenger.mottak.innsending.pdf;


import com.google.common.base.Joiner;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.Frilans;
import no.nav.foreldrepenger.mottak.domain.felles.ÅpenPeriode;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

@Component
public class SvangerskapspengerInfoRenderer {
    private static final float STARTY = PDFElementRenderer.calculateStartY();
    private static final int INDENT = 20;
    private final PDFElementRenderer renderer;
    private final SøknadTextFormatter textFormatter;

    public SvangerskapspengerInfoRenderer(PDFElementRenderer renderer, SøknadTextFormatter textFormatter) {
        this.renderer = renderer;
        this.textFormatter = textFormatter;
    }

    public float frilansOpptjening(Frilans frilans, FontAwareCos cos, float y) throws IOException {
        if (frilans == null) {
            return y;
        }
        y = frilans(frilans, cos, y);
        return y;
    }

    public float frilans(Frilans frilans, FontAwareCos cos, float y) throws IOException {
        y -= renderer.addLeftHeading(txt("frilans"), cos, y);
        List<String> attributter = new ArrayList<>();
        if (frilans.getPeriode().getTom() == null) {
            addIfSet(attributter, "frilanspågår", textFormatter.dato(frilans.getPeriode().getFom()));
        } else {
            attributter.add(txt("frilansavsluttet", textFormatter.dato(frilans.getPeriode().getFom()),
                textFormatter.dato(frilans.getPeriode().getTom())));
        }
        attributter.add(txt("fosterhjem", jaNei(frilans.isHarInntektFraFosterhjem())));
        attributter.add(txt("nyoppstartet", jaNei(frilans.isNyOppstartet())));

        y -= renderer.addLinesOfRegularText(attributter, cos, y);
        if (!frilans.getFrilansOppdrag().isEmpty()) {
            y -= renderer.addLineOfRegularText(txt("oppdrag"), cos, y);
            List<String> oppdrag = safeStream(frilans.getFrilansOppdrag())
                .map(o -> o.getOppdragsgiver() + " " + textFormatter.periode(o.getPeriode()))
                .collect(toList());
            y -= renderer.addBulletList(INDENT, oppdrag, cos, y);
            y -= renderer.addBlankLine();
        } else {
            y -= renderer.addLineOfRegularText(txt("oppdrag") + ": Nei", cos, y);
        }
        y -= renderer.addBlankLine();
        return y;
    }

    private String txt(String key, Object... values) {
        return textFormatter.fromMessageSource(key, values);
    }

    private String jaNei(boolean value) {
        return textFormatter.yesNo(value);
    }

    private void addIfSet(List<String> attributter, String key, String value) {
        if (value != null) {
            attributter.add(txt(key, value));
        }
    }

    private void addListIfSet(List<String> attributter, String key, List<String> values) {
        if (CollectionUtils.isEmpty(values)) {
            return;
        }
        addIfSet(attributter, key, Joiner.on(",").join(values));
    }

    private void addIfSet(List<String> attributter, boolean value, String key, String otherValue) {
        if (value) {
            attributter.add(txt(key, otherValue));
        }
    }

    private void addIfSet(List<String> attributter, String key, LocalDate dato) {
        if (dato != null) {
            attributter.add(txt(key, textFormatter.dato(dato)));
        }
    }

    private void addIfSet(List<String> attributter, String key, List<LocalDate> datoer) {
        if (!CollectionUtils.isEmpty(datoer)) {
            attributter.add(txt(key, textFormatter.datoer(datoer)));
        }
    }

    private void addIfSet(List<String> attributter, String key, Optional<LocalDate> dato) {
        if (dato.isPresent()) {
            attributter.add(txt(key, textFormatter.dato(dato.get())));
        }
    }

    private void addIfSet(List<String> attributter, ÅpenPeriode periode) {
        if (periode != null) {
            addIfSet(attributter, "fom", periode.getFom());
            addIfSet(attributter, "tom", periode.getTom());
        }
    }

}
