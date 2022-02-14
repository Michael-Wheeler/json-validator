package com.michael.jsonvalidator.application.schema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.michael.jsonvalidator.application.util.ResponseUtil;
import com.michael.jsonvalidator.infrastructure.exception.AlreadyExistsException;
import com.michael.jsonvalidator.infrastructure.exception.DatabaseConnectionException;
import com.michael.jsonvalidator.infrastructure.schema.FileSystemSchemaRepository;
import io.javalin.http.Context;
import io.javalin.http.HttpCode;
import com.michael.jsonvalidator.domain.schema.Schema;
import com.michael.jsonvalidator.infrastructure.exception.EntryNotFoundException;
import com.michael.jsonvalidator.infrastructure.schema.SchemaRepositoryInterface;
import org.everit.json.schema.SchemaException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;

public class SchemaController {
    SchemaRepositoryInterface repo;

    public SchemaController(SchemaRepositoryInterface repo) {
        this.repo = repo;
    }

    public static void createSchema(Context ctx) {
        String schemaId = ctx.pathParam("schema-id");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode schemaJson;

        try {
            schemaJson = (ObjectNode) mapper.readTree(ctx.body());
            JSONObject inputJsonObject = new JSONObject(mapper.writeValueAsString(schemaJson));
            SchemaLoader.load(inputJsonObject);
        } catch (JsonProcessingException e) {
            ResponseUtil.createErrorJsonResponse(HttpCode.BAD_REQUEST, ctx, "uploadSchema", schemaId, "Schema is invalid JSON");
            return;
        } catch (ClassCastException | SchemaException $e) {
            ResponseUtil.createErrorJsonResponse(HttpCode.UNPROCESSABLE_ENTITY, ctx, "uploadSchema", schemaId, "Invalid JSON schema provided");
            return;
        }

        Schema schema = new Schema(schemaId, schemaJson);

        try {
            new FileSystemSchemaRepository().createSchema(schema);
        } catch (DatabaseConnectionException | JsonProcessingException e) {
            ResponseUtil.createErrorJsonResponse(HttpCode.INTERNAL_SERVER_ERROR, ctx, "uploadSchema", schemaId, "Internal server error");
            return;
        } catch (AlreadyExistsException e) {
            ResponseUtil.createErrorJsonResponse(HttpCode.CONFLICT, ctx, "uploadSchema", schemaId, "Schema with given ID already exists");
            return;
        }

        ResponseUtil.createSuccessJsonResponse(HttpCode.CREATED, ctx, "uploadSchema", schemaId);
    }

    public static void getSchemaById(Context ctx) {
        String schemaId = ctx.pathParam("schema-id");

        try {
            Schema schema = new FileSystemSchemaRepository().getSchemaById(schemaId);
            ResponseUtil.createSchemaJsonResponse(HttpCode.OK, ctx, "downloadSchema", schema);
        } catch (DatabaseConnectionException e) {
            ResponseUtil.createErrorJsonResponse(HttpCode.INTERNAL_SERVER_ERROR, ctx, "downloadSchema", schemaId, "Internal server error");
        } catch (EntryNotFoundException e) {
            ResponseUtil.createErrorJsonResponse(HttpCode.NOT_FOUND, ctx, "downloadSchema", schemaId, "Schema not found");
        }
    }
}
