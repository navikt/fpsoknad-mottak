package no.nav.foreldrepenger.mottak.domain.serialization;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import no.nav.foreldrepenger.mottak.errorhandling.UnexpectedInputException;

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
                .map(DoubleNode::doubleValue)
                .orElse(null);
    }

    static Double doubleValue(ObjectNode rootNode) {
        Iterator<Entry<String, JsonNode>> iterator = rootNode.fields();
        while (iterator.hasNext()) {
            JsonNode entry = iterator.next().getValue();
            if (entry instanceof IntNode) {
                return (double) IntNode.class.cast(entry).asInt();
            }
            if (entry instanceof DoubleNode) {
                return DoubleNode.class.cast(entry).asDouble();
            }
        }
        throw new UnexpectedInputException("Ukjent node type %s", rootNode.getClass().getSimpleName());
    }
}
