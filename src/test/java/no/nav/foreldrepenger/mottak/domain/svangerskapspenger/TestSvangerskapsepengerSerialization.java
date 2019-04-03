package no.nav.foreldrepenger.mottak.domain.svangerskapspenger;

import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.svp;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.TestForeldrepengerSerialization.test;
import static no.nav.foreldrepenger.mottak.innsending.SøknadType.INITIELL_SVANGERSKAPSPENGER;
import static no.nav.foreldrepenger.mottak.util.Versjon.V1;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.innsending.mappers.DomainMapper;
import no.nav.foreldrepenger.mottak.innsending.mappers.V1SvangerskapspengerDomainMapper;
import no.nav.foreldrepenger.mottak.innsyn.Inspektør;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsyn.XMLStreamSøknadInspektør;
import no.nav.foreldrepenger.mottak.innsyn.mappers.V1SVPXMLMapper;

@AutoConfigureJsonTesters
@ExtendWith(SpringExtension.class)
public class TestSvangerskapsepengerSerialization {

    @Autowired
    ObjectMapper mapper;

    private static final Inspektør INSPEKTØR = new XMLStreamSøknadInspektør();

    private static final DomainMapper DOMAINMAPPER = new V1SvangerskapspengerDomainMapper();

    @Test
    public void inspiser() {
        SøknadEgenskap resultat = INSPEKTØR.inspiser(svp());
        assertEquals(resultat.getVersjon(), V1);
        assertEquals(resultat.getType(), INITIELL_SVANGERSKAPSPENGER);
    }

    @Test
    public void testSVP() {
        test(svp(), true, mapper);
    }

    @Test
    public void testXML() {
        Søknad svp = svp();
        String xml = DOMAINMAPPER.tilXML(svp, new AktorId("42"), INSPEKTØR.inspiser(svp));
        System.out.println(xml);
        SøknadEgenskap resultat = INSPEKTØR.inspiser(xml);
        assertEquals(V1, resultat.getVersjon());
        assertEquals(INITIELL_SVANGERSKAPSPENGER, resultat.getType());
    }

    @Test
    public void testRoundtrip() {
        Søknad svp = svp();
        String xml = DOMAINMAPPER.tilXML(svp, new AktorId("42"), INSPEKTØR.inspiser(svp));
        System.out.println(xml);
        SøknadEgenskap egenskap = INSPEKTØR.inspiser(xml);
        Søknad svp1 = new V1SVPXMLMapper(null).tilSøknad(xml, egenskap);
        // assertEquals(svp1.getYtelse(), svp.getYtelse());
    }

}
