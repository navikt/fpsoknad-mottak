package no.nav.foreldrepenger.mottak.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;

import no.nav.foreldrepenger.mottak.http.interceptors.TokenXConfigFinder;
import no.nav.security.token.support.client.core.ClientAuthenticationProperties;
import no.nav.security.token.support.client.core.ClientProperties;
import no.nav.security.token.support.client.core.OAuth2GrantType;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;

@ExtendWith(SpringExtension.class)
@RestClientTest
@ContextConfiguration(classes = RestClientConfiguration.class)
class RestClientConfigurationTest {

    @Autowired
    private RestTemplateBuilder builder;

    @Autowired
    private TokenXConfigFinder finder;

    private static ClientConfigurationProperties properties;

    @BeforeAll
    private static void instansiererClientPropertiesForAaregOgFpfordel() {
        var aaregClientProperties = ClientProperties.builder()
            .wellKnownUrl(URI.create("http://localhost:8060/rest/tokenx/.well-known/oauth-authorization-server"))
            .tokenEndpointUrl(URI.create("http://localhost:8060/rest/tokenx/token"))
            .tokenExchange(ClientProperties.TokenExchangeProperties.builder().audience("prod-fss:arbeidsforhold:aareg-services-nais").build())
            .grantType(OAuth2GrantType.TOKEN_EXCHANGE)
            .authentication(ClientAuthenticationProperties.builder()
                .clientId("fpsoknad-mottak")
                .clientJwk("src/test/resources/tokenx/jwk.json")
                .clientAuthMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT)
                .build())
            .build();
        var fpfordelClientProperties = ClientProperties.builder()
            .wellKnownUrl(URI.create("http://localhost:8060/rest/tokenx/.well-known/oauth-authorization-server"))
            .tokenEndpointUrl(URI.create("http://localhost:8060/rest/tokenx/token"))
            .tokenExchange(ClientProperties.TokenExchangeProperties.builder().audience("prod-fss:arbeidsforhold:aareg-services-nais").build())
            .grantType(OAuth2GrantType.TOKEN_EXCHANGE)
            .authentication(ClientAuthenticationProperties.builder()
                .clientId("fpsoknad-mottak")
                .clientJwk("src/test/resources/tokenx/jwk.json")
                .clientAuthMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT)
                .build())
            .build();
        var clientProperties = Map.of(
            "aareg-services", aaregClientProperties,
            "fpfordel", fpfordelClientProperties);
        properties = new ClientConfigurationProperties(clientProperties);
    }


    @Test
    void sjekkAtViKlarerÅHenteUtConfigForAaregServiceFraPath() {
        var aaregUri = URI.create("https://modapp-q1.adeo.no/aareg-services/api/v1/arbeidstaker/arbeidsforhold?historikk=false&sporingsinformasjon=true&ansettelsesperiodeFom=2019-03-08");
        var klientProperty = finder.findProperties(properties, aaregUri);
        assertThat(klientProperty).isNotNull();

        var aaregUriProd = URI.create("https://modapp.adeo.no/aareg-services/api/v1/arbeidstaker/arbeidsforhold?historikk=false&sporingsinformasjon=true&ansettelsesperiodeFom=2019-03-08");
        var klientPropertyProd = finder.findProperties(properties, aaregUriProd);
        assertThat(klientPropertyProd).isNotNull();
    }

    @Test
    void sjekkAtViFinnerConfigForFpfordelFraHost() {
        var aaregUri = URI.create("http://fpfordel/fpfordel/api/dokumentforsendelse");
        var klientProperty = finder.findProperties(properties, aaregUri);
        assertThat(klientProperty).isNotNull();
    }

    @Test
    void sjekkAtViReturnererNullNårKlientIkkeErRegistrert() {
        var fpsakUri = URI.create("http://fpsak/fpsak/saksnummer/en/to");
        var klientProperty = finder.findProperties(properties, fpsakUri);
        assertThat(klientProperty).isNull();
    }
}
