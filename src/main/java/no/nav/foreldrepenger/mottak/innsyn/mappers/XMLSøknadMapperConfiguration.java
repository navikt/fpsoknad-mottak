package no.nav.foreldrepenger.mottak.innsyn.mappers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.foreldrepenger.common.innsyn.mappers.UkjentXMLSøknadMapper;
import no.nav.foreldrepenger.common.innsyn.mappers.V1EngangsstønadDokmotXMLMapper;
import no.nav.foreldrepenger.common.innsyn.mappers.V1EngangsstønadPapirXMLMapper;
import no.nav.foreldrepenger.common.innsyn.mappers.V1ForeldrepengerXMLMapper;
import no.nav.foreldrepenger.common.innsyn.mappers.V1SVPXMLMapper;
import no.nav.foreldrepenger.common.innsyn.mappers.V2EngangsstønadXMLMapper;
import no.nav.foreldrepenger.common.innsyn.mappers.V2ForeldrepengerXMLMapper;
import no.nav.foreldrepenger.common.innsyn.mappers.V3EngangsstønadXMLMapper;
import no.nav.foreldrepenger.common.innsyn.mappers.V3ForeldrepengerXMLMapper;
import no.nav.foreldrepenger.common.innsyn.mappers.XMLSøknadMapper;
import no.nav.foreldrepenger.common.oppslag.Oppslag;

@Configuration
public class XMLSøknadMapperConfiguration {

    @Bean
    public XMLSøknadMapper ukjentXmlMapper() {
        return new UkjentXMLSøknadMapper();
    }

    @Bean
    public XMLSøknadMapper V1EngangsstønadDokmotXMLMapper() {
        return new V1EngangsstønadDokmotXMLMapper();
    }

    @Bean
    public XMLSøknadMapper V1EngangsstønadPapirXMLMapper(Oppslag oppslag) {
        return new V1EngangsstønadPapirXMLMapper(oppslag);
    }

    @Bean
    public XMLSøknadMapper V1ForeldrepengerXMLMapper(Oppslag oppslag) {
        return new V1ForeldrepengerXMLMapper(oppslag);
    }

    @Bean
    public XMLSøknadMapper V1SVPXMLMapper() {
        return new V1SVPXMLMapper();
    }


    @Bean
    public XMLSøknadMapper V2EngangsstønadXMLMapper(Oppslag oppslag) {
        return new V2EngangsstønadXMLMapper(oppslag);
    }

    @Bean
    public XMLSøknadMapper V2ForeldrepengerXMLMapper(Oppslag oppslag) {
        return new V2ForeldrepengerXMLMapper(oppslag);
    }

    @Bean
    public XMLSøknadMapper V3EngangsstønadXMLMapper(Oppslag oppslag) {
        return new V3EngangsstønadXMLMapper(oppslag);
    }

    @Bean
    public XMLSøknadMapper V3ForeldrepengerXMLMapper(Oppslag oppslag) {
        return new V3ForeldrepengerXMLMapper(oppslag);
    }

}
