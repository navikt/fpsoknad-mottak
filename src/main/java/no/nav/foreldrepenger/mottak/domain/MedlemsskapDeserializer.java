package no.nav.foreldrepenger.mottak.domain;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class MedlemsskapDeserializer extends StdDeserializer<Medlemsskap> {

    public MedlemsskapDeserializer() {
        this(null);
    }

    public MedlemsskapDeserializer(Class<Medlemsskap> t) {
        super(t);
    }

    @Override
    public Medlemsskap deserialize(JsonParser parser, DeserializationContext ctx)
            throws IOException, JsonProcessingException {
        JsonNode rootNode = parser.getCodec().readTree(parser);
        return new Medlemsskap(tidligereOpphold(rootNode, parser), framtidigOpphold(rootNode, parser));
    }

    private TidligereOppholdsInformasjon tidligereOpphold(JsonNode rootNode, JsonParser parser) {
        return new TidligereOppholdsInformasjon(iNorgeSiste12(rootNode), arbeidsInfo(rootNode),
                utenlandsOpphold(rootNode, parser, "utenlandsopphold"));
    }

    private static FramtidigOppholdsInformasjon framtidigOpphold(JsonNode rootNode, JsonParser parser) {
        return new FramtidigOppholdsInformasjon(booleanValue(rootNode, "fødselINorge"),
                utenlandsOpphold(rootNode, parser, "framtidigUtenlandsopphold"));
    }

    private static List<Utenlandsopphold> utenlandsOpphold(JsonNode rootNode, JsonParser parser, String nodeName) {
        return utenlandsOpphold(iterator(arrayNode(rootNode, nodeName)), parser);
    }

    private static List<Utenlandsopphold> utenlandsOpphold(Iterator<JsonNode> iterator, JsonParser parser) {
        return StreamSupport
                .stream(((Iterable<JsonNode>) () -> iterator).spliterator(), false)
                .map(s -> utenlandopphold(s, iterator, parser.getCodec()))
                .collect(toList());
    }

    private static Utenlandsopphold utenlandopphold(JsonNode node, Iterator<JsonNode> utland, ObjectCodec codec) {
        try {
            JsonParser parser = ((ObjectNode) utland.next()).traverse();
            parser.setCodec(codec);
            return parser.readValueAs(Utenlandsopphold.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private ArbeidsInformasjon arbeidsInfo(JsonNode rootNode) {
        return ArbeidsInformasjon.valueOf(textValue(rootNode, "arbeidSiste12"));
    }

    private boolean iNorgeSiste12(JsonNode rootNode) {
        return booleanValue(rootNode, "iNorgeSiste12");
    }

    private static ArrayNode arrayNode(JsonNode rootNode, String nodeName) {
        return ArrayNode.class.cast(rootNode.get(nodeName));
    }

    private static String textValue(JsonNode rootNode, String fieldName) {
        return TextNode.class.cast(rootNode.get(fieldName)).textValue();
    }

    private static boolean booleanValue(JsonNode rootNode, String fieldName) {
        return BooleanNode.class.cast(rootNode.get(fieldName)).booleanValue();
    }

    private static Iterator<JsonNode> iterator(ArrayNode utland) {
        return utland != null ? utland.iterator() : Collections.emptyIterator();
    }
}
