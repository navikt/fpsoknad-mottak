package no.nav.foreldrepenger.mottak.http.interceptors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public interface TokenExchangeResponseMixin {

}
