package com.michael.jsonvalidator.application.validate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.michael.jsonvalidator.application.util.JsonCleaner;
import com.michael.jsonvalidator.application.util.ResponseUtil;
import com.michael.jsonvalidator.domain.validate.ValidateService;
import com.michael.jsonvalidator.infrastructure.exception.DatabaseConnectionException;
import com.michael.jsonvalidator.infrastructure.schema.FileSystemSchemaRepository;
import io.javalin.http.Context;
import io.javalin.http.HttpCode;
import com.michael.jsonvalidator.infrastructure.exception.EntryNotFoundException;

public class ValidateController {
    private ValidateService service;

    public ValidateController(ValidateService service) {
        this.service = service;
    }

    public static void validateJson(Context ctx) {
        String schemaId = ctx.pathParam("schema-id");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode cleanedJson;
        try {
            JsonNode json = mapper.readTree(ctx.body());
            cleanedJson = JsonCleaner.removeNullValues(json);
        } catch (JsonProcessingException e) {
            ResponseUtil.createErrorJsonResponse(HttpCode.BAD_REQUEST, ctx, "validateDocument", schemaId, "Invalid JSON body provided");
            return;
        }

        String errors;
        try {
            errors = new ValidateService(new FileSystemSchemaRepository()).validateJson(cleanedJson, schemaId);
        } catch (DatabaseConnectionException e) {
            ResponseUtil.createErrorJsonResponse(HttpCode.INTERNAL_SERVER_ERROR, ctx, "validateDocument", schemaId, "Internal server error");
            return;
        } catch (EntryNotFoundException e) {
            ResponseUtil.createErrorJsonResponse(HttpCode.NOT_FOUND, ctx, "validateDocument", schemaId, "Schema not found");
            return;
        }

        if (errors != null) {
            ResponseUtil.createErrorJsonResponse(HttpCode.UNPROCESSABLE_ENTITY, ctx, "validateDocument", schemaId, errors);
            return;
        }

        ResponseUtil.createSuccessJsonResponse(HttpCode.OK, ctx, "validateDocument", schemaId);
    }
}
