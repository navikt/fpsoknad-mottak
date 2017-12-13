package no.nav.foreldrepenger.selvbetjening.openam.json;

import static org.springframework.util.StringUtils.hasText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import no.nav.foreldrepenger.selvbetjening.openam.domain.NavPrincipal;
import no.nav.foreldrepenger.selvbetjening.openam.exception.EmptyAttributeException;

public class NavPrincipalDeserializer extends JsonDeserializer<NavPrincipal> {

    public static final String UID = "uid";
    public static final String SECURITY_LEVEL = "SecurityLevel";
    public static final String CULTURE = "Culture";
    public static final String NAME = "name";
    public static final String VALUES = "values";
    public static final String ATTRIBUTES = "attributes";
    public static final String AUTH_METHOD = "AuthMethod";
    public static final String AUTH_TYPE = "AuthType";

    @Override
    public NavPrincipal deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        String uid = "";
        String securityLevel = "";
        String culture = "NO_no";
        String authMethod = "";
        String authType = "";
        ObjectCodec objectCodec = jsonParser.getCodec();
        JsonNode node = objectCodec.readTree(jsonParser);

        JsonNode tokenNode = node.get("token");
        String tokenId = tokenNode.get("tokenId").textValue();
        JsonNode attributesNode = node.get(ATTRIBUTES);

        for (JsonNode jsonNode : attributesNode) {
            String nodeName = jsonNode.get(NAME).textValue();
            JsonNode valuesNode = jsonNode.get(VALUES);

            if (valuesNode.size() > 0 && hasText(valuesNode.get(0).textValue())) {
                String value = valuesNode.get(0).textValue();
                if (nodeName.equalsIgnoreCase(UID)) {
                    uid = value;
                } else if (nodeName.equalsIgnoreCase(SECURITY_LEVEL)) {
                    securityLevel = value;
                } else if (nodeName.equalsIgnoreCase(CULTURE)) {
                    culture = value;
                } else if (nodeName.equalsIgnoreCase(AUTH_METHOD)) {
                    authMethod = value;
                } else if (nodeName.equalsIgnoreCase(AUTH_TYPE)) {
                    authType = value;
                }
            }
        }

        List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
        verifyValidOrThrowException(uid, securityLevel, authMethod);
        return new NavPrincipal(uid, tokenId, culture, securityLevel, authMethod, authType, grantedAuthorities);
    }

    private void verifyValidOrThrowException(String uid, String securityLevel, String authMethod) {
        if (!hasText(uid)) {
            throw new EmptyAttributeException(UID + " has noe value");
        }
        if (!hasText(securityLevel)) {
            throw new EmptyAttributeException(SECURITY_LEVEL + " has no value!");
        }
        if (!hasText(authMethod)) {
            throw new EmptyAttributeException(AUTH_METHOD + " has no value!");
        }
    }
}