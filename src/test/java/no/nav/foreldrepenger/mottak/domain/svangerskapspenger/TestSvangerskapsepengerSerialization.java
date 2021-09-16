package no.nav.foreldrepenger.mottak.domain.svangerskapspenger;

import static no.nav.foreldrepenger.common.innsending.SøknadType.INITIELL_SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.common.util.Versjon.V1;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.delvisTilrettelegging;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.svp;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.TestForeldrepengerSerialization.test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.Svangerskapspenger;
import no.nav.foreldrepenger.mottak.innsending.mappers.DomainMapper;
import no.nav.foreldrepenger.mottak.innsending.mappers.V1SvangerskapspengerDomainMapper;
import no.nav.foreldrepenger.mottak.innsyn.Inspektør;
import no.nav.foreldrepenger.mottak.innsyn.XMLStreamSøknadInspektør;
import no.nav.foreldrepenger.mottak.innsyn.mappers.V1SVPXMLMapper;

@AutoConfigureJsonTesters
@ExtendWith(SpringExtension.class)
class TestSvangerskapsepengerSerialization {

    @Autowired
    ObjectMapper mapper;

    private static final Inspektør INSPEKTØR = new XMLStreamSøknadInspektør();

    private static final DomainMapper DOMAINMAPPER = new V1SvangerskapspengerDomainMapper(false);

    @Test
    void testSVP() {
        test(svp(), false, mapper);
    }

    @Test
    void testTilrettelegging() {
        test(delvisTilrettelegging(), false, mapper);
    }

    @Test
    void testInspeksjon() {
        var svp = svp();
        String xml = DOMAINMAPPER.tilXML(svp, new AktørId("42"), INSPEKTØR.inspiser(svp));
        var resultat = INSPEKTØR.inspiser(xml);
        assertEquals(V1, resultat.getVersjon());
        assertEquals(INITIELL_SVANGERSKAPSPENGER, resultat.getType());
    }

    @Test
    void testRoundtrip() {
        var svp = svp();
        String xml = DOMAINMAPPER.tilXML(svp, new AktørId("42"), INSPEKTØR.inspiser(svp));
        var egenskap = INSPEKTØR.inspiser(xml);
        var svp1 = new V1SVPXMLMapper(true).tilSøknad(xml, egenskap);
        var orig = (Svangerskapspenger) svp.getYtelse();
        var res = (Svangerskapspenger) svp1.getYtelse();
        assertEquals(orig.getOpptjening(), res.getOpptjening());
    }
}
