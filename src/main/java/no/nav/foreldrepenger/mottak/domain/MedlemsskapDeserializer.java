package no.nav.foreldrepenger.mottak.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public Medlemsskap deserialize(JsonParser p, DeserializationContext ctx)
            throws IOException, JsonProcessingException {
        JsonNode rootNode = p.getCodec().readTree(p);
        return new Medlemsskap(tidligereOpphold(rootNode, p.getCodec()), fremtidigOpphold(rootNode));
    }

    private static TidligereOppholdsInformasjon tidligereOpphold(JsonNode rootNode, ObjectCodec codec)
            throws IOException {
        ArrayNode utland = (ArrayNode) rootNode.get("utenlandsopphold");
        return new TidligereOppholdsInformasjon(booleanValue(rootNode, "iNorgeSiste12"),
                ArbeidsInformasjon.valueOf(textValue(rootNode, "arbeidSiste12")),
                utenlandsOpphold(utland, codec));
    }

    private static FramtidigOppholdsInformasjon fremtidigOpphold(JsonNode rootNode) {
        return new FramtidigOppholdsInformasjon(booleanValue(rootNode, "fødselINorge"),
                booleanValue(rootNode, "iNorgeNeste12"));
    }

    private static String textValue(JsonNode rootNode, String fieldName) {
        return ((TextNode) rootNode.get(fieldName)).textValue();
    }

    private static boolean booleanValue(JsonNode rootNode, String fieldName) {
        return ((BooleanNode) rootNode.get(fieldName)).booleanValue();
    }

    private static List<Utenlandsopphold> utenlandsOpphold(ArrayNode utland, ObjectCodec codec) throws IOException {
        List<Utenlandsopphold> utenlands = new ArrayList<>();
        if (utland != null) {
            for (int i = 0; i < utland.size(); i++) {
                ObjectNode periode = (ObjectNode) utland.get(i);
                JsonParser parser = periode.traverse();
                parser.setCodec(codec);
                utenlands.add(parser.readValueAs(Utenlandsopphold.class));
            }
        }
        return utenlands;
    }

}
