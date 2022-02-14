package com.michael.jsonvalidator.domain.schema;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class Schema {
    private final String id;
    private final ObjectNode schema;

    public Schema(String id, ObjectNode schema) {
        this.id = id;
        this.schema = schema;
    }

    public String getId() {
        return id;
    }

    public ObjectNode getSchema() {
        return schema;
    }
}
