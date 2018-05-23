package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static no.nav.foreldrepenger.mottak.util.Jaxb.context;

import javax.xml.bind.JAXBContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.mottak.domain.UUIDIdGenerator;
import no.nav.foreldrepenger.mottak.fpfordel.FPFordelConfig;
import no.nav.foreldrepenger.mottak.fpfordel.FPFordelConfiguration;
import no.nav.foreldrepenger.mottak.fpfordel.FPFordelConnection;
import no.nav.foreldrepenger.mottak.fpfordel.FPFordelMetdataGenerator;
import no.nav.foreldrepenger.mottak.fpfordel.FPFordelSpringKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.fpfordel.FPFordelSøknadGenerator;
import no.nav.foreldrepenger.mottak.http.AktørIdService;
import no.nav.foreldrepenger.mottak.pdf.ForeldrepengerPDFGenerator;
import no.nav.security.spring.oidc.SpringOIDCRequestContextHolder;
import no.nav.security.spring.oidc.validation.interceptor.BearerTokenClientHttpRequestInterceptor;
import no.nav.vedtak.felles.xml.soeknad.v1.Soeknad;

@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration(classes = { FPFordelConfiguration.class, FPFordelConnection.class, FPFordelConfig.class,
        FPFordelSpringKonvoluttGenerator.class,
        FPFordelSøknadGenerator.class, UUIDIdGenerator.class, FPFordelMetdataGenerator.class,
        SpringOIDCRequestContextHolder.class,
        BearerTokenClientHttpRequestInterceptor.class,
        ForeldrepengerPDFGenerator.class, AktørIdService.class })
@AutoConfigureJsonTesters
public class TestFPFordelSerialization {

    private static JAXBContext SØKNADCTX = context(Soeknad.class);

    @Autowired
    ObjectMapper mapper;
    @Autowired
    UUIDIdGenerator refGenerator;
    @Autowired
    FPFordelSøknadGenerator søknadXMLGenerator;
    @Autowired
    FPFordelSpringKonvoluttGenerator konvoluttGenerator;
    @Autowired
    FPFordelConnection config;

    @Test
    public void testKonvolutt() throws Exception {

    }

}
