package no.nav.foreldrepenger.selvbetjening.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

	    @Bean
	    public Docket productApi() {
	        return new Docket(DocumentationType.SWAGGER_2)
	        		.protocols(hashSet("http", "https"))
	        		.select()    
	                .apis(RequestHandlerSelectors.any())              
	                .paths(PathSelectors.any())
	                .build();
	    }

		private static  Set<String> hashSet(String... schemes) {
			Set<String> supportedSchemes = new HashSet<>();
			for (String scheme : schemes) {
				supportedSchemes.add(scheme);
			}
			return supportedSchemes;
		}
	
}
