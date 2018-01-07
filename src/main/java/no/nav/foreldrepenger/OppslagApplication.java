package no.nav.foreldrepenger;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.*;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;


@SpringBootApplication
@ComponentScan("no.nav.foreldrepenger")
public class OppslagApplication {

	public static void main(String[] args) {
		System.setProperty("no.nav.modig.security.sts.url",
         System.getenv("SECURITYTOKENSERVICE_URL"));
		System.setProperty("no.nav.modig.security.systemuser.username",
         System.getenv("FPSELVBETJENING_USERNAME"));
		System.setProperty("no.nav.modig.security.systemuser.password",
         System.getenv("FPSELVBETJENING_PASSWORD"));
		SpringApplication.run(OppslagApplication.class, args);
	}

	@Bean
	public Module jodaModule() {
		return new JodaModule();
	}

}
