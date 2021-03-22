package no.nav.foreldrepenger.mottak.http.interceptors;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class TokenExchangeResponseMixin {
    @JsonIgnore
    abstract String getIssuedTokenType();

    @JsonIgnore
    String issued_token_type;
}
