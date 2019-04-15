package no.nav.foreldrepenger.mottak.domain.serialization;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

final class JacksonUtils {

    private JacksonUtils() {
    }

    static ArrayNode arrayNode(JsonNode rootNode, String nodeName) {
        return Optional.ofNullable(rootNode.get(nodeName))
                .filter(s -> s instanceof ArrayNode)
                .map(ArrayNode.class::cast)
                .orElse(null);
    }

    static String textValue(JsonNode rootNode, String fieldName) {
        return Optional.ofNullable(rootNode.get(fieldName))
                .filter(s -> s instanceof TextNode)
                .map(TextNode.class::cast)
                .map(TextNode::textValue)
                .orElse(null);
    }

    static Double doubleValue(DoubleNode rootNode) {
        return Optional.ofNullable(rootNode)
                .map(DoubleNode::doubleValue).orElse(null);
    }

    static Double doubleValue(ObjectNode rootNode) {
        return rootNode.findValue("prosent").doubleValue();
    }
}
