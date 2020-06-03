package no.nav.foreldrepenger.mottak.oppslag;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "arbeidsforhold", ignoreInvalidFields = true)
@Configuration
public class ArbeidsforholdConfig {

}
