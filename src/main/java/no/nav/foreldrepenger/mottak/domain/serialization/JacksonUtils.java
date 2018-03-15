package no.nav.foreldrepenger.mottak.domain.serialization;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.TextNode;

import no.nav.foreldrepenger.mottak.domain.Navn;

final class JacksonUtils {

    private JacksonUtils() {

    }

    static ArrayNode arrayNode(JsonNode rootNode, String nodeName) {
        return ArrayNode.class.cast(rootNode.get(nodeName));
    }

    static String textValue(JsonNode rootNode, String fieldName) {
        return Optional.ofNullable(rootNode.get(fieldName))
                .filter(s -> s instanceof TextNode)
                .map(s -> TextNode.class.cast(s))
                .map(s -> s.textValue())
                .orElse(null);
    }

    static boolean booleanValue(JsonNode rootNode, String fieldName) {
        return booleanValue(rootNode, fieldName, false);
    }

    static boolean booleanValue(JsonNode rootNode, String fieldName, boolean defaultValue) {
        return Optional.ofNullable(rootNode.get(fieldName))
                .filter(s -> s instanceof BooleanNode)
                .map(s -> BooleanNode.class.cast(s))
                .map(s -> s.booleanValue())
                .orElse(defaultValue);
    }

    static void writeNavn(Navn navn, JsonGenerator jgen) throws IOException {
        jgen.writeStringField("fornavn", navn.getFornavn());
        jgen.writeStringField("mellomnavn", navn.getMellomnavn());
        jgen.writeStringField("etternavn", navn.getEtternavn());
    }

    static Navn navn(JsonNode rootNode) {
        return new Navn(
                textValue(rootNode, "fornavn"), textValue(rootNode, "mellomnavn"),
                textValue(rootNode, "etternavn"));
    }

}
