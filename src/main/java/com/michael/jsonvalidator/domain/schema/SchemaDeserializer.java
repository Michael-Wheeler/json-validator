package com.michael.jsonvalidator.domain.schema;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class SchemaDeserializer extends StdDeserializer<Schema> {
    public SchemaDeserializer() {
        this(null);
    }

    public SchemaDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Schema deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException {
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);

        JsonNode idNode = node.get("id");
        String id = idNode.asText();
        ObjectNode schemaJson = node.get("schema").deepCopy();

        return new Schema(id, schemaJson);
    }
}
