package no.nav.foreldrepenger.selvbetjening.security.openam.domain;


import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import no.nav.foreldrepenger.selvbetjening.security.openam.json.NavPrincipalDeserializer;

@JsonDeserialize(using = NavPrincipalDeserializer.class)
public class NavPrincipal extends User {

    private String token;
    private String uid;
    private String securityLevel = "0";
    private String culture;
    private String authMethod;
    private String authType;

    public NavPrincipal(String uid, String token, String culture, String securityLevel, String authMethod, String authType, Collection<? extends GrantedAuthority> roller) {
        super(uid, "", true, true, true, true, roller);
        this.uid = uid;
        this.token = token;
        this.culture = culture;
        this.securityLevel = securityLevel;
        this.authMethod = authMethod;
        this.authType = authType;
       
    }

    public String getToken() {
        return token;
    }

    public String getUid() {
        return uid;
    }

    public String getSecurityLevel() {
        return securityLevel;
    }

    public String getCulture() {
        return culture;
    }

    public String getAuthMethod() {
        return authMethod;
    }

    public String getAuthType() {
        return authType;
    }

    @Override
    public String toString() {
        return "NavPrincipal{" +
                ", uid='" + uid + '\'' +
                ", securityLevel=" + securityLevel +
                ", culture='" + culture + '\'' +
                ", authType'" + authType + '\'' +
                ", authMethod='" + authMethod + '\'' +
                '}';
    }
}
