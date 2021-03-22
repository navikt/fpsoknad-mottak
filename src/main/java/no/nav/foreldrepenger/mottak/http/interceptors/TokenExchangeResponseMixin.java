package no.nav.foreldrepenger.mottak.http.interceptors;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class TokenExchangeResponseMixin {
    @JsonIgnore
    abstract int getIssuedTokenType();

    @JsonIgnore
    int issued_token_type;
}
