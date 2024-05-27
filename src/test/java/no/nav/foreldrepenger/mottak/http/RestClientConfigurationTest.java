package no.nav.foreldrepenger.mottak.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;

import no.nav.security.token.support.client.core.ClientAuthenticationProperties;
import no.nav.security.token.support.client.core.ClientProperties;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
import no.nav.security.token.support.client.spring.oauth2.ClientConfigurationPropertiesMatcher;

@ExtendWith(SpringExtension.class)
class RestClientConfigurationTest {

    private static ClientConfigurationPropertiesMatcher matcher = new ClientConfigurationPropertiesMatcherConfiguration();
    private static ClientConfigurationProperties properties;

    @BeforeAll
    static void instansiererClientPropertiesForAaregOgFpfordel() {
        var clientAuthentication = ClientAuthenticationProperties.builder("fpsoknad-mottak", ClientAuthenticationMethod.PRIVATE_KEY_JWT)
            .clientJwk("src/test/resources/tokenx/jwk.json")
            .build();
        var generellKlientProperties = ClientProperties.builder(GrantType.TOKEN_EXCHANGE, clientAuthentication)
            .wellKnownUrl(URI.create("http://localhost:8060/rest/tokenx/.well-known/oauth-authorization-server"))
            .tokenEndpointUrl(URI.create("http://localhost:8060/rest/tokenx/token"))
            .tokenExchange(new ClientProperties.TokenExchangeProperties("prod-fss:namespace:services"))
            .build();
        var clientProperties = Map.of(
            "aareg-services", generellKlientProperties,
            "digdir-krr-proxy", generellKlientProperties,
            "sokos-kontoregister-person", generellKlientProperties,
            "fpfordel", generellKlientProperties);
        properties = new ClientConfigurationProperties(clientProperties);
    }


    @Test
    void sjekkAtViKlarerÅHenteUtConfigForAaregServiceFraPath() {
        var aaregUri = URI.create("https://aareg-services.dev.intern.nav.no/api/v1/arbeidstaker/arbeidsforhold?historikk=false&sporingsinformasjon=true&ansettelsesperiodeFom=2019-03-08");
        var klientProperty = matcher.findProperties(properties, aaregUri);
        assertThat(klientProperty).isNotNull();

        var aaregUriProd = URI.create("https://aareg-services.intern.nav.no/v1/arbeidstaker/arbeidsforhold?historikk=false&sporingsinformasjon=true&ansettelsesperiodeFom=2019-03-08");
        var klientPropertyProd = matcher.findProperties(properties, aaregUriProd);
        assertThat(klientPropertyProd).isNotNull();
    }

    @Test
    void sjekkAtViKlarerÅHenteUtConfigForDigdirKrrProxy() {
        var aaregUri = URI.create("https://digdir-krr-proxy.intern.nav.no/rest/v1/person");
        var klientProperty = matcher.findProperties(properties, aaregUri);
        assertThat(klientProperty).isNotNull();
    }

    @Test
    void sjekkAtViKlarerÅHenteUtConfigForKontonummer() {
        var aaregUri = URI.create("https://sokos-kontoregister-person.dev.intern.nav.no/kontoregister/api/kontoregister/v1");
        var klientProperty = matcher.findProperties(properties, aaregUri);
        assertThat(klientProperty).isNotNull();
    }

    @Test
    void sjekkAtViFinnerConfigForFpfordelFraHost() {
        var aaregUri = URI.create("http://fpfordel/fpfordel/api/dokumentforsendelse");
        var klientProperty = matcher.findProperties(properties, aaregUri);
        assertThat(klientProperty).isNotNull();
    }

    @Test
    void sjekkAtViReturnererNullNårKlientIkkeErRegistrert() {
        var fpsakUri = URI.create("http://fpsak/fpsak/saksnummer/en/to");
        var klientProperty = matcher.findProperties(properties, fpsakUri);
        assertThat(klientProperty).isNull();
    }
}
