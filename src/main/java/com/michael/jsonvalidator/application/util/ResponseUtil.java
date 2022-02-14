package com.michael.jsonvalidator.application.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.http.Context;
import io.javalin.http.HttpCode;
import com.michael.jsonvalidator.domain.schema.Schema;

public class ResponseUtil {
    public static Context createErrorJsonResponse(HttpCode code, Context ctx, String action, String id, String message) {
        ctx.status(code);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("action", action);
        rootNode.put("id", id);
        rootNode.put("status", "error");
        rootNode.put("message", message);

        return ctx.json(rootNode);
    }

    public static Context createSuccessJsonResponse(HttpCode code, Context ctx, String action, String id) {
        ctx.status(code);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("action", action);
        rootNode.put("id", id);
        rootNode.put("status", "success");

        return ctx.json(rootNode);
    }

    public static Context createSchemaJsonResponse(HttpCode code, Context ctx, String action, Schema schema) {
        ctx.status(code);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("action", "downloadSchema");
        rootNode.put("id", schema.getId());
        rootNode.put("status", "success");
        rootNode.set("schema", schema.getSchema());

        return ctx.json(rootNode);
    }
}
