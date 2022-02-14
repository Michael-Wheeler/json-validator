package name.michael.jsonvalidator.application.validate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.HttpCode;
import name.michael.jsonvalidator.application.util.JsonCleaner;
import name.michael.jsonvalidator.domain.validate.ValidateService;
import name.michael.jsonvalidator.infrastructure.exception.DatabaseConnectionException;
import name.michael.jsonvalidator.infrastructure.exception.EntryNotFoundException;
import name.michael.jsonvalidator.infrastructure.schema.FileSystemSchemaRepository;

import static name.michael.jsonvalidator.application.util.ResponseUtil.createErrorJsonResponse;
import static name.michael.jsonvalidator.application.util.ResponseUtil.createSuccessJsonResponse;

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
            ctx.status(HttpCode.BAD_REQUEST);
            createErrorJsonResponse(ctx, "validateDocument", schemaId, "Invalid JSON body provided");
            return;
        }

        String errors;
        try {
            errors = new ValidateService(new FileSystemSchemaRepository()).validateJson(cleanedJson, schemaId);
        } catch (DatabaseConnectionException e) {
            ctx.status(HttpCode.INTERNAL_SERVER_ERROR);
            createErrorJsonResponse(ctx, "validateDocument", schemaId, "Internal server error");
            return;
        } catch (EntryNotFoundException e) {
            ctx.status(HttpCode.NOT_FOUND);
            createErrorJsonResponse(ctx, "validateDocument", schemaId, "Schema not found");
            return;
        }

        if (errors != null) {
            createErrorJsonResponse(ctx, "validateDocument", schemaId, errors);
            return;
        }

        createSuccessJsonResponse(ctx, "validateDocument", schemaId);
    }
}
