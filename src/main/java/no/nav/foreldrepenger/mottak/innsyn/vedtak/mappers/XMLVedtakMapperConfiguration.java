package no.nav.foreldrepenger.mottak.innsyn.vedtak.mappers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.foreldrepenger.common.innsyn.vedtak.mappers.UkjentXMLVedtakMapper;
import no.nav.foreldrepenger.common.innsyn.vedtak.mappers.V1EngangsstønadXMLVedtakMapper;
import no.nav.foreldrepenger.common.innsyn.vedtak.mappers.V2XMLVedtakMapper;
import no.nav.foreldrepenger.common.innsyn.vedtak.mappers.XMLVedtakMapper;

@Configuration
public class XMLVedtakMapperConfiguration {

    @Bean
    public XMLVedtakMapper UkjentXMLVedtakMapper() {
        return new UkjentXMLVedtakMapper();
    }

    @Bean
    public XMLVedtakMapper V1EngangsstønadXMLVedtakMapper() {
        return new V1EngangsstønadXMLVedtakMapper();
    }

    @Bean
    public XMLVedtakMapper V2XMLVedtakMapper() {
        return new V2XMLVedtakMapper();
    }
}
